package me.sxmurai.inferno.impl.features.module.modules.visual;

import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.option.Option;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

@Module.Define(name = "Brightness", category = Module.Category.Visual)
@Module.Info(description = "Makes the game brighter.")
public class Brightness extends Module {
    public static float oldGamma = -1.0f;

    public final Option<Mode> mode = new Option<>("Mode", Mode.Gamma);
    public final Option<Float> gamma = new Option<>("Gamma", 100.0f, 0.0f, 100.0f, () -> this.mode.getValue() == Mode.Gamma);

    private boolean gavePotion = false; // this is because we only want to remove the potion effect if the client gave the player it

    @Override
    protected void onDeactivated() {
        if (this.gavePotion && mc.player.isPotionActive(MobEffects.NIGHT_VISION)) {
            this.gavePotion = false;
            mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
        }

        if (Brightness.oldGamma != -1.0f) {
            mc.gameSettings.gammaSetting = Brightness.oldGamma;
            Brightness.oldGamma = -1.0f;
        }
    }

    @Override
    public void onUpdate() {
        if (this.mode.getValue() == Mode.Gamma) {
            if (this.gavePotion && mc.player.isPotionActive(MobEffects.NIGHT_VISION)) {
                this.gavePotion = false;
                mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
            }

            if (Brightness.oldGamma == -1.0f) {
                Brightness.oldGamma = mc.gameSettings.gammaSetting;
            }

            mc.gameSettings.gammaSetting = this.gamma.getValue();
        } else if (this.mode.getValue() == Mode.Potion) {
            if (Brightness.oldGamma != -1.0f) {
                mc.gameSettings.gammaSetting = Brightness.oldGamma;
                Brightness.oldGamma = -1.0f;
            }

            if (!this.gavePotion || !mc.player.isPotionActive(MobEffects.NIGHT_VISION)) {
                this.gavePotion = true;
                mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 100000));
            }
        }
    }

    public enum Mode {
        Gamma, Potion
    }
}
