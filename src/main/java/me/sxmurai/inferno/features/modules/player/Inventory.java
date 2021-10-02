package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Inventory", description = "Does shit with ur inventory", category = Module.Category.PLAYER)
public class Inventory extends Module {
    public final Setting<Boolean> xCarry = new Setting<>("XCarry", false);
    public final Setting<Boolean> bypass = new Setting<>("Bypass", false);

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck()) {
            // @todo make a bypass
            if (event.getPacket() instanceof CPacketCloseWindow && xCarry.getValue()) {
                event.setCanceled(true);
            }

            if (event.getPacket() instanceof CPacketClickWindow && bypass.getValue() && mc.player.moveForward != 0.0f) {
                mc.player.setVelocity(0.0, 0.0, 0.0);
            }
        }
    }
}
