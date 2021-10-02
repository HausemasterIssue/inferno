package me.sxmurai.inferno.utils;

public class ColorUtils {
    public static int toRGBA(int red, int green, int blue, int alpha) {
        return (red << 16) + (green << 8) + blue + (alpha << 24);
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
    }
}