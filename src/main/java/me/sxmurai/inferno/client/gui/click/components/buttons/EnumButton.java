package me.sxmurai.inferno.client.gui.click.components.buttons;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.values.EnumConverter;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.commands.text.ChatColor;
import me.sxmurai.inferno.api.utils.RenderUtils;

public class EnumButton extends Button {
    private final Value<Enum> value;

    public EnumButton(Value<Enum> value) {
        super(value.getName());
        this.value = value;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Inferno.textManager.drawRegularString(value.getName() + " " + ChatColor.Dark_Gray.text(EnumConverter.getActualName(value.getValue())), this.x + 2.3f, RenderUtils.centerVertically(this.y, this.height), -1);
    }

    @Override
    public void onClick(int button) {
        if (button == 0) {
            this.value.setValue(EnumConverter.increaseEnum(value.getValue()));
        }
    }

    @Override
    public boolean isVisible() {
        return this.value.isVisible();
    }
}
