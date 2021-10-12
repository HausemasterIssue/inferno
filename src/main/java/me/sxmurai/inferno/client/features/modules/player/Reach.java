package me.sxmurai.inferno.client.features.modules.player;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;

@Module.Define(name = "Reach", description = "Reaches further", category = Module.Category.PLAYER)
public class Reach extends Module {
    public static Reach INSTANCE;

    public final Value<Float> distance = new Value<>("Distance", 4.5f, 4.5f, 15.0f);

    public Reach() {
        INSTANCE = this;
    }
}
