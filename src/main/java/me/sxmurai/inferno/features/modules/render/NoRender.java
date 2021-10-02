package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

@Module.Define(name = "NoRender", description = "Stops shit from rendering", category = Module.Category.RENDER)
public class NoRender extends Module {
    public static NoRender INSTANCE;

    public final Setting<Boolean> hurtcam = new Setting<>("Hurtcam", false);
    public final Setting<Boolean> weather = new Setting<>("Weather", false);
    public final Setting<Boolean> fireOverlay = new Setting<>("FireOverlay", false);
    public final Setting<Boolean> pumpkinOverlay = new Setting<>("PumpkinOverlay", false);
    public final Setting<Boolean> scoreboard = new Setting<>("Scoreboard", false);
    public final Setting<Boolean> particles = new Setting<>("Particles", false);
    public final Setting<Boolean> totemAnimation = new Setting<>("TotemAnimation", false);

    public NoRender() {
        INSTANCE = this;
    }
}
