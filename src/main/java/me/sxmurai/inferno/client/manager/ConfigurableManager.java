package me.sxmurai.inferno.client.manager;

import me.sxmurai.inferno.client.config.BaseConfig;

public abstract class ConfigurableManager<T> extends AbstractManager<T> {
    protected BaseConfig configuration;

    public ConfigurableManager() {
        this(null);
    }

    public ConfigurableManager(BaseConfig configuration) {
        this.configuration = configuration;
    }

    public BaseConfig getConfiguration() {
        return configuration;
    }

    public abstract void load();
    public abstract void unload();
}
