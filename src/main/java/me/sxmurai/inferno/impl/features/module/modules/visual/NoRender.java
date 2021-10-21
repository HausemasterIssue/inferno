package me.sxmurai.inferno.impl.features.module.modules.visual;

import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.option.Option;

@Module.Define(name = "NoRender", category = Module.Category.Visual)
@Module.Info(description = "Stops things from rendering")
public class NoRender extends Module {
    public static NoRender INSTANCE;

    public static Option<Boolean> hurtcam = new Option<>("Hurtcam", false);
    public static Option<Boolean> fire = new Option<>("Fire", false);
    public static Option<Boolean> particles = new Option<>("Particles", false);
    public static Option<Boolean> totems = new Option<>("Totems", false);
    public static Option<Boolean> blocks = new Option<>("Blocks", false);
    public static Option<Boolean> weather = new Option<>("Weather", false);
    public static Option<Boolean> fog = new Option<>("Fog", false);
    public static Option<Boolean> pumpkin = new Option<>("Pumpkin", false);
    public static Option<Boolean> potions = new Option<>("Potions", false);
    public static Option<Boolean> scoreboard = new Option<>("Scoreboard", false);
    public static Option<Boolean> advancements = new Option<>("Advancements", false);
    public static Option<Boolean> xp = new Option<>("XP", false);
    public static Option<Boolean> portals = new Option<>("Portals", false);
    public static Option<Boolean> fov = new Option<>("FOV", false);
    public static Option<Armor> armor = new Option<>("Armor", Armor.None);
    public static Option<Bossbar> bossbar = new Option<>("Bossbar", Bossbar.None);

    public NoRender() {
        INSTANCE = this;
    }

    public enum Armor {
        None, Glint, Remove
    }

    public enum Bossbar {
        None, Remove, Stack
    }
}
