package me.sxmurai.inferno.client.features.modules.client;

import me.sxmurai.inferno.client.features.gui.hud.HudEditorScreen;
import me.sxmurai.inferno.client.manager.managers.modules.Module;

@Module.Define(name = "HudEditor", description = "Opens the HUD editor", category = Module.Category.CLIENT)
public class HudEditor extends Module {
    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            this.toggle();
            return;
        }

        mc.displayGuiScreen(HudEditorScreen.getInstance());
    }

    @Override
    public void onUpdate() {
        if (!(mc.currentScreen instanceof HudEditorScreen)) {
            this.toggle();
        }
    }
}
