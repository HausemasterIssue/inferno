package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "AntiLevitation", description = "Stops you from levitating lol", category = Module.Category.PLAYER)
public class AntiLevitation extends Module {
    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.isPotionActive(MobEffects.LEVITATION)) {
            mc.player.removePotionEffect(MobEffects.LEVITATION);
        }
    }
}
