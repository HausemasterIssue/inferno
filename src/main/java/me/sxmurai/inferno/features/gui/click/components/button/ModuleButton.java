package me.sxmurai.inferno.features.gui.click.components.button;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.gui.click.components.Component;
import me.sxmurai.inferno.features.gui.click.components.other.Slider;
import me.sxmurai.inferno.features.settings.Bind;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

import java.util.ArrayList;

public class ModuleButton extends Button {
    private final Module module;
    private final ArrayList<Component> components = new ArrayList<>();
    private boolean expanded = false;

    public ModuleButton(Module module) {
        super(module.getName(), 0.0f, 0.0f);

        this.module = module;
        this.init();
    }

    private void init() {
        for (Setting setting : module.getSettings()) {
            if (setting instanceof Bind) {
                components.add(new BindButton((Bind) setting));
                continue;
            }

            if (setting.getValue() instanceof Boolean) {
                components.add(new BooleanButton(setting));
                continue;
            } else if (setting.getValue() instanceof Enum) {
                components.add(new EnumButton(setting));
                continue;
            }

            if (setting.isNumberSetting()) {
                if (setting.hasRestriction()) {
                    components.add(new Slider(setting));
                }

                continue;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Inferno.textManager.drawRegularString(module.getName(), x + 2.3f, centerShit(y, height), module.isToggled() ? -1 : -5592406);

        if (expanded) {
            float h = 1.0f;
            for (Component component : components) {
                if (!component.isVisible()) {
                    continue;
                }

                component.setX(this.x + 1.0f);
                component.setY(this.y + (h += component.getHeight() + 1.0f));
                component.setWidth(this.width + 4.0f);
                component.setHeight(14.0f);

                component.drawScreen(mouseX, mouseY, partialTicks);
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
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        for (Component component : components) {
            if (!component.isVisible()) {
                continue;
            }

            component.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        for (Component component : components) {
            if (!component.isVisible()) {
                continue;
            }

            component.keyTyped(character, keyCode);
        }
    }

    @Override
    public float getHeight() {
        return 14.0f + (expanded ? components.stream().filter(Component::isVisible).map(Component::getHeight).reduce(0.0f, (a, b) -> b + (a + 1)) : 0.0f) + 1.5f;
    }
}
