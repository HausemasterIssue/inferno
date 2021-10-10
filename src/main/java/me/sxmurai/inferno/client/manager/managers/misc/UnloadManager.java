package me.sxmurai.inferno.client.manager.managers.misc;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.client.manager.Manager;
import me.sxmurai.inferno.client.manager.managers.commands.Command;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UnloadManager extends Manager {
    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (Inferno.state == Inferno.State.UNLOADED && event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet = (CPacketChatMessage) event.getPacket();
            if (packet.message.equalsIgnoreCase(",reload")) {
                Command.send("Reloading " + Inferno.MOD_NAME + " v" + Inferno.MOD_VER + "...");
                Inferno.load();
                Command.send("Loaded! Welcome back to " + Inferno.MOD_NAME + "!");

                event.setCanceled(true);
            }
        }
    }
}
