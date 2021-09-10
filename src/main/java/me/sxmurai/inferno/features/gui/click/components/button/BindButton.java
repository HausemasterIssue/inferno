package me.sxmurai.inferno.features.gui.click.components.button;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.Bind;
import org.lwjgl.input.Keyboard;

public class BindButton extends Button {
    private final Bind bind;
    private boolean waiting = false;

    public BindButton(Bind bind) {
        super(bind.getName() + ": ??", 0.0f, 0.0f);

        this.bind = bind;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Inferno.textManager.drawRegularString(this.waiting ? "Listening..." : "Bind: " + Keyboard.getKeyName(this.bind.getValue()), this.x + 2.3f, centerShit(this.y, this.height), -1);
    }

    @Override
    public void onClick(int button) {
        if (button == 0) {
            this.waiting = !this.waiting;
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        if (this.waiting) {
            this.waiting = false;

            if (keyCode == Keyboard.KEY_ESCAPE) {
                return;
            }

            this.bind.setValue(keyCode);
        }
    }

    @Override
    public boolean isVisible() {
        return bind.isVisible();
    }
}
