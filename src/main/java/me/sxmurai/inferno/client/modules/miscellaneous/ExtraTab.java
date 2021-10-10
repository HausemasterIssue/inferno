package me.sxmurai.inferno.client.modules.miscellaneous;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;

@Module.Define(name = "ExtraTab", description = "Overrides the vanilla player list limit in the tab gui overlay")
public class ExtraTab extends Module {
    public static ExtraTab INSTANCE;

    // 80 is the default according to the decompiled code
    public final Value<Integer> players = new Value<>("Players", 80, 1, 1000);
    public final Value<Boolean> friendHighlight = new Value<>("FriendHighlight", true);

    public ExtraTab() {
        INSTANCE = this;
    }
}
