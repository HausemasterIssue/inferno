package me.sxmurai.inferno.managers;

import me.sxmurai.inferno.events.entity.UpdateMoveEvent;
import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.utils.RotationUtils;
import me.sxmurai.inferno.utils.timing.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RotationManager extends Feature {
    private final RotationUtils.Rotation current = new RotationUtils.Rotation(0.0f, 0.0f);
    private final Timer timer = new Timer();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUpdateMove(UpdateMoveEvent event) {
        if (event.getEra() == UpdateMoveEvent.Era.PRE && this.timer.passedMs(100L)) {
            this.reset();
        }
    }

    public void reset() {
        this.current.setYaw(mc.player.rotationYaw);
        this.current.setPitch(mc.player.rotationPitch);
    }

    public void setRotations(float yaw, float pitch) {
        this.timer.reset();
        this.current.setYaw(yaw);
        this.current.setPitch(pitch);
    }

    public void look(Entity entity) {
        this.look(entity.getPositionEyes(mc.getRenderPartialTicks()));
    }

    public void look(Vec3d vec) {
        RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), vec);
        this.setRotations(rotation.getYaw(), rotation.getPitch());
    }

    public float getYaw() {
        return this.current.getYaw();
    }

    public float getPitch() {
        return this.current.getPitch();
    }
}
