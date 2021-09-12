package me.sxmurai.inferno.features.gui.hud.components;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.gui.click.components.Component;
import me.sxmurai.inferno.features.gui.click.components.buttons.Button;
import me.sxmurai.inferno.features.gui.hud.HUDComponent;
import me.sxmurai.inferno.utils.RenderUtils;

import java.util.ArrayList;

public class HudElementButton extends Button {
    private final ArrayList<Component> components = new ArrayList<>();
    private final HUDComponent component;
    private boolean expanded = false;

    public HudElementButton(HUDComponent component) {
        super(component.getName());
        this.component = component;
        this.height = 13.0f;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Inferno.textManager.drawRegularString(this.component.getName(), this.x + 2.3f, RenderUtils.centerVertically(this.y, this.height), component.isHidden() ? -5592406 : -1);
    }

    @Override
    public void onClick(int button) {
        if (button == 0) {
            this.component.setHidden(!this.component.isHidden());
        } else if (button == 1) {
            this.expanded = !this.expanded;
        }
    }
}
