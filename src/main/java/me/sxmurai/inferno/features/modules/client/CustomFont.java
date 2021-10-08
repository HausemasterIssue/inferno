package me.sxmurai.inferno.features.modules.client;

import me.sxmurai.inferno.events.inferno.SettingChangeEvent;
import me.sxmurai.inferno.features.gui.font.CustomFontRenderer;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.TextManager;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    @Override
    protected void onActivated() {
        this.createCustomFontInstance();
    }

    @SubscribeEvent
    public void onSettingChange(SettingChangeEvent event) {
        if (event.getConfigurable() instanceof CustomFont) {
            this.createCustomFontInstance();
        }
    }

    private void createCustomFontInstance() {
        TextManager.customFontRenderer = new CustomFontRenderer(
                new Font(this.font.getValue(), this.style.getValue().getStyle(), this.size.getValue()),
                this.antiAliasing.getValue(),
                this.fractionalMetrics.getValue()
        );
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
