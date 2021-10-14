package me.sxmurai.inferno.client.features.hud;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.features.modules.client.Colors;
import me.sxmurai.inferno.client.manager.managers.hud.HudComponent;
import net.minecraft.client.gui.ScaledResolution;

public class Watermark extends HudComponent {
    public final Value<String> text = new Value<>("Text", Inferno.MOD_NAME);
    public final Value<Boolean> version = new Value<>("Version", true);

    public Watermark() {
        super("Watermark");
    }

    @Override
    public void draw() {
        Inferno.textManager.drawRegularString(this.getText(), this.x, this.y, Colors.color());
    }

    @Override
    public void update(ScaledResolution resolution) {
        this.width = Inferno.textManager.getWidth(this.getText());
        this.height = Inferno.textManager.getHeight();
    }

    private String getText() {
        return this.text.getValue() + " " + (this.version.getValue() ? ("v" + Inferno.MOD_VER) : "");
    }
}
