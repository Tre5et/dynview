package net.treset.adaptiveview.unlocking;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.treset.adaptiveview.tools.TextTools;

public class ViewDistanceLocker {
    private final LockReason lockReason;
    private final int distance;
    private final int timeout;
    private int remainingTime;
    private final LockManager lockManager;
    private final ServerPlayerEntity player;
    private Vec3d startPos;
    private final CommandContext<ServerCommandSource> ctx;

    public ViewDistanceLocker(LockReason lockReason, int distance, int timeout, LockManager lockManager, ServerPlayerEntity player, CommandContext<ServerCommandSource> ctx) {
        this.lockReason = lockReason;
        this.distance = distance;
        this.timeout = this.remainingTime = timeout;
        this.lockManager = lockManager;
        this.player = player;
        this.ctx = ctx;

        if(this.getUnlockReason() == LockReason.PLAYER_MOVE) {
            this.startPos = player.getPos();
        }
    }

    public LockReason getUnlockReason() { return lockReason; }
    public int getDistance() { return distance; }
    public int getTimeout() { return timeout; }

    public void onTick() {
        if(this.getUnlockReason() == LockReason.TIMEOUT) {
            this.remainingTime--;
            if(this.remainingTime <= 0) {
                lockManager.finishUnlocker(this);
                TextTools.replyFormatted(ctx, String.format("Cleared View Distance lock of ?b%s chunks?b after ?b%s ticks", this.getDistance(), this.getTimeout()), true);
            }
        } else if(this.getUnlockReason() == LockReason.PLAYER_DISCONNECT) {
             if(this.player.isDisconnected()) {
                 lockManager.finishUnlocker(this);
                 TextTools.replyFormatted(ctx, String.format("Cleared View Distance lock of $b%s chunks$b after $b%s disconnected", this.getDistance(), this.player.getName().getString()), true);
             }
        } else if(this.getUnlockReason() == LockReason.PLAYER_MOVE) {
            if(this.player.isDisconnected() || this.player.getPos() != this.startPos) {
                lockManager.finishUnlocker(this);
                TextTools.replyFormatted(ctx, String.format("Cleared View Distance lock of $b%s chunks$b after $b%s moved", this.getDistance(), this.player.getName().getString()), true);
            }
        }
    }

    public String getReasonString() {
        if(this.getUnlockReason() == LockReason.TIMEOUT) {
            return String.format("until $b%s ticks$b have passed", this.timeout);
        } else if(this.getUnlockReason() == LockReason.PLAYER_DISCONNECT) {
            return String.format("until $b%s disconnects$b", this.player.getName().getString());
        } else if(this.getUnlockReason() == LockReason.PLAYER_MOVE) {
            return String.format("until $b%s moves$b", this.player.getName().getString());
        }
        return "";
    }
}
