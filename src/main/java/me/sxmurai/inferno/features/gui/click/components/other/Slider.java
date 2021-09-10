package me.sxmurai.inferno.features.gui.click.components.other;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.gui.click.components.Component;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.utils.ColorUtils;
import me.sxmurai.inferno.utils.RenderUtils;
import org.lwjgl.input.Mouse;

public class Slider extends Component {
    private final Setting<Number> setting;
    private final float difference;

    public Slider(Setting<Number> setting) {
        super(setting.getName(), 0.0f, 0.0f, 88.0f, 0.0f);

        this.setting = setting;
        this.difference = setting.getMax().floatValue() - setting.getMin().floatValue();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (canSetValue(mouseX, mouseY)) {
            setValue(mouseX);
        }

        RenderUtils.drawRect(x, y, setting.getValue().floatValue() <= setting.getMin().floatValue() ? 0.0f : width * partialMultiplier(), height, ColorUtils.toRGBA(2, 112, 222, 255));
        Inferno.textManager.drawRegularString(setting.getName() + ": " + setting.getValue(), this.x + 2.3f, centerShit(this.y, this.height), -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (canSetValue(mouseX, mouseY)) {
            setValue(mouseX);
        }
    }

    private void setValue(double mouseX) {
        float percent = ((float) mouseX - x) / width;

        if (setting.getValue() instanceof Float) {
            float result = setting.getMin().floatValue() + difference * percent;
            setting.setValue(Math.round(10.0f * result) / 10.0f);
        } else if (setting.getValue() instanceof Double) {
            // @todo
        } else {
            setting.setValue(Math.round(setting.getMin().intValue() + difference * percent));
        }
    }

    private boolean canSetValue(int mouseX, int mouseY) {
        return Mouse.isButtonDown(0) && isMouseInBounds(mouseX, mouseY);
    }

    private float part() {
        return setting.getValue().floatValue() - setting.getMin().floatValue();
    }

    private float partialMultiplier() {
        return part() / difference;
    }

    @Override
    public boolean isVisible() {
        return setting.isVisible();
    }
}
