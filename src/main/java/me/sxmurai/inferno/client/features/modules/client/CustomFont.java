package me.sxmurai.inferno.client.features.modules.client;

import me.sxmurai.inferno.api.events.inferno.SettingChangeEvent;
import me.sxmurai.inferno.client.features.gui.font.CustomFontRenderer;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.misc.TextManager;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@Module.Define(name = "CustomFont", description = "Manages the client's custom font renderer", category = Module.Category.CLIENT)
public class CustomFont extends Module {
    public static CustomFont INSTANCE;

    public final Value<String> font = new Value<>("Font", "Verdana");
    public final Value<Boolean> shadow = new Value<>("Shadow", true);
    public final Value<Style> style = new Value<>("Style", Style.PLAIN);
    public final Value<Integer> size = new Value<>("Size", 18);
    public final Value<Boolean> antiAliasing = new Value<>("AntiAliasing", true);
    public final Value<Boolean> fractionalMetrics = new Value<>("FractionMetrics", false);

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
