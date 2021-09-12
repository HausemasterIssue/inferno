package me.sxmurai.inferno.managers.commands;

import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.features.commands.Ping;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends Feature {
    private final ArrayList<Command> commands = new ArrayList<>();
    private String prefix = ",";

    public CommandManager() {
        this.commands.add(new Ping());
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
                // @todo
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
