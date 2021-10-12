package me.sxmurai.inferno.client.manager.managers.misc;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.features.gui.font.CustomFontRenderer;
import me.sxmurai.inferno.client.features.modules.client.CustomFont;
import me.sxmurai.inferno.client.manager.Manager;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;

public class TextManager extends Manager {
    private static final CustomFont fontMod = CustomFont.INSTANCE;

    public static final FontRenderer defaultFontRenderer = Inferno.mc.fontRenderer;
    public static CustomFontRenderer customFontRenderer;

    public void editCustomFont(String name, int style, int size, boolean antiAlias, boolean fractionalMetrics) {
        customFontRenderer = new CustomFontRenderer(new Font(name, style, size), antiAlias, fractionalMetrics);
    }

    public void drawString(String text, float x, float y, int color) {
        if (fontMod.isToggled()) {
            customFontRenderer.drawString(text, x, y, color);
        } else {
            defaultFontRenderer.drawString(text, (int) x, (int) y, color);
        }
    }

    public void drawStringWithShadow(String text, float x, float y, int color) {
        if (fontMod.isToggled()) {
            customFontRenderer.drawStringWithShadow(text, x, y, color);
        } else {
            defaultFontRenderer.drawStringWithShadow(text, x, y, color);
        }
    }

    public void drawRegularString(String text, float x, float y, int color) {
        if (fontMod.isToggled() && fontMod.shadow.getValue()) {
            drawStringWithShadow(text, x, y, color);
            return;
        }

        drawString(text, x, y, color);
    }

    public float getWidth(String text) {
        if (fontMod.isToggled()) {
            return customFontRenderer.getStringWidth(text);
        } else {
            return defaultFontRenderer.getStringWidth(text);
        }
    }

    public int getHeight() {
        if (fontMod.isToggled()) {
            return customFontRenderer.getHeight();
        } else {
            return defaultFontRenderer.FONT_HEIGHT;
        }
    }
}
