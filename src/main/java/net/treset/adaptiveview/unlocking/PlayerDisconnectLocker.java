package net.treset.adaptiveview.unlocking;

import net.minecraft.server.network.ServerPlayerEntity;
import net.treset.adaptiveview.tools.Message;

public class PlayerDisconnectLocker extends Locker {
    private final ServerPlayerEntity player;

    public PlayerDisconnectLocker(ServerPlayerEntity player, int distance, LockTarget target, LockManager lockManager) {
        super(distance, target, lockManager);
        this.player = player;
    }

    @Override
    public boolean shouldUnlock() {
        return this.player.isDisconnected();
    }

    @Override
    public Message getUnlockReason() {
        return new Message("$b%s disconnected", this.player.getName().getString());
    }

    @Override
    public Message getLockedReason() {
        return new Message("$b%s disconnects", this.player.getName().getString());
    }
}
