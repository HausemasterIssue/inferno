package me.sxmurai.inferno.features.gui.click.components.button;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.Setting;

public class BooleanButton extends Button {
    private final Setting<Boolean> setting;

    public BooleanButton(Setting<Boolean> setting) {
        super(setting.getName(), 0.0f, 0.0f);

        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Inferno.textManager.drawRegularString(setting.getName(), x + 2.3f, centerShit(y, height), setting.getValue() ? -1 : -5592406);
    }

    @Override
    public void onClick(int button) {
        if (button == 0) {
            setting.setValue(!setting.getValue());
        }
    }

    @Override
    public boolean isVisible() {
        return setting.isVisible();
    }
}
