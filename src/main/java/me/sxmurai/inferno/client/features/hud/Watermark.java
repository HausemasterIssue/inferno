package me.sxmurai.inferno.client.features.hud;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.features.modules.client.Colors;
import me.sxmurai.inferno.client.manager.managers.hud.HudComponent;

public class Watermark extends HudComponent {
    public final Value<String> text = new Value<>("Text", Inferno.MOD_NAME);
    public final Value<Boolean> version = new Value<>("Version", true);

    public Watermark() {
        super("Watermark");
    }

    @Override
    public void draw() {
        String text = this.text.getValue() + " " + (this.version.getValue() ? ("v" + Inferno.MOD_VER) : "");
        Inferno.textManager.drawRegularString(text, this.x, this.y, Colors.color());
    }
}
