package net.treset.adaptiveview.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.treset.adaptiveview.AdaptiveViewMod;
import net.treset.adaptiveview.config.OldConfig;
import net.treset.adaptiveview.tools.TextTools;
import net.treset.adaptiveview.unlocking.LockManager;
import net.treset.adaptiveview.unlocking.LockReason;
import net.treset.adaptiveview.unlocking.ViewDistanceLocker;

public class LockCommandHandler {
    private final OldConfig config;
    private final LockManager lockManager;

    public LockCommandHandler(OldConfig config, LockManager lockManager) {
        this.config = config;
        this.lockManager = lockManager;
    }

    public int base(CommandContext<ServerCommandSource> ctx) {
        ViewDistanceLocker currentLocker = lockManager.getCurrentLocker();
        int numLockers = lockManager.getNumUnlockers();
        int lockedManually = lockManager.isLockedManually();

        if(config.getLocked() == 0) {
            TextTools.replyFormatted(ctx, "?iThe view distance is ?Bunlocked", false);
            return 1;
        }

        if(lockedManually > 0) {
            if(numLockers > 0) {
                TextTools.replyFormatted(ctx, String.format("?iThe view distance is manually locked to ?B%s chunks?B and there %s ?B%s %s?B queued", lockedManually, (numLockers > 1)? "are" : "is", numLockers, (numLockers > 1)? "lockers" : "locker"), false);
            } else TextTools.replyFormatted(ctx, String.format("?iThe view distance is manually locked to ?B%s chunks", lockedManually), false);
            return 1;
        }

        if(currentLocker != null) {
            if(numLockers > 1) {
                TextTools.replyFormatted(ctx, String.format("?iThe view distance is locked to ?B%s chunks?B %s and ?B%s other %s?B active", currentLocker.getDistance(), currentLocker.getReasonString(), numLockers - 1, (numLockers > 2)? "lockers are" : "locker is"), false);
            } else  TextTools.replyFormatted(ctx, String.format("?iThe view distance is locked to ?B%s chunks?B %s", currentLocker.getDistance(), currentLocker.getReasonString()), false);
            return 1;
        }

        TextTools.replyFormatted(ctx, String.format("?iThe view distance is currently locked to ?B%s chunks", config.getLocked()), false);
        return 1;
    }

    public int set(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "?i?ILocks the view distance to the provided chunks", false);
        return 1;
    }

    public int setChunks(CommandContext<ServerCommandSource> ctx) {
        int chunks = IntegerArgumentType.getInteger(ctx, "chunks");

        lockManager.lockManually(chunks);

        TextTools.replyFormatted(ctx, String.format("?gLocked the view distance to ?B%s chunks", chunks), true);
        return 1;
    }

    public int setChunksTimeout(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "?i?IThe view distance will be unlocked after the provided amount of ticks", false);
        return 1;
    }

    public int setChunksTimeoutTicks(CommandContext<ServerCommandSource> ctx) {
        int chunks = IntegerArgumentType.getInteger(ctx, "chunks");
        int ticks = IntegerArgumentType.getInteger(ctx, "ticks");

        lockManager.addUnlocker(new ViewDistanceLocker(LockReason.TIMEOUT, chunks, ticks, lockManager, null, ctx));

        TextTools.replyFormatted(ctx, String.format("?gLocked the view distance to ?B%s chunks?B for ?B%s ticks", chunks, ticks), true);
        return 1;
    }

    public int setChunksPlayer(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "?i?IThe view distance will be unlocked after the provided player disconnects or moves", false);
        return 1;
    }

    public int setChunksPlayerDisconnect(CommandContext<ServerCommandSource> ctx) {
        int chunks = IntegerArgumentType.getInteger(ctx, "chunks");
        ServerPlayerEntity player;
        try {
            player = EntityArgumentType.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            TextTools.replyError(ctx, "Cannot parse the provided player");
            AdaptiveViewMod.LOGGER.error("Failed to parse player", e);
            return 0;
        }

        lockManager.addUnlocker(new ViewDistanceLocker(LockReason.PLAYER_DISCONNECT, chunks, -1, lockManager, player, ctx));

        TextTools.replyFormatted(ctx, String.format("?gLocked the view distance to ?B%s chunks?B until ?Bplayer %s disconnects", chunks, player.getName().getString()), true);
        return 1;
    }

    public int setChunksPlayerMove(CommandContext<ServerCommandSource> ctx) {
        int chunks = IntegerArgumentType.getInteger(ctx, "chunks");
        ServerPlayerEntity player;
        try {
            player = EntityArgumentType.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            TextTools.replyError(ctx, "Cannot parse the provided player");
            AdaptiveViewMod.LOGGER.error("Failed to parse player", e);
            return 0;
        }

        lockManager.addUnlocker(new ViewDistanceLocker(LockReason.PLAYER_MOVE, chunks, -1, lockManager, player, ctx));

        TextTools.replyFormatted(ctx, String.format("?gLocked the view distance to ?B%s chunks?B until ?Bplayer %s moves", chunks, player.getName().getString()), true);
        return 1;
    }

    public int unlock(CommandContext<ServerCommandSource> ctx) {
        int numLocks = lockManager.getNumUnlockers();
        int lockedManually = lockManager.isLockedManually();

        if(lockedManually == 0) {
            TextTools.replyFormatted(ctx, "?pThe view distance isn't manually locked", true);
            return 1;
        }

        lockManager.unlockManually();

        if(lockedManually > 0 && numLocks > 0) {
            TextTools.replyFormatted(ctx, String.format("?g?BUnlocked?B the view distance but there %s still ?B%s %s?B active", (numLocks > 1)? "are" : "is", numLocks, (numLocks > 1)? "lockers": "locker"), true);
            return 1;
        }

        TextTools.replyFormatted(ctx, "?g?BUnlocked?B the view distance", true);
        return 1;
    }

    public int clear(CommandContext<ServerCommandSource> ctx) {
        int numLocks = lockManager.getNumUnlockers();
        int lockedManually = lockManager.isLockedManually();

        if(numLocks == 0 && lockedManually == 0) {
            TextTools.replyFormatted(ctx, "?pNothing to unlock and no lockers to clear", true);
            return 1;
        }

        lockManager.clear();
        lockManager.unlockManually();

        if(lockedManually > 0 && numLocks > 0) {
            TextTools.replyFormatted(ctx, String.format("?g?BUnlocked?B the view distance and ?Bcleared %s %s", numLocks, (numLocks > 1)? "lockers" : "locker"), true);
            return 1;
        }

        if(lockedManually > 0) {
            TextTools.replyFormatted(ctx, "?g?BUnlocked?B the view distance", true);
            return 1;
        }

        TextTools.replyFormatted(ctx, String.format("?g?BCleared %s %s", numLocks, (numLocks > 1)? "lockers" : "locker"), true);
        return 1;
    }
}
