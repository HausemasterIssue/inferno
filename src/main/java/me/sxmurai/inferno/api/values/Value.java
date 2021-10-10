package me.sxmurai.inferno.api.values;

import me.sxmurai.inferno.api.events.inferno.SettingChangeEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Predicate;

// @todo make it so the predicate doesnt need a param, another example of useless skidded code
public class Value<T> {
    private Configurable configurable = null;

    private final String name;
    private T value;
    private final T defaultValue;
    private Predicate<T> visibility;

    private Number min;
    private Number max;

    public Value(String name, T value) {
        this(name, value, null);
    }

    public Value(String name, T value, Predicate<T> visibility) {
        this(name, value, null, null, visibility);
    }

    public Value(String name, T value, Number min, Number max) {
        this(name, value, min, max, null);
    }

    public Value(String name, T value, Number min, Number max, Predicate<T> visibility) {
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
        MinecraftForge.EVENT_BUS.post(new SettingChangeEvent(this, this.configurable));
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

    public Value setVisibility(Predicate<T> visibility) {
        this.visibility = visibility;
        return this;
    }

    public boolean isVisible() {
        return visibility == null || visibility.test(value);
    }

    public Value setConfigurable(Configurable configurable) {
        this.configurable = configurable;
        return this;
    }

    public Configurable getConfigurable() {
        return configurable;
    }
}
