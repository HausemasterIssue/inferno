package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.entity.MoveEvent;
import me.sxmurai.inferno.events.entity.UpdateMoveEvent;
import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.RotationUtils;
import me.sxmurai.inferno.utils.timing.Timer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "ElytraFly", description = "Makes elytras easier to use", category = Module.Category.MOVEMENT)
public class ElytraFly extends Module {
    public static ElytraFly INSTANCE;

    public final Setting<Float> speed = new Setting<>("Speed", 2.5f, 0.1f, 5.0f);
    public final Setting<Float> speedLimit = new Setting<>("SpeedLimit", 180.0f, 1.0f, 250.0f);
    public final Setting<Boolean> takeOff = new Setting<>("TakeOff", true);
    public final Setting<Float> takeOffTimerAmount = new Setting<>("TakeOffTimer", 0.1f, 0.1f, 1.0f, (v) -> takeOff.getValue());
    public final Setting<Up> up = new Setting<>("GoUp", Up.MOTION);
    public final Setting<Float> fireworkDelay = new Setting<>("FireworkDelay", 2.0f, 1.0f, 5.0f, (v) -> this.up.getValue() == Up.FIREWORK);
    public final Setting<Boolean> infinite = new Setting<>("InfiniteElytra", false);
    public final Setting<Boolean> sounds = new Setting<>("Sounds", true);

    private State state = State.IDLE;
    private boolean timerState = false;
    private final Timer retryTimer = new Timer();

    public ElytraFly() {
        INSTANCE = this;
    }

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck() && this.timerState) {
            mc.timer.tickLength = 50.0f;
        }

        this.state = State.IDLE;
        this.timerState = false;
        this.retryTimer.reset();
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (mc.player.isElytraFlying()) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                return;
            }

            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.motionY = -(this.speed.getValue().doubleValue() / 10.0);
            } else if (event.getY() != -1.01E-4) {
                mc.player.motionY = -1.01E-4;
            }

            event.setY(mc.player.motionY);
        }
    }

    @SubscribeEvent
    public void onUpdateMove(UpdateMoveEvent event) {
        if (event.getEra() == UpdateMoveEvent.Era.PRE) {
            if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).item != Items.ELYTRA) {
                return;
            }

            if (!mc.player.isElytraFlying()) {
                if (!this.takeOff.getValue()) {
                    return;
                }

                this.state = State.TAKEOFF;

                mc.player.rotateElytraX = 0.0f;
                mc.player.rotateElytraY = 0.0f;
                mc.player.rotateElytraZ = 0.0f;

                if (!this.timerState) {
                    this.timerState = true;
                    mc.timer.tickLength = 50.0f / takeOffTimerAmount.getValue();
                }

                mc.player.moveForward = 1.0f;

                if (takeOff.getValue() && this.retryTimer.passedMs(1125L)) {
                    this.retryTimer.reset();
                    if (mc.player.onGround) {
                        mc.player.jump();
                    }

                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                }
            } else {
                this.state = State.FLYING;
                if (this.timerState) {
                    this.timerState = false;
                    mc.timer.tickLength = 50.0f;
                }

                mc.player.setVelocity(0.0, 0.0, 0.0);
                event.setCanceled(true);

                if (mc.player.ticksExisted < 20) {
                    return;
                }

                if (Inferno.speedManager.getSpeedKmh(true) <= this.speedLimit.getValue()) {
                    RotationUtils.Rotation rotation = RotationUtils.getDirectionalSpeed(speed.getValue());
                    mc.player.motionX = rotation.getYaw();
                    mc.player.motionZ = rotation.getPitch();
                }

                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    if (this.up.getValue() == Up.MOTION) {
                        mc.player.motionY += speed.getValue() / 100.0;
                    } else if (this.up.getValue() == Up.FIREWORK) {
                        // @todo
                    }
                }

                Vec3d pos = mc.player.getPositionVector().add(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
                mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(pos.x, pos.y, pos.z, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
                mc.player.setPosition(pos.x, pos.y, pos.z);

                if (this.infinite.getValue()) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();

            mc.player.connection.sendPacket(new CPacketConfirmTeleport(packet.teleportId));
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(packet.x, packet.y, packet.z, packet.yaw, packet.pitch, mc.player.onGround));
            mc.player.setPosition(packet.x, packet.y, packet.z);

            event.setCanceled(true);
        }
    }

    public enum Up {
        MOTION, FIREWORK
    }

    private enum State {
        IDLE, TAKEOFF, FLYING
    }
}
