package net.treset.adaptiveview.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.config.Rule;
import net.treset.adaptiveview.config.RuleTarget;
import net.treset.adaptiveview.config.RuleType;
import net.treset.adaptiveview.tools.BroadcastLevel;
import net.treset.adaptiveview.tools.TextTools;

import java.io.IOException;
import java.util.function.BiConsumer;

public class ConfigCommandHandler {
    private final Config config;

    public ConfigCommandHandler(Config config) {
        this.config = config;
    }

    public int list(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Current Configuration:");
        TextTools.replyFormatted(ctx, "Update Rate: $b%d ticks", config.getUpdateRate());
        TextTools.replyFormatted(ctx, "View Distance Range: $b%d-%d chunks", config.getMinViewDistance(), config.getMaxViewDistance());
        TextTools.replyFormatted(ctx, "Simulation Distance Range: $b%d-%d chunks", config.getMinSimDistance(), config.getMaxSimDistance());
        TextTools.replyFormatted(ctx, "Chunk-Ticking Distance Range: $b%d-%d chunks", config.getMinChunkTickingDistance(), config.getMaxChunkTickingDistance());
        TextTools.replyFormatted(ctx, "Rules: $b%s$b", config.getRules().size());
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

    public int maxSim(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Max Simulation Distance: $b%s chunks", config.getMaxSimDistance());
        return 1;
    }

    public int setMaxSim(CommandContext<ServerCommandSource> ctx) {
        Integer chunks = ctx.getArgument("chunks", Integer.class);
        config.setMaxSimDistance(chunks);
        config.save();
        TextTools.replyFormatted(ctx, "Set Max Simulation Distance to $b%s chunks", config.getMaxSimDistance());
        return 1;
    }

    public int minSim(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Min Simulation Distance: $b%s chunks", config.getMinSimDistance());
        return 1;
    }

    public int setMinSim(CommandContext<ServerCommandSource> ctx) {
        Integer chunks = ctx.getArgument("chunks", Integer.class);
        config.setMinSimDistance(chunks);
        config.save();
        TextTools.replyFormatted(ctx, "Set Min Simulation Distance to $b%s chunks", config.getMinSimDistance());
        return 1;
    }

    public int maxChunkTicking(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Max Chunk-Ticking Distance: $b%s chunks", config.getMaxChunkTickingDistance());
        return 1;
    }

    public int setMaxChunkTicking(CommandContext<ServerCommandSource> ctx) {
        Integer chunks = ctx.getArgument("chunks", Integer.class);
        config.setMaxChunkTickingDistance(chunks);
        config.save();
        TextTools.replyFormatted(ctx, "Set Max Chunk-Ticking Distance to $b%s chunks", config.getMaxChunkTickingDistance());
        return 1;
    }

    public int minChunkTicking(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Min Chunk-Ticking Distance: $b%s chunks", config.getMinChunkTickingDistance());
        return 1;
    }

    public int setMinChunkTicking(CommandContext<ServerCommandSource> ctx) {
        Integer chunks = ctx.getArgument("chunks", Integer.class);
        config.setMinChunkTickingDistance(chunks);
        config.save();
        TextTools.replyFormatted(ctx, "Set Min Chunk-Ticking Distance to $b%s chunks", config.getMinChunkTickingDistance());
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

    public int ruleTarget(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Action Target of rule $b%d$b: $b%s$b", i, r.getTarget().getName());
        });
    }

    private int ruleSetTarget(CommandContext<ServerCommandSource> ctx, RuleTarget target) {
        return performRuleAction(ctx, (i, r) -> {
            r.setTarget(target);
            config.save();
            TextTools.replyFormatted(ctx, "Set Action Target of rule $b%d$b to $b%s$b", i, r.getTarget().getName());
        });
    }

    public int ruleSetTargetView(CommandContext<ServerCommandSource> ctx) {
        return ruleSetTarget(ctx, RuleTarget.VIEW);
    }

    public int ruleSetTargetSim(CommandContext<ServerCommandSource> ctx) {
        return ruleSetTarget(ctx, RuleTarget.SIMULATION);
    }

