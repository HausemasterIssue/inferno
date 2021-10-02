package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

@Module.Define(name = "ExtraTab", description = "Overrides the vanilla player list limit in the tab gui overlay")
public class ExtraTab extends Module {
    public static ExtraTab INSTANCE;

    // 80 is the default according to the decompiled code
    public final Setting<Integer> players = new Setting<>("Players", 80, 1, 1000);

    public ExtraTab() {
        INSTANCE = this;
    }
}
