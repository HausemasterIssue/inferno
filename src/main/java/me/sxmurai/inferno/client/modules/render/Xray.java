package me.sxmurai.inferno.client.modules.render;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;

@Module.Define(name = "Xray", description = "Lets you see through shit", category = Module.Category.RENDER)
public class Xray extends Module {
    public static Xray INSTANCE;

    public final Value<Mode> mode = new Value<>("Mode", Mode.NORMAL);
    public final Value<Float> opacity = new Value<>("Opacity", 120.0f, 0.0f, 255.0f, (v) -> mode.getValue() == Mode.WALLHACK);

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
