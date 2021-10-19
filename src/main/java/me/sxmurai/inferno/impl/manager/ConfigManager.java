package me.sxmurai.inferno.impl.manager;

import me.sxmurai.inferno.impl.config.Config;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static ConfigManager INSTANCE;

    private final Map<String, Config> configs = new HashMap<>();

    public ConfigManager() {

    }

    public <T extends Config> T getConfig(String name) {
        for (Map.Entry<String, Config> entry : this.configs.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return (T) entry.getValue();
            }
        }

        return null;
    }

    public static ConfigManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigManager();
        }

        return INSTANCE;
    }
}
