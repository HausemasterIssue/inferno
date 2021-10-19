package me.sxmurai.inferno.impl.config.configs;

import me.sxmurai.inferno.impl.config.Config;

@Config.Define(value = "modules", paths = {"configs", "modules.json"})
public class Modules extends Config {
    @Override
    protected void save() {

    }

    @Override
    protected void load() {
        String text = this.fileManager.read(this.path);
        if (text == null || text.isEmpty()) {
            this.save();
            return;
        }


    }
}
