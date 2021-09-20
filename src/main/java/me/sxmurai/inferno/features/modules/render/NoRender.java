package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

@Module.Define(name = "NoRender", description = "Stops shit from rendering", category = Module.Category.RENDER)
public class NoRender extends Module {
    public static NoRender INSTANCE;

    public final Setting<Boolean> hurtcam = this.register(new Setting<>("Hurtcam", false));
    public final Setting<Boolean> weather = this.register(new Setting<>("Weather", false));
    public final Setting<Boolean> fireOverlay = this.register(new Setting<>("FireOverlay", false));
    public final Setting<Boolean> pumpkinOverlay = this.register(new Setting<>("PumpkinOverlay", false));
    public final Setting<Boolean> scoreboard = this.register(new Setting<>("Scoreboard", false));
    public final Setting<Boolean> particles = this.register(new Setting<>("Particles", false));
    public final Setting<Boolean> totemAnimation = this.register(new Setting<>("TotemAnimation", false));

    public NoRender() {
        INSTANCE = this;
    }
}
