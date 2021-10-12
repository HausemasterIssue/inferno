package me.sxmurai.inferno.client.features.gui.click.components.other;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.features.gui.click.components.Component;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.api.utils.RenderUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class Slider extends Component {
    private final Value<Number> value;
    private final float difference;

    public Slider(Value<Number> value) {
        super(value.getName());
        this.value = value;
        this.difference = value.getMax().floatValue() - value.getMin().floatValue();
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (this.canSetValue(mouseX, mouseY)) {
            this.doSet(mouseX);
        }

        RenderUtils.drawRect(
                this.x - 0.5f,
                this.y,
                this.value.getValue().floatValue() <= this.value.getMin().floatValue() ?
                        0.0f :
                        this.width * this.partialMultiplier(),
                this.height,
                new Color(56, 60, 64, 255).getRGB()
        );

        Inferno.textManager.drawRegularString(this.value.getName() + " " + this.value.getValue(), this.x + 2.3f, RenderUtils.centerVertically(this.y, this.height), -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (this.canSetValue(mouseX, mouseY)) {
            this.doSet(mouseX);
        }
    }

    private void doSet(int mouseX) {
        float percent = (float) (((float) mouseX - this.x) / this.width);

        if (this.value.getValue() instanceof Float) {
            float result = this.value.getMin().floatValue() + this.difference * percent;
            this.value.setValue(Math.round(10.0f * result) / 10.0f);
        } else if (this.value.getValue() instanceof Double) {
            double result = this.value.getMin().doubleValue() + this.difference * percent;
            this.value.setValue(Math.round(10.0 * result) / 10.0);
        } else {
            this.value.setValue(Math.round(this.value.getMin().intValue() + this.difference * percent));
        }
    }

    private boolean canSetValue(int mouseX, int mouseY) {
        return Mouse.isButtonDown(0) && this.isMouseInBounds(mouseX, mouseY);
    }

    private float part() {
        return this.value.getValue().floatValue() - this.value.getMin().floatValue();
    }

    private float partialMultiplier() {
        return this.part() / this.difference;
    }

    @Override
    public boolean isVisible() {
        return this.value.isVisible();
    }
}
