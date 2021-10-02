package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "AntiLevitation", description = "Stops you from levitating lol", category = Module.Category.PLAYER)
public class AntiLevitation extends Module {
    public final Setting<Mode> mode = new Setting<>("Mode", Mode.REMOVE);

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
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
