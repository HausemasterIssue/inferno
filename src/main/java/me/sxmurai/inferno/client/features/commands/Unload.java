package me.sxmurai.inferno.client.features.commands;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.manager.managers.commands.Command;

import java.util.List;

@Command.Define(handles = {"unload", "un", "ul"}, description = "Unloads Inferno")
public class Unload extends Command {
    @Override
    public void execute(List<String> args) throws Exception {
        Command.send("Unloading " + Inferno.MOD_NAME + "...");
        Inferno.unload();
        Command.send("Unloaded " + Inferno.MOD_NAME + "!");
    }
}
