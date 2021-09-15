package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.features.settings.Setting;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// @todo: merge with norender once sxmurai adds it
@Module.Define(name = "BlockVision", description = "Removes the overlay when moving through liquids", category = Module.Category.RENDER)
public class BlockVision extends Module {
	
	@SubscribeEvent
	public void onRenderBlockOverlayEvent(RenderBlockOverlayEvent event) {
		if (event.getOverlayType() == OverlayType.BLOCK) {
			event.setCanceled(true);
		}
}
