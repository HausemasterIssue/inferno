package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.entity.MoveEvent;
import me.sxmurai.inferno.events.entity.UpdateMoveEvent;
import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.modules.player.Freecam;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.RotationUtils;
import me.sxmurai.inferno.utils.timing.Timer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@Module.Define(name = "Speed", description = "Speeds you up", category = Module.Category.MOVEMENT)
public class Speed extends Module {
    public final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.STRAFE));
    public final Setting<Integer> speed = this.register(new Setting<>("Speed", 30, 1, 50));
    public final Setting<Boolean> liquids = this.register(new Setting<>("Liquids", true));
    public final Setting<Boolean> webs = this.register(new Setting<>("Webs", true));
    public final Setting<Boolean> hop = this.register(new Setting<>("Hop", true));

    // strafe specific settings
    public final Setting<Boolean> strict = this.register(new Setting<>("Strict", false, (v) -> mode.getValue() == Mode.STRAFE));
    public final Setting<Integer> startStage = this.register(new Setting<>("Stage", 2, 0, 4, (v) -> mode.getValue() == Mode.STRAFE));
    public final Setting<Boolean> limiter = this.register(new Setting<>("Limiter", false, (v) -> mode.getValue() == Mode.STRAFE));
    public final Setting<Boolean> noLag = this.register(new Setting<>("NoLag", true, (v) -> mode.getValue() == Mode.STRAFE));

    private final Timer timer = new Timer();
    private int strafeStage = 1;
    private double strafeSpeed = 0.0;
    private double distance = 0.0;

    @Override
    protected void onActivated() {
        if (!Module.fullNullCheck()) {
            strafeSpeed = getBaseMoveSpeed();
            this.timer.reset();
        }
    }

    @Override
    protected void onDeactivated() {
        strafeStage = startStage.getValue();
        strafeSpeed = 0.0;
        distance = 0.0;
        this.timer.reset();
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

        if ((!this.liquids.getValue() && (mc.player.isInWater() || mc.player.isInLava())) || (!this.webs.getValue() && mc.player.isInWeb)) {
            return;
        }

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
        if (mode.getValue() != Mode.STRAFE || (!this.liquids.getValue() && (mc.player.isInWater() || mc.player.isInLava())) || (!this.webs.getValue() && mc.player.isInWeb) || shouldStop()) {
            return;
        }

        if (mc.player.ticksExisted < 20 && this.strict.getValue()) {
            return;
        }

        if (mc.player.onGround && !this.limiter.getValue()) {
            this.strafeStage = 2;
        }

        switch (strafeStage) {
            case 0: {
                ++this.strafeStage;
                this.distance = 0.0;
                break;
            }

            case 2: {
                if (!this.hop.getValue()) {
                    break;
                }

                if (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f || !mc.player.onGround) {
                    break;
                }

                if (this.strict.getValue() && !this.timer.passedMs(150L)) {
                    break;
                }
                this.timer.reset();

                double motionY = 0.3994;
                if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    motionY += (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1;
                }

                event.setY(mc.player.motionY = motionY);
                this.strafeSpeed *= 2.149;

                break;
            }

            case 3: {
                this.strafeSpeed = this.distance - 0.66 * (this.distance - this.getBaseMoveSpeed());
                break;
            }

            default: {
                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically || strafeStage > 0) {
                    this.strafeStage = (mc.player.moveForward != 0.0f && mc.player.moveStrafing != 0.0f) ? 1 : 0;
                }

                this.strafeSpeed = this.distance - this.distance / 159.0;
                break;
            }
        }

        this.strafeSpeed = Math.max(this.strafeSpeed, this.getBaseMoveSpeed());

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

        double radYaw = Math.toRadians(yaw),
                sin = Math.sin(radYaw),
                cos = Math.cos(radYaw);

        event.setX(forward * this.strafeSpeed * -sin + strafe * this.strafeSpeed * cos * 0.99);
        event.setZ(forward * this.strafeSpeed * cos - strafe * this.strafeSpeed * -sin * 0.99);

        ++this.strafeStage;
    }

    private double getBaseMoveSpeed() {
        double base = this.speed.getValue().doubleValue() / 100.0;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            base *= 1.0 + 0.2 * Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
        }

        return base;
    }

    private boolean shouldStop() {
        return Inferno.moduleManager.getModule(ElytraFly.class).isToggled() || Inferno.moduleManager.getModule(Freecam.class).isToggled();
    }

    public enum Mode {
        STRAFE, ONGROUND, VANILLA
    }

    public enum Hop {
        JUMP, MOTION
    }
}