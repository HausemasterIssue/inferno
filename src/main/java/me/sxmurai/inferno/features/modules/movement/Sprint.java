package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Sprint", description = "Makes you automatically sprint", category = Module.Category.MOVEMENT)
public class Sprint extends Module {
    public final Setting<Mode> mode = new Setting<>("Mode", Mode.LEGIT);

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mode.getValue() == Mode.LEGIT) {
            if (mc.player.getFoodStats().getFoodLevel() <= 6 || mc.player.isHandActive() || mc.player.isSneaking() || mc.player.collidedHorizontally || mc.player.moveForward == 0.0f) {
                return;
            }
        }

        if (!mc.player.isSprinting()) {
            mc.player.setSprinting(true);
        }
    }

    public enum Mode {
        LEGIT, RAGE
    }
}
