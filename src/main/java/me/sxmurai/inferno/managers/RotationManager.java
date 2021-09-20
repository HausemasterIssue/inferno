package me.sxmurai.inferno.managers;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.utils.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RotationManager extends Feature {
    private float yaw;
    private float pitch;

    public void update() {
        this.yaw = mc.player.rotationYaw;
        this.pitch = mc.player.rotationPitch;
    }

    public void reset() {
        mc.player.rotationYaw = this.yaw;
        mc.player.renderYawOffset = this.yaw;
        mc.player.rotationYawHead = this.yaw;
        mc.player.rotationPitch = this.pitch;
    }

    public void setRotations(float yaw, float pitch) {
        mc.player.rotationYaw = yaw;
        mc.player.renderYawOffset = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public void look(Entity entity) {
        this.look(entity.getPositionEyes(mc.getRenderPartialTicks()));
    }

    public void look(Vec3d vec) {
        RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), vec);
        this.setRotations(rotation.getYaw(), rotation.getPitch());
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        mc.player.renderYawOffset = this.yaw;
        mc.player.rotationYawHead = this.yaw;
    }
}
