package me.sxmurai.inferno.config;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.managers.FileManager;
import me.sxmurai.inferno.managers.macros.Macro;
import me.sxmurai.inferno.managers.macros.MacroManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class MacrosConfig extends BaseConfig {
    private final MacroManager manager;

    public MacrosConfig(MacroManager manager) {
        super(FileManager.getInstance().getClientFolder().resolve("macros.json"));
        this.manager = manager;
    }

    @Override
    public void save() {
        JSONArray json = new JSONArray();

        for (Macro macro : this.manager.getMacros()) {
            json.put(new JSONObject().put("text", macro.getText()).put("code", macro.getKey()));
        }

        files.writeFile(this.path, json.toString(4));
    }

    @Override
    public void load() {
        String config = this.read();
        if (config == null || config.isEmpty()) {
            Inferno.LOGGER.info("Configuration not found, making a new one...");
            this.save();
            return;
        }

        for (Object object : new JSONArray(config).toList()) {
            if (!(object instanceof JSONObject)) {
                continue;
            }

            JSONObject json = (JSONObject) object;
            this.manager.add(this.get(json, "text", null), this.get(json, "code", -1));
        }
    }
}
