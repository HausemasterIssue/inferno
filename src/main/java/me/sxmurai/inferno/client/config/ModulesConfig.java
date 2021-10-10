package me.sxmurai.inferno.client.config;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.values.EnumConverter;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.misc.FileManager;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.client.manager.managers.modules.ModuleManager;
import me.sxmurai.inferno.api.utils.ColorUtils;
import org.json.JSONObject;

public class ModulesConfig extends BaseConfig {
    private final ModuleManager modules;

    public ModulesConfig(ModuleManager moduleManager) {
        super(FileManager.getInstance().getClientFolder().resolve("modules.json"));
        this.modules = moduleManager;
    }

    @Override
    public void save() {
        JSONObject base = new JSONObject();

        for (Module module : this.modules.getModules()) {
            JSONObject mod = new JSONObject().put("toggled", module.isToggled());

            JSONObject settings = new JSONObject();
            for (Value value : module.getSettings()) {
                // @todo
                if (value.getValue() instanceof ColorUtils.Color) {
                    continue;
                } else if (value.getValue() instanceof Enum) {
                    settings.put(value.getName(), ((Enum<?>) value.getValue()).name());
                } else {
                    settings.put(value.getName(), value.getValue());
                }
            }

            base.put(module.getName(), mod.put("settings", settings));
        }

        this.files.writeFile(this.path, base.toString(4));
    }

    @Override
    public void load() {
        String config = this.read();
        if (config == null || config.isEmpty()) {
            Inferno.LOGGER.info("Configuration not found, making a new one...");
            this.save();
            return;
        }

        JSONObject json = new JSONObject(config);
        for (String key : json.keySet()) {
            Module mod = this.modules.getModule(key);
            if (mod == null) {
                return;
            }

            JSONObject module = this.get(json, key);
            if (module == null) {
                continue;
            }

            if (this.get(module, "toggled", false)) {
                mod.toggle();
            }

            JSONObject settings = this.get(module, "settings");
            if (settings == null) {
                continue;
            }

            for (String settingKey : settings.keySet()) {
                Value setting = mod.getSetting(settingKey);
                if (setting == null) {
                    continue;
                }

                Object value = this.get(settings, settingKey, setting.getDefaultValue());
                if (setting.getValue() instanceof Enum) {
                    try {
                        setting.setValue(new EnumConverter(((Enum) setting.getValue()).getClass()).doBackward((String) value));
                    } catch (Exception ignored) { }
                } else if (setting.getValue() instanceof Float) {
                    setting.setValue(settings.getFloat(settingKey));
                } else if (setting.getValue() instanceof Double) {
                    setting.setValue(settings.getDouble(settingKey));
                } else {
                    setting.setValue(value);
                }
            }
        }
    }
}
