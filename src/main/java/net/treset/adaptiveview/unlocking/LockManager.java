package net.treset.adaptiveview.unlocking;

import net.minecraft.server.network.ServerPlayerEntity;
import net.treset.adaptiveview.AdaptiveViewMod;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.distance.ViewDistanceHandler;
import net.treset.adaptiveview.tools.NotificationState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class LockManager {
    private final Config config;
    private final ViewDistanceHandler viewDistanceHandler;
    private final List<Locker> lockers = new ArrayList<>();
    private Integer viewLockedManually = null;
    private Integer simLockedManually = null;
    private Integer chunkTickLockedManually = null;

    private Locker currentViewLocker = null;
    private Locker currentSimLocker = null;
    private Locker currentChunkTickLocker = null;

    public LockManager(Config config, ViewDistanceHandler viewDistanceHandler) {
        this.config = config;
        this.viewDistanceHandler = viewDistanceHandler;
    }

    public void lockManually(Integer chunks, LockTarget target) {
        if(target == LockTarget.VIEW || target == LockTarget.MAIN) {
            viewLockedManually = chunks;
            lockView(chunks);
        }
        if(target == LockTarget.SIM || target == LockTarget.MAIN) {
            simLockedManually = chunks;
            lockSim(chunks);
        }
        if(target == LockTarget.CHUNK) {
            chunkTickLockedManually = chunks;
            lockChunkTick(chunks);
        }

        updateLocker();
    }

    public Integer getLockedManually(LockTarget target) {
        return switch (target) {
            case VIEW -> viewLockedManually;
            case SIM -> simLockedManually;
            case MAIN -> {
                if(viewLockedManually != null && simLockedManually != null) {
                    yield Math.min(viewLockedManually, simLockedManually);
                } else if(viewLockedManually != null) {
                    yield viewLockedManually;
                }
                yield simLockedManually;
            }
            case CHUNK -> chunkTickLockedManually;
        };
    }

    public Locker getCurrentLocker(LockTarget target) {
        return switch (target) {
            case VIEW -> currentViewLocker;
            case SIM -> currentSimLocker;
            case MAIN -> {
                if (currentViewLocker != null && currentSimLocker != null) {
                    yield currentViewLocker.getDistance() < currentSimLocker.getDistance() ? currentViewLocker : currentSimLocker;
                } else if (currentViewLocker != null) {
                    yield currentViewLocker;
                } else if (currentSimLocker != null) {
                    yield currentSimLocker;
                } else {
                    yield null;
                }
            }
            case CHUNK -> currentChunkTickLocker;
        };
    }

    public int getNumLockers(LockTarget target) {
        return switch (target) {
            case VIEW -> (int) lockers.stream().filter(e -> e.getTarget() == LockTarget.VIEW || e.getTarget() == LockTarget.MAIN).count();
            case SIM -> (int) lockers.stream().filter(e -> e.getTarget() == LockTarget.SIM || e.getTarget() == LockTarget.MAIN).count();
            case MAIN -> (int) lockers.stream().filter(e -> e.getTarget() == LockTarget.VIEW || e.getTarget() == LockTarget.SIM || e.getTarget() == LockTarget.MAIN).count();
            case CHUNK -> (int) lockers.stream().filter(e -> e.getTarget() == LockTarget.CHUNK).count();
        };
    }

    public void addLocker(Locker unlocker) {
        lockers.add(unlocker);
        updateLocker();
    }

    public void clearLockers(LockTarget target) {
        switch (target) {
            case VIEW -> {
                for(Locker e : lockers) {
                    if(e.getTarget() == LockTarget.VIEW) {
                        finishLocker(e);
                    } else if(e.getTarget() == LockTarget.MAIN) {
                        e.setTarget(LockTarget.SIM);
                    }
                }
            }
            case SIM -> {
                for(Locker e : lockers) {
                    if(e.getTarget() == LockTarget.SIM) {
                        finishLocker(e);
                    } else if(e.getTarget() == LockTarget.MAIN) {
                        e.setTarget(LockTarget.VIEW);
                    }
                }
            }
            case MAIN -> {
                for(Locker e : lockers) {
                    if(e.getTarget() == LockTarget.SIM || e.getTarget() == LockTarget.VIEW || e.getTarget() == LockTarget.MAIN) {
                        finishLocker(e);
                    }
                }
            }
            case CHUNK -> {
                for(Locker e : lockers) {
                    if(e.getTarget() == LockTarget.CHUNK) {
                        finishLocker(e);
                    }
                }
            }
        }
        updateLocker();
    }


    private final List<Locker> toRemove = new ArrayList<>();
    public void finishLocker(Locker unlocker) {
        toRemove.add(unlocker);
    }

    public void updateLocker() {
        if(getLockedManually(LockTarget.MAIN) != null) return;

        ArrayList<Locker> viewLockers = new ArrayList<>();
        ArrayList<Locker> simLockers = new ArrayList<>();
        ArrayList<Locker> chunkTickLockers = new ArrayList<>();
        for(Locker e : lockers) {
            switch (e.getTarget()) {
                case MAIN -> {
                    viewLockers.add(e);
                    simLockers.add(e);
                }
                case VIEW -> viewLockers.add(e);
                case SIM -> simLockers.add(e);
                case CHUNK -> chunkTickLockers.add(e);
            }
        }

        if(getLockedManually(LockTarget.VIEW) == null) {
            processLock(viewLockers, l -> {
                currentViewLocker = l;
                lockView(l != null ? l.getDistance() : null);
            });
        }

        if(getLockedManually(LockTarget.SIM) == null) {
            processLock(simLockers, l -> {
                currentSimLocker = l;
                lockSim(l != null ? l.getDistance() : null);
            });
        }

        if(getLockedManually(LockTarget.CHUNK) == null) {
            processLock(chunkTickLockers, l -> {
                currentChunkTickLocker = l;
                lockChunkTick(l != null ? l.getDistance() : null);
            });
        }
    }

    private void processLock(List<Locker> lockers, Consumer<Locker> lock) {
        Locker locker = lockers.stream().min(Comparator.comparingInt(Locker::getDistance)).orElse(null);
        lock.accept(locker);
    }

    public void lockView(Integer chunks) {
        config.setViewLocked(chunks != null);
        if(chunks != null) {
            ViewDistanceHandler.setViewDistance(chunks);
        }
    }

    public void lockSim(Integer chunks) {
        config.setSimLocked(chunks != null);
        if(chunks != null) {
            ViewDistanceHandler.setSimDistance(chunks);
        }
    }

    public void lockChunkTick(Integer chunks)  {
        config.setChunkTickingLocked(chunks != null);
        if(chunks != null) {
            ViewDistanceHandler.setChunkTickingDistance(chunks);
        }
    }

    public void onTick() {
        for(Locker e : lockers) {
            e.onTick();
        }

        lockers.removeAll(toRemove);

        updateLocker();
    }

    public Config getConfig() {
        return config;
    }

    public static boolean shouldBroadcastLock(ServerPlayerEntity player, Config config) {
        NotificationState state = NotificationState.getFromPlayer(player, config.getBroadcastLock());
        if(state == NotificationState.ADDED) {
            return true;
        }
        if(state == NotificationState.REMOVED) {
            return false;
        }
        return switch(config.getBroadcastLockDefault()) {
            case ALL -> true;
            case NONE -> false;
            case OPS -> AdaptiveViewMod.getServer().getPlayerManager().isOperator(player.getGameProfile());
        };
    }
}
