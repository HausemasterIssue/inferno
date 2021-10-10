package me.sxmurai.inferno.api.events.inferno;

import me.sxmurai.inferno.client.manager.managers.modules.Module;
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
