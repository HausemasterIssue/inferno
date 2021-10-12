package me.sxmurai.inferno.client.features.modules.render;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.ColorUtils;

@Module.Define(name = "HandModifier", description = "Changes hand rendering", category = Module.Category.RENDER)
public class HandModifier extends Module {
    public static HandModifier INSTANCE;

    public final Value<Chams> chams = new Value<>("Chams", Chams.NONE);
    public final Value<Float> lineWidth = new Value<>("LineWidth", 1.0f, 0.1f, 5.0f, (v) -> chams.getValue() == Chams.WIREFRAME);
    public final Value<ColorUtils.Color> color = new Value<>("Color", new ColorUtils.Color(0, 255, 0, 80), (v) -> chams.getValue() != Chams.NONE);

    // translations
    public final Value<Float> translatedX = new Value<>("TranslatedX", 0.0f, -2.0f, 2.0f);
    public final Value<Float> translatedY = new Value<>("TranslatedY", 0.0f, -2.0f, 2.0f);
    public final Value<Float> translatedZ = new Value<>("TranslatedZ", 0.0f, -2.0f, 2.0f);

    // scaling
    public final Value<Float> scaledX = new Value<>("ScaledX", 1.0f, 0.0f, 2.0f);
    public final Value<Float> scaledY = new Value<>("ScaledY", 1.0f, 0.0f, 2.0f);
    public final Value<Float> scaledZ = new Value<>("ScaledZ", 1.0f, 0.0f, 2.0f);

    // rotations
    public final Value<Float> rotatedX = new Value<>("RotatedX", 0.0f, -100.0f, 100.0f);
    public final Value<Float> rotatedY = new Value<>("RotatedY", 0.0f, -100.0f, 100.0f);
    public final Value<Float> rotatedZ = new Value<>("RotatedZ", 0.0f, -100.0f, 100.0f);

    public HandModifier() {
        INSTANCE = this;
    }

    public enum Chams {
        NONE,
        COLORED,
        WIREFRAME
    }
}
