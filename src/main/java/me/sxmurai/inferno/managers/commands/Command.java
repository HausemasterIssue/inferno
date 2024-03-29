package me.sxmurai.inferno.managers.commands;

import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.managers.commands.text.TextBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

public abstract class Command extends Feature {
    private final List<String> triggers;
    private final String description;

    public Command() {
        Define definition = this.getClass().getDeclaredAnnotation(Define.class);

        this.triggers = Arrays.asList(definition.handles());
        this.description = definition.description();
    }

    public abstract void execute(List<String> args) throws Exception;

    public List<String> getTriggers() {
        return triggers;
    }

    public String getDescription() {
        return description;
    }

    public static void send(String text) {
        mc.player.sendMessage(new TextBuilder.ChatMessage(
                ChatColor.Red.text("<Inferno>") +
                        " " +
                        ChatColor.Dark_Gray.text("\u00BB") +
                        " " +
                        text
        ));
    }

    public static void send(TextBuilder builder) {
        send(builder.build());
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Define {
        String[] handles();
        String description() default "No description provided";
    }
}
