package me.sxmurai.inferno.client.modules.player;

import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Inventory", description = "Does shit with ur inventory", category = Module.Category.PLAYER)
public class Inventory extends Module {
    public final Value<Boolean> xCarry = new Value<>("XCarry", false);
    public final Value<Boolean> bypass = new Value<>("Bypass", false);

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
