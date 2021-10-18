package me.sxmurai.inferno.client.manager.managers.hud;

import me.sxmurai.inferno.api.values.Configurable;
import me.sxmurai.inferno.client.config.HudConfig;
import me.sxmurai.inferno.client.features.hud.Speed;
import me.sxmurai.inferno.client.features.hud.Watermark;
import me.sxmurai.inferno.client.manager.ConfigurableManager;

import java.util.List;

public class HudManager extends ConfigurableManager<HudComponent> {
    public HudManager() {
        this.items.add(new Speed());
        this.items.add(new Watermark());

        this.items.forEach(Configurable::registerSettings);
    }

    @Override
    public void load() {
        this.configuration = new HudConfig(this);
        this.configuration.load();
    }

    @Override
    public void unload() {
        this.configuration.stop();
    }

    public List<HudComponent> getComponents() {
        return this.items;
    }
}
