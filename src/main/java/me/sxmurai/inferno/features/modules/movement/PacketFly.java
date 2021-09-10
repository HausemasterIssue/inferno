package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.events.entity.MoveEvent;
import me.sxmurai.inferno.events.entity.UpdateMoveEvent;
import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.RotationUtils;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "PacketFly", description = "Fly with packets and shit", category = Module.Category.MOVEMENT)
public class PacketFly extends Module {
    public final Setting<Float> speed = this.register(new Setting<>("Speed", 0.2f, 0.1f, 2.5f));
    public final Setting<Boolean> invalidPacket = this.register(new Setting<>("InvalidPacket", true));
    public final Setting<Float> invalidPacketOffset = this.register(new Setting<>("InvalidPacketOffset", 5.0f, -10.0f, 20.0f, (v) -> invalidPacket.getValue()));
    public final Setting<Boolean> setMotion = this.register(new Setting<>("SetMotion", true));
    public final Setting<Boolean> setPos = this.register(new Setting<>("SetPos", false));
    public final Setting<Boolean> fallPacket = this.register(new Setting<>("FallPacket", false));
    public final Setting<Boolean> sneakPacket = this.register(new Setting<>("SneakPacket", true));

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (this.setMotion.getValue()) {
            event.setX(mc.player.motionX);
            event.setY(mc.player.motionY);
            event.setZ(mc.player.motionZ);
        }
    }

    @SubscribeEvent
    public void onUpdateMove(UpdateMoveEvent event) {
        if (event.getEra() == UpdateMoveEvent.Era.PRE) {
            mc.player.setVelocity(0.0, 0.0, 0.0);
            event.setCanceled(true);

            if (mc.player.ticksExisted < 20) {
                return;
            }

            RotationUtils.Rotation rotation = RotationUtils.getDirectionalSpeed(speed.getValue());
            mc.player.motionX = rotation.getYaw();
            mc.player.motionZ = rotation.getPitch();

            if (mc.gameSettings.keyBindJump.pressed) {
                mc.player.motionY += speed.getValue();
            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.motionY -= speed.getValue();
            }

            if (sneakPacket.getValue()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }

            if (fallPacket.getValue()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            }

            Vec3d yes = mc.player.getPositionVector().add(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
            mc.player.connection.sendPacket(new CPacketPlayer.Position(yes.x, yes.y, yes.z, mc.player.onGround));

            if (this.setPos.getValue()) {
                mc.player.setPosition(yes.x, yes.y, yes.z);
            }

            if (this.invalidPacket.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(yes.x, yes.y + invalidPacketOffset.getValue().doubleValue(), yes.z, mc.player.onGround));
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();

            mc.player.connection.sendPacket(new CPacketConfirmTeleport(packet.teleportId));
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(packet.x, packet.y, packet.z, packet.yaw, packet.pitch, false));
            mc.player.setPosition(packet.x, packet.y, packet.z);

            event.setCanceled(true);
        }
    }
}
