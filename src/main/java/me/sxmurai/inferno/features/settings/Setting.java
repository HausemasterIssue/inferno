package me.sxmurai.inferno.features.settings;

import me.sxmurai.inferno.events.inferno.SettingChangeEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Predicate;

public class Setting<T> {
    private final String name;
    private T value;
    private final T defaultValue;
    private Predicate<T> visibility;

    private Number min;
    private Number max;

    public Setting(String name, T value) {
        this(name, value, null);
    }

    public Setting(String name, T value, Predicate<T> visibility) {
        this(name, value, null, null, visibility);
    }

    public Setting(String name, T value, Number min, Number max) {
        this(name, value, min, max, null);
    }

    public Setting(String name, T value, Number min, Number max, Predicate<T> visibility) {
        this.name = name;
        this.value = value;
        this.defaultValue = value;
        this.visibility = visibility;

        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
        MinecraftForge.EVENT_BUS.post(new SettingChangeEvent(this));
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Number getMin() {
        return min;
    }

    public Number getMax() {
        return max;
    }

    public boolean isNumberSetting() {
        return value instanceof Number || min != null && max != null;
    }

    public boolean hasRestriction() {
        return value instanceof Number && max != null && min != null;
    }

    public Setting setVisibility(Predicate<T> visibility) {
        this.visibility = visibility;
        return this;
    }

    public boolean isVisible() {
        return visibility == null || visibility.test(value);
    }
}
