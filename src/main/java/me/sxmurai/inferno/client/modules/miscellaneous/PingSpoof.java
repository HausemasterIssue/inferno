package me.sxmurai.inferno.client.modules.miscellaneous;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.timing.Timer;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "PingSpoof", description = "Spoofs ur ping lol")
public class PingSpoof extends Module {
    public final Value<Float> delay = new Value<>("Delay", 100.0f, 0.0f, 3000.0f);

    private final Queue<CPacketKeepAlive> packets = new ConcurrentLinkedQueue<>();
    private boolean sending = false;
    private final Timer timer = new Timer();

    @Override
    protected void onDeactivated() {
        if (Module.fullNullCheck()) {
            packets.clear();
        } else {
            emptyKeepAlives();
        }

        timer.reset();
        sending = false;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (!Module.fullNullCheck()) {
            if (timer.passedMs(delay.getValue().longValue())) {
                emptyKeepAlives();
            }
        } else {
            toggle();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof CPacketKeepAlive && !sending) {
            packets.add((CPacketKeepAlive) event.getPacket());
            event.setCanceled(true);
        }
    }

    private void emptyKeepAlives() {
        sending = true;

        while (!packets.isEmpty()) {
            CPacketKeepAlive packet = packets.poll();
            if (Module.fullNullCheck() || packet == null) {
                break;
            }

            mc.player.connection.getNetworkManager().dispatchPacket(packet, null);
        }

        sending = false;
    }
}
