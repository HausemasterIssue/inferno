package me.sxmurai.inferno.managers;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.RotationUtils;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RotationManager extends Feature {
    private final Queue<RotationUtils.Rotation> queue = new ConcurrentLinkedQueue<>();

    private RotationUtils.Rotation server;
    private RotationUtils.Rotation current;

    private int ticks = 0;

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        ++this.ticks;
        if (this.ticks >= 2) {
            this.ticks = 0;

            if (!this.queue.isEmpty()) {
                this.current = this.queue.poll();
                if (this.current == null) {
                    return;
                }

                mc.player.rotationYawHead = this.current.getYaw();
                mc.player.renderYawOffset = this.current.getYaw();

                mc.player.connection.getNetworkManager().dispatchPacket(new CPacketPlayer.Rotation(this.current.getYaw(), this.current.getPitch(), mc.player.onGround), null);
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck() && this.current != null && !this.queue.isEmpty() && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            if (packet.rotating) {
                this.queue.add(new RotationUtils.Rotation(packet.yaw, packet.pitch));
                event.setCanceled(true);
            }
        }
    }
}
