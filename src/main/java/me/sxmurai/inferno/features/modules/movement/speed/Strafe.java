package me.sxmurai.inferno.features.modules.movement.speed;

import me.sxmurai.inferno.events.entity.MoveEvent;
import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.modules.movement.Speed;
import me.sxmurai.inferno.managers.modules.Mode;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Strafe extends Mode<Speed> {
    public Strafe(Speed module, Enum<?> mode) {
        super(module, mode);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
            if (mc.player.onGround) {
                float yaw = this.getYaw();
                mc.player.motionX -= Math.sin(yaw) * 0.2f;
                mc.player.motionZ += Math.cos(yaw) * 0.2f;
            }
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        double speed = this.getBaseSpeed();
        float forward = mc.player.movementInput.moveForward,
                strafe = mc.player.movementInput.moveStrafe,
                yaw = mc.player.rotationYaw;

        if ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
            double hop = 0.4050000011920929;
            if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                hop += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1;
            }

            event.setY(mc.player.motionY = hop);
            speed *= 2.149;
        }

        speed *= 1.0064;

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

    private float getYaw() {
        float forward = mc.player.movementInput.moveForward,
                strafe = mc.player.movementInput.moveStrafe,
                yaw = mc.player.rotationYaw;

        if (forward < 0.0f) {
            yaw += 180.0f;
        }

        float offset = 1.0f;
        if (forward < 0.0f) {
            offset = -0.5f;
        } else if (forward > 0.0f) {
            offset = 0.5f;
        }

        if (strafe > 0.0f) {
            yaw -= 90.0f * offset;
        } else if (strafe < 0.0f) {
            yaw += 90.0f * offset;
        }

        return yaw * 0.017453292f;
    }

    private double getBaseSpeed() {
        double speed = 0.2872;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            speed *= 1.0 + 0.2 * (mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1);
        }

        return speed;
    }
}
