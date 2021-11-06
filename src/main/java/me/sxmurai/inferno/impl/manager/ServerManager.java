package me.sxmurai.inferno.impl.manager;

import me.sxmurai.inferno.impl.event.network.PacketEvent;
import me.sxmurai.inferno.impl.features.Wrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

public class ServerManager implements Wrapper {
    private final double[] previous = new double[20];
    private double tps = 20.0;
    private long time = -1L;

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            long currentTime = System.currentTimeMillis();
            if (this.time == -1L) {
                this.time = currentTime;
                return;
            }

            double time = (currentTime - this.time) / 20.0;
            if (time == 0.0) {
                time = 50.0; // 50ms = 1 tick
            }

            System.arraycopy(this.previous, 0, this.previous, 1, this.previous.length - 1);
            this.previous[0] = Math.min(1000.0 / time, 20.0);

            double average = Arrays.stream(this.previous).reduce(Double::sum).getAsDouble();
            this.tps = Math.min(average / this.previous.length, 20.0);
            this.time = currentTime;
        }
    }

    public double getTps() {
        return this.tps;
    }

    public int getSelfLatency() {
        return this.getLatency(mc.player);
    }

    public int getLatency(EntityPlayer player) {
        try {
            return mc.player.connection.getPlayerInfo(player.getUniqueID()).getResponseTime();
        } catch (Exception exception) {
            return 0;
        }
    }
}
