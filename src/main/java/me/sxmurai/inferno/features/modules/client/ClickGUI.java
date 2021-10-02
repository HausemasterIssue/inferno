package me.sxmurai.inferno.features.modules.client;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.gui.click.InfernoClickGUIScreen;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@Module.Define(name = "ClickGUI", description = "Does shit lol", category = Module.Category.CLIENT, bind = Keyboard.KEY_P)
public class ClickGUI extends Module {
    public static ClickGUI INSTANCE;

    public final Setting<Integer> scrollSpeed = new Setting<>("ScrollSpeed", 10, 1, 25);
    public final Setting<Boolean> pause = new Setting<>("Pause", false);

    public ClickGUI() {
        INSTANCE = this;
    }

    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            toggle();
            return;
        }

        mc.displayGuiScreen(InfernoClickGUIScreen.getInstance());
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (!(mc.currentScreen instanceof InfernoClickGUIScreen)) {
            toggle();
        }
    }
}
