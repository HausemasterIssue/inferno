package me.sxmurai.inferno.features.gui.click.components.other;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.gui.click.components.Component;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.utils.RenderUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class Slider extends Component {
    private final Setting<Number> setting;
    private final float difference;

    public Slider(Setting<Number> setting) {
        super(setting.getName());
        this.setting = setting;
        this.difference = setting.getMax().floatValue() - setting.getMin().floatValue();
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (this.canSetValue(mouseX, mouseY)) {
            this.doSet(mouseX);
        }

        RenderUtils.drawRect(
                this.x - 0.5f,
                this.y,
                this.setting.getValue().floatValue() <= this.setting.getMin().floatValue() ?
                        0.0f :
                        this.width * this.partialMultiplier(),
                this.height,
                new Color(56, 60, 64, 255).getRGB()
        );

        Inferno.textManager.drawRegularString(this.setting.getName() + " " + this.setting.getValue(), this.x + 2.3f, RenderUtils.centerVertically(this.y, this.height), -1);
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

        if (this.setting.getValue() instanceof Float) {
            float result = this.setting.getMin().floatValue() + this.difference * percent;
            this.setting.setValue(Math.round(10.0f * result) / 10.0f);
        } else if (this.setting.getValue() instanceof Double) {
            double result = this.setting.getMin().doubleValue() + this.difference * percent;
            this.setting.setValue(Math.round(10.0 * result) / 10.0);
        } else {
            this.setting.setValue(Math.round(this.setting.getMin().intValue() + this.difference * percent));
        }
    }

    private boolean canSetValue(int mouseX, int mouseY) {
        return Mouse.isButtonDown(0) && this.isMouseInBounds(mouseX, mouseY);
    }

    private float part() {
        return this.setting.getValue().floatValue() - this.setting.getMin().floatValue();
    }

    private float partialMultiplier() {
        return this.part() / this.difference;
    }

    @Override
    public boolean isVisible() {
        return this.setting.isVisible();
    }
}
