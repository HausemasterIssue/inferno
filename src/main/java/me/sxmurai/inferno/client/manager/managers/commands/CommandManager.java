package me.sxmurai.inferno.client.manager.managers.commands;

import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.features.commands.*;
import me.sxmurai.inferno.client.manager.AbstractManager;
import me.sxmurai.inferno.client.manager.managers.commands.exceptions.BaseException;
import me.sxmurai.inferno.client.manager.managers.commands.text.ChatColor;
import me.sxmurai.inferno.client.manager.managers.commands.text.TextBuilder;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class CommandManager extends AbstractManager<Command> {
    private String prefix = ",";

    public CommandManager() {
        this.items.add(new Bind());
        this.items.add(new Font());
        this.items.add(new Ping());
        this.items.add(new Unload());
        this.items.add(new XRay());

        Inferno.LOGGER.info("Loaded {} commands.", this.items.size());
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
        for (Command command : this.items) {
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
