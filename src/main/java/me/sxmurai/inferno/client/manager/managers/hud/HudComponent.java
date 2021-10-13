package me.sxmurai.inferno.client.manager.managers.hud;

import me.sxmurai.inferno.api.values.Configurable;
import me.sxmurai.inferno.api.values.Value;
import net.minecraft.client.gui.ScaledResolution;

public abstract class HudComponent extends Configurable {
    protected final String name;
    protected float x = 2.0f, y = 2.0f;
    protected float width, height;

    protected Value<Boolean> visible = new Value<>("Visible", true);

    public HudComponent(String name) {
        this.name = name;
    }

    public abstract void draw();
    public void update(ScaledResolution resolution) { }

    public String getName() {
        return name;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isVisible() {
        return this.visible.getValue();
    }

    public boolean isMouseInBounds(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
