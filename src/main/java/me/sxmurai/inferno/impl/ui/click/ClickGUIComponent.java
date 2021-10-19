package me.sxmurai.inferno.impl.ui.click;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.ui.click.components.Panel;
import me.sxmurai.inferno.impl.ui.click.components.button.ModuleButton;
import me.sxmurai.inferno.impl.ui.components.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClickGUIComponent extends Component {
    public static ClickGUIComponent INSTANCE;
    private final ArrayList<Panel> panels = new ArrayList<>();

    private ClickGUIComponent() {
        super("clickgui");

        double x = 4.0;
        for (Module.Category category : Module.Category.values()) {
            List<Module> modules = Inferno.moduleManager.getModules().stream().filter((module) -> module.getCategory().equals(category)).collect(Collectors.toList());
            if (modules.isEmpty()) {
                continue;
            }

            this.panels.add(new Panel(category.name(), x, 25.0) {
                @Override
                protected void init() {
                    modules.forEach((module) -> this.buttons.add(new ModuleButton(module)));
                }
            });

            x += 98.0;
        }
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);
        this.panels.forEach((panel) -> panel.render(mouseX, mouseY));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.panels.forEach((b) -> b.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.panels.forEach((button) -> button.mouseReleased(mouseX, mouseY, state));
    }

    @Override
    public void keyTyped(char character, int code) {
        super.keyTyped(character, code);
        this.panels.forEach((button) -> button.keyTyped(character, code));
    }

    public static ClickGUIComponent getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGUIComponent();
        }

        return INSTANCE;
    }
}
