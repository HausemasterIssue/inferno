package me.sxmurai.inferno.client.modules.movement.speed;

import me.sxmurai.inferno.api.events.entity.MoveEvent;
import me.sxmurai.inferno.client.modules.movement.Speed;
import me.sxmurai.inferno.client.manager.managers.modules.Mode;
import me.sxmurai.inferno.api.utils.timing.Timer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Strafe extends Mode<Speed> {
    private boolean hopped = false;
    private final Timer timer = new Timer();

    public Strafe(Speed module, Enum<?> mode) {
        super(module, mode);
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        double speed = this.getBaseSpeed();
        float forward = mc.player.movementInput.moveForward,
                strafe = mc.player.movementInput.moveStrafe,
                yaw = mc.player.rotationYaw;

        if ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround && this.timer.passedMs(200L)) {
            double hop = 0.40123128;
            if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                hop += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1;
            }

            event.setY(mc.player.motionY = hop);
            speed *= this.getBaseSpeed() * (mc.player.isInWater() || mc.player.collidedHorizontally ? 0.9: 1.901);

            this.hopped = true;
            this.timer.reset();
        } else {
            if (this.hopped || mc.player.collidedHorizontally) {
                speed -= 0.45 * this.getBaseSpeed();
                this.hopped = false;
            } else {
                speed -= speed / 159.0;
            }
        }

        speed = Math.max(speed, this.getBaseSpeed());

        if (forward == 0.0f && strafe == 0.0f) {
            event.setX(0.0);
            event.setZ(0.0);
        } else if (forward != 0.0 && strafe != 0.0) {
            forward *= Math.sin(0.7853981633974483);
            strafe *= Math.cos(0.7853981633974483);
        }

        double radYaw = Math.toRadians(yaw),
                sin = Math.sin(radYaw),
                cos = Math.cos(radYaw);

        event.setX((forward * speed * -sin + strafe * speed * cos) * 0.99);
        event.setZ((forward * speed * cos - strafe * speed * -sin) * 0.99);
    }

    private double getBaseSpeed() {
        double speed = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            speed *= 1.0 + 0.2 * (mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1);
        }

        return speed;
    }
}
