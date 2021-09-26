package me.sxmurai.inferno.managers;

import me.sxmurai.inferno.events.entity.UpdateMoveEvent;
import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.RotationUtils;
import me.sxmurai.inferno.utils.timing.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RotationManager extends Feature {
    private final RotationUtils.Rotation current = new RotationUtils.Rotation(0.0f, 0.0f);
    private final Timer timer = new Timer();

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.current.getYaw() != mc.player.rotationYaw && this.current.getPitch() != mc.player.rotationPitch) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(this.current.getYaw(), this.current.getPitch(), mc.player.onGround));
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            if (packet.rotating) {
                packet.yaw = this.current.getYaw();
                packet.pitch = this.current.getPitch();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUpdateMove(UpdateMoveEvent event) {
        if (event.getEra() == UpdateMoveEvent.Era.PRE && this.timer.passedMs(250L)) {
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
