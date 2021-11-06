package me.sxmurai.inferno.impl.manager;

import me.sxmurai.inferno.impl.event.entity.UpdateWalkingPlayerEvent;
import me.sxmurai.inferno.impl.event.network.PacketEvent;
import me.sxmurai.inferno.api.entity.RotationUtil;
import me.sxmurai.inferno.api.timing.Timer;
import me.sxmurai.inferno.impl.features.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RotationManager implements Wrapper {
    private final RotationUtil.Rotation rotation = new RotationUtil.Rotation();
    private final Timer timer = new Timer();

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (fullNullCheck() && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            if (packet.rotating && (this.rotation.getYaw() != -1.0f && this.rotation.getPitch() != -1.0f)) {
                packet.yaw = this.rotation.getYaw();
                packet.pitch = this.rotation.getPitch();
            }
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPre(UpdateWalkingPlayerEvent event) {
        if (event.getEra() == UpdateWalkingPlayerEvent.Era.PRE && this.timer.passedMs(250L)) {
            this.reset();
        }
    }

    private void reset() {
        this.rotation.setYaw(mc.player.rotationYaw);
        this.rotation.setPitch(mc.player.rotationPitch);
    }

    public void setRotations(float yaw, float pitch) {
        this.timer.reset();
        this.rotation.setYaw(yaw);
        this.rotation.setPitch(pitch);
    }

    public void look(Entity entity) {
        this.look(entity.getPositionEyes(mc.getRenderPartialTicks()));
    }

    public void look(BlockPos pos) {
        this.look(new Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5));
    }

    public void look(Vec3d vec) {
        RotationUtil.Rotation rotations = RotationUtil.calcRotations(vec);
        this.setRotations(rotations.getYaw(), rotations.getPitch());
    }

    public float getYaw() {
        return this.rotation.getYaw();
    }

    public float getPitch() {
        return this.rotation.getPitch();
    }
}
