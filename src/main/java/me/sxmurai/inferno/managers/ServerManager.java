package me.sxmurai.inferno.managers;

import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerManager extends Feature {
    private float tps = 20.0f;
    private long lastUpdate = -1L;
    private final float[] counts = new float[20];

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof SPacketTimeUpdate) {
            float ticksPerSec;

            long currentTime = System.currentTimeMillis();
            if (this.lastUpdate == -1L) {
                this.lastUpdate = currentTime;
                return;
            }

            long diff = currentTime - this.lastUpdate;
            float time = diff / 20.0f;
            if (time == 0.0f) {
                time = 50.0f;
            }

            ticksPerSec = Math.min(1000.0f / time, 20.0f);

            System.arraycopy(this.counts, 0, this.counts, 1, this.counts.length - 1);
            this.counts[0] = ticksPerSec;

            float total = 0.0f;
            for (float f : this.counts) {
                total += f;
            }

            this.tps = Math.min(total / this.counts.length, 20.0f);
            this.lastUpdate = currentTime;
        }
    }

    public float getTps() {
        return tps;
    }
}
