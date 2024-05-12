package net.treset.adaptiveview.unlocking;

import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.distance.ViewDistanceHandler;

import java.util.ArrayList;
import java.util.List;

public class LockManager {
    private final Config config;
    private final ViewDistanceHandler viewDistanceHandler;
    private final List<ViewDistanceLocker> unlockers = new ArrayList<>();
    private int lockedManually = 0;

    private ViewDistanceLocker currentLocker = null;

    public LockManager(Config config, ViewDistanceHandler viewDistanceHandler) {
        this.config = config;
        this.viewDistanceHandler = viewDistanceHandler;
    }

    public ViewDistanceLocker getCurrentLocker() {
        return currentLocker;
    }

    public int isLockedManually() { return lockedManually; }
    public void lockManually(int chunks) {
        lockedManually = chunks;
        lock(chunks);
    }

    public int getNumUnlockers() { return unlockers.size(); }

    public void addUnlocker(ViewDistanceLocker unlocker) {
        unlockers.add(unlocker);
        updateUnlocker();
    }

    public void clearUnlockers() {
        unlockers.clear();
    }


    private final List<ViewDistanceLocker> toRemove = new ArrayList<>();
    public void finishUnlocker(ViewDistanceLocker unlocker) {
        toRemove.add(unlocker);
    }

    public void updateUnlocker() {
        if(isLockedManually() != 0) return;

        if(unlockers.isEmpty()) {
            clear();
            return;
        }

        int smallestViewDistance = unlockers.get(0).getDistance();
        ViewDistanceLocker newLocker = unlockers.get(0);
        for(ViewDistanceLocker e : unlockers) {
            if(e.getDistance() < smallestViewDistance) {
                smallestViewDistance = e.getDistance();
                newLocker = e;
            }
        }

        currentLocker = newLocker;
        lock(smallestViewDistance);
    }

    public void lock(int chunks) {
        viewDistanceHandler.setViewDistance(chunks);
        config.setLocked(true);
    }

    public void clear() {
        clearUnlockers();

        if(lockedManually > 0) {
            lock(lockedManually);
            currentLocker = null;
        } else unlock();
    }

    public void unlockManually() {
        lockedManually = 0;
        updateUnlocker();
    }

    public void unlock() {
        currentLocker = null;

        config.setLocked(false);
    }

    public void onTick() {
        for(ViewDistanceLocker e : unlockers) {
            e.onTick();
        }

        unlockers.removeAll(toRemove);

        updateUnlocker();
    }
}
