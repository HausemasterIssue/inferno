package me.sxmurai.inferno.impl.ui.components.widgets.button.buttons;

import me.sxmurai.inferno.impl.ui.components.widgets.button.Button;

public class TextButton extends Button {
    public TextButton(String name) {
        super(name);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        mc.fontRenderer.drawStringWithShadow(this.name, (float) this.x, (float) this.y, -1);
    }
}
