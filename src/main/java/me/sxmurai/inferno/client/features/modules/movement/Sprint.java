package me.sxmurai.inferno.client.features.modules.movement;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Sprint", description = "Makes you automatically sprint", category = Module.Category.MOVEMENT)
public class Sprint extends Module {
    public final Value<Mode> mode = new Value<>("Mode", Mode.LEGIT);

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
