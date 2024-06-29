package net.treset.adaptiveview.unlocking;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.treset.adaptiveview.tools.Message;

public class PlayerMoveLocker extends Locker {
    private final ServerPlayerEntity player;
    private final Vec3d startPos;

    public PlayerMoveLocker(ServerPlayerEntity player, int distance, LockTarget target, LockManager lockManager) {
        super(distance, target, lockManager);
        this.player = player;
        this.startPos = player.getPos();
    }

    @Override
    public boolean shouldUnlock() {
        return this.player.isDisconnected() || !this.player.getPos().equals(this.startPos);
    }

    @Override
    public Message getUnlockReason() {
        return new Message("$b%s moved", this.player.getName().getString());
    }

    @Override
    public Message getLockedReason() {
        return new Message("$b%s moves", this.player.getName().getString());
    }
}
