package me.sxmurai.inferno.client.modules.miscellaneous;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;

@Module.Define(name = "NoEntityTrace", description = "Allows you to mine blocks through entities")
public class NoEntityTrace extends Module {
    public static NoEntityTrace INSTANCE;

    public final Value<Boolean> pickaxe = new Value<>("Pickaxe", true);
    public final Value<Boolean> mining = new Value<>("Mining", true);

    public NoEntityTrace() {
        INSTANCE = this;
    }
}
