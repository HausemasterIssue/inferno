package me.sxmurai.inferno.features.gui.click.components.buttons;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.gui.click.components.Component;
import me.sxmurai.inferno.features.gui.click.components.other.Slider;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class ModuleButton extends Button {
    private final ArrayList<Component> components = new ArrayList<>();
    private final Module module;
    private boolean expanded = false;

    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        this.height = 13.0f;

        this.init();
    }

    private void init() {
        this.components.add(new BindButton(this.module));

        for (Setting setting : this.module.getSettings()) {
            if (setting.getValue() instanceof Boolean) {
                this.components.add(new BooleanButton(setting));
                continue;
            } else if (setting.getValue() instanceof Enum) {
                this.components.add(new EnumButton(setting));
                continue;
            }

            if (setting.isNumberSetting()) {
                if (setting.hasRestriction()) {
                    this.components.add(new Slider(setting));
                } else {
                    // @todo
                }

                continue;
            }
        }
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Inferno.textManager.drawRegularString(this.module.getName(), this.x + 2.3f, RenderUtils.centerVertically(this.y, this.height), module.isToggled() ? -1 : -5592406);

        if (this.expanded) {
            float posY = 1.0f;
            for (Component component : this.components) {
                if (component.isVisible()) {
                    component.setX(this.x + 1.0f);
                    component.setY(this.y + (posY += component.getHeight() + 1.0f));
                    component.setWidth(this.width - 4.0f);
                    component.setHeight(13.0f);

                    component.draw(mouseX, mouseY);
                }
            }

            RenderUtils.drawRect(this.x, this.y + this.height, 0.5f, this.getHeight() - this.height, new Color(51, 119, 222).getRGB());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (this.expanded) {
            for (Component component : this.components) {
                component.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        if (this.expanded) {
            for (Component component : this.components) {
                component.keyTyped(character, keyCode);
            }
        }
    }

    @Override
    public void onClick(int button) {
        if (button == 0) {
            this.module.toggle();
        } else if (button == 1) {
            this.expanded = !this.expanded;
        }
    }

    @Override
    public float getHeight() {
        return 13.0f + (this.expanded ? components.stream().filter(Component::isVisible).map(Component::getHeight).reduce(0.0f, (a, b) -> b + (a + 1.0f)) : 0.0f) + 1.5f;
    }
}
