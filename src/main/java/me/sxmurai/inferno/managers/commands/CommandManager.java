package me.sxmurai.inferno.managers.commands;

import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.features.commands.Bind;
import me.sxmurai.inferno.features.commands.Font;
import me.sxmurai.inferno.features.commands.Ping;
import me.sxmurai.inferno.features.commands.Unload;
import me.sxmurai.inferno.managers.commands.exceptions.BaseException;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.managers.commands.text.TextBuilder;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends Feature {
    private final ArrayList<Command> commands = new ArrayList<>();
    private String prefix = ",";

    public CommandManager() {
        this.commands.add(new Bind());
        this.commands.add(new Font());
        this.commands.add(new Ping());
        this.commands.add(new Unload());
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!fullNullCheck() && event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet = (CPacketChatMessage) event.getPacket();
            if (packet.message.startsWith(this.prefix)) {
                event.setCanceled(true);
                this.handle(packet.message);
            }
        }
    }

    private void handle(String message) {
        List<String> args = Arrays.asList(message.substring(this.prefix.length()).trim().split(" "));
        if (!args.isEmpty()) {
            Command command = this.getCommand(args.get(0));
            if (command == null) {
                Command.send(ChatColor.Dark_Gray.text("That command wasn't recognized. Please run the help command."));
                return;
            }

            try {
                command.execute(args.subList(1, args.size()));
            } catch (Exception exception) {
                if (exception instanceof BaseException) {
                    Command.send(new TextBuilder()
                            .append(ChatColor.Dark_Gray, "There was an exception while running this command.")
                            .append("\n").append("\n")
                            .append(ChatColor.Red, ((BaseException) exception).getReason())
                    );
                } else {
                    Command.send(new TextBuilder()
                            .append(ChatColor.Dark_Gray, "There was an exception while running this command.")
                            .append("\n").append("\n")
                            .append(ChatColor.Red, exception.toString())
                    );
                }
            }
        }
    }

    private <T extends Command> T getCommand(String name) {
        for (Command command : this.commands) {
            if (command.getTriggers().stream().anyMatch((trigger) -> trigger.equalsIgnoreCase(name))) {
                return (T) command;
            }
        }

        return null;
    }

    public String getPrefix() {
        return prefix;
    }
}
