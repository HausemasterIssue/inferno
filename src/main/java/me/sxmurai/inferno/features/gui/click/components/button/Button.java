package me.sxmurai.inferno.features.gui.click.components.button;

import me.sxmurai.inferno.features.gui.click.components.Component;

public class Button extends Component {
    public Button(String name, float x, float y) {
        super(name, x, y, 88.0f, 14.0f);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseInBounds(mouseX, mouseY)) {
            onClick(button);
            playClickSound(1.0f);
        }
    }

    public void onClick(int button) { }
}
