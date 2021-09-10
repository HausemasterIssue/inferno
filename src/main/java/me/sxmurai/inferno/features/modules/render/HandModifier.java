package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.ColorUtils;

@Module.Define(name = "HandModifier", description = "Changes hand rendering", category = Module.Category.RENDER)
public class HandModifier extends Module {
    public static HandModifier INSTANCE;

    public final Setting<Chams> chams = this.register(new Setting<>("Chams", Chams.NONE));
    public final Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 1.0f, 0.1f, 5.0f, (v) -> chams.getValue() == Chams.WIREFRAME));
    public final Setting<ColorUtils.Color> color = this.register(new Setting<>("Color", new ColorUtils.Color(0, 255, 0, 80), (v) -> chams.getValue() != Chams.NONE));

    // translations
    public final Setting<Float> translatedX = this.register(new Setting<>("TranslatedX", 0.0f, -2.0f, 2.0f));
    public final Setting<Float> translatedY = this.register(new Setting<>("TranslatedY", 0.0f, -2.0f, 2.0f));
    public final Setting<Float> translatedZ = this.register(new Setting<>("TranslatedZ", 0.0f, -2.0f, 2.0f));

    // scaling
    public final Setting<Float> scaledX = this.register(new Setting<>("ScaledX", 1.0f, 0.0f, 2.0f));
    public final Setting<Float> scaledY = this.register(new Setting<>("ScaledY", 1.0f, 0.0f, 2.0f));
    public final Setting<Float> scaledZ = this.register(new Setting<>("ScaledZ", 1.0f, 0.0f, 2.0f));

    // rotations
    public final Setting<Float> rotatedX = this.register(new Setting<>("RotatedX", 0.0f, -100.0f, 100.0f));
    public final Setting<Float> rotatedY = this.register(new Setting<>("RotatedY", 0.0f, -100.0f, 100.0f));
    public final Setting<Float> rotatedZ = this.register(new Setting<>("RotatedZ", 0.0f, -100.0f, 100.0f));

    public HandModifier() {
        INSTANCE = this;
    }

    public enum Chams {
        NONE,
        COLORED,
        WIREFRAME
    }
}
