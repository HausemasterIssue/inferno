package me.sxmurai.inferno.client.features.hud;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.features.gui.hud.HUDComponent;
import me.sxmurai.inferno.client.manager.managers.commands.text.ChatColor;
import net.minecraft.client.gui.ScaledResolution;

import java.text.DecimalFormat;

public class TpsHudComponent extends HUDComponent {
    private static final DecimalFormat FORMAT = new DecimalFormat("##.#");

    public TpsHudComponent() {
        super("TPS");

        this.x = 2.0f;
        this.y = 2.0f;
    }

    @Override
    public void draw() {
        Inferno.textManager.drawRegularString("TPS " + ChatColor.Dark_Gray.text(this.getTPS()), this.x, this.y, -1);
    }

    @Override
    public void update(ScaledResolution resolution) {
//        if (this.x == -1.0f || this.y == -1.0f) {
//            this.x = 2.0f;
//            this.y = resolution.getScaledWidth() - 2.0f;
//        }

        this.width = Inferno.textManager.getWidth("TPS " + ChatColor.Dark_Gray.text(this.getTPS()));
        this.height = Inferno.textManager.getHeight();
    }

    private String getTPS() {
        return FORMAT.format(Inferno.serverManager.getTps());
    }
}
