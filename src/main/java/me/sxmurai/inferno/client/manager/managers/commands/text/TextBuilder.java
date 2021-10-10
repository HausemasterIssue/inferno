package me.sxmurai.inferno.client.manager.managers.commands.text;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextBuilder {
    private final StringBuilder builder = new StringBuilder();

    public TextBuilder append(ChatColor color, String text) {
        builder.append(color.text(text));
        return this;
    }

    public TextBuilder append(String text) {
        builder.append(text);
        return this;
    }

    public String build() {
        return builder.toString();
    }

    public static class ChatMessage extends TextComponentBase {
        private final String text;

        public ChatMessage(String text) {
            Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
            Matcher matcher = pattern.matcher(text);
            StringBuffer buffer = new StringBuffer();

            while (matcher.find()) {
                matcher.appendReplacement(buffer, "\u00a7" + matcher.group().substring(1));
            }

            this.text = matcher.appendTail(buffer).toString();
        }

        @Override
        public String getUnformattedComponentText() {
            return text;
        }

        @Override
        public ITextComponent createCopy() {
            return new ChatMessage(this.text);
        }
    }
}