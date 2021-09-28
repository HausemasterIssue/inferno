package me.sxmurai.inferno.features.settings;

import me.sxmurai.inferno.features.Feature;

import java.util.ArrayList;

public class Configurable extends Feature {
    protected final ArrayList<Setting> settings = new ArrayList<>();

    public <T> T register(Setting setting) {
        this.settings.add(setting);
        return (T) setting;
    }

    public <T> T getSetting(String name) {
        for (Setting setting : this.settings) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return (T) setting;
            }
        }

        return null;
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }
}
