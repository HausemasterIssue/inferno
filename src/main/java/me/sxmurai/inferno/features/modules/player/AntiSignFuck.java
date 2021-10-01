package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// @todo test
@Module.Define(name = "AntiSignFuck", description = "Attempts to stop you from getting kicked with the new sign kick method", category = Module.Category.PLAYER)
public class AntiSignFuck extends Module {
    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof SPacketBlockBreakAnim) {
            SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim) event.getPacket();
            Entity entity = mc.world.getEntityByID(packet.getBreakerId());

            if (entity instanceof EntityPlayer && entity != mc.player && mc.currentScreen instanceof GuiEditSign) {
                if (mc.player.getDistance(entity) <= 5.0f) {
                    mc.currentScreen.onGuiClosed();
                    mc.currentScreen = null;
                }
            }
        }
    }
}
