package me.sxmurai.inferno.client.features.modules.client;

import me.sxmurai.inferno.api.utils.ColorUtils;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;

import java.awt.*;

@Module.Define(name = "Colors", description = "Syncs colors throughout the client", category = Module.Category.CLIENT)
public class Colors extends Module {
    public static Colors INSTANCE;

    public final Value<Boolean> rainbow = new Value<>("Rainbow", false);
    public final Value<Double> delay = new Value<>("Delay", 1.0, 0.0, 10.0, (v) -> rainbow.getValue());
    public final Value<Float> saturation = new Value<>("Saturation", 0.5f, 0.1f, 1.0f, (v) -> rainbow.getValue());
    public final Value<Float> brightness = new Value<>("Brightness", 1.0f, 0.1f, 1.0f, (v) -> rainbow.getValue());

    public final Value<ColorUtils.Color> color = new Value<>("Color", new ColorUtils.Color(255, 255, 255), (v) -> !rainbow.getValue());

    public Colors() {
        INSTANCE = this;
    }

    public static int color() {
        ColorUtils.Color c = INSTANCE.color.getValue();
        return INSTANCE.rainbow.getValue() ? rainbow(INSTANCE.delay.getValue() * 300.0) : ColorUtils.toRGBA(c.red, c.green, c.blue, 255);
    }

    public static int rainbow(double delay) {
        double state = Math.ceil((System.currentTimeMillis() + delay) / 20.0) % 360;
        return Color.getHSBColor((float) (state / 360.0f), INSTANCE.saturation.getValue(), INSTANCE.brightness.getValue()).getRGB();
    }
}
