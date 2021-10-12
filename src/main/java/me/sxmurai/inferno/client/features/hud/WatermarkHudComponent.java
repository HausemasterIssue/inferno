package me.sxmurai.inferno.client.features.hud;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.features.gui.hud.HUDComponent;
import me.sxmurai.inferno.api.values.Value;
import net.minecraft.client.gui.ScaledResolution;

public class WatermarkHudComponent extends HUDComponent {
    public final Value<Boolean> version = this.register(new Value<>("Version", true));

    public WatermarkHudComponent() {
        super("Watermark");

        this.x = 2.0f;
        this.y = 2.0f;
    }

    @Override
    public void draw() {
        String text = Inferno.MOD_NAME + " " + (version.getValue() ? ("v" + Inferno.MOD_VER) : "");
        Inferno.textManager.drawRegularString(text, this.x, this.y, -1);

        this.width = Inferno.textManager.getWidth(text);
        this.height = Inferno.textManager.getHeight();
    }

    @Override
    public void update(ScaledResolution resolution) { }
}
