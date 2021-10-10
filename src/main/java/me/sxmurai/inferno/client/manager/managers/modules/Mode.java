package me.sxmurai.inferno.client.manager.managers.modules;

import me.sxmurai.inferno.api.values.Configurable;

public class Mode<T extends Module> extends Configurable {
    protected final Enum<?> mode;
    protected final T module;

    public Mode(T module, Enum<?> mode) {
        this.module = module;
        this.mode = mode;
    }

    public Enum<?> getMode() {
        return mode;
    }

    public T getModule() {
        return module;
    }
}
