package me.sxmurai.inferno.features.commands;

import me.sxmurai.inferno.features.gui.font.CustomFontRenderer;
import me.sxmurai.inferno.features.modules.client.CustomFont;
import me.sxmurai.inferno.managers.TextManager;
import me.sxmurai.inferno.managers.commands.Command;
import me.sxmurai.inferno.managers.commands.exceptions.InvalidUsageException;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.managers.commands.text.TextBuilder;

import java.awt.*;
import java.util.List;

@Command.Define(handles = {"font", "f"}, description = "Changes the clients font")
public class Font extends Command {
    @Override
    public void execute(List<String> args) throws Exception {
        if (args.isEmpty()) {
            throw new InvalidUsageException("Please provide a font name.");
        }

        String name = String.join(" ", args);
        for (java.awt.Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
            if (!font.getName().equalsIgnoreCase(name)) {
                continue;
            }

            CustomFont.INSTANCE.font.setValue(font.getName());

            Command.send(new TextBuilder()
                    .append(ChatColor.Dark_Gray, "Changed font to")
                    .append(" ")
                    .append(ChatColor.Red, font.getName())
                    .append(ChatColor.Dark_Gray, ".")
            );
        }
    }
}