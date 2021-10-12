package me.sxmurai.inferno.client.features.modules.render;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;

@Module.Define(name = "ViewClip", description = "Clips your camera through blocks", category = Module.Category.RENDER)
public class ViewClip extends Module {
    public static ViewClip INSTANCE;

    public final Value<Double> distance = new Value<>("Distance", 5.0, 1.0, 50.0);

    public ViewClip() {
        INSTANCE = this;
    }
}
