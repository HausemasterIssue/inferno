package me.sxmurai.inferno.utils;

public class ColorUtils {
    public static int toRGBA(int red, int green, int blue, int alpha) {
        return (red << 16) + (green << 8) + blue + (alpha << 24);
    }

    public static Color fromRGBA(int color) {
        float alpha = (color >> 24 & 0xff) / 255f;
        float red = (color >> 16 & 0xff) / 255f;
        float green = (color >> 8 & 0xff) / 255f;
        float blue = (color & 0xff) / 255f;

        return new Color((int) red, (int) green, (int) blue, (int) alpha);
    }

    public static class Color {
        public int red;
        public int green;
        public int blue;
        public int alpha;

        public Color(int red, int green, int blue) {
            this(red, green, blue, 255);
        }

        public Color(int red, int green, int blue, int alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public Color darker() {
            java.awt.Color c = new java.awt.Color(red, green, blue);
            c = c.darker();

            return fromRGBA(toRGBA(c.getRed(), c.getBlue(), c.getBlue(), alpha));
        }
    }
}