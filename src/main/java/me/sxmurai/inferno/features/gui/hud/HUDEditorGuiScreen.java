package me.sxmurai.inferno.features.gui.hud;

import me.sxmurai.inferno.features.gui.click.components.Container;
import me.sxmurai.inferno.features.gui.hud.components.HudElementButton;
import me.sxmurai.inferno.features.hud.TpsHudComponent;
import me.sxmurai.inferno.features.hud.WatermarkHudComponent;
import me.sxmurai.inferno.utils.ColorUtils;
import me.sxmurai.inferno.utils.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class HUDEditorGuiScreen extends GuiScreen {
    private static HUDEditorGuiScreen INSTANCE;

    private final ArrayList<HUDComponent> components = new ArrayList<>();
    private final Container container;

    private HUDComponent dragging;
    private float x2, y2;

    public HUDEditorGuiScreen() {
        this.components.add(new TpsHudComponent());
        this.components.add(new WatermarkHudComponent());

        this.container = new Container("Elements", 4.0f, 4.0f) {
            @Override
            protected void init() {
                components.forEach((component) -> this.buttons.add(new HudElementButton(component)));
            }
        };
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        for (HUDComponent component : this.components) {
            if (component.isHidden()) {
                continue;
            }

            component.update(new ScaledResolution(mc));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();

        int scroll = Mouse.getDWheel();
        if (scroll < 0) {
            this.container.setY(this.container.getY() - 10.0f);
        } else if (scroll > 0) {
            this.container.setY(this.container.getY() + 10.0f);
        }

        this.container.draw(mouseX, mouseY);

        for (HUDComponent component : this.components) {
            if (component.isHidden()) {
                continue;
            }

            if (this.dragging == component) {
                component.setX(this.x2 + mouseX);
                component.setY(this.y2 + mouseY);
            }

            RenderUtils.drawRect(component.getX() - 2.0f, component.getY(), component.getWidth() + 2.0f, component.getHeight() + 2.0f, ColorUtils.toRGBA(255, 255, 255, 80));
            component.draw();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.container.mouseClicked(mouseX, mouseY, mouseButton);

        for (HUDComponent component : this.components) {
            if (component.isMouseInBounds(mouseX, mouseY) && dragging == null) {
                this.dragging = component;
                break;
            }
        }

        if (this.dragging != null) {
            this.x2 = this.dragging.getX() - mouseX;
            this.y2 = this.dragging.getY() - mouseY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.container.mouseReleased(mouseX, mouseY, state);

        if (this.dragging != null) {
            this.dragging = null;
        }
    }

    public ArrayList<HUDComponent> getComponents() {
        return components;
    }

    public static HUDEditorGuiScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUDEditorGuiScreen();
        }

        return INSTANCE;
    }
}
