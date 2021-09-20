package me.sxmurai.inferno.features.commands;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.EnumConverter;
import me.sxmurai.inferno.managers.commands.Command;
import me.sxmurai.inferno.managers.commands.exceptions.InvalidArgumentException;
import me.sxmurai.inferno.managers.commands.exceptions.InvalidUsageException;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.managers.commands.text.TextBuilder;
import me.sxmurai.inferno.managers.modules.Module;
import org.lwjgl.input.Keyboard;

import java.util.List;

@Command.Define(handles = {"bind", "b", "keybind", "key"}, description = "Sets a bind for a module")
public class Bind extends Command {
    @Override
    public void execute(List<String> args) throws Exception {
        if (args.isEmpty()) {
            throw new InvalidUsageException("You need to provide a module name");
        }

        Module module = Inferno.moduleManager.getModule(args.get(0));
        if (module == null) {
            throw new InvalidArgumentException("Couldn't find that module.");
        }

        if (!Command.containsArg(args, 1)) {
            throw new InvalidUsageException("You need to provide a key name");
        }

        module.setBind(Keyboard.getKeyIndex(args.get(1).toUpperCase()));

        Command.send(new TextBuilder()
                .append(ChatColor.Dark_Gray, "Set the module")
                .append(" ")
                .append(ChatColor.Red, module.getName())
                .append(ChatColor.Dark_Gray, "'s bind to")
                .append(" ")
                .append(ChatColor.Red, EnumConverter.getActualName(Keyboard.getKeyName(module.getBind())))
                .append(ChatColor.Dark_Gray, ".")
        );
    }
}
