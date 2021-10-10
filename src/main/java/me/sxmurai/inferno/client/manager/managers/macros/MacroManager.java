package me.sxmurai.inferno.client.manager.managers.macros;

import me.sxmurai.inferno.client.config.MacrosConfig;
import me.sxmurai.inferno.client.manager.ConfigurableManager;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

public class MacroManager extends ConfigurableManager<Macro> {
    public MacroManager() {
        this.configuration = new MacrosConfig(this);
    }

    @Override
    public void load() {
        this.configuration.load();
    }

    public void add(Macro macro) {
        this.items.add(macro);
        MinecraftForge.EVENT_BUS.register(macro);
    }

    public void add(String text, int code) {
        this.add(new Macro(text, code));
    }

    public void remove(Macro macro) {
        this.items.remove(macro);
        MinecraftForge.EVENT_BUS.unregister(macro);
    }

    public ArrayList<Macro> getMacros() {
        return this.items;
    }

    public void unload() {
        this.configuration.stop();
    }
}
