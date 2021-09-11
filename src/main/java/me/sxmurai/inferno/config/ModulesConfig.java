package me.sxmurai.inferno.config;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.EnumConverter;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.FileManager;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.managers.modules.ModuleManager;
import me.sxmurai.inferno.utils.ColorUtils;
import org.json.JSONObject;
import org.lwjgl.input.Keyboard;

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
            JSONObject mod = new JSONObject()
                    .put("toggled", module.isToggled())
                    .put("bind", module.getBind());

            JSONObject settings = new JSONObject();
            for (Setting setting : module.getSettings()) {
                // @todo
                if (setting.getValue() instanceof ColorUtils.Color) {
                    continue;
                } else if (setting.getValue() instanceof Enum) {
                    settings.put(setting.getName(), ((Enum<?>) setting.getValue()).name());
                } else {
                    settings.put(setting.getName(), setting.getValue());
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

            mod.setBind(this.get(module, "bind", Keyboard.KEY_NONE));

            JSONObject settings = this.get(module, "settings");
            if (settings == null) {
                continue;
            }

            for (String settingKey : settings.keySet()) {
                Setting setting = mod.getSetting(settingKey);
                if (setting == null) {
                    continue;
                }

                Object value = this.get(settings, settingKey, setting.getDefaultValue());
                if (setting.getValue() instanceof Enum) {
                    setting.setValue(new EnumConverter(((Enum) setting.getValue()).getClass()).doBackward((String) value));
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
