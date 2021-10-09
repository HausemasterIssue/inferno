package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "AutoWalk", description = "Automatically makes you walk", category = Module.Category.MOVEMENT)
public class AutoWalk extends Module {
    // @todo add baritone
    public final Setting<Mode> mode = new Setting<>("Mode", Mode.VANILLA);

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false);
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.mode.getValue() == Mode.VANILLA) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true);
        }
    }

    public enum Mode {
        VANILLA, BARITONE
    }
}