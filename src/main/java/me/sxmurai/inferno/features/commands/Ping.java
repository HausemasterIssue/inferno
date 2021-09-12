package me.sxmurai.inferno.features.commands;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.managers.commands.Command;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.managers.commands.text.TextBuilder;

import java.util.List;

@Command.Define(handles = {"ping", "pong", "p"}, description = "Displays your latency to the server")
public class Ping extends Command {
    @Override
    public void execute(List<String> args) throws Exception {
        Command.send(new TextBuilder()
                .append(ChatColor.Dark_Gray, "Your latency to the server is")
                .append(" ")
                .append(ChatColor.Red, String.valueOf(Inferno.serverManager.getLatency()))
                .append(ChatColor.Red, "ms")
                .append(ChatColor.Dark_Gray, ".")
        );
    }
}
