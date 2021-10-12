package me.sxmurai.inferno.client.features.gui.click.components.buttons;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.values.EnumConverter;
import me.sxmurai.inferno.client.manager.managers.commands.text.ChatColor;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.RenderUtils;
import org.lwjgl.input.Keyboard;

public class BindButton extends Button {
    private final Module module;
    private boolean listening = false;

    public BindButton(Module module) {
        super("");
        this.module = module;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        String bind = this.module.getBind() == Keyboard.KEY_NONE ? "None" : EnumConverter.getActualName(Keyboard.getKeyName(this.module.getBind()));
        Inferno.textManager.drawRegularString("Bind " + ChatColor.Dark_Gray.text(bind), this.x + 2.3f, RenderUtils.centerVertically(this.y, this.height), -1);
    }

    @Override
    public void onClick(int button) {
        if (button == 0) {
            this.listening = !this.listening;
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        if (this.listening) {
            this.listening = false;

            if (keyCode == Keyboard.KEY_NONE) {
                return;
            }

            if (keyCode == Keyboard.KEY_ESCAPE) {
                this.module.setBind(Keyboard.KEY_NONE);
                return;
            }

            this.module.setBind(keyCode);
        }
    }
}
