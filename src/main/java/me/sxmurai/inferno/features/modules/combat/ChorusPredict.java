package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.managers.commands.Command;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.managers.commands.text.TextBuilder;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "ChorusPredict", description = "Tells you where someone chorused out of", category = Module.Category.COMBAT)
public class ChorusPredict extends Module {
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT) {
                Command.send(new TextBuilder()
                        .append(ChatColor.Dark_Gray, "A player chorused to")
                        .append(" ")
                        .append(ChatColor.Red, "X: ")
                        .append(ChatColor.Red, String.valueOf(packet.getX()))
                        .append(ChatColor.Dark_Gray, ", ")
                        .append(ChatColor.Red, "Y: ")
                        .append(ChatColor.Red, String.valueOf(packet.getY()))
                        .append(ChatColor.Dark_Gray, ", ")
                        .append(ChatColor.Red, String.valueOf(packet.getZ()))
                        .append(ChatColor.Dark_Gray, ".")
                );
            }
        }
    }
}
