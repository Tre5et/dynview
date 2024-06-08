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
    private int lockedManually = 0;

    private Locker currentLocker = null;

    public LockManager(Config config, ViewDistanceHandler viewDistanceHandler) {
        this.config = config;
        this.viewDistanceHandler = viewDistanceHandler;
    }

    public Locker getCurrentLocker() {
        return currentLocker;
    }

    public int isLockedManually() { return lockedManually; }
    public void lockManually(int chunks) {
        lockedManually = chunks;
        lock(chunks);
    }

    public int getNumLockers() { return lockers.size(); }

    public void addLocker(Locker unlocker) {
        lockers.add(unlocker);
        updateLocker();
    }

    public void clearLockers() {
        lockers.clear();
    }


    private final List<Locker> toRemove = new ArrayList<>();
    public void finishLocker(Locker unlocker) {
        toRemove.add(unlocker);
    }

    public void updateLocker() {
        if(isLockedManually() != 0) return;

        if(lockers.isEmpty()) {
            clear();
            return;
        }

        int smallestViewDistance = lockers.get(0).getDistance();
        Locker newLocker = lockers.get(0);
        for(Locker e : lockers) {
            if(e.getDistance() < smallestViewDistance) {
                smallestViewDistance = e.getDistance();
                newLocker = e;
            }
        }

        currentLocker = newLocker;
        lock(smallestViewDistance);
    }

    public void lock(int chunks) {
        if(ViewDistanceHandler.getViewDistance() != chunks) {
            viewDistanceHandler.setViewDistance(chunks);
        }
        config.setLocked(true);
    }

    public void clear() {
        clearLockers();

        if(lockedManually > 0) {
            lock(lockedManually);
            currentLocker = null;
        } else unlock();
    }

    public void unlockManually() {
        lockedManually = 0;
        updateLocker();
    }

    public void unlock() {
        currentLocker = null;

        config.setLocked(false);
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
