package me.sxmurai.inferno.client.gui.hud;

import me.sxmurai.inferno.api.utils.Wrapper;
import me.sxmurai.inferno.api.values.Value;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

public abstract class HUDComponent extends Wrapper {
    protected final String name;
    protected float x = -1.0f, y = -1.0f;
    protected float width, height;
    protected boolean hidden = true;

    private final ArrayList<Value> values = new ArrayList<>();

    public HUDComponent(String name) {
        this.name = name;
    }

    public abstract void draw();
    public abstract void update(ScaledResolution resolution);

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

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public <T> T register(Value value) {
        this.values.add(value);
        return (T) value;
    }

    public ArrayList<Value> getSettings() {
        return values;
    }

    public boolean isMouseInBounds(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
