package me.sxmurai.inferno.client.modules.render;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;

@Module.Define(name = "NoRender", description = "Stops shit from rendering", category = Module.Category.RENDER)
public class NoRender extends Module {
    public static NoRender INSTANCE;

    public final Value<Boolean> hurtcam = new Value<>("Hurtcam", false);
    public final Value<Boolean> weather = new Value<>("Weather", false);
    public final Value<Boolean> fireOverlay = new Value<>("FireOverlay", false);
    public final Value<Boolean> pumpkinOverlay = new Value<>("PumpkinOverlay", false);
    public final Value<Boolean> scoreboard = new Value<>("Scoreboard", false);
    public final Value<Boolean> particles = new Value<>("Particles", false);
    public final Value<Boolean> totemAnimation = new Value<>("TotemAnimation", false);
    public final Value<Armor> armor = new Value<>("Armor", Armor.NONE);
    public final Value<Boolean> signText = new Value<>("SignText", false);

    public NoRender() {
        INSTANCE = this;
    }

    public enum Armor {
        NONE, ALL, GLINT
    }
}
