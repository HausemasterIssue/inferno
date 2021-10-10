package me.sxmurai.inferno.client.modules.client;

import me.sxmurai.inferno.client.gui.hud.HUDComponent;
import me.sxmurai.inferno.client.gui.hud.HUDEditorGuiScreen;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "HUD", description = "Renders the clients HUD", category = Module.Category.CLIENT)
public class HUD extends Module {
    @SubscribeEvent
    public void onHudRender(RenderGameOverlayEvent.Text event) {
        GlStateManager.pushMatrix();

        for (HUDComponent component : HUDEditorGuiScreen.getInstance().getComponents()) {
            if (component.isHidden()) {
                continue;
            }

            component.update(event.getResolution());
            component.draw();
        }

        GlStateManager.popMatrix();
    }
}
