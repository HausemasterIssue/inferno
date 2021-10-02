package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

@Module.Define(name = "ViewClip", description = "Clips your camera through blocks", category = Module.Category.RENDER)
public class ViewClip extends Module {
    public static ViewClip INSTANCE;

    public final Setting<Float> distance = new Setting<>("Distance", 5.0f, 1.0f, 50.0f);

    public ViewClip() {
        INSTANCE = this;
    }
}
