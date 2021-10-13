package me.sxmurai.inferno.client.config;

import me.sxmurai.inferno.client.manager.managers.hud.HudManager;
import me.sxmurai.inferno.client.manager.managers.misc.FileManager;

public class HudConfig extends BaseConfig {
    private final HudManager manager;

    public HudConfig(HudManager manager) {
        super(FileManager.getInstance().getClientFolder().resolve("macros.json"));
        this.manager = manager;
    }

    @Override
    public void save() {

    }

    @Override
    public void load() {

    }
}
