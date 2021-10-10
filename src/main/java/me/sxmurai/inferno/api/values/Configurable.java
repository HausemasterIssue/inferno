package me.sxmurai.inferno.api.values;

import me.sxmurai.inferno.api.utils.Wrapper;

import java.util.ArrayList;
import java.util.Arrays;

public class Configurable extends Wrapper {
    protected final ArrayList<Value> values = new ArrayList<>();

    public void registerSettings() {
        Arrays.stream(this.getClass().getDeclaredFields())
                .filter(field -> Value.class.isAssignableFrom(field.getType()))
                .forEach((field) -> {
                    try {
                        field.setAccessible(true);
                        this.values.add(((Value) field.get(this)).setConfigurable(this));
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                });
    }

    public Value getSetting(String name) {
        for (Value value : this.values) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }

        return null;
    }

    public ArrayList<Value> getSettings() {
        return values;
    }
}
