package me.sxmurai.inferno.client.modules.movement;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;

@Module.Define(name = "Step", description = "Steps up blocks", category = Module.Category.MOVEMENT)
public class Step extends Module {
    public final Value<Mode> mode = new Value<>("Mode", Mode.NCP);
    public final Value<Integer> height = new Value<>("Height", 1, 0, 2);
    public final Value<Float> velocity = new Value<>("Velocity", 0.3f, 0.1f, 10.0f, (v) -> mode.getValue() == Mode.SPIDER);

    public enum Mode {
        NCP, VANILLA, SPIDER
    }
}
