package net.treset.adaptiveview.unlocking;

import net.minecraft.server.network.ServerPlayerEntity;
import net.treset.adaptiveview.AdaptiveViewMod;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.distance.ViewDistanceHandler;
import net.treset.adaptiveview.tools.NotificationState;

import java.util.ArrayList;
import java.util.List;

public class LockManager {
    private final Config config;
    private final ViewDistanceHandler viewDistanceHandler;
    private final List<Locker> lockers = new ArrayList<>();
    private int viewLockedManually = 0;
    private int simLockedManually = 0;

    private Locker currentViewLocker = null;
    private Locker currentSimLocker = null;

    public LockManager(Config config, ViewDistanceHandler viewDistanceHandler) {
        this.config = config;
        this.viewDistanceHandler = viewDistanceHandler;
    }

    public void lockManually(Integer chunks, LockTarget target) {
        if(target == LockTarget.VIEW || target == LockTarget.ALL) {
            if(chunks == null || chunks == Integer.MAX_VALUE) {
                viewLockedManually = 0;
                lockView(Integer.MAX_VALUE);
            } else {
                viewLockedManually = chunks;
                lockView(chunks);
            }
        }
        if(target == LockTarget.SIM || target == LockTarget.ALL) {
            if(chunks == null || chunks == Integer.MAX_VALUE) {
                simLockedManually = 0;
                lockSim(Integer.MAX_VALUE);
            } else {
                simLockedManually = chunks;
                lockSim(chunks);
            }
        }
        updateLocker();
    }

    public Integer getLockedManually(LockTarget target) {
        return switch (target) {
            case VIEW -> viewLockedManually != 0 ? viewLockedManually : null;
            case SIM -> simLockedManually != 0 ? simLockedManually : null;
            case ALL -> {
                if(viewLockedManually != 0 && simLockedManually != 0) {
                    yield Math.min(viewLockedManually, simLockedManually);
                } else if(viewLockedManually != 0) {
                    yield viewLockedManually;
                } else if(simLockedManually != 0) {
                    yield simLockedManually;
                } else {
                    yield null;
                }
            }
        };
    }

    public Locker getCurrentLocker(LockTarget target) {
        return switch (target) {
            case VIEW -> currentViewLocker;
            case SIM -> currentSimLocker;
            case ALL -> {
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
        };
    }

    public int getNumLockers(LockTarget target) {
        return switch (target) {
            case VIEW -> (int) lockers.stream().filter(e -> e.getTarget() == LockTarget.VIEW || e.getTarget() == LockTarget.ALL).count();
            case SIM -> (int) lockers.stream().filter(e -> e.getTarget() == LockTarget.SIM || e.getTarget() == LockTarget.ALL).count();
            case ALL -> lockers.size();
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
                    } else if(e.getTarget() == LockTarget.ALL) {
                        e.setTarget(LockTarget.SIM);
                    }
                }
            }
            case SIM -> {
                for(Locker e : lockers) {
                    if(e.getTarget() == LockTarget.SIM) {
                        finishLocker(e);
                    } else if(e.getTarget() == LockTarget.ALL) {
                        e.setTarget(LockTarget.VIEW);
                    }
                }
            }
            case ALL -> {
                for(Locker e : lockers) {
                    finishLocker(e);
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
        if(getLockedManually(LockTarget.ALL) != null) return;

        ArrayList<Locker> viewLockers = new ArrayList<>();
        ArrayList<Locker> simLockers = new ArrayList<>();
        for(Locker e : lockers) {
            switch (e.getTarget()) {
                case ALL -> {
                    viewLockers.add(e);
                    simLockers.add(e);
                }
                case VIEW -> viewLockers.add(e);
                case SIM -> simLockers.add(e);
            }
        }

        if(getLockedManually(LockTarget.VIEW) == null) {
            int viewDistance = Integer.MAX_VALUE;
            Locker viewLocker = null;

            for(Locker e : viewLockers) {
                if(e.getDistance() < viewDistance) {
                    viewDistance = e.getDistance();
                    viewLocker = e;
                }
            }

            currentViewLocker = viewLocker;
            lockView(viewDistance);
        }

        if(getLockedManually(LockTarget.SIM) == null) {
            int simDistance = Integer.MAX_VALUE;
            Locker simLocker = null;

            for(Locker e : simLockers) {
                if(e.getDistance() < simDistance) {
                    simDistance = e.getDistance();
                    simLocker = e;
                }
            }

            currentSimLocker = simLocker;
            lockSim(simDistance);
        }
    }

    public void lockView(int chunks) {
        if(chunks == Integer.MAX_VALUE) {
            currentViewLocker = null;
            config.setViewLocked(false);
        } else if(ViewDistanceHandler.getViewDistance() != chunks) {
            viewDistanceHandler.setViewDistance(chunks);
            config.setViewLocked(true);
        }
    }

    public void lockSim(int chunks) {
        if(chunks == Integer.MAX_VALUE) {
            currentSimLocker = null;
            config.setViewLocked(false);
        } else if(ViewDistanceHandler.getSimDistance() != chunks) {
            viewDistanceHandler.setSimDistance(chunks);
            config.setSimLocked(true);
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
