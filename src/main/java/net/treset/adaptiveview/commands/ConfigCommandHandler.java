package net.treset.adaptiveview.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.config.Rule;
import net.treset.adaptiveview.config.RuleType;
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
        TextTools.replyFormatted(ctx, "Update rate: ?B%d ticks", config.getUpdateRate());
        TextTools.replyFormatted(ctx, "View Distance Range: ?B%d-%d chunks", config.getMinViewDistance(), config.getMaxViewDistance());
        TextTools.replyFormatted(ctx, "?B%d Rules", config.getRules().size());
        return 1;
    }

    public int reload(CommandContext<ServerCommandSource> ctx) {
        Config config;
        try {
            config = Config.load();
        } catch (IOException e) {
            TextTools.replyFormatted(ctx, "Failed to reload Config! Check for syntax errors.", false);
            return 0;
        }

        this.config.copy(config);
        TextTools.replyFormatted(ctx, "Reloaded Configuration!", false);
        return 1;
    }

    public int updateRate(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Update Rate: ?B%s ticks", config.getUpdateRate());
        return 1;
    }

    public int setUpdateRate(CommandContext<ServerCommandSource> ctx) {
        Integer ticks = ctx.getArgument("ticks", Integer.class);
        config.setUpdateRate(ticks);
        config.save();
        TextTools.replyFormatted(ctx, "Set Update Rate to ?B%s ticks", config.getUpdateRate());
        return 1;
    }

    public int maxView(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Max View Distance: ?B%d chunks", config.getMaxViewDistance());
        return 1;
    }

    public int setMaxView(CommandContext<ServerCommandSource> ctx) {
        Integer chunks = ctx.getArgument("chunks", Integer.class);
        config.setMaxViewDistance(chunks);
        config.save();
        TextTools.replyFormatted(ctx, "Set Max View Distance to ?B%d chunks", config.getMaxViewDistance());
        return 1;
    }

    public int minView(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "Min View Distance: ?B%s chunks", config.getMinViewDistance());
        return 1;
    }

    public int setMinView(CommandContext<ServerCommandSource> ctx) {
        Integer chunks = ctx.getArgument("chunks", Integer.class);
        config.setMinViewDistance(chunks);
        config.save();
        TextTools.replyFormatted(ctx, "Set Min View Distance to ?B%s chunks", config.getMinViewDistance());
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
            TextTools.replyError(ctx, "Rule of index " + index + " doesn't exist. Needs to be at most " + (config.getRules().size() - 1) + ".");
            return 0;
        }
        action.accept(index, config.getRules().get(index - 1));
        return 1;
    }

    public int ruleIndex(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> TextTools.replyFormatted(ctx, "Rule %d: %s", i, r));
    }

    public int ruleRemove(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            config.getRules().remove(i - 1);
            config.save();
            TextTools.replyFormatted(ctx, "Removed rule %d.", i);
        });
    }

    public int ruleCondition(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Condition of rule %d: %s", i, r.toConditionString());
        });
    }

    public int ruleType(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Condition type of rule %d: %s", i, r.getType());
        });
    }

    private int setRuleType(CommandContext<ServerCommandSource> ctx, RuleType type) {
        return performRuleAction(ctx, (i, r) -> {
            r.setType(type);
            config.save();
            TextTools.replyFormatted(ctx, "Set Condition type of rule %d to %s", i, r.getType());
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
           TextTools.replyFormatted(ctx, "Value of rule %d: %s", i, r.getValue());
        });
    }

    public int ruleSetValue(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            String value = ctx.getArgument("value", String.class);
            r.setValue(value);
            config.save();
            TextTools.replyFormatted(ctx, "Set Value of rule %d to %s", i, r.getValue());
        });
    }

    public int ruleClearValue(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setValue(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Value of rule %d", i);
        });
    }

    public int ruleMin(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Min value of rule %d: %s", i, r.getMin());
        });
    }

    public int ruleSetMin(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer min = ctx.getArgument("min", Integer.class);
            r.setMin(min);
            config.save();
            TextTools.replyFormatted(ctx, "Set Min value of rule %d to %s", i, r.getMin());
        });
    }

    public int ruleClearMin(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setMin(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Min value of rule %d", i);
        });
    }

    public int ruleMax(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Max value of rule %d: %s", i, r.getMax());
        });
    }

    public int ruleSetMax(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer max = ctx.getArgument("max", Integer.class);
            r.setMax(max);
            config.save();
            TextTools.replyFormatted(ctx, "Set Max value of rule %d to %s", i, r.getMax());
        });
    }

    public int ruleClearMax(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setMax(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Max value of rule %d", i);
        });
    }

    public int ruleAction(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Action of rule %d: %s", i, r.toActionString());
        });
    }

    public int ruleUpdateRate(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Update rate of rule %d: %s", i, r.getUpdateRate());
        });
    }

    public int ruleSetUpdateRate(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer updateRate = ctx.getArgument("ticks", Integer.class);
            r.setUpdateRate(updateRate);
            config.save();
            TextTools.replyFormatted(ctx, "Set Update rate of rule %d to %s", i, r.getUpdateRate());
        });
    }

    public int ruleClearUpdateRate(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setUpdateRate(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Update rate of rule %d", i);
        });
    }

    public int ruleStep(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Step of rule %d: %s", i, r.getStep());
        });
    }

    public int ruleSetStep(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer step = ctx.getArgument("step", Integer.class);
            r.setStep(step);
            config.save();
            TextTools.replyFormatted(ctx, "Set Step of rule %d to %s", i, r.getStep());
        });
    }

    public int ruleClearStep(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setStep(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Step of rule %d", i);
        });
    }

    public int ruleMinView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Min view distance of rule %d: %s", i, r.getMinViewDistance());
        });
    }

    public int ruleSetMinView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer min = ctx.getArgument("chunks", Integer.class);
            r.setMinViewDistance(min);
            config.save();
            TextTools.replyFormatted(ctx, "Set Min view distance of rule %d to %s", i, r.getMinViewDistance());
        });
    }

    public int ruleClearMinView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setMinViewDistance(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Min view distance of rule %d", i);
        });
    }

    public int ruleMaxView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            TextTools.replyFormatted(ctx, "Max view distance of rule %d: %s", i, r.getMaxViewDistance());
        });
    }

    public int ruleSetMaxView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            Integer max = ctx.getArgument("chunks", Integer.class);
            r.setMaxViewDistance(max);
            config.save();
            TextTools.replyFormatted(ctx, "Set Max view distance of rule %d to %s", i, r.getMaxViewDistance());
        });
    }

    public int ruleClearMaxView(CommandContext<ServerCommandSource> ctx) {
        return performRuleAction(ctx, (i, r) -> {
            r.setMaxViewDistance(null);
            config.save();
            TextTools.replyFormatted(ctx, "Cleared Max view distance of rule %d", i);
        });
    }

    public int addMsptMin(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Rule r = new Rule(
                RuleType.MSPT,
                null,
                null,
                min,
                null,
                null,
                null,
                null
        );
        config.getRules().add(r);
        config.save();
        TextTools.replyFormatted(ctx, "Added new Rule at index %d. Modify the action to make it effective.", config.getRules().size() -1);
        return 1;
    }

    public int addMsptMax(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        Rule r = new Rule(
                RuleType.MSPT,
                null,
                max,
                null,
                null,
                null,
                null,
                null
        );
        config.getRules().add(r);
        config.save();
        TextTools.replyFormatted(ctx, "Added new Rule at index %d. Modify the action to make it effective.", config.getRules().size() -1);
        return 1;
    }

    public int addMsptRange(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        Rule r = new Rule(
                RuleType.MSPT,
                null,
                max,
                min,
                null,
                null,
                null,
                null
        );
        config.getRules().add(r);
        config.save();
        TextTools.replyFormatted(ctx, "Added new Rule at index %d. Modify the action to make it effective.", config.getRules().size() -1);
        return 1;
    }

    public int addMemoryMin(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Rule r = new Rule(
                RuleType.MEMORY,
                null,
                null,
                min,
                null,
                null,
                null,
                null
        );
        config.getRules().add(r);
        config.save();
        TextTools.replyFormatted(ctx, "Added new Rule at index %d. Modify the action to make it effective.", config.getRules().size() -1);
        return 1;
    }

    public int addMemoryMax(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        Rule r = new Rule(
                RuleType.MEMORY,
                null,
                max,
                null,
                null,
                null,
                null,
                null
        );
        config.getRules().add(r);
        config.save();
        TextTools.replyFormatted(ctx, "Added new Rule at index %d. Modify the action to make it effective.", config.getRules().size() -1);
        return 1;
    }

    public int addMemoryRange(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        Rule r = new Rule(
                RuleType.MEMORY,
                null,
                max,
                min,
                null,
                null,
                null,
                null
        );
        config.getRules().add(r);
        config.save();
        TextTools.replyFormatted(ctx, "Added new Rule at index %d. Modify the action to make it effective.", config.getRules().size() -1);
        return 1;
    }

    public int addPlayersMin(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Rule r = new Rule(
                RuleType.PLAYERS,
                null,
                null,
                min,
                null,
                null,
                null,
                null
        );
        config.getRules().add(r);
        config.save();
        TextTools.replyFormatted(ctx, "Added new Rule at index %d. Modify the action to make it effective.", config.getRules().size() -1);
        return 1;
    }

    public int addPlayersMax(CommandContext<ServerCommandSource> ctx) {
        Integer max = ctx.getArgument("max", Integer.class);
        Rule r = new Rule(
                RuleType.PLAYERS,
                null,
                max,
                null,
                null,
                null,
                null,
                null
        );
        config.getRules().add(r);
        config.save();
        TextTools.replyFormatted(ctx, "Added new Rule at index %d. Modify the action to make it effective.", config.getRules().size() -1);
        return 1;
    }

    public int addPlayersRange(CommandContext<ServerCommandSource> ctx) {
        Integer min = ctx.getArgument("min", Integer.class);
        Integer max = ctx.getArgument("max", Integer.class);
        Rule r = new Rule(
                RuleType.PLAYERS,
                null,
                max,
                min,
                null,
                null,
                null,
                null
        );
        config.getRules().add(r);
        config.save();
        TextTools.replyFormatted(ctx, "Added new Rule at index %d. Modify the action to make it effective.", config.getRules().size() -1);
        return 1;
    }

    public int addPlayersName(CommandContext<ServerCommandSource> ctx) {
        String name = ctx.getArgument("name", String.class);
        Rule r = new Rule(
                RuleType.PLAYERS,
                name,
                null,
                null,
                null,
                null,
                null,
                null
        );
        config.getRules().add(r);
        config.save();
        TextTools.replyFormatted(ctx, "Added new Rule at index %d. Modify the action to make it effective.", config.getRules().size() - 1);
        return 1;
    }
}
