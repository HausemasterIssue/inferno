package me.sxmurai.inferno.features.gui.click.components.buttons;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.EnumConverter;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.utils.RenderUtils;

public class EnumButton extends Button {
    private final Setting<Enum> setting;

    public EnumButton(Setting<Enum> setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Inferno.textManager.drawRegularString(setting.getName() + " " + ChatColor.Dark_Gray.text(EnumConverter.getActualName(setting.getValue())), this.x + 2.3f, RenderUtils.centerVertically(this.y, this.height), -1);
    }

    @Override
    public void onClick(int button) {
        if (button == 0) {
            this.setting.setValue(EnumConverter.increaseEnum(setting.getValue()));
        }
    }

    @Override
    public boolean isVisible() {
        return this.setting.isVisible();
    }
}
