package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

@Module.Define(name = "TotemPopNotifier", description = "Notifies you how many totems someone pops", category = Module.Category.COMBAT)
public class TotemPopNotifier extends Module {
    public static TotemPopNotifier INSTANCE;

    public final Setting<Float> delay = new Setting<>("Delay", 2.5f, 0.0f, 10.0f);
    public final Setting<Boolean> clearOnLog = new Setting<>("ClearOnLogout", false);

    public TotemPopNotifier() {
        INSTANCE = this;
    }
}
