package me.sxmurai.inferno.impl.features.module.modules.client;

import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.option.Option;
import me.sxmurai.inferno.impl.ui.InfernoGUI;
import org.lwjgl.input.Keyboard;

@Module.Define(name = "GUI", category = Module.Category.Client)
@Module.Info(description = "Displays the clients GUI", bind = Keyboard.KEY_R)
public class GUI extends Module {
    public static Option<Boolean> pause = new Option<>("Pause", false);

    @Override
    protected void onActivated() {
        if (!fullNullCheck()) {
            this.toggle();
            return;
        }

        mc.displayGuiScreen(InfernoGUI.getInstance());
    }

    @Override
    protected void onDeactivated() {
        if (fullNullCheck()) {
            mc.displayGuiScreen(null);
        }
    }
}
