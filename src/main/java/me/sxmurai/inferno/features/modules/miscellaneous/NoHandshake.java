package me.sxmurai.inferno.features.modules.miscellaneous;

import io.netty.buffer.Unpooled;
import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

@Module.Define(name = "NoHandshake", description = "Stops forge from becoming 6ix9ine")
public class NoHandshake extends Module {
    public final Setting<Brand> brand = this.register(new Setting<>("Brand", Brand.FORGE));

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck()) {
            if (event.getPacket() instanceof FMLProxyPacket && !mc.isSingleplayer()) {
                event.setCanceled(true);
            }

            if (event.getPacket() instanceof CPacketCustomPayload && this.brand.getValue() != Brand.FORGE) {
                CPacketCustomPayload packet = (CPacketCustomPayload) event.getPacket();
                if (packet.channel.equalsIgnoreCase("MC|Brand")) {
                    packet.data = new PacketBuffer(Unpooled.buffer()).writeString(this.brand.getValue().brand);
                }
            }
        }
    }

    public enum Brand {
        FORGE(null),
        VANILLA("vanilla"),
        LUNAR("Lunar-Client");

        private final String brand;
        Brand(String brand) {
            this.brand = brand;
        }
    }
}
