package net.treset.adaptiveview.unlocking;

import net.treset.adaptiveview.tools.Message;
import net.treset.adaptiveview.tools.TextTools;

public abstract class Locker {

    private final LockManager lockManager;
    private final int distance;

    public Locker(int distance, LockManager lockManager) {
        this.lockManager = lockManager;
        this.distance = distance;
    }

    public final void onTick() {
        beforeTick();
        if(shouldUnlock()) {
            lockManager.finishLocker(this);
            TextTools.sendMessage((p) -> true, "Cleared View distance lock of $b%s chunks$b after ", getUnlockReason());
        }
    }

    public int getDistance() {
        return distance;
    }

    public void beforeTick() {}
    public abstract boolean shouldUnlock();
    public abstract Message getUnlockReason();
    public abstract Message getLockedReason();
}
