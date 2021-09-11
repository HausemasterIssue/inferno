package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "MountBypass", description = "Attempts to bypass servers that prevent you from riding chested animals")
public class MountBypass extends Module {
    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getEntityFromWorld(mc.world) instanceof AbstractHorse && packet.action == CPacketUseEntity.Action.INTERACT) {
                event.setCanceled(true);
            }
        }
    }
}
