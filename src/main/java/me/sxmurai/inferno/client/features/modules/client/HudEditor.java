package me.sxmurai.inferno.client.features.modules.client;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.client.features.gui.hud.HudEditorScreen;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (!(mc.currentScreen instanceof HudEditorScreen)) {
            this.toggle();
        }
    }
}
