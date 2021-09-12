package me.sxmurai.inferno.features.modules.client;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.gui.hud.HUDEditorGuiScreen;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "HudEditor", description = "Opens the HUD editor", category = Module.Category.CLIENT)
public class HudEditor extends Module {
    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            this.toggle();
            return;
        }

        mc.displayGuiScreen(HUDEditorGuiScreen.getInstance());
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (!(mc.currentScreen instanceof HUDEditorGuiScreen)) {
            this.toggle();
        }
    }
}
