package me.sxmurai.inferno.managers.macros;

import me.sxmurai.inferno.config.MacrosConfig;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

public class MacroManager {
    private final ArrayList<Macro> macros = new ArrayList<>();
    private final MacrosConfig config;

    public MacroManager() {
        this.config = new MacrosConfig(this);
        this.config.load();
    }

    public void add(Macro macro) {
        this.macros.add(macro);
        MinecraftForge.EVENT_BUS.register(macro);
    }

    public void add(String text, int code) {
        this.add(new Macro(text, code));
    }

    public void remove(Macro macro) {
        this.macros.remove(macro);
        MinecraftForge.EVENT_BUS.unregister(macro);
    }

    public ArrayList<Macro> getMacros() {
        return macros;
    }

    public void unload() {
        this.config.stop();
    }
}
