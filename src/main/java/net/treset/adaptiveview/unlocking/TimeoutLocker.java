package net.treset.adaptiveview.unlocking;

import net.treset.adaptiveview.config.RuleTarget;
import net.treset.adaptiveview.tools.Message;

public class TimeoutLocker extends Locker {
    private final int timeout;
    private int remaining;

    public TimeoutLocker(int distance, int timeout, LockTarget target, LockManager lockManager) {
        super(distance, target, lockManager);
        this.timeout = timeout;
        this.remaining = timeout;
    }

    @Override
    public void beforeTick() {
        this.remaining--;
    }

    @Override
    public boolean shouldUnlock() {
        return this.remaining <= 0;
    }

    @Override
    public Message getUnlockReason() {
        return new Message("$b%s ticks", this.timeout);
    }

    @Override
    public Message getLockedReason() {
        return new Message("$b%s more ticks have passed", this.remaining);
    }
}
