package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

@Module.Define(name = "Reach", description = "Reaches further", category = Module.Category.PLAYER)
public class Reach extends Module {
    public static Reach INSTANCE;

    public final Setting<Float> distance = this.register(new Setting<>("Distance", 4.5f, 4.5f, 15.0f));

    public Reach() {
        INSTANCE = this;
    }
}
