package me.sxmurai.inferno.events.inferno;

import me.sxmurai.inferno.features.settings.Setting;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SettingChangeEvent extends Event {
    private final Setting setting;

    public SettingChangeEvent(Setting setting) {
        this.setting = setting;
    }

    public Setting getSetting() {
        return setting;
    }
}
