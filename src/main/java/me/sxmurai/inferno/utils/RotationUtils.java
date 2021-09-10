package me.sxmurai.inferno.utils;

import me.sxmurai.inferno.features.Feature;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtils extends Feature {
    public static Rotation calcRotations(Vec3d to) {
        Vec3d from = mc.player.getPositionVector();

        double[] difference = new double[] { from.x - to.x, from.y - to.y, from.z - to.z };
        double distance = MathHelper.sqrt(difference[0] * difference[0] + difference[2] * difference[2]);

        return new Rotation(
                (float) (Math.toDegrees(MathHelper.atan2(difference[0], difference[0])) - 90.0f),
                (float) - Math.toDegrees(MathHelper.atan2(difference[1], distance))
        );
    }

    public static Rotation calcRotations(Vec3d from, Vec3d to) {
        double[] difference = new double[] { to.x - from.x, to.y - from.y, to.z - from.z };
        double distance = MathHelper.sqrt(difference[0] * difference[0] + difference[2] * difference[2]);

        return new Rotation(
                (float) (Math.toDegrees(MathHelper.atan2(difference[0], difference[0])) - 90.0f),
                (float) - Math.toDegrees(MathHelper.atan2(difference[1], distance))
        );
    }

    public static Rotation getDirectionalSpeed(double speed) {
        float forward = mc.player.movementInput.moveForward,
                strafe = mc.player.movementInput.moveStrafe,
                yaw = (float) RenderUtils.interpolate(mc.player.rotationYaw, mc.player.prevRotationYaw);

        if (forward != 0.0f) {
            if (strafe > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (strafe < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }

            strafe = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }

        return new Rotation(
                (float) (forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw))),
                (float) (forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)))
        );
    }

    public static class Rotation {
        private final float yaw;
        private final float pitch;

        public Rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public float getYaw() {
            return yaw;
        }

        public float getPitch() {
            return getPitch(false);
        }

        public float getPitch(boolean normalize) {
            return normalize ? MathHelper.normalizeAngle((int) pitch, 360) : pitch;
        }
    }
}
