package me.sxmurai.inferno.client.features.modules.client;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.features.gui.click.InfernoClickGUIScreen;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@Module.Define(name = "ClickGUI", description = "Does shit lol", category = Module.Category.CLIENT, bind = Keyboard.KEY_P)
public class ClickGUI extends Module {
    public static ClickGUI INSTANCE;

    public final Value<Integer> scrollSpeed = new Value<>("ScrollSpeed", 10, 1, 25);
    public final Value<Boolean> pause = new Value<>("Pause", false);

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
