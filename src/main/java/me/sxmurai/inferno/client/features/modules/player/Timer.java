package me.sxmurai.inferno.client.features.modules.player;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Timer", description = "Makes the game go zoom zoom", category = Module.Category.PLAYER)
public class Timer extends Module {
    public final Value<Boolean> sync = new Value<>("Sync", false);
    public final Value<Float> speed = new Value<>("Speed", 2.0f, 0.1f, 20.0f, (v) -> !sync.getValue());

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck()) {
            mc.timer.tickLength = 50.0f;
        }
    }

    @Override
    public void onUpdate() {
    	if (sync.getValue()) {
            mc.timer.tickLength = 1000.0f / Inferno.serverManager.getTps();
    	} else {
            mc.timer.tickLength = 50.0f / speed.getValue();
    	}
    }
}
