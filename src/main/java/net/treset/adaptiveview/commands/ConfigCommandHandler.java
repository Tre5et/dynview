package net.treset.adaptiveview.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.treset.adaptiveview.config.OldConfig;
import net.treset.adaptiveview.tools.TextTools;

public class ConfigCommandHandler {
    private final OldConfig config;

    public ConfigCommandHandler(OldConfig config) {
        this.config = config;
    }

    public int base(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, "?iCurrent Configuration:", false);
        TextTools.replyFormatted(ctx, String.format("?iUpdate interval: ?B%s ticks", config.getUpdateInterval()), false);
        TextTools.replyFormatted(ctx, String.format("?iTarget MSPT: ?B%s-%s MSPT", config.getMinMspt(), config.getMaxMspt()), false);
        TextTools.replyFormatted(ctx, String.format("?iTarget MSPT aggressive: ?B%s-%s MSPT", config.getMinMsptAggressive(), config.getMaxMsptAggressive()), false);
        TextTools.replyFormatted(ctx, String.format("?iView Distance Range: ?B%s-%s chunks", config.getMinViewDistance(), config.getMaxViewDistance()), false);
        return 1;
    }

    public int updateInterval(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, String.format("?i?IThe interval in which the view distance is adjusted. Currently ?B%s ticks", config.getUpdateInterval()), false);
        return 1;
    }

    public int updateIntervalInterval(CommandContext<ServerCommandSource> ctx) {
        int interval = IntegerArgumentType.getInteger(ctx, "interval");
        int oldInterval = config.getUpdateInterval();
        config.setUpdateInterval(interval);
        config.save();
        TextTools.replyFormatted(ctx, String.format("?gChanged update interval from ?B%s?B to ?B%s ticks", oldInterval, interval), true);
        return 1;
    }

    public int targetMspt(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, String.format("?i?IThe MSPT range the server aims to stay in. Currently ?B%s-%s MSPT", config.getMinMspt(), config.getMaxMspt()), false);
        return 1;
    }

    public int targetMsptMinMsptMaxMspt(CommandContext<ServerCommandSource> ctx) {
        int min = IntegerArgumentType.getInteger(ctx, "minMspt");
        int max = IntegerArgumentType.getInteger(ctx, "maxMspt");
        int oldMin = config.getMinMspt();
        int oldMax = config.getMaxMspt();
        config.setMinMspt(min);
        config.setMaxMspt(max);
        config.save();
        TextTools.replyFormatted(ctx, String.format("?gChanged target MSPT range from ?B%s-%s?B to ?B%s-%s MSPT", oldMin, oldMax, min, max), true);
        return 1;
    }

    public int targetMsptAggressive(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, String.format("?i?IThe MSPT range in which the view distance will be adjusted at max one chunk per update. Outside this range the view distance will be adjsuted two chunks at once. Currently ?B%s-%s MSPT", config.getMinMsptAggressive(), config.getMaxMsptAggressive()), false);
        return 1;
    }

    public int targetMsptAggressiveMinMsptMaxMsptAggressive(CommandContext<ServerCommandSource> ctx) {
        int min = IntegerArgumentType.getInteger(ctx, "minMsptAggressive");
        int max = IntegerArgumentType.getInteger(ctx, "maxMsptAggressive");
        int oldMin = config.getMinMsptAggressive();
        int oldMax = config.getMaxMsptAggressive();
        config.setMinMsptAggressive(min);
        config.setMaxMsptAggressive(max);
        config.save();
        TextTools.replyFormatted(ctx, String.format("?gChanged aggressive MSPT range from ?B%s-%s?B to ?B%s-%s MSPT", oldMin, oldMax, min, max), true);
        return 1;
    }

    public int viewDistanceRange(CommandContext<ServerCommandSource> ctx) {
        TextTools.replyFormatted(ctx, String.format("?i?IThe view distance range the server will adjust view distance in. Currently ?B%s-%s chunks", config.getMinViewDistance(), config.getMaxViewDistance()), false);
        return 1;
    }

    public int viewDistanceRangeMinVDMaxVD(CommandContext<ServerCommandSource> ctx) {
        int min = IntegerArgumentType.getInteger(ctx, "minVD");
        int max = IntegerArgumentType.getInteger(ctx, "maxVD");
        int oldMin = config.getMinViewDistance();
        int oldMax = config.getMaxViewDistance();
        config.setMinViewDistance(min);
        config.setMaxViewDistance(max);
        config.save();
        TextTools.replyFormatted(ctx, String.format("?gChanged view distance range from ?B%s-%s?B to ?B%s-%s chunks", oldMin, oldMax, min, max), true);
        return 1;
    }
}
