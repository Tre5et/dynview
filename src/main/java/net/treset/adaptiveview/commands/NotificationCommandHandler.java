package net.treset.adaptiveview.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.distance.ViewDistanceHandler;
import net.treset.adaptiveview.tools.NotificationState;
import net.treset.adaptiveview.tools.TextTools;
import net.treset.adaptiveview.unlocking.LockManager;

public class NotificationCommandHandler {
    private final Config config;

    public NotificationCommandHandler(Config config) {
        this.config = config;
    }

    public int notifications(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) {
            TextTools.replyError(ctx, "Error getting player from command context!");
            return 0;
        }
        String status = "You are ";
        NotificationState changeState = NotificationState.getFromPlayer(player, config.getBroadcastChanges());
        NotificationState lockState = NotificationState.getFromPlayer(player, config.getBroadcastLock());

        switch (changeState) {
            case NONE -> {
                if (ViewDistanceHandler.shouldBroadcastChange(player, config)) {
                    status += "receiving view distance change notifications by default";
                } else {
                    status += "not receiving view distance change notifications";
                }
            }
            case ADDED -> status += "subscribed to view distance change notifications";
            case REMOVED -> status += "unsubscribed from view distance change notifications";
        }

        status += " and ";

        switch (lockState) {
            case NONE -> {
                if (LockManager.shouldBroadcastLock(player, config)) {
                    status += "receiving lock notifications by default";
                } else {
                    status += "not receiving lock notifications";
                }
            }
            case ADDED -> status += "subscribed to lock notifications";
            case REMOVED -> status += "unsubscribed from lock notifications";
        }
        status += ".";
        TextTools.replyFormatted(ctx, status);
        return 1;
    }

    public int notificationsChanges(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) {
            TextTools.replyError(ctx, "Error getting player from command context!");
            return 0;
        }
        String status = "";
        NotificationState state = NotificationState.getFromPlayer(player, config.getBroadcastChanges());
        switch(state) {
            case NONE -> {
                if(ViewDistanceHandler.shouldBroadcastChange(player, config)) {
                    status = "You are receiving view distance change notifications by default.";
                } else {
                    status = "You are not receiving view distance change notifications.";
                }
            }
            case ADDED -> status = "You are subscribed to view distance change notifications.";
            case REMOVED -> status = "You are unsubscribed from view distance change notifications.";
        }

        TextTools.replyFormatted(ctx, status);
        return 1;
    }

    public int notificationsChangesSubscribe(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) {
            TextTools.replyError(ctx, "Error getting player from command context!");
            return 0;
        }

        NotificationState state = NotificationState.getFromPlayer(player, config.getBroadcastChanges());
        switch (state) {
            case NONE -> {
                config.getBroadcastChanges().add(player.getName().getString().toLowerCase());
                TextTools.replyFormatted(ctx, "Subscribed to view distance change notifications.");
            }
            case ADDED -> TextTools.replyFormatted(ctx, "You are already subscribed to view distance change notifications.");
            case REMOVED -> {
                config.getBroadcastChanges().removeIf(s -> s.startsWith("!") && s.substring(1).equalsIgnoreCase(player.getName().getString()));
                config.getBroadcastChanges().add(player.getName().getString().toLowerCase());
                TextTools.replyFormatted(ctx, "Resubscribed to view distance change notifications.");
            }
        }

        config.save();
        return 1;
    }

    public int notificationsChangesUnsubscribe(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) {
            TextTools.replyError(ctx, "Error getting player from command context!");
            return 0;
        }

        NotificationState state = NotificationState.getFromPlayer(player, config.getBroadcastChanges());
        switch (state) {
            case NONE -> {
                config.getBroadcastChanges().add("!" + player.getName().getString().toLowerCase());
                TextTools.replyFormatted(ctx, "Unsubscribed from view distance change notifications.");
            }
            case ADDED -> {
                config.getBroadcastChanges().remove(player.getName().getString().toLowerCase());
                config.getBroadcastChanges().add("!" + player.getName().getString().toLowerCase());
                TextTools.replyFormatted(ctx, "Unsubscribed from view distance change notifications.");
            }
            case REMOVED -> TextTools.replyFormatted(ctx, "You are already unsubscribed from view distance change notifications.");
        }

        config.save();
        return 1;
    }

    public int notificationsLock(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) {
            TextTools.replyError(ctx, "Error getting player from command context!");
            return 0;
        }
        String status = "";
        NotificationState state = NotificationState.getFromPlayer(player, config.getBroadcastLock());
        switch(state) {
            case NONE -> {
                if (LockManager.shouldBroadcastLock(player, config)) {
                    status = "You are receiving lock notifications by default.";
                } else {
                    status = "You are not receiving lock notifications.";
                }
            }
            case ADDED -> status = "You are subscribed to lock notifications.";
            case REMOVED -> status = "You are unsubscribed from lock notifications.";
        }

        TextTools.replyFormatted(ctx, status);
        return 1;
    }

    public int notificationsLockSubscribe(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) {
            TextTools.replyError(ctx, "Error getting player from command context!");
            return 0;
        }

        NotificationState state = NotificationState.getFromPlayer(player, config.getBroadcastLock());
        switch (state) {
            case NONE -> {
                config.getBroadcastLock().add(player.getName().getString().toLowerCase());
                TextTools.replyFormatted(ctx, "Subscribed to lock notifications.");
            }
            case ADDED -> TextTools.replyFormatted(ctx, "You are already subscribed to lock notifications.");
            case REMOVED -> {
                config.getBroadcastLock().removeIf(s -> s.startsWith("!") && s.substring(1).equalsIgnoreCase(player.getName().getString()));
                config.getBroadcastLock().add(player.getName().getString().toLowerCase());
                TextTools.replyFormatted(ctx, "Resubscribed to lock notifications.");
            }
        }

        config.save();
        return 1;
    }

    public int notificationsLockUnsubscribe(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if(player == null) {
            TextTools.replyError(ctx, "Error getting player from command context!");
            return 0;
        }

        NotificationState state = NotificationState.getFromPlayer(player, config.getBroadcastLock());
        switch (state) {
            case NONE -> {
                config.getBroadcastLock().add("!" + player.getName().getString().toLowerCase());
                TextTools.replyFormatted(ctx, "Unsubscribed from lock notifications.");
            }
            case ADDED -> {
                config.getBroadcastLock().remove(player.getName().getString().toLowerCase());
                config.getBroadcastLock().add("!" + player.getName().getString().toLowerCase());
                TextTools.replyFormatted(ctx, "Unsubscribed from lock notifications.");
            }
            case REMOVED -> TextTools.replyFormatted(ctx, "You are already unsubscribed from lock notifications.");
        }

        config.save();
        return 1;
    }

    public void registerCommands(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.then(CommandManager.literal("notifications")
                .executes(this::notifications)
                .then(CommandManager.literal("changes")
                        .executes(this::notificationsChanges)
                        .then(CommandManager.literal("subscribe")
                                .executes(this::notificationsChangesSubscribe)
                        )
                        .then(CommandManager.literal("unsubscribe")
                                .executes(this::notificationsChangesUnsubscribe)
                        )
                )
                .then(CommandManager.literal("lock")
                        .executes(this::notificationsLock)
                        .then(CommandManager.literal("subscribe")
                                .executes(this::notificationsLockSubscribe)
                        )
                        .then(CommandManager.literal("unsubscribe")
                                .executes(this::notificationsLockUnsubscribe)
                        )
                )
        );
    }
}
