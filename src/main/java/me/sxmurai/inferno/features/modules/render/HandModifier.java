package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.ColorUtils;

@Module.Define(name = "HandModifier", description = "Changes hand rendering", category = Module.Category.RENDER)
public class HandModifier extends Module {
    public static HandModifier INSTANCE;

    public final Setting<Chams> chams = new Setting<>("Chams", Chams.NONE);
    public final Setting<Float> lineWidth = new Setting<>("LineWidth", 1.0f, 0.1f, 5.0f, (v) -> chams.getValue() == Chams.WIREFRAME);
    public final Setting<ColorUtils.Color> color = new Setting<>("Color", new ColorUtils.Color(0, 255, 0, 80), (v) -> chams.getValue() != Chams.NONE);

    // translations
    public final Setting<Float> translatedX = new Setting<>("TranslatedX", 0.0f, -2.0f, 2.0f);
    public final Setting<Float> translatedY = new Setting<>("TranslatedY", 0.0f, -2.0f, 2.0f);
    public final Setting<Float> translatedZ = new Setting<>("TranslatedZ", 0.0f, -2.0f, 2.0f);

    // scaling
    public final Setting<Float> scaledX = new Setting<>("ScaledX", 1.0f, 0.0f, 2.0f);
    public final Setting<Float> scaledY = new Setting<>("ScaledY", 1.0f, 0.0f, 2.0f);
    public final Setting<Float> scaledZ = new Setting<>("ScaledZ", 1.0f, 0.0f, 2.0f);

    // rotations
    public final Setting<Float> rotatedX = new Setting<>("RotatedX", 0.0f, -100.0f, 100.0f);
    public final Setting<Float> rotatedY = new Setting<>("RotatedY", 0.0f, -100.0f, 100.0f);
    public final Setting<Float> rotatedZ = new Setting<>("RotatedZ", 0.0f, -100.0f, 100.0f);

    public HandModifier() {
        INSTANCE = this;
    }

    public enum Chams {
        NONE,
        COLORED,
        WIREFRAME
    }
}
