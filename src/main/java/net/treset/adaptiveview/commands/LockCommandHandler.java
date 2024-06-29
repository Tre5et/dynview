package net.treset.adaptiveview.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.treset.adaptiveview.AdaptiveViewMod;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.distance.ViewDistanceHandler;
import net.treset.adaptiveview.tools.TextTools;
import net.treset.adaptiveview.unlocking.*;

public class LockCommandHandler {
    private final Config config;
    private final LockManager lockManager;

    public LockCommandHandler(Config config, LockManager lockManager) {
        this.config = config;
        this.lockManager = lockManager;
    }

    private void replyAndBroadcastLock(CommandContext<ServerCommandSource> ctx, String message, Object... args) {
        TextTools.replyAndBroadcastIf((p) -> LockManager.shouldBroadcastLock(p, config), ctx, message, args);
    }

    private int lockStatus(CommandContext<ServerCommandSource> ctx, LockTarget target) {
        Locker currentLocker = lockManager.getCurrentLocker(target);
        int numLockers = lockManager.getNumLockers(target);
        Integer lockedManually = lockManager.getLockedManually(target);

        if(lockedManually == null && numLockers == 0) {
            TextTools.replyFormatted(ctx, "The %s is $bunlocked", target.getPrettyString());
            return 1;
        } else if(lockedManually != null) {
            StringBuilder sb = new StringBuilder(String.format("The %s is manually locked to $b%s chunks$b", target.getPrettyString(), lockedManually));
            if(numLockers > 0) {
                sb.append(String.format(" and there %s $b%s %s$b queued", (numLockers > 1)? "are" : "is", numLockers, (numLockers > 1)? "lockers" : "locker"));
            }
            TextTools.replyFormatted(ctx, sb.toString());
        } else if(currentLocker != null) {
            StringBuilder sb = new StringBuilder(String.format("The %s is locked to $b%s chunks$b until %s", target.getPrettyString(), currentLocker.getDistance(), currentLocker.getLockedReason()));
            if(numLockers > 1) {
                sb.append(String.format(" and $b%s other %s$b queued", numLockers - 1, (numLockers > 2)? "lockers are" : "locker is"));
            }
            TextTools.replyFormatted(ctx, sb.toString());
        } else {
            TextTools.replyError(ctx, "An error occurred while fetching the lock status");
            return 0;
        }
        return 1;
    }

    private int status(CommandContext<ServerCommandSource> ctx) {
        if(lockStatus(ctx, LockTarget.VIEW) == 1 && lockStatus(ctx, LockTarget.SIM) == 1) {
            return 1;
        }
        return 0;
    }

    private int lock(CommandContext<ServerCommandSource> ctx, LockTarget target) {
        int chunks = IntegerArgumentType.getInteger(ctx, "chunks");

        lockManager.lockManually(chunks, target);

        replyAndBroadcastLock(ctx, "Locked the %s to $b%s chunks", target.getPrettyString(), chunks);
        return 1;
    }

    private int lockTimeout(CommandContext<ServerCommandSource> ctx, LockTarget target) {
        int chunks = IntegerArgumentType.getInteger(ctx, "chunks");
        int ticks = IntegerArgumentType.getInteger(ctx, "ticks");

        lockManager.addLocker(new TimeoutLocker(chunks, ticks, target, lockManager));

        replyAndBroadcastLock(ctx, "Locked the %s to $b%s chunks$b for $b%s ticks", target.getPrettyString(), chunks, ticks);
        return 1;
    }

    private int lockPlayerDisconnect(CommandContext<ServerCommandSource> ctx, LockTarget target) {
        int chunks = IntegerArgumentType.getInteger(ctx, "chunks");
        ServerPlayerEntity player;
        try {
            player = EntityArgumentType.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            TextTools.replyError(ctx, "Cannot parse the provided player");
            AdaptiveViewMod.LOGGER.error("Failed to parse player", e);
            return 0;
        }

        lockManager.addLocker(new PlayerDisconnectLocker(player, chunks, target, lockManager));

        replyAndBroadcastLock(ctx, "Locked the %s to $b%s chunks$b until $b%s disconnects", target.getPrettyString(), chunks, player.getName().getString());
        return 1;
    }

