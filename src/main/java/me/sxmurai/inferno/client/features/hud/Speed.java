package me.sxmurai.inferno.client.features.hud;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.manager.managers.hud.HudComponent;
import net.minecraft.client.gui.ScaledResolution;

public class Speed extends HudComponent {
    public final Value<Boolean> round = new Value<>("Round", true);

    public Speed() {
        super("Speed");
    }

    @Override
    public void draw() {
        Inferno.textManager.drawRegularString(this.getText(), this.x, this.y, -1);
    }

    @Override
    public void update(ScaledResolution resolution) {
        this.width = Inferno.textManager.getWidth(this.getText());
        this.height = Inferno.textManager.getHeight();
    }

    private String getText() {
        return Inferno.speedManager.getSpeedKmh(this.round.getValue()) + " km/h";
    }
}
