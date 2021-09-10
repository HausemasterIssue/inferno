package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.managers.modules.Module;

@Module.Define(name = "MultiTask", description = "Allows you to eat and do shit at the same time", category = Module.Category.PLAYER)
public class MultiTask extends Module {
    public static MultiTask INSTANCE;

    public MultiTask() {
        INSTANCE = this;
    }
}