    private int lockPlayerMove(CommandContext<ServerCommandSource> ctx, LockTarget target) {
        int chunks = IntegerArgumentType.getInteger(ctx, "chunks");
        ServerPlayerEntity player;
        try {
            player = EntityArgumentType.getPlayer(ctx, "player");
        } catch (CommandSyntaxException e) {
            TextTools.replyError(ctx, "Cannot parse the provided player");
            AdaptiveViewMod.LOGGER.error("Failed to parse player", e);
            return 0;
        }

        lockManager.addLocker(new PlayerMoveLocker(player, chunks, target, lockManager));

        replyAndBroadcastLock(ctx, "Locked the %s to $b%s chunks$b until $b%s moves", target.getPrettyString(), chunks, player.getName().getString());
        return 1;
    }

    private int unlock(CommandContext<ServerCommandSource> ctx, LockTarget target) {
        int numLocks = lockManager.getNumLockers(target);
        Integer lockedManually = lockManager.getLockedManually(target);

        if(lockedManually == null) {
            if(numLocks == 0) {
                TextTools.replyFormatted(ctx, "The %s %sn't locked", target.getPrettyString(), target.getIs());
                return 1;
            } else {
                TextTools.replyFormatted(ctx, "The %s %sn't locked manually but there %s %s %s active. Clear them with by appending 'clear' to this command", target.getPrettyString(), target.getIs(), (numLocks > 1)? "are" : "is", numLocks, (numLocks > 1)? "lockers": "locker");
            }
            return 1;
        }

        lockManager.lockManually(null, target);

        if(lockedManually > 0 && numLocks > 0) {
            replyAndBroadcastLock(ctx, "$bUnlocked$b the %s but there %s still $b%s %s$b active", target.getPrettyString(), (numLocks > 1)? "are" : "is", numLocks, (numLocks > 1)? "lockers": "locker");
            return 1;
        }

        replyAndBroadcastLock(ctx, "$bUnlocked$b the %s", target.getPrettyString());
        return 1;
    }

    private int unlockClear(CommandContext<ServerCommandSource> ctx, LockTarget target) {
        int numLocks = lockManager.getNumLockers(target);
        Integer lockedManually = lockManager.getLockedManually(target);

        if(numLocks == 0 && lockedManually == null) {
            TextTools.replyFormatted(ctx, "Nothing to unlock and no lockers to clear");
            return 1;
        }

        lockManager.clearLockers(target);
        lockManager.lockManually(null, target);

        if(lockedManually != null && lockedManually > 0 && numLocks > 0) {
            replyAndBroadcastLock(ctx, "$bUnlocked$b the %s and $bcleared %s %s", target.getPrettyString(), numLocks, (numLocks > 1)? "lockers" : "locker");
            return 1;
        }

        if(lockedManually != null && lockedManually > 0) {
            replyAndBroadcastLock(ctx, "$bUnlocked$b the %s", target.getPrettyString());
            return 1;
        }

        replyAndBroadcastLock(ctx, "$bCleared %s %s", numLocks, (numLocks > 1)? "lockers" : "locker");
        return 1;
    }

    public int allChunks(CommandContext<ServerCommandSource> ctx) {
        return lock(ctx, LockTarget.ALL);
    }

    public int allChunksTimeoutTicks(CommandContext<ServerCommandSource> ctx) {
        return lockTimeout(ctx, LockTarget.ALL);
    }

    public int allChunksPlayerDisconnect(CommandContext<ServerCommandSource> ctx) {
        return lockPlayerDisconnect(ctx, LockTarget.ALL);
    }

    public int allChunksPlayerMove(CommandContext<ServerCommandSource> ctx) {
        return lockPlayerMove(ctx, LockTarget.ALL);
    }

    public int viewChunks(CommandContext<ServerCommandSource> ctx) {
        return lock(ctx, LockTarget.VIEW);
    }

    public int viewChunksTimeoutTicks(CommandContext<ServerCommandSource> ctx) {
        return lockTimeout(ctx, LockTarget.VIEW);
    }

    public int viewChunksPlayerDisconnect(CommandContext<ServerCommandSource> ctx) {
        return lockPlayerDisconnect(ctx, LockTarget.VIEW);
    }

