package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Timer", description = "Makes the game go zoom zoom", category = Module.Category.PLAYER)
public class Timer extends Module {
    public final Setting<Float> speed = this.register(new Setting<>("Speed", 2.0f, 0.1f, 20.0f));

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
        mc.timer.tickLength = 50.0f / speed.getValue();
    }
}
