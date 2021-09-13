package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Weather", description = "Forces the weather clientside", category = Module.Category.RENDER)
public class Weather extends Module {
    public final Setting<Type> type = this.register(new Setting<>("Type", Type.CLEAR));

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        WorldInfo info = mc.world.getWorldInfo();

        switch (type.getValue()) {
            case CLEAR: {
                info.setCleanWeatherTime(0);
                info.setRainTime(0);
                info.setThunderTime(0);
                info.setRaining(false);
                info.setThundering(false);
                break;
            }

            case RAIN: {
                info.setCleanWeatherTime(0);
                info.setRainTime(0);
                info.setThunderTime(0);
                info.setRaining(true);
                info.setThundering(false);
                break;
            }

            case THUNDER: {
                info.setCleanWeatherTime(0);
                info.setRainTime(0);
                info.setThunderTime(0);
                info.setRaining(true);
                info.setThundering(true);
                break;
            }
        }
    }

    public enum Type {
        CLEAR, RAIN, THUNDER
    }
}
