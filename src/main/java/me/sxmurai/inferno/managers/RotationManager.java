package me.sxmurai.inferno.managers;

import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.utils.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

public class RotationManager extends Feature {
    private float yaw;
    private float pitch;

    public void reset() {
        mc.player.rotationYaw = this.yaw;
        mc.player.renderYawOffset = this.yaw;
        mc.player.rotationYawHead = this.yaw;
        mc.player.rotationPitch = this.pitch;

        mc.player.connection.getNetworkManager().dispatchPacket(new CPacketPlayer.Rotation(this.yaw, this.pitch, mc.player.onGround), null);
    }

    public void update() {
        this.yaw = mc.player.rotationYaw;
        this.pitch = mc.player.rotationPitch;
    }

    public void setRotations(float yaw, float pitch) {
        mc.player.rotationYaw = yaw;
        mc.player.renderYawOffset = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;

        mc.player.connection.getNetworkManager().dispatchPacket(new CPacketPlayer.Rotation(this.yaw, this.pitch, mc.player.onGround), null);
    }

    public void look(Vec3d vec) {
        RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), vec);
        this.yaw = rotation.getYaw();
        this.pitch = rotation.getPitch();
    }

    public void look(Entity entity) {
        this.look(entity.getPositionEyes(mc.getRenderPartialTicks()));
    }
}
