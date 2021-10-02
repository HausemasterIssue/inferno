package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Yaw", description = "Forces you to look in a certain direction", category = Module.Category.PLAYER)
public class Yaw extends Module {
    public final Setting<Direction> direction = new Setting<>("Direction", Direction.CUSTOM);
    public final Setting<Float> yaw = new Setting<>("Yaw", 0.0f, 0.0f, 360.0f, (v) -> direction.getValue() == Direction.CUSTOM);
    public final Setting<Boolean> lockPitch = new Setting<>("LockPitch", false);
    public final Setting<Float> pitch = new Setting<>("Pitch", 0.0f, -90.0f, 90.0f, (v) -> lockPitch.getValue());

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
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
