package me.sxmurai.inferno.client.features.modules.movement;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.client.settings.KeyBinding;

@Module.Define(name = "AutoWalk", description = "Automatically makes you walk", category = Module.Category.MOVEMENT)
public class AutoWalk extends Module {
    // @todo add baritone
    public final Value<Mode> mode = new Value<>("Mode", Mode.VANILLA);

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false);
        }
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Mode.VANILLA) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true);
        }
    }

    public enum Mode {
        VANILLA, BARITONE
    }
}