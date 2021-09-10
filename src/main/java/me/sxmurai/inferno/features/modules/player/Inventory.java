package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Inventory", description = "Does shit with ur inventory", category = Module.Category.PLAYER)
public class Inventory extends Module {
    public final Setting<Boolean> xcarry = this.register(new Setting<>("XCarry", false));
    public final Setting<Boolean> bypass = this.register(new Setting<>("Bypass", false)); // this is for 2b2t, stops movement when a CPacketHeldItemChange packet is sent

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck()) {
            // @todo make a bypass
            if (event.getPacket() instanceof SPacketCloseWindow && xcarry.getValue()) {
                event.setCanceled(true);
            }

            if (event.getPacket() instanceof CPacketClickWindow && bypass.getValue() && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
                mc.player.setVelocity(0.0, 0.0, 0.0);
            }
        }
    }
}
