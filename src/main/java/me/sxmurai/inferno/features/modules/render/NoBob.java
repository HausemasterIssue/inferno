package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

@Module.Define(name = "NoBob", description = "The best module to ever exist")
public class NoBob extends Module {
	 
	public void onUpdate() {
		mc.player.distanceWalkedModified = 4.0f;
	}

}