    public int viewChunksPlayerMove(CommandContext<ServerCommandSource> ctx) {
        return lockPlayerMove(ctx, LockTarget.VIEW);
    }

    public int simChunks(CommandContext<ServerCommandSource> ctx) {
        return lock(ctx, LockTarget.SIM);
    }

    public int simChunksTimeoutTicks(CommandContext<ServerCommandSource> ctx) {
        return lockTimeout(ctx, LockTarget.SIM);
    }

    public int simChunksPlayerDisconnect(CommandContext<ServerCommandSource> ctx) {
        return lockPlayerDisconnect(ctx, LockTarget.SIM);
    }

    public int simChunksPlayerMove(CommandContext<ServerCommandSource> ctx) {
        return lockPlayerMove(ctx, LockTarget.SIM);
    }

    public int unlockAll(CommandContext<ServerCommandSource> ctx) {
        return unlock(ctx, LockTarget.ALL);
    }

    public int unlockAllClear(CommandContext<ServerCommandSource> ctx) {
        return unlockClear(ctx, LockTarget.ALL);
    }

    public int unlockView(CommandContext<ServerCommandSource> ctx) {
        return unlock(ctx, LockTarget.VIEW);
    }

    public int unlockViewClear(CommandContext<ServerCommandSource> ctx) {
        return unlockClear(ctx, LockTarget.VIEW);
    }

    public int unlockSim(CommandContext<ServerCommandSource> ctx) {
        return unlock(ctx, LockTarget.SIM);
    }

    public int unlockSimClear(CommandContext<ServerCommandSource> ctx) {
        return unlockClear(ctx, LockTarget.SIM);
    }

    public LiteralArgumentBuilder<ServerCommandSource> getLockCommands() {
        return CommandManager.literal("lock")
                .executes(this::status)
                .then(CommandManager.literal("status")
                        .executes(this::status)
                )
                .then(CommandManager.literal("all")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
                                .executes(this::allChunks)
                                .then(CommandManager.literal("timeout")
                                        .then(CommandManager.argument("ticks", IntegerArgumentType.integer(1))
                                                .executes(this::allChunksTimeoutTicks)
                                        )
                                )
                                .then(CommandManager.literal("player")
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .then(CommandManager.literal("disconnect")
                                                        .executes(this::allChunksPlayerDisconnect)
                                                )
                                                .then(CommandManager.literal("move")
                                                        .executes(this::allChunksPlayerMove)
                                                )
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("view")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
                                .executes(this::viewChunks)
                                .then(CommandManager.literal("timeout")
                                        .then(CommandManager.argument("ticks", IntegerArgumentType.integer(1))
                                                .executes(this::viewChunksTimeoutTicks)
                                        )
                                )
                                .then(CommandManager.literal("player")
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .then(CommandManager.literal("disconnect")
                                                        .executes(this::viewChunksPlayerDisconnect)
                                                )
                                                .then(CommandManager.literal("move")
                                                        .executes(this::viewChunksPlayerMove)
                                                )
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("simulation")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
                                .executes(this::simChunks)
                                .then(CommandManager.literal("timeout")
                                        .then(CommandManager.argument("ticks", IntegerArgumentType.integer(1))
                                                .executes(this::simChunksTimeoutTicks)
                                        )
                                )
                                .then(CommandManager.literal("player")
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .then(CommandManager.literal("disconnect")
                                                        .executes(this::simChunksPlayerDisconnect)
                                                )
                                                .then(CommandManager.literal("move")
                                                        .executes(this::simChunksPlayerMove)
                                                )
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("unlock")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(this::unlockAll)
                        .then(CommandManager.literal("all")
                                .executes(this::unlockAll)
                                .then(CommandManager.literal("clear")
                                        .executes(this::unlockAllClear)
                                )
                        )
                        .then(CommandManager.literal("view")
                                .executes(this::unlockView)
                                .then(CommandManager.literal("clear")
                                        .executes(this::unlockViewClear)
                                )
                        )
                        .then(CommandManager.literal("simulation")
                                .executes(this::unlockSim)
                                .then(CommandManager.literal("clear")
                                        .executes(this::unlockSimClear)
                                )
                        )
                );
    }
}
