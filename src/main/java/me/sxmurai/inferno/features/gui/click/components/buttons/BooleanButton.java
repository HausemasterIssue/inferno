package me.sxmurai.inferno.features.gui.click.components.buttons;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.utils.RenderUtils;

public class BooleanButton extends Button {
    private final Setting<Boolean> setting;

    public BooleanButton(Setting<Boolean> setting) {
        super(setting.getName());
        this.setting = setting;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Inferno.textManager.drawRegularString(setting.getName(), this.x + 2.3f, RenderUtils.centerVertically(this.y, this.height), setting.getValue() ? -1 : -5592406);
    }

    @Override
    public void onClick(int button) {
        if (button == 0) {
            this.setting.setValue(!this.setting.getValue());
        }
    }

    @Override
    public boolean isVisible() {
        return this.setting.isVisible();
    }
}
