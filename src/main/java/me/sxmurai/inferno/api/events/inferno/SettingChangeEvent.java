package me.sxmurai.inferno.api.events.inferno;

import me.sxmurai.inferno.api.values.Configurable;
import me.sxmurai.inferno.api.values.Value;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SettingChangeEvent extends Event {
    private final Value value;
    private final Configurable configurable;

    public SettingChangeEvent(Value value, Configurable configurable) {
        this.value = value;
        this.configurable = configurable;
    }

    public Value getSetting() {
        return value;
    }

    public Configurable getConfigurable() {
        return configurable;
    }
}
