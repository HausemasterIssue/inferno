package me.sxmurai.inferno.client.features.gui.hud;

import me.sxmurai.inferno.api.utils.ColorUtils;
import me.sxmurai.inferno.api.utils.RenderUtils;
import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.manager.managers.hud.HudComponent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

public class HudEditorScreen extends GuiScreen {
    private static HudEditorScreen INSTANCE;

    private HudComponent current;
    private float x2, y2;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();

        if (this.current != null) {
            this.current.setX(this.x2 + mouseX);
            this.current.setY(this.y2 + mouseY);
        }

        for (HudComponent component : Inferno.hudManager.getComponents()) {
            if (!component.isVisible()) {
                continue;
            }

            RenderUtils.drawRect(component.getX(), component.getY(), component.getWidth(), component.getHeight(), ColorUtils.toRGBA(255, 255, 255, 80));
            component.draw();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            for (HudComponent component : Inferno.hudManager.getComponents()) {
                if (component.isMouseInBounds(mouseX, mouseY)) {
                    this.current = component;
                    break;
                }
            }

            if (this.current != null) {
                this.x2 = this.current.getX() - mouseX;
                this.y2 = this.current.getY() - mouseY;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        if (state == 0 && this.current != null) {
            this.current = null;
        }
    }

    @Override
    public void updateScreen() {
        ScaledResolution resolution = new ScaledResolution(mc);
        for (HudComponent component : Inferno.hudManager.getComponents()) {
            component.setX(Math.min(component.getX(), resolution.getScaledWidth()));
            component.setY(Math.min(component.getY(), resolution.getScaledHeight()));
        }
    }

    public static HudEditorScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HudEditorScreen();
        }

        return INSTANCE;
    }
}
