package me.sxmurai.inferno.features.gui.click;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.gui.click.components.Panel;
import me.sxmurai.inferno.features.gui.click.components.button.ModuleButton;
import me.sxmurai.inferno.features.modules.client.ClickGUI;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClickGUIScreen extends GuiScreen {
    public static ClickGUIScreen INSTANCE;

    private final ArrayList<Panel> panels = new ArrayList<>();

    public ClickGUIScreen() {
        float x = 4.0f, y = 4.0f;

        for (Module.Category category : Module.Category.values()) {
            List<Module> modules = Inferno.moduleManager.getModules().stream()
                    .filter(module -> module.getCategory().getDisplayName().equalsIgnoreCase(category.getDisplayName()))
                    .collect(Collectors.toList());

            if (!modules.isEmpty()) {
                panels.add(new Panel(category.getDisplayName(), x, y, 88.0f, 14.0f) {
                    @Override
                    protected void init() {
                        modules.forEach(module -> buttons.add(new ModuleButton(module)));
                    }
                });

                x += 94.0f;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawDefaultBackground();

        int scroll = Mouse.getDWheel();
        if (scroll < 0) {
            panels.forEach(panel -> panel.setY(panel.getY() - ClickGUI.INSTANCE.scrollSpeed.getValue()));
        } else if (scroll > 0) {
            panels.forEach(panel -> panel.setY(panel.getY() + ClickGUI.INSTANCE.scrollSpeed.getValue()));
        }

        panels.forEach(panel -> panel.drawScreen(mouseX, mouseY, partialTicks));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        panels.forEach(panel -> panel.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        panels.forEach(panel -> panel.keyTyped(typedChar, keyCode));
    }

    public static ClickGUIScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGUIScreen();
        }

        return INSTANCE;
    }
}
