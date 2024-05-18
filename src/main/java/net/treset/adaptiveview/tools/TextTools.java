package net.treset.adaptiveview.tools;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.treset.adaptiveview.AdaptiveViewMod;

import java.util.List;
import java.util.function.Function;

public class TextTools {
    public static MutableText formatText(String text) {
        boolean italic = false;
        boolean bold = false;
        boolean underline = false;
        TextColor color = TextColor.fromFormatting(Formatting.WHITE);

        int lastSplitIndex = 0;

        MutableText out = Text.literal("");

        for(int i = 0; i < text.length(); i++) {
            if(text.charAt(i) != '$') continue;

            if(text.charAt(i + 1) == 'i') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                italic = !italic;
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'b') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                bold = !bold;
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'u') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                underline = !underline;
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'R') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.RED);
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'N') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.GRAY);
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'g') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.DARK_GREEN);
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'P') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.DARK_PURPLE);
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'G') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.GOLD);
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'A') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.DARK_AQUA);
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'W') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.WHITE);
                lastSplitIndex = i + 2;
            }
        }

        out.append(applyFormatting(text.substring(lastSplitIndex), italic, bold, underline, color));

        return out;
    }

    private static MutableText applyFormatting(String text, boolean italic, boolean bold, boolean underline, TextColor color) {
        MutableText add = Text.literal(text);
        if(italic) add.formatted(Formatting.ITALIC);
        if(bold) add.formatted(Formatting.BOLD);
        if(underline) add.formatted(Formatting.UNDERLINE);
        if(color != TextColor.fromFormatting(Formatting.WHITE)) add.formatted(Formatting.byName(color.getName()));
        return add;
    }

    public static void replyFormatted(CommandContext<ServerCommandSource> ctx, boolean broadcastToOps, String text, Object... args) {
        ctx.getSource().sendFeedback(() -> formatText(String.format(text, args)), broadcastToOps);
    }

    public static void replyFormatted(CommandContext<ServerCommandSource> ctx, String text, Object... args) {
        replyFormatted(ctx, false, text, args);
    }

    public static void replyError(CommandContext<ServerCommandSource> ctx, String text) {
        ctx.getSource().sendError(Text.literal(text));
    }

    public static void sendMessage(Function<ServerPlayerEntity, Boolean> shouldSend, String message, Object... args) {
        Text formated = formatText(String.format(message, args));

        List<ServerPlayerEntity> players = AdaptiveViewMod.getServer().getPlayerManager().getPlayerList();
        for(ServerPlayerEntity player : players) {
            if(shouldSend.apply(player)) {
                player.sendMessage(formated);
            }
        }
    }

    public static boolean containsIgnoreCase(List<String> list, String str) {
        return list.stream().map(String::toLowerCase).toList().contains(str.toLowerCase());
    }
}
