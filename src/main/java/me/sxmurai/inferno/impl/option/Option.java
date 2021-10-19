package me.sxmurai.inferno.impl.option;

import java.util.function.Supplier;

public class Option<T> {
    private final String name;
    private T value;
    private final T defaultValue;
    private Supplier<Boolean> visibility;

    private Number min;
    private Number max;

    public Option(String name, T value) {
        this(name, value, null);
    }

    public Option(String name, T value, Supplier<Boolean> visibility) {
        this(name, value, null, null, visibility);
    }

    public Option(String name, T value, Number min, Number max) {
        this(name, value, min, max, null);
    }

    public Option(String name, T value, Number min, Number max, Supplier<Boolean> visibility) {
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

    public Option setVisibility(Supplier<Boolean> visibility) {
        this.visibility = visibility;
        return this;
    }

    public boolean isVisible() {
        return visibility == null || visibility.get();
    }
}
