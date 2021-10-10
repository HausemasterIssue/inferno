package me.sxmurai.inferno.client.gui.click.components.buttons;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.api.utils.RenderUtils;

public class BooleanButton extends Button {
    private final Value<Boolean> value;

    public BooleanButton(Value<Boolean> value) {
        super(value.getName());
        this.value = value;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Inferno.textManager.drawRegularString(value.getName(), this.x + 2.3f, RenderUtils.centerVertically(this.y, this.height), value.getValue() ? -1 : -5592406);
    }

    @Override
    public void onClick(int button) {
        if (button == 0) {
            this.value.setValue(!this.value.getValue());
        }
    }

    @Override
    public boolean isVisible() {
        return this.value.isVisible();
    }
}
