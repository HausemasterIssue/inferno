package me.sxmurai.inferno.client.features.modules.miscellaneous;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.api.utils.timing.Timer;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "PingSpoof", description = "Makes your ping higher, while still being able to play as if you were on lower ping")
public class PingSpoof extends Module {
    public final Value<Double> delay = new Value<>("Delay", 0.2, 0.1, 5.0);

    private final Queue<CPacketKeepAlive> packets = new ConcurrentLinkedQueue<>();
    private final Timer timer = new Timer();
    private boolean pause = false;

    @Override
    public void onUpdate() {
        if (this.timer.passedS(this.delay.getValue())) {
            this.timer.reset();
            this.pause = true;

            this.emptyQueue();

            this.pause = false;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (Module.fullNullCheck() && event.getPacket() instanceof CPacketKeepAlive && !this.pause) {
            this.packets.add((CPacketKeepAlive) event.getPacket());
            event.setCanceled(true);
        }
    }

    private void emptyQueue() {
        while (!this.packets.isEmpty()) {
            CPacketKeepAlive packet = this.packets.poll();
            if (packet == null) {
                break;
            }

            mc.player.connection.sendPacket(packet);
        }
    }
}
