package me.sxmurai.inferno.impl.features.module.modules.player;

import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.option.Option;

@Module.Define(name = "Timer", category = Module.Category.Player)
@Module.Info(description = "Changes the game's tick rate")
public class Timer extends Module {
    public final Option<Boolean> sync = new Option<>("Sync", false);
    public final Option<Float> speed = new Option<>("Speed", 1.0f, 0.1f, 20.0f, () -> !this.sync.getValue());

    @Override
    protected void onDeactivated() {
        if (fullNullCheck()) {
            mc.timer.tickLength = 50.0f;
        }
    }

    @Override
    public void onUpdate() {
        if (this.sync.getValue()) {
            // @todo server manager is needed for calculating TPS
        } else {
            mc.timer.tickLength = (50.0f / this.speed.getValue());
        }
    }
}
