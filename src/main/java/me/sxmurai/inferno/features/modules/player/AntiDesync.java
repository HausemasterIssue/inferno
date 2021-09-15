package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.utils.Wrapper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Iterator;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.network.play.server.SPacketSoundEffect;


@Module.Define(name = "AntiDesync", description = "Prevents you from getting desynced on servers", category = Module.Category.PLAYER)
public class AntiDesync extends Module {
	
	 @SubscribeEvent
	    public void onPacketReceive(final PacketEvent event) {
	        if (event.getPacket() instanceof SPacketSoundEffect) {
	            final SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
	            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
	                try {
	                    for (final Entity e : Wrapper.getWorld().loadedEntityList) {
	                        if (e instanceof EntityEnderCrystal && e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0) {
	                            e.setDead();
	                        }
	                    }
	                }
	                catch (Exception e2) {
	                    e2.printStackTrace();
	                }
	            }
	        }
	    }
	
}
