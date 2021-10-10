package me.sxmurai.inferno.client.gui.click.components.buttons;

import me.sxmurai.inferno.client.gui.click.components.Component;

public class Button extends Component {
    public Button(String name) {
        super(name);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.isMouseInBounds(mouseX, mouseY)) {
            this.onClick(button);
            playClickSound(1.0f);
        }
    }

    public void onClick(int button) { }
}
