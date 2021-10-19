package me.sxmurai.inferno.api.util;

public class ScaleUtil implements Util {
    public static float centerTextX(float x, float width, float textWidth) {
        return x + (width / 2.0f) - (textWidth / 2.0f);
    }

    public static float centerTextY(float y, float height) {
        return (y + (height / 2.0f)) - (mc.fontRenderer.FONT_HEIGHT / 2.0f);
    }
}
