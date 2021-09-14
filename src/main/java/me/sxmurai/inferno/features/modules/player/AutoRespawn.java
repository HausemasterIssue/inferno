package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@Module.Define(name = "AutoRespawn", description = "Automatically respawns if you die")
public class AutoRespawn extends Module {
  
  @SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if (mc.player != null && mc.player.isDead) {
			mc.player.respawnPlayer();
		}
	}
}