    public int ruleSetTargetChunkTick(CommandContext<ServerCommandSource> ctx) {
        return ruleSetTarget(ctx, RuleTarget.CHUNK_TICKING);
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
            Integer step = ctx.getArgument("step-after", Integer.class);
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

    public int ruleMinDistance(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Min Distance of rule $b%d$b: $b%s$b", i, r.getMinDistance());
        });
    }

    public int ruleSetMinDistance(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer min = ctx.getArgument("chunks", Integer.class);
            r.setMinDistance(min);
            config.save();
            TextTools.replyFormatted(ctx, "Set Min Distance of rule $b%d$b to $b%s$b", i, r.getMinDistance());
        });
    }

    public int ruleClearMinDistance(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setMinDistance(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Min Distance of rule $b%d$b", i);
        });
    }

    public int ruleMaxDistance(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Max Distance of rule $b%d$b: $b%s$b", i, r.getMaxDistance());
        });
    }

    public int ruleSetMaxDistance(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer max = ctx.getArgument("chunks", Integer.class);
            r.setMaxDistance(max);
            config.save();
            TextTools.replyFormatted(ctx, "Set Max Distance of rule $b%d$b to $b%s$b", i, r.getMaxDistance());
        });
    }

    public int ruleClearMaxDistance(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setMaxDistance(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Max Distance of rule $b%d$b", i);
        });
    }

    private int addRule(CommandContext<ServerCommandSource> ctx, RuleType type, String value, Integer max, Integer min, RuleTarget target) {
        Rule r = new Rule(
                type,
                value,
                max,
                min,
                target,
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

    public int addMsptMinView(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, null, min, RuleTarget.VIEW);
    }

    public int addMsptMinSim(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, null, min, RuleTarget.SIMULATION);
    }

    public int addMsptMinChunkTick(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, null, min, RuleTarget.CHUNK_TICKING);
    }

    public int addMsptMaxView(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, max, null, RuleTarget.VIEW);
    }

    public int addMsptMaxSim(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, max, null, RuleTarget.SIMULATION);
    }

    public int addMsptMaxChunkTick(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, max, null, RuleTarget.CHUNK_TICKING);
    }

    public int addMsptRangeView(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, max, min, RuleTarget.VIEW);
    }

    public int addMsptRangeSim(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, max, min, RuleTarget.SIMULATION);
    }

    public int addMsptRangeChunkTick(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MSPT, null, max, min, RuleTarget.CHUNK_TICKING);
    }

    public int addMemoryMinView(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, null, min, RuleTarget.VIEW);
    }

    public int addMemoryMinSim(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, null, min, RuleTarget.SIMULATION);
    }

    public int addMemoryMinChunkTick(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, null, min, RuleTarget.CHUNK_TICKING);
    }

    public int addMemoryMaxView(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, max, null, RuleTarget.VIEW);
    }

    public int addMemoryMaxSim(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, max, null, RuleTarget.SIMULATION);
    }

    public int addMemoryMaxChunkTick(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, max, null, RuleTarget.CHUNK_TICKING);
    }

    public int addMemoryRangeView(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, max, min, RuleTarget.VIEW);
    }

    public int addMemoryRangeSim(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, max, min, RuleTarget.SIMULATION);
    }

    public int addMemoryRangeChunkTick(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.MEMORY, null, max, min, RuleTarget.CHUNK_TICKING);
    }

    public int addPlayersMinView(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, null, min, RuleTarget.VIEW);
    }

    public int addPlayersMinSim(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, null, min, RuleTarget.SIMULATION);
    }

    public int addPlayersMinChunkTick(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, null, min, RuleTarget.CHUNK_TICKING);
    }

    public int addPlayersMaxView(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, max, null, RuleTarget.VIEW);
    }

    public int addPlayersMaxSim(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, max, null, RuleTarget.SIMULATION);
    }

    public int addPlayersMaxChunkTick(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, max, null, RuleTarget.CHUNK_TICKING);
    }

    public int addPlayersRangeView(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, max, min, RuleTarget.VIEW);
    }

    public int addPlayersRangeSim(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, max, min, RuleTarget.SIMULATION);
    }

    public int addPlayersRangeChunkTick(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        return addRule(ctx, RuleType.PLAYERS, null, max, min, RuleTarget.CHUNK_TICKING);
    }

    public int addPlayersNameView(CommandContext<ServerCommandSource> ctx) {
        String name = ctx.getArgument("names", String.class);
        return addRule(ctx, RuleType.PLAYERS, name, null, null, RuleTarget.VIEW);
    }

    public int addPlayersNameSim(CommandContext<ServerCommandSource> ctx) {
        String name = ctx.getArgument("names", String.class);
        return addRule(ctx, RuleType.PLAYERS, name, null, null, RuleTarget.SIMULATION);
    }

    public int addPlayersNameChunkTick(CommandContext<ServerCommandSource> ctx) {
        String name = ctx.getArgument("names", String.class);
        return addRule(ctx, RuleType.PLAYERS, name, null, null, RuleTarget.CHUNK_TICKING);
    }

    public void registerCommands(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.then(CommandManager.literal("config")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(this::list)
                .then(CommandManager.literal("status")
                        .executes(this::list)
                )
                .then(CommandManager.literal("reload")
                        .executes(this::reload)
                )
                .then(CommandManager.literal("update-rate")
                        .executes(this::updateRate)
                        .then(CommandManager.argument("ticks", IntegerArgumentType.integer(1, 72000))
                                .executes(this::setUpdateRate)
                        )
                )
                .then(CommandManager.literal("max-view-distance")
                        .executes(this::maxView)
                        .then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
                                .executes(this::setMaxView)
                        )
                )
                .then(CommandManager.literal("min-view-distance")
                        .executes(this::minView)
                        .then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
                                .executes(this::setMinView)
                        )
                )
                .then(CommandManager.literal("max-simulation-distance")
                        .executes(this::maxSim)
                        .then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
                                .executes(this::setMaxSim)
                        )
                )
                .then(CommandManager.literal("min-simulation-distance")
                        .executes(this::minSim)
                        .then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
                                .executes(this::setMinSim)
                        )
                )
                .then(CommandManager.literal("max-chunk-tick-distance")
                        .executes(this::maxChunkTicking)
                        .then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
                                .executes(this::setMaxChunkTicking)
                        )
                )
                .then(CommandManager.literal("min-chunk-tick-distance")
                        .executes(this::minChunkTicking)
                        .then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
                                .executes(this::setMinChunkTicking)
                        )
                )
                .then(CommandManager.literal("broadcast-changes")
                        .executes(this::broadcastChanges)
                        .then(CommandManager.literal("none")
                                .executes(this::broadcastChangesNone)
                        )
                        .then(CommandManager.literal("ops")
                                .executes(this::broadcastChangesOps)
                        )
                        .then(CommandManager.literal("all")
                                .executes(this::broadcastChangesAll)
                        )
                )
                .then(CommandManager.literal("broadcast-lock")
                        .executes(this::broadcastLock)
                        .then(CommandManager.literal("none")
                                .executes(this::broadcastLockNone)
                        )
                        .then(CommandManager.literal("ops")
                                .executes(this::broadcastLockOps)
                        )
                        .then(CommandManager.literal("all")
                                .executes(this::broadcastLockAll)
                        )
                )
                .then(CommandManager.literal("rules")
                        .executes(this::rules)
                        .then(CommandManager.argument("index", IntegerArgumentType.integer(1, 100))
                                .executes(this::ruleIndex)
                                .then(CommandManager.literal("remove")
                                        .executes(this::ruleRemove)
                                )
                                .then(CommandManager.literal("name")
                                        .executes(this::ruleName)
                                        .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                                .executes(this::ruleSetName)
                                        )
                                        .then(CommandManager.literal("clear")
                                                .executes(this::ruleClearName)
                                        )
                                )
                                .then(CommandManager.literal("condition")
                                        .executes(this::ruleCondition)
                                        .then(CommandManager.literal("type")
                                                .executes(this::ruleType)
                                                .then(CommandManager.literal("mspt")
                                                        .executes(this::ruleTypeSetMspt)
                                                )
                                                .then(CommandManager.literal("memory")
                                                        .executes(this::ruleTypeSetMemory)
                                                )
                                                .then(CommandManager.literal("players")
                                                        .executes(this::ruleTypeSetPlayers)
                                                )
                                        )
                                        .then(CommandManager.literal("value")
                                                .executes(this::ruleValue)
                                                .then(CommandManager.argument("value", StringArgumentType.greedyString())
                                                        .executes(this::ruleSetValue)
                                                )
                                                .then(CommandManager.literal("clear")
                                                        .executes(this::ruleClearValue)
                                                )
                                        )
                                        .then(CommandManager.literal("min")
                                                .executes(this::ruleMin)
                                                .then(CommandManager.argument("min", IntegerArgumentType.integer(0))
                                                        .executes(this::ruleSetMin)
                                                )
                                                .then(CommandManager.literal("clear")
                                                        .executes(this::ruleClearMin)
                                                )
                                        )
                                        .then(CommandManager.literal("max")
                                                .executes(this::ruleMax)
                                                .then(CommandManager.argument("max", IntegerArgumentType.integer(0))
                                                        .executes(this::ruleSetMax)
                                                )
                                                .then(CommandManager.literal("clear")
                                                        .executes(this::ruleClearMax)
                                                )
                                        )
                                )
                                .then(CommandManager.literal("action")
                                        .executes(this::ruleAction)
                                        .then(CommandManager.literal("target")
                                                .executes(this::ruleTarget)
                                                .then(CommandManager.literal("view")
                                                        .executes(this::ruleSetTargetView)
                                                )
                                                .then(CommandManager.literal("simulation")
                                                        .executes(this::ruleSetTargetSim)
                                                )
                                                .then(CommandManager.literal("chunk-tick")
                                                        .executes(this::ruleSetTargetChunkTick)
                                                )
                                        )
                                        .then(CommandManager.literal("update-rate")
                                                .executes(this::ruleUpdateRate)
                                                .then(CommandManager.argument("ticks", IntegerArgumentType.integer(1, 72000))
                                                        .executes(this::ruleSetUpdateRate)
                                                )
                                                .then(CommandManager.literal("clear")
                                                        .executes(this::ruleClearUpdateRate)
                                                )
                                        )
                                        .then(CommandManager.literal("step")
                                                .executes(this::ruleStep)
                                                .then(CommandManager.argument("step", IntegerArgumentType.integer(-32, 32))
                                                        .executes(this::ruleSetStep)
                                                )
                                                .then(CommandManager.literal("clear")
                                                        .executes(this::ruleClearStep)
                                                )
                                        )
                                        .then(CommandManager.literal("step-after")
                                                .executes(this::ruleStepAfter)
                                                .then(CommandManager.argument("step-after", IntegerArgumentType.integer(1, 100))
                                                        .executes(this::ruleSetStepAfter)
                                                )
                                                .then(CommandManager.literal("clear")
                                                        .executes(this::ruleClearStepAfter)
                                                )
                                        )
                                        .then(CommandManager.literal("min-distance")
                                                .executes(this::ruleMinDistance)
                                                .then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
                                                        .executes(this::ruleSetMinDistance)
                                                )
                                                .then(CommandManager.literal("clear")
                                                        .executes(this::ruleClearMinDistance)
                                                )
                                        )
                                        .then(CommandManager.literal("max-distance")
                                                .executes(this::ruleMaxDistance)
                                                .then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
                                                        .executes(this::ruleSetMaxDistance)
                                                )
                                                .then(CommandManager.literal("clear")
                                                        .executes(this::ruleClearMaxDistance)
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("add")
                                .then(CommandManager.literal("mspt")
                                        .then(CommandManager.literal("min")
                                                .then(CommandManager.argument("min", IntegerArgumentType.integer(0, 1000))
                                                        .executes(this::addMsptMinView)
                                                        .then(CommandManager.literal("view")
                                                                .executes(this::addMsptMinView)
                                                        )
                                                        .then(CommandManager.literal("simulation")
                                                                .executes(this::addMsptMinSim)
                                                        )
                                                        .then(CommandManager.literal("chunk-tick")
                                                                .executes(this::addMsptMinChunkTick)
                                                        )
                                                )
                                        )
                                        .then(CommandManager.literal("max")
                                                .then(CommandManager.argument("max", IntegerArgumentType.integer(0, 1000))
                                                        .executes(this::addMsptMaxView)
                                                        .then(CommandManager.literal("view")
                                                                .executes(this::addMsptMaxView)
                                                        )
                                                        .then(CommandManager.literal("simulation")
                                                                .executes(this::addMsptMaxSim)
                                                        )
                                                        .then(CommandManager.literal("chunk-tick")
                                                                .executes(this::addMsptMaxChunkTick)
                                                        )
                                                )
                                        )
                                        .then(CommandManager.literal("range")
                                                .then(CommandManager.argument("min", IntegerArgumentType.integer(0, 1000))
                                                        .then(CommandManager.argument("max", IntegerArgumentType.integer(0, 1000))
                                                                .executes(this::addMsptRangeView)
                                                                .then(CommandManager.literal("view")
                                                                        .executes(this::addMsptRangeView)
                                                                )
                                                                .then(CommandManager.literal("simulation")
                                                                        .executes(this::addMsptRangeSim)
                                                                )
                                                                .then(CommandManager.literal("chunk-tick")
                                                                        .executes(this::addMsptRangeChunkTick)
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(CommandManager.literal("memory")
                                        .then(CommandManager.literal("min")
                                                .then(CommandManager.argument("min", IntegerArgumentType.integer(0, 100))
                                                        .executes(this::addMemoryMinView)
                                                        .then(CommandManager.literal("view")
                                                                .executes(this::addMemoryMinView)
                                                        )
                                                        .then(CommandManager.literal("simulation")
                                                                .executes(this::addMemoryMinSim)
                                                        )
                                                        .then(CommandManager.literal("chunk-tick")
                                                                .executes(this::addMemoryMinChunkTick)
                                                        )
                                                )
                                        )
                                        .then(CommandManager.literal("max")
                                                .then(CommandManager.argument("max", IntegerArgumentType.integer(0, 100))
                                                        .executes(this::addMemoryMaxView)
                                                        .then(CommandManager.literal("view")
                                                                .executes(this::addMemoryMaxView)
                                                        )
                                                        .then(CommandManager.literal("simulation")
                                                                .executes(this::addMemoryMaxSim)
                                                        )
                                                        .then(CommandManager.literal("chunk-tick")
                                                                .executes(this::addMemoryMaxChunkTick)
                                                        )
                                                )
                                        )
                                        .then(CommandManager.literal("range")
                                                .then(CommandManager.argument("min", IntegerArgumentType.integer(0, 100))
                                                        .then(CommandManager.argument("max", IntegerArgumentType.integer(0, 100))
                                                                .executes(this::addMemoryRangeView)
                                                                .then(CommandManager.literal("view")
                                                                        .executes(this::addMemoryRangeView)
                                                                )
                                                                .then(CommandManager.literal("simulation")
                                                                        .executes(this::addMemoryRangeSim)
                                                                )
                                                                .then(CommandManager.literal("chunk-tick")
                                                                        .executes(this::addMemoryRangeChunkTick)
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(CommandManager.literal("players")
                                        .then(CommandManager.literal("min")
                                                .then(CommandManager.argument("min", IntegerArgumentType.integer(0, 1000))
                                                        .executes(this::addPlayersMinView)
                                                        .then(CommandManager.literal("view")
                                                                .executes(this::addPlayersMinView)
                                                        )
                                                        .then(CommandManager.literal("simulation")
                                                                .executes(this::addPlayersMinSim)
                                                        )
                                                        .then(CommandManager.literal("chunk-tick")
                                                                .executes(this::addPlayersMinChunkTick)
                                                        )
                                                )
                                        )
                                        .then(CommandManager.literal("max")
                                                .then(CommandManager.argument("max", IntegerArgumentType.integer(0, 1000))
                                                        .executes(this::addPlayersMaxView)
                                                        .then(CommandManager.literal("view")
                                                                .executes(this::addPlayersMaxView)
                                                        )
                                                        .then(CommandManager.literal("simulation")
                                                                .executes(this::addPlayersMaxSim)
                                                        )
                                                        .then(CommandManager.literal("chunk-tick")
                                                                .executes(this::addPlayersMaxChunkTick)
                                                        )
                                                )
                                        )
                                        .then(CommandManager.literal("range")
                                                .then(CommandManager.argument("min", IntegerArgumentType.integer(0, 1000))
                                                        .then(CommandManager.argument("max", IntegerArgumentType.integer(0, 1000))
                                                                .executes(this::addPlayersRangeView)
                                                                .then(CommandManager.literal("view")
                                                                        .executes(this::addPlayersRangeView)
                                                                )
                                                                .then(CommandManager.literal("simulation")
                                                                        .executes(this::addPlayersRangeSim)
                                                                )
                                                                .then(CommandManager.literal("chunk-tick")
                                                                        .executes(this::addPlayersRangeChunkTick)
                                                                )
                                                        )
                                                )
                                        )
                                        .then(CommandManager.literal("names")
                                                .then(CommandManager.argument("names", StringArgumentType.greedyString())
                                                        .executes(this::addPlayersNameView)
                                                        .then(CommandManager.literal("view")
                                                                .executes(this::addPlayersNameView)
                                                        )
                                                        .then(CommandManager.literal("simulation")
                                                                .executes(this::addPlayersNameSim)
                                                        )
                                                        .then(CommandManager.literal("chunk-tick")
                                                                .executes(this::addPlayersNameChunkTick)
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
