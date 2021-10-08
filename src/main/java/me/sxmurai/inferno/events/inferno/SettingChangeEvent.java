package me.sxmurai.inferno.events.inferno;

import me.sxmurai.inferno.features.settings.Configurable;
import me.sxmurai.inferno.features.settings.Setting;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SettingChangeEvent extends Event {
    private final Setting setting;
    private final Configurable configurable;

    public SettingChangeEvent(Setting setting, Configurable configurable) {
        this.setting = setting;
        this.configurable = configurable;
    }

    public Setting getSetting() {
        return setting;
    }

    public Configurable getConfigurable() {
        return configurable;
    }
}
