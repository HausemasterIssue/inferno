package me.sxmurai.inferno.features.gui.click.components.button;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.EnumConverter;
import me.sxmurai.inferno.features.settings.Setting;

public class EnumButton extends Button {
    private final Setting<Enum> setting;

    public EnumButton(Setting<Enum> setting) {
        super(setting.getName() + ": e", 0.0f, 0.0f);

        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Inferno.textManager.drawRegularString(this.setting.getName() + ": " + EnumConverter.getActualName(this.setting.getValue()), this.x + 2.3f, centerShit(this.y, this.height), -5592406);
    }

    @Override
    public void onClick(int button) {
        if (button == 0) {
            setting.setValue(EnumConverter.increaseEnum(setting.getValue()));
        }
    }

    @Override
    public boolean isVisible() {
        return setting.isVisible();
    }
}
