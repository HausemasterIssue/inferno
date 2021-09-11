package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Timer", description = "Makes the game go zoom zoom", category = Module.Category.PLAYER)
public class Timer extends Module {
    public final Setting<Float> speed = this.register(new Setting<>("ClientTPS", 2.0f, 0.1f, 20.0f));
    public final Setting<Boolean> sync = this.register(new Setting<>("TPSSync", true));
   
    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            toggle();
        }
    }

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck()) {
            mc.timer.tickLength = 50.0f;
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
    	if (sync.getValue()) {
            mc.timer.tickLength = (1000.0f / Inferno.serverManager.getTps());
    	} else {
            mc.timer.tickLength = 50.0f / speed.getValue();
    	}
    	

    }
}
