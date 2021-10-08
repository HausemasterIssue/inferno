package me.sxmurai.inferno.features.settings;

import me.sxmurai.inferno.features.Feature;

import java.util.ArrayList;
import java.util.Arrays;

public class Configurable extends Feature {
    protected final ArrayList<Setting> settings = new ArrayList<>();

    public void registerSettings() {
        Arrays.stream(this.getClass().getDeclaredFields())
                .filter(field -> Setting.class.isAssignableFrom(field.getType()))
                .forEach((field) -> {
                    try {
                        field.setAccessible(true);
                        this.settings.add(((Setting) field.get(this)).setConfigurable(this));
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                });
    }

    public Setting getSetting(String name) {
        for (Setting setting : this.settings) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }

        return null;
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }
}
