package me.sxmurai.inferno.client.features.modules.player;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.init.MobEffects;

@Module.Define(name = "AntiLevitation", description = "Stops you from levitating lol", category = Module.Category.PLAYER)
public class AntiLevitation extends Module {
    public final Value<Mode> mode = new Value<>("Mode", Mode.REMOVE);

    @Override
    public void onUpdate() {
        if (mc.player.isPotionActive(MobEffects.LEVITATION)) {
            if (this.mode.getValue() == Mode.REMOVE) {
                mc.player.removePotionEffect(MobEffects.LEVITATION);
            } else if (this.mode.getValue() == Mode.MOTION) {
                mc.player.motionY = 0.0f;
            }
        }
    }

    public enum Mode {
        REMOVE, MOTION
    }
}
