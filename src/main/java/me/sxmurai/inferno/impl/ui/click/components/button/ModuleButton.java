package me.sxmurai.inferno.impl.ui.click.components.button;

import me.sxmurai.inferno.api.util.ScaleUtil;
import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.ui.components.widgets.button.Button;

public class ModuleButton extends Button {
    private final Module module;
    private boolean expanded = false;

    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        mc.fontRenderer.drawStringWithShadow(this.name, ((float) this.x) + 2.3f, ScaleUtil.centerTextY((float) this.y, (float) this.height), this.module.isOn() ? -1 : -5592406);
    }

    @Override
    public void doAction(int button) {
        if (button == 0) {
            this.module.toggle();
        } else {
            this.expanded = !this.expanded;
        }
    }
}
