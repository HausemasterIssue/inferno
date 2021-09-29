package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.ColorUtils;

@Module.Define(name = "GlintColor", description = "Changes the color of armor enchants", category = Module.Category.RENDER)
public class GlintColor extends Module {
    public static GlintColor instance;

    public final Setting<Boolean> rainbow = this.register(new Setting<>("Rainbow", false));
    public final Setting<ColorUtils.Color> color = this.register(new Setting<>("Color", new ColorUtils.Color(255, 0, 0), (v) -> !rainbow.getValue()));

    public GlintColor() {
        instance = this;
    }
}
