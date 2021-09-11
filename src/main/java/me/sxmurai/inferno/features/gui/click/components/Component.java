package me.sxmurai.inferno.features.gui.click.components;

import me.sxmurai.inferno.Inferno;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Component {
    protected final String name;
    protected float x, y;
    protected float width, height;

    public Component(String name) {
        this.name = name;
    }

    public void draw(int mouseX, int mouseY) { }
    public void mouseClicked(int mouseX, int mouseY, int button) { }
    public void mouseReleased(int mouseX, int mouseY, int state) { }
    public void keyTyped(char character, int keyCode) { }

    public boolean isMouseInBounds(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void playClickSound(float pitch) {
        Inferno.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, pitch));
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

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isVisible() {
        return true;
    }
}
