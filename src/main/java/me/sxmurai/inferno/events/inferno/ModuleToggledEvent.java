package me.sxmurai.inferno.events.inferno;

import me.sxmurai.inferno.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ModuleToggledEvent extends Event {
    private final Module module;

    public ModuleToggledEvent(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }
}
