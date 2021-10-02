package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

@Module.Define(name = "ViewClip", description = "Clips your camera through blocks", category = Module.Category.RENDER)
public class ViewClip extends Module {
    public static ViewClip INSTANCE;

    public final Setting<Double> distance = new Setting<>("Distance", 5.0, 1.0, 50.0);

    public ViewClip() {
        INSTANCE = this;
    }
}
