package net.treset.adaptiveview.unlocking;

import net.treset.adaptiveview.tools.Message;

public class TimeoutLocker extends Locker {
    private final int timeout;
    private final long startTime;

    public TimeoutLocker(int distance, int timeout, LockManager lockManager) {
        super(distance, lockManager);
        this.timeout = timeout;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean shouldUnlock() {
        return System.currentTimeMillis() - startTime >= timeout;
    }

    @Override
    public Message getUnlockReason() {
        return new Message("$b%s ticks", this.timeout);
    }

    @Override
    public Message getLockedReason() {
        return new Message("$b%s ticks have passed", this.timeout);
    }
}
