package me.sxmurai.inferno.impl.manager;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.impl.features.Wrapper;
import me.sxmurai.inferno.impl.features.module.Module;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventManager implements Wrapper {
    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (fullNullCheck() && event.getEntityLiving() == mc.player) {
            for (Module module : Inferno.moduleManager.getModules()) {
                if (module.isOff()) {
                    continue;
                }

                module.onUpdate();
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (fullNullCheck()) {
            for (Module module : Inferno.moduleManager.getModules()) {
                if (module.isOff()) {
                    continue;
                }

                module.onTick();
            }
        }
    }
}
