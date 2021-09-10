package me.sxmurai.inferno.features.gui.click.components;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.gui.click.components.button.ModuleButton;
import me.sxmurai.inferno.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class Panel extends Component {
    protected final ArrayList<ModuleButton> buttons = new ArrayList<>();
    private float x2, y2;
    private boolean expanded = true;
    private boolean dragging = false;

    public Panel(String name, float x, float y, float width, float height) {
        super(name, x, y, width, height);
        this.init();
    }

    protected void init() { }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            this.x = x2 + mouseX;
            this.y = y2 + mouseY;
        }

        RenderUtils.drawRect(this.x, this.y, this.width, this.height, new Color(195, 195, 195).getRGB());
        Inferno.textManager.drawRegularString(this.name, this.x + 2.3f, centerShit(this.y, this.height), -1);

        if (expanded) {
            RenderUtils.drawRect(this.x, this.y + this.height, this.width, getTotalHeight(), 0x77000000);

            float componentY = (this.y + this.height) + 1.5f;
            for (ModuleButton button : this.buttons) {
                button.setX(this.x + 2.0f);
                button.setY(componentY);
                button.setWidth(this.width - 4.0f);

                button.drawScreen(mouseX, mouseY, partialTicks);

                componentY += button.getHeight() + 1.5f;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (isMouseInBounds(mouseX, mouseY)) {
            if (button == 0) {
                x2 = x - mouseX;
                y2 = y - mouseY;
                this.dragging = true;
            } else if (button == 1) {
                this.expanded = !this.expanded;
            }
        }

        if (this.expanded) {
            buttons.forEach(b -> b.mouseClicked(mouseX, mouseY, button));
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        if (this.expanded) {
            buttons.forEach(b -> b.keyTyped(character, keyCode));
        }
    }

    private float getTotalHeight() {
        return this.expanded ? this.buttons.stream().map(ModuleButton::getHeight).reduce(0.0f, (a, b) -> b + (a + 1.5f)) : 0.0f;
    }
}
