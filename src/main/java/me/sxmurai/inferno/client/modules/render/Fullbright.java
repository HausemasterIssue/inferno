package me.sxmurai.inferno.client.modules.render;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Fullbright", description = "Makes the game... brigther... tf else?", category = Module.Category.RENDER)
public class Fullbright extends Module {
    public static float oldGamma = -1.0f;

    public final Value<Mode> mode = new Value<>("Mode", Mode.GAMMA);
    public final Value<Float> gamma = new Value<>("Gamma", 100.0f, 1.0f, 100.0f, (v) -> mode.getValue() == Mode.GAMMA);

    @Override
    protected void onDeactivated() {
        if (oldGamma != -1.0f) {
            mc.gameSettings.gammaSetting = oldGamma;
            oldGamma = -1.0f;
        }

        if (!Module.fullNullCheck() && mc.player.isPotionActive(MobEffects.NIGHT_VISION)) {
            mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mode.getValue() == Mode.GAMMA) {
            if (oldGamma == -1.0f) {
                oldGamma = mc.gameSettings.gammaSetting;
            }

            if (mc.player.isPotionActive(MobEffects.NIGHT_VISION)) {
                mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
            }

            mc.gameSettings.gammaSetting = gamma.getValue();
        } else if (mode.getValue() == Mode.POTION) {
            if (oldGamma != -1.0f) {
                mc.gameSettings.gammaSetting = oldGamma;
                oldGamma = -1.0f;
            }

            if (!mc.player.isPotionActive(MobEffects.NIGHT_VISION)) {
                mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 99999));
            }
        }
    }

    public enum Mode {
        GAMMA, POTION
    }
}
