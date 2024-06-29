package net.treset.adaptiveview.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.config.Rule;
import net.treset.adaptiveview.config.RuleTarget;
import net.treset.adaptiveview.config.RuleType;
import net.treset.adaptiveview.distance.ViewDistanceHandler;
import net.treset.adaptiveview.tools.BroadcastLevel;
import net.treset.adaptiveview.tools.NotificationState;
import net.treset.adaptiveview.tools.TextTools;
import net.treset.adaptiveview.unlocking.LockManager;

import java.io.IOException;
import java.util.function.BiConsumer;

public class ConfigCommandHandler {
    private final Config config;

    public ConfigCommandHandler(Config config) {
        this.config = config;
    }

    public int list(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Current Configuration:");
        TextTools.replyFormatted(ctx, "Update rate: $b%d ticks", config.getUpdateRate());
        TextTools.replyFormatted(ctx, "View Distance Range: $b%d-%d chunks", config.getMinViewDistance(), config.getMaxViewDistance());
        TextTools.replyFormatted(ctx, "Rules: $b%s$b", config.getRules().size());
        return 1;
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

    public int reload(CommandContext<ServerCommandSource> ctx) {
        Config config;
        try {
            config = Config.load();
        } catch (IOException e) {
            TextTools.replyError(ctx, "Failed to reload Config! Check for syntax errors.");
            return 0;
        }

        this.config.copy(config);
        TextTools.replyFormatted(ctx, "Reloaded Configuration!", false);
        return 1;
    }

    public int updateRate(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Update Rate: $b%s ticks", config.getUpdateRate());
        return 1;
    }

    public int setUpdateRate(CommandContext<ServerCommandSource> ctx) {
        Integer ticks = ctx.getArgument("ticks", Integer.class);
        config.setUpdateRate(ticks);
        config.save();
        TextTools.replyFormatted(ctx, "Set Update Rate to $b%s ticks", config.getUpdateRate());
        return 1;
    }

    public int maxView(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Max View Distance: $b%d chunks", config.getMaxViewDistance());
        return 1;
    }

    public int setMaxView(CommandContext<ServerCommandSource> ctx) {
        Integer chunks = ctx.getArgument("chunks", Integer.class);
        config.setMaxViewDistance(chunks);
        config.save();
        TextTools.replyFormatted(ctx, "Set Max View Distance to $b%d chunks", config.getMaxViewDistance());
        return 1;
    }

    public int minView(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Min View Distance: $b%s chunks", config.getMinViewDistance());
        return 1;
    }

    public int setMinView(CommandContext<ServerCommandSource> ctx) {
        Integer chunks = ctx.getArgument("chunks", Integer.class);
        config.setMinViewDistance(chunks);
        config.save();
        TextTools.replyFormatted(ctx, "Set Min View Distance to $b%s chunks", config.getMinViewDistance());
        return 1;
    }

    public int broadcastChanges(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Broadcasting view distance changes to $b%s", switch(config.getBroadcastChangesDefault()) {
            case ALL -> "all players";
            case OPS -> "operators";
            case NONE -> "no one";
        });
        return 1;
    }

    public int broadcastChangesNone(CommandContext<ServerCommandSource> ctx) {
        config.setBroadcastChangesDefault(BroadcastLevel.NONE);
        config.save();
        TextTools.replyFormatted(ctx, "Set broadcast changes to $bno one");
        return 1;
    }

    public int broadcastChangesOps(CommandContext<ServerCommandSource> ctx) {
        config.setBroadcastChangesDefault(BroadcastLevel.OPS);
        config.save();
        TextTools.replyFormatted(ctx, "Set broadcast changes to $boperators");
        return 1;
    }

    public int broadcastChangesAll(CommandContext<ServerCommandSource> ctx) {
        config.setBroadcastChangesDefault(BroadcastLevel.ALL);
        config.save();
        TextTools.replyFormatted(ctx, "Set broadcast changes to $ball players");
        return 1;
    }

    public int broadcastLock(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Broadcasting view distance locking and unlocking to $b%s", switch(config.getBroadcastLockDefault()) {
            case ALL -> "all players";
            case OPS -> "operators";
            case NONE -> "no one";
        });
        return 1;
    }

    public int broadcastLockNone(CommandContext<ServerCommandSource> ctx) {
        config.setBroadcastLockDefault(BroadcastLevel.NONE);
        config.save();
        TextTools.replyFormatted(ctx, "Set broadcast lock to $bno one");
        return 1;
    }

    public int broadcastLockOps(CommandContext<ServerCommandSource> ctx) {
        config.setBroadcastLockDefault(BroadcastLevel.OPS);
        config.save();
        TextTools.replyFormatted(ctx, "Set broadcast lock to $boperators");
        return 1;
    }

    public int broadcastLockAll(CommandContext<ServerCommandSource> ctx) {
        config.setBroadcastLockDefault(BroadcastLevel.ALL);
        config.save();
        TextTools.replyFormatted(ctx, "Set broadcast lock to $ball players");
        return 1;
    }

    public int rules(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Current Rules:");
        for(int i = 0; i < config.getRules().size(); i++) {
            TextTools.replyFormatted(ctx, "%d. %s", i + 1, config.getRules().get(i));
        }
        return 1;
    }

    private int performRuleAction(CommandContext<ServerCommandSource> ctx, BiConsumer<Integer, Rule> action) {
        Integer index = ctx.getArgument("index", Integer.class);
        if(index == null || index <= 0 || index > config.getRules().size()) {
            TextTools.replyError(ctx, "Rule at index $b" + index + "$b doesn't exist. Needs to be at most " + (config.getRules().size() - 1) + ".");
            return 0;
        }
        action.accept(index, config.getRules().get(index - 1));
        return 1;
    }

    public int ruleIndex(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> TextTools.replyFormatted(ctx, "Rule $b%d$b: %s", i, r));
    }

    public int ruleRemove(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            config.getRules().remove(i - 1);
            config.save();
            TextTools.replyFormatted(ctx, "Removed rule $b%d$b.", i);
        });
    }

    public int ruleName(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Name of rule $b%d$b: $b%s$b", i, r.getName());
        });
    }

    public int ruleSetName(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            String name = ctx.getArgument("name", String.class);
            r.setName(name);
            config.save();
            TextTools.replyFormatted(ctx, "Set Name of rule $b%d$b to $b%s$b", i, r.getName());
        });
    }

    public int ruleClearName(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setName(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Name of rule $b%d$b", i);
        });
    }

    public int ruleCondition(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Condition of rule $b%d$b: %s", i, r.toConditionString());
        });
    }

    public int ruleType(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Condition Type of rule $b%d$b: $b%s$b", i, r.getType());
        });
    }

    private int setRuleType(CommandContext<ServerCommandSource> ctx, RuleType type) {
        return performRuleAction(ctx, (i, r) -> {
            r.setType(type);
            config.save();
            TextTools.replyFormatted(ctx, "Set Condition Type of rule $b%d$b to $b%s$b", i, r.getType());
        });
    }

    public int ruleTypeSetMspt(CommandContext<ServerCommandSource> ctx) {
        return setRuleType(ctx, RuleType.MSPT);
    }

    public int ruleTypeSetMemory(CommandContext<ServerCommandSource> ctx) {
        return setRuleType(ctx, RuleType.MEMORY);
    }

    public int ruleTypeSetPlayers(CommandContext<ServerCommandSource> ctx) {
        return setRuleType(ctx, RuleType.PLAYERS);
    }

    public int ruleValue(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
           TextTools.replyFormatted(ctx, "Value of rule $b%d$b: $b%s$b", i, r.getValue());
        });
    }

    public int ruleSetValue(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            String value = ctx.getArgument("value", String.class);
            r.setValue(value);
            config.save();
            TextTools.replyFormatted(ctx, "Set Value of rule $b%d$b to $b%s$b", i, r.getValue());
        });
    }

    public int ruleClearValue(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setValue(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Value of rule $b%d$b", i);
        });
    }

    public int ruleMin(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Min value of rule $b%d$b: $b%s$b", i, r.getMin());
        });
    }

    public int ruleSetMin(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer min = ctx.getArgument("min", Integer.class);
            r.setMin(min);
            config.save();
            TextTools.replyFormatted(ctx, "Set Min value of rule $b%d$b to $b%s$b", i, r.getMin());
        });
    }

    public int ruleClearMin(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setMin(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Min value of rule $b%d$b", i);
        });
    }

    public int ruleMax(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Max value of rule $b%d$b: $b%s$b", i, r.getMax());
        });
    }

    public int ruleSetMax(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer max = ctx.getArgument("max", Integer.class);
            r.setMax(max);
            config.save();
            TextTools.replyFormatted(ctx, "Set Max value of rule $b%d$b to $b%s$b", i, r.getMax());
        });
    }

    public int ruleClearMax(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setMax(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Max value of rule $b%d$b", i);
        });
    }

    public int ruleAction(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Action of rule $b%d$b: %s", i, r.toActionString());
        });
    }

    public int ruleUpdateRate(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Update Rate of rule $b%d$b: $b%s$b", i, r.getUpdateRate());
        });
    }

    public int ruleSetUpdateRate(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer updateRate = ctx.getArgument("ticks", Integer.class);
            r.setUpdateRate(updateRate);
            config.save();
            TextTools.replyFormatted(ctx, "Set Update Rate of rule $b%d$b to $b%s$b", i, r.getUpdateRate());
        });
    }

    public int ruleClearUpdateRate(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setUpdateRate(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Update Rate of rule $b%d$b", i);
        });
    }

    public int ruleStep(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Step of rule $b%d$b: $b%s$b", i, r.getStep());
        });
    }

    public int ruleSetStep(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer step = ctx.getArgument("step", Integer.class);
            r.setStep(step);
            config.save();
            TextTools.replyFormatted(ctx, "Set Step of rule $b%d$b to $b%s$b", i, r.getStep());
        });
    }

    public int ruleClearStep(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setStep(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Step of rule $b%d$b", i);
        });
    }

    public int ruleStepAfter(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Step After of rule $b%d$b: $b%s$b", i, r.getStepAfter());
        });
    }

    public int ruleSetStepAfter(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer step = ctx.getArgument("step_after", Integer.class);
            r.setStepAfter(step);
            config.save();
            TextTools.replyFormatted(ctx, "Set Step After of rule $b%d$b to $b%s$b", i, r.getStepAfter());
        });
    }

    public int ruleClearStepAfter(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setStepAfter(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Step After of rule $b%d$b", i);
        });
    }

    public int ruleMinView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Min View Distance of rule $b%d$b: $b%s$b", i, r.getMinDistance());
        });
    }

    public int ruleSetMinView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer min = ctx.getArgument("chunks", Integer.class);
            r.setMinDistance(min);
            config.save();
            TextTools.replyFormatted(ctx, "Set Min View Distance of rule $b%d$b to $b%s$b", i, r.getMinDistance());
        });
    }

    public int ruleClearMinView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setMinDistance(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Min View Distance of rule $b%d$b", i);
        });
    }

    public int ruleMaxView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Max View Distance of rule $b%d$b: $b%s$b", i, r.getMaxDistance());
        });
    }

    public int ruleSetMaxView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer max = ctx.getArgument("chunks", Integer.class);
            r.setMaxDistance(max);
            config.save();
            TextTools.replyFormatted(ctx, "Set Max View Distance of rule $b%d$b to $b%s$b", i, r.getMaxDistance());
        });
    }

    public int ruleClearMaxView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setMaxDistance(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Max View Distance of rule $b%d$b", i);
        });
    }

    private int addRule(CommandContext<ServerCommandSource> ctx, RuleType type, String value, Integer max, Integer min) {
        Rule r = new Rule(
                type,
                value,
                max,
                min,
                RuleTarget.VIEW, //TODO
                null,
                null,
                null,
                null,
                null,
                null
        );
        config.getRules().add(r);
        config.save();
        TextTools.replyFormatted(ctx, "Added new Rule at index $b%d$b. Modify the action to make it effective.", config.getRules().size());
        return 1;
    }

    public int addMsptMin(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, null, min);
    }

    public int addMsptMax(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, max, null);
    }

    public int addMsptRange(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, max, min);
    }

    public int addMemoryMin(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, null, min);
    }

    public int addMemoryMax(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, max, null);
    }

    public int addMemoryRange(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, max, min);
    }

    public int addPlayersMin(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, null, min);
    }

    public int addPlayersMax(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, max, null);
    }

    public int addPlayersRange(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, max, min);
    }

    public int addPlayersName(CommandContext<ServerCommandSource> ctx) {
        String name = ctx.getArgument("names", String.class);
        return addRule(ctx, RuleType.PLAYERS, name, null, null);
    }
}
