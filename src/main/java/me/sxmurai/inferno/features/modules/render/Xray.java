package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

@Module.Define(name = "Xray", description = "Lets you see through shit", category = Module.Category.RENDER)
public class Xray extends Module {
    public static Xray INSTANCE;

    public final Setting<Mode> mode = new Setting<>("Mode", Mode.NORMAL);
    public final Setting<Float> opacity = new Setting<>("Opacity", 120.0f, 0.0f, 255.0f, (v) -> mode.getValue() == Mode.WALLHACK);

    public Xray() {
        INSTANCE = this;
    }

    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            this.toggle();
            return;
        }

        mc.renderGlobal.loadRenderers();
    }

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck()) {
            mc.renderGlobal.loadRenderers();
        }
    }

    public enum Mode {
        NORMAL, WALLHACK
    }
}
