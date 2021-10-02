package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.ColorUtils;

@Module.Define(name = "Chams", description = "Allows you to see entities through walls", category = Module.Category.RENDER)
public class Chams extends Module {
    public static Chams INSTANCE;

    public final Setting<Mode> mode = new Setting<>("Mode", Mode.NORMAL);
    public final Setting<ColorUtils.Color> color = new Setting<>("Color", new ColorUtils.Color(255, 0, 0), (v) -> mode.getValue() == Mode.WALLHACK);

    public Chams() {
        INSTANCE = this;
    }

    public enum Mode {
        NORMAL, WALLHACK
    }
}
