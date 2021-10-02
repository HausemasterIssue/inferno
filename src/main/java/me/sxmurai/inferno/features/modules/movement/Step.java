package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;

@Module.Define(name = "Step", description = "Steps up blocks", category = Module.Category.MOVEMENT)
public class Step extends Module {
    public final Setting<Mode> mode = new Setting<>("Mode", Mode.NCP);
    public final Setting<Integer> height = new Setting<>("Height", 1, 0, 2);
    public final Setting<Float> velocity = new Setting<>("Velocity", 0.3f, 0.1f, 10.0f, (v) -> mode.getValue() == Mode.SPIDER);

    public enum Mode {
        NCP, VANILLA, SPIDER
    }
}
