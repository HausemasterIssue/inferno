package me.sxmurai.inferno.client.hud;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.gui.hud.HUDComponent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.commands.text.ChatColor;
import net.minecraft.client.gui.ScaledResolution;

public class SpeedHudComponent extends HUDComponent {
    public final Value<Boolean> round = this.register(new Value<>("Round", true));

    public SpeedHudComponent() {
        super("Speed");

        this.x = 2.0f;
        this.y = 2.0f;
    }

    private String speed = "Speed 0.0 km/h";

    @Override
    public void draw() {
        Inferno.textManager.drawRegularString(this.speed, this.x, this.y, -1);
    }

    @Override
    public void update(ScaledResolution resolution) {
        this.speed = ChatColor.Gray.text("Speed") + " " + Inferno.speedManager.getSpeedKmh(this.round.getValue()) + " km/h";
        this.width = Inferno.textManager.getWidth(this.speed);
        this.height = Inferno.textManager.getHeight();
    }
}
