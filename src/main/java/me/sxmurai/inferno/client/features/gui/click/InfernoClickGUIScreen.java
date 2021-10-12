package me.sxmurai.inferno.client.features.gui.click;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.features.gui.click.components.Container;
import me.sxmurai.inferno.client.features.gui.click.components.buttons.ModuleButton;
import me.sxmurai.inferno.client.features.modules.client.ClickGUI;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InfernoClickGUIScreen extends GuiScreen {
    private static InfernoClickGUIScreen INSTANCE;
    private final ArrayList<Container> containers = new ArrayList<>();

    private InfernoClickGUIScreen() {
        float x = 8.0f, y = 4.0f;

        for (Module.Category category : Module.Category.values()) {
            List<Module> modules = Inferno.moduleManager.getModules()
                    .stream()
                    .filter((module) -> module.getCategory().getDisplayName().equalsIgnoreCase(category.getDisplayName()))
                    .collect(Collectors.toList());

            if (modules.isEmpty()) {
                continue;
            }

            this.containers.add(new Container(category.getDisplayName(), x, y) {
                @Override
                protected void init() {
                    modules.forEach((module) -> this.buttons.add(new ModuleButton(module)));
                }
            });

            x += 100.0f;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();

        int scroll = Mouse.getDWheel();
        if (scroll < 0) {
            for (Container container : this.containers) {
                container.setY(container.getY() - ClickGUI.INSTANCE.scrollSpeed.getValue());
            }
        } else if (scroll > 0) {
            for (Container container : this.containers) {
                container.setY(container.getY() + ClickGUI.INSTANCE.scrollSpeed.getValue());
            }
        }

        for (Container container : this.containers) {
            container.draw(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (Container container : this.containers) {
            container.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (Container container : this.containers) {
            container.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (Container container : this.containers) {
            container.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return ClickGUI.INSTANCE.pause.getValue();
    }

    public static InfernoClickGUIScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InfernoClickGUIScreen();
        }

        return INSTANCE;
    }
}
