package me.sxmurai.inferno.client.features.modules.client;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.manager.managers.hud.HudComponent;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "HUD", description = "Renders the clients HUD", category = Module.Category.CLIENT)
public class HUD extends Module {
    @SubscribeEvent
    public void onHudRender(RenderGameOverlayEvent.Text event) {
        for (HudComponent component : Inferno.hudManager.getComponents()) {
            if (!component.isVisible()) {
                continue;
            }

            GlStateManager.pushMatrix();
            component.draw();
            component.update(event.getResolution());
            GlStateManager.popMatrix();
        }
    }
}
