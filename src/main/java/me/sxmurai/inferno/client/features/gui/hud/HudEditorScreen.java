package me.sxmurai.inferno.client.features.gui.hud;

import me.sxmurai.inferno.api.utils.ColorUtils;
import me.sxmurai.inferno.api.utils.RenderUtils;
import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.manager.managers.hud.HudComponent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

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
            Vec2f bounds = this.stopClipping(this.x2 + mouseX, this.y2 + mouseY, this.current.getWidth(), this.current.getHeight());
            this.current.setX(bounds.x);
            this.current.setY(bounds.y);
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
        for (HudComponent component : Inferno.hudManager.getComponents()) {
            this.stopClipping(component);
        }
    }

    private void stopClipping(HudComponent component) {
        Vec2f bounds = this.stopClipping(component.getX(), component.getY(), component.getWidth(), component.getHeight());
        component.setX(bounds.x);
        component.setY(bounds.y);
    }

    private Vec2f stopClipping(float x, float y, float w, float h) {
        ScaledResolution resolution = new ScaledResolution(mc);
        return new Vec2f(
                MathHelper.clamp(x, 0.0f, resolution.getScaledWidth() - w),
                MathHelper.clamp(y, 0.0f, resolution.getScaledHeight() - h)
        );
    }

    public static HudEditorScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HudEditorScreen();
        }

        return INSTANCE;
    }
}
