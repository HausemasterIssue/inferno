package me.sxmurai.inferno.features.modules.client;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

import java.awt.*;

@Module.Define(name = "CustomFont", description = "Manages the client's custom font renderer", category = Module.Category.CLIENT)
public class CustomFont extends Module {
    public static CustomFont INSTANCE;

    public final Setting<String> font = new Setting<>("Font", "Verdana");
    public final Setting<Boolean> shadow = new Setting<>("Shadow", true);
    public final Setting<Style> style = new Setting<>("Style", Style.PLAIN);
    public final Setting<Integer> size = new Setting<>("Size", 18);
    public final Setting<Boolean> antiAliasing = new Setting<>("AntiAliasing", true);
    public final Setting<Boolean> fractionalMetrics = new Setting<>("FractionMetrics", false);

    public CustomFont() {
        INSTANCE = this;
    }

    public enum Style {
        PLAIN(Font.PLAIN),
        BOLD(Font.BOLD),
        ITALIC(Font.ITALIC),
        BOLDED_ITALIC(Font.ITALIC + Font.BOLD);

        private int style;
        Style(int style) {
            this.style = style;
        }

        public int getStyle() {
            return style;
        }
    }
}
