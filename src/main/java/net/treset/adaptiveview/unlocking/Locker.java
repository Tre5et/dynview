package net.treset.adaptiveview.unlocking;

import net.treset.adaptiveview.tools.Message;
import net.treset.adaptiveview.tools.TextTools;

public abstract class Locker {

    private final LockManager lockManager;
    private LockTarget target;
    private final int distance;

    public Locker(int distance, LockTarget target, LockManager lockManager) {
        this.lockManager = lockManager;
        this.target = target;
        this.distance = distance;
    }

    public final void onTick() {
        beforeTick();
        if(shouldUnlock()) {
            lockManager.finishLocker(this);
            TextTools.broadcastIf((p) -> LockManager.shouldBroadcastLock(p, lockManager.getConfig()), "Cleared View distance lock of $b%s chunks$b after %s", distance, getUnlockReason());
        }
    }

    public int getDistance() {
        return distance;
    }

    public LockTarget getTarget() {
        return target;
    }

    public void setTarget(LockTarget target) {
        this.target = target;
    }

    public void beforeTick() {}
    public abstract boolean shouldUnlock();
    public abstract Message getUnlockReason();
    public abstract Message getLockedReason();
}
