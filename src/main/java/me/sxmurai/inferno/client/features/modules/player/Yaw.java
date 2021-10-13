package me.sxmurai.inferno.client.features.modules.player;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;

@Module.Define(name = "Yaw", description = "Forces you to look in a certain direction", category = Module.Category.PLAYER)
public class Yaw extends Module {
    public final Value<Direction> direction = new Value<>("Direction", Direction.CUSTOM);
    public final Value<Float> yaw = new Value<>("Yaw", 0.0f, 0.0f, 360.0f, (v) -> direction.getValue() == Direction.CUSTOM);
    public final Value<Boolean> lockPitch = new Value<>("LockPitch", false);
    public final Value<Float> pitch = new Value<>("Pitch", 0.0f, -90.0f, 90.0f, (v) -> lockPitch.getValue());

    @Override
    public void onUpdate() {
        mc.player.rotationYaw = this.direction.getValue() != Direction.CUSTOM ?
                this.direction.getValue().yaw :
                this.yaw.getValue();

        if (this.lockPitch.getValue()) {
            mc.player.rotationPitch = this.pitch.getValue();
        }
    }

    public enum Direction {
        NORTH(180.0f),
        NE(225.0f),
        EAST(270.0f),
        SE(315.0f),
        SOUTH(0.0f),
        SW(45.0f),
        WEST(90.0f),
        NW(135.0f),
        CUSTOM(-1.0f);

        private final float yaw;
        Direction(float yaw) {
            this.yaw = yaw;
        }
    }
}
