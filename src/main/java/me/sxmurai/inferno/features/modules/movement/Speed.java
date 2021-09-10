package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.entity.MoveEvent;
import me.sxmurai.inferno.events.entity.UpdateMoveEvent;
import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.RotationUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@Module.Define(name = "Speed", description = "Speeds you up", category = Module.Category.MOVEMENT)
public class Speed extends Module {
    public final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.STRAFE));
    public final Setting<Float> speed = this.register(new Setting<>("Speed", 30.0f, 1.0f, 50.0f, (v) -> mode.getValue() != Mode.BHOP));
    public final Setting<Boolean> hop = this.register(new Setting<>("Hop", true, (v) -> mode.getValue() != Mode.BHOP));

    // strafe specific settings
    public final Setting<Hop> strafeHop = this.register(new Setting<>("StrafeHop", Hop.MOTION, (v) -> hop.getValue() && mode.getValue() == Mode.STRAFE));
    public final Setting<Float> hopHeight = this.register(new Setting<>("HopHeight", 0.4f, 0.1f, 1.0f, (v) -> hop.getValue() && strafeHop.getValue() == Hop.MOTION && mode.getValue() == Mode.STRAFE));
    public final Setting<Boolean> strict = this.register(new Setting<>("Strict", false, (v) -> mode.getValue() == Mode.STRAFE));
    public final Setting<Integer> startStage = this.register(new Setting<>("Stage", 2, 0, 4, (v) -> mode.getValue() == Mode.STRAFE));
    public final Setting<Boolean> limiter = this.register(new Setting<>("Limiter", false, (v) -> mode.getValue() == Mode.STRAFE));
    public final Setting<Boolean> noLag = this.register(new Setting<>("NoLag", true, (v) -> mode.getValue() == Mode.STRAFE));

    private int strafeStage = 1;
    private double strafeSpeed = 0.0;
    private double distance = 0.0;

    @Override
    protected void onActivated() {
        if (!Module.fullNullCheck()) {
            strafeSpeed = getBaseMoveSpeed();
        }
    }

    @Override
    protected void onDeactivated() {
        strafeStage = startStage.getValue();
        strafeSpeed = 0.0;
        distance = 0.0;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof SPacketPlayerPosLook && noLag.getValue() && mode.getValue() == Mode.STRAFE) {
            strafeStage = 4;
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (shouldStop()) {
            return;
        }
        // @todo still broken
//        if (mode.getValue() == Mode.BHOP || (mode.getValue() == Mode.STRAFE && hop.getValue() && strafeHop.getValue() == Hop.JUMP) || hop.getValue() && mc.player.onGround) {
//            mc.player.jump();
//        }

        if (mode.getValue() == Mode.VANILLA) {
            RotationUtils.Rotation dir = RotationUtils.getDirectionalSpeed(speed.getValue() / 100.0);

            mc.player.motionX = dir.getYaw();
            mc.player.motionZ = dir.getPitch();
        }
    }

    @SubscribeEvent
    public void onMoveUpdate(UpdateMoveEvent event) {
        if (Module.fullNullCheck() && event.getEra() == UpdateMoveEvent.Era.PRE && mode.getValue() == Mode.STRAFE) {
            EntityPlayerSP player = mc.player;
            distance = Math.sqrt((player.posX - player.prevPosX) * (player.posX - player.prevPosX) + (player.posZ - player.prevPosZ) * (player.posZ - player.prevPosZ));
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (mode.getValue() != Mode.STRAFE || shouldStop()) {
            return;
        }

        if (mc.player.ticksExisted < 20 && strict.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer(mc.player.onGround));
            return;
        }

        if (mc.player.onGround && limiter.getValue()) {
            strafeStage = 2;
        }

        switch (strafeStage) {
            case 0: {
                ++strafeStage;
                distance = 0.0;
                break;
            }

            case 2: {
                if (hop.getValue() && strafeHop.getValue() == Hop.MOTION) {
                    if ((mc.player.moveForward == 0.0f || mc.player.moveStrafing == 0.0f) || !mc.player.onGround) {
                        break;
                    }

                    double motionY = hopHeight.getValue().doubleValue();
                    if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                        motionY += (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1;
                    }

                    mc.player.motionY = motionY;
                    event.setY(mc.player.motionY);

                    strafeSpeed *= strict.getValue() ? 1.98234128 : 2.149;
                }

                break;
            }

            case 3: {
                strafeSpeed = distance - 0.76 * (distance - getBaseMoveSpeed());
                break;
            }

            default: {
                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically || strafeStage > 0) {
                    strafeStage = (mc.player.moveForward != 0.0f && mc.player.moveStrafing != 0.0f) ? 1 : 0;
                }

                strafeSpeed = distance - distance / 159.0;
                break;
            }
        }

        strafeSpeed = Math.max(strafeSpeed, getBaseMoveSpeed());

        double forward = mc.player.movementInput.moveForward,
                strafe = mc.player.movementInput.moveStrafe,
                yaw = mc.player.rotationYaw;

        if (forward == 0.0f && strafe == 0.0f) {
            event.setX(0.0);
            event.setZ(0.0);
        } else if (forward != 0.0 && strafe != 0.0) {
            forward *= Math.sin(0.7853981633974483);
            strafe *= Math.cos(0.7853981633974483);
        }

        double radYaw = Math.toRadians(yaw);

        event.setX((forward * strafeSpeed * -Math.sin(radYaw) + strafe * strafeSpeed * Math.cos(radYaw)) * 0.99);
        event.setZ((forward * strafeSpeed * Math.cos(radYaw) - strafe * strafeSpeed * -Math.sin(radYaw)) * 0.99);

        ++strafeStage;
    }

    public double getBaseMoveSpeed() {
        double baseSpeed = speed.getValue() / 100.0;

        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            baseSpeed *= 1.0 + 0.2 * Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
        }

        return baseSpeed;
    }

    private boolean shouldStop() {
        return Inferno.moduleManager.getModule(ElytraFly.class).isToggled() || Inferno.moduleManager.getModule(PacketFly.class).isToggled();
    }

    public enum Mode {
        STRAFE, ONGROUND, VANILLA, BHOP
    }

    public enum Hop {
        JUMP, MOTION
    }
}