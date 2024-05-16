package net.treset.adaptiveview.tools;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class TextTools {
    public static MutableText formatText(String text) {
        boolean italic = false;
        boolean bold = false;
        boolean underline = false;
        TextColor color = TextColor.fromFormatting(Formatting.WHITE);

        int lastSplitIndex = 0;

        MutableText out = Text.literal("");

        for(int i = 0; i < text.length(); i++) {
            if(text.charAt(i) != '?') continue;

            if(text.charAt(i + 1) == 'I') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                italic = !italic;
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'B') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                bold = !bold;
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'U') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                underline = !underline;
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'i') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.GRAY);
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'g') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.DARK_GREEN);
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'p') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.DARK_PURPLE);
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'G') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.GOLD);
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'a') {
                out.append(applyFormatting(text.substring(lastSplitIndex, i), italic, bold, underline, color));
                color = TextColor.fromFormatting(Formatting.DARK_AQUA);
                lastSplitIndex = i + 2;
            }
            else if(text.charAt(i + 1) == 'w') {
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
}
