package me.sxmurai.inferno.client.features.modules.render;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.ColorUtils;

@Module.Define(name = "Chams", description = "Allows you to see entities through walls", category = Module.Category.RENDER)
public class Chams extends Module {
    public static Chams INSTANCE;

    public final Value<Mode> mode = new Value<>("Mode", Mode.NORMAL);
    public final Value<ColorUtils.Color> color = new Value<>("Color", new ColorUtils.Color(255, 0, 0), (v) -> mode.getValue() == Mode.WALLHACK);

    public Chams() {
        INSTANCE = this;
    }

    public enum Mode {
        NORMAL, WALLHACK
    }
}
