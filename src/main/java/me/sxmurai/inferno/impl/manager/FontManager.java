package me.sxmurai.inferno.impl.manager;

import me.sxmurai.inferno.impl.features.Wrapper;
import me.sxmurai.inferno.impl.features.module.modules.client.CustomFont;
import me.sxmurai.inferno.impl.ui.font.CFontRenderer;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;

public class FontManager implements Wrapper {
    private final FontRenderer defaultFontRenderer;
    private CFontRenderer customFontRender;

    public FontManager() {
        this.defaultFontRenderer = mc.fontRenderer;
        this.resetCustomFont();
    }

    public void resetCustomFont() {
        this.customFontRender = new CFontRenderer(new Font(CustomFont.font.getName(), CustomFont.style.getValue().getStyle(), CustomFont.size.getValue()), CustomFont.antiAlias.getValue(), CustomFont.fractionalMetrics.getValue());
    }

    public void drawString(String text, double x, double y, int color) {
        if (CustomFont.INSTANCE.isOn()) {
            this.customFontRender.drawString(text, (int) x, (int) y, color);
        } else {
            this.defaultFontRenderer.drawString(text, (int) x, (int) y, color);
        }
    }

    public void drawStringWithShadow(String text, double x, double y, int color) {
        if (CustomFont.INSTANCE.isOn()) {
            this.customFontRender.drawStringWithShadow(text, x, y, color);
        } else {
            this.defaultFontRenderer.drawStringWithShadow(text, (float) x, (float) y, color);
        }
    }

    public void drawCorrectString(String text, double x, double y, int color) {
        if (CustomFont.INSTANCE.isOn()) {
            if (CustomFont.shadow.getValue()) {
                this.drawStringWithShadow(text, x, y, color);
            } else {
                this.drawString(text, x, y, color);
            }
        } else {
            this.drawString(text, x, y, color);
        }
    }

    public int getHeight() {
        return CustomFont.INSTANCE.isOn() ? this.customFontRender.getHeight() : this.defaultFontRenderer.FONT_HEIGHT;
    }

    public int getWidth(String text) {
        return CustomFont.INSTANCE.isOn() ? this.customFontRender.getStringWidth(text) : this.defaultFontRenderer.getStringWidth(text);
    }
}
