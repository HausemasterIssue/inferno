package me.sxmurai.inferno.features.gui.click.components;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.gui.click.components.buttons.ModuleButton;
import me.sxmurai.inferno.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class Container extends Component {
    protected final ArrayList<Component> buttons = new ArrayList<>();

    private boolean expanded = true;
    private boolean dragging = false;
    private float x2, y2;

    public Container(String name, float x, float y) {
        super(name);

        this.x = x;
        this.y = y;
        this.width = 94.0f;
        this.height = 14.0f;

        this.init();
    }

    protected void init() { }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (this.dragging) {
            this.x = this.x2 + mouseX;
            this.y = this.y2 + mouseY;
        }

        RenderUtils.drawRect(this.x, this.y, this.width, this.height, new Color(35, 39, 42).getRGB());
        Inferno.textManager.drawRegularString(this.name, RenderUtils.centerHorizontally(this.x, this.width, Inferno.textManager.getWidth(this.name)), RenderUtils.centerVertically(this.y, this.height), -1);

        if (this.expanded) {
            RenderUtils.drawRect(this.x, this.y + this.height, this.width, this.getTotalHeight(), new Color(35, 39, 42).getRGB());

            float positionY = (this.y + this.height) + 1.5f;
            for (Component button : this.buttons) {
                button.setX(this.x + 2.0f);
                button.setY(positionY);
                button.setWidth(this.width - 4.0f);

                button.draw(mouseX, mouseY);

                positionY += button.getHeight() + 1.5f;
            }
        }

        // we put this here to persist after drawing another rect @ line 41
        RenderUtils.drawLine(this.x, this.y + this.height, this.x + this.width, this.y + this.height, 2.0f, new Color(253, 31, 31).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.isMouseInBounds(mouseX, mouseY)) {
            if (button == 0) {
                this.x2 = this.x - mouseX;
                this.y2 = this.y - mouseY;
                this.dragging = true;
            } else if (button == 1) {
                this.expanded = !this.expanded;
                playClickSound(1.0f);
            }
        }

        if (this.expanded) {
            for (Component b : this.buttons) {
                b.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.dragging) {
            this.dragging = false;
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        if (this.expanded) {
            for (Component button : this.buttons) {
                button.keyTyped(character, keyCode);
            }
        }
    }

    private float getTotalHeight() {
        return this.expanded ? this.buttons.stream().map(Component::getHeight).reduce(0.0f, (a, b) -> b + (a + 1.5f)) : 0.0f;
    }
}
