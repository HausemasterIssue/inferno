package me.sxmurai.inferno.client.features.modules.movement;

import me.sxmurai.inferno.api.events.entity.MoveEvent;
import me.sxmurai.inferno.api.events.entity.UpdateMoveEvent;
import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.api.utils.RotationUtils;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Speed", description = "Speeds you up", category = Module.Category.MOVEMENT)
public class Speed extends Module {
    public final Value<Mode> mode = new Value<>("Mode", Mode.STRAFE);
    public final Value<Boolean> timer = new Value<>("Timer", false);
    public final Value<Double> speed = new Value<>("Speed", 0.2, 0.1, 5.0);

    public final Value<Boolean> antiLag = new Value<>("AntiLag", true, (v) -> mode.getValue() == Mode.STRAFE || mode.getValue() == Mode.STRICT_STRAFE);

    // other shit
    private boolean up = false;

    // strafe
    private double moveSpeed = 0.0;
    private double lastDistance = 0.0;
    private int stage = 4;

    @Override
    public void onUpdate() {
        if (this.up && this.mode.getValue() != Mode.ONGROUND) {
            mc.player.jump();
            this.up = false;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof SPacketPlayerPosLook && this.antiLag.getValue() && (this.mode.getValue() == Mode.STRAFE || this.mode.getValue() == Mode.STRICT_STRAFE)) {
            this.moveSpeed = 0.0;
            this.lastDistance = 0.0;
            this.stage = 4;
        }
    }

    @SubscribeEvent
    public void onUpdateMove(UpdateMoveEvent event) {
        if (event.getEra() == UpdateMoveEvent.Era.PRE) {
            this.lastDistance = Math.sqrt(Math.pow(mc.player.posX - mc.player.prevPosX, 2.0) + Math.pow(mc.player.posZ - mc.player.prevPosZ, 2.0));
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        switch (this.mode.getValue()) {
            case STRAFE:
            case STRICT_STRAFE: {
                if (this.isMoving() && mc.player.onGround) {
                    this.stage = 2;
                }

                switch (this.stage) {
                    case 0: {
                        this.moveSpeed = this.mode.getValue() == Mode.STRICT_STRAFE ? 0.33119999999999994 : 1.29 * this.getBaseNCPSpeed();
                        this.lastDistance = 0.0;
                        ++this.stage;
                        break;
                    }

                    case 2: {
                        if (this.isMoving() && mc.player.onGround) {
                            double y = 0.3995;
                            if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                                y += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1;
                            }

                            event.setY(mc.player.motionY = y);
                            this.moveSpeed *= 2.149;
                        }
                        break;
                    }

                    case 3: {
                        this.moveSpeed = this.lastDistance - 0.76 * (this.lastDistance - (this.mode.getValue() == Mode.STRICT_STRAFE ? 0.2372 : this.getBaseNCPSpeed()));
                        break;
                    }

                    default: {
                        if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).isEmpty() || mc.player.collidedVertically && this.stage > 0) {
                            this.stage = 0;
                        }

                        this.moveSpeed = this.lastDistance - this.lastDistance / 159.0;
                        break;
                    }
                }

                this.moveSpeed = Math.max(this.moveSpeed, this.getBaseNCPSpeed());

                double forward = mc.player.movementInput.moveForward,
                        strafe = mc.player.movementInput.moveStrafe,
                        yaw = mc.player.rotationYaw;

                if (forward == 0.0 && strafe == 0.0) {
                    event.setX(0.0);
                    event.setZ(0.0);
                } else if (forward != 0.0 && strafe != 0.0) {
                    forward *= Math.sin(0.7853981633974483);
                    strafe *= Math.cos(0.7853981633974483);
                }

                double rad = Math.toRadians(yaw);
                double sin = Math.sin(rad);
                double cos = Math.cos(rad);

                event.setX((forward * this.moveSpeed * -sin + strafe * this.moveSpeed * cos) * 0.99);
                event.setZ((forward * this.moveSpeed * cos - strafe * this.moveSpeed * -sin) * 0.99);

                ++this.stage;
                break;
            }

            case YPORT: {
                if (this.isMoving()) {
                    if (mc.player.onGround) {
                        this.up = true;
                    }

                    RotationUtils.Rotation rotation = RotationUtils.getDirectionalSpeed(this.getBaseNCPSpeed() + (this.speed.getValue() / 10.0));

                    event.setX(rotation.getYaw());
                    event.setZ(rotation.getPitch());
                } else {
                    mc.player.motionY = -1.0;
                }
                break;
            }

            case BHOP:
            case VANILLA: {
                if (this.isMoving()) {
                    RotationUtils.Rotation rotation = RotationUtils.getDirectionalSpeed(this.speed.getValue());

                    event.setX(rotation.getYaw());
                    event.setZ(rotation.getPitch());

                    if (this.mode.getValue() == Mode.BHOP) {
                        if (mc.player.onGround) {
                            this.up = true;
                        }

                        event.setX(event.getX() * 1.2);
                        event.setZ(event.getZ() * 1.2);
                    }
                }
                break;
            }

            case ONGROUND: {
                event.setX(event.getX() * 1.590000033378601);
                event.setZ(event.getZ() * 1.590000033378601);

                if (this.up) {
                    event.setY(event.getY() + 0.4);
                }

                this.up = !this.up;
                break;
            }
        }
    }

    private double getBaseNCPSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            baseSpeed *= 1.0 + 0.2 * (mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1);
        }

        return baseSpeed;
    }

    private boolean isMoving() {
        return mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f;
    }

    public enum Mode {
        STRAFE, STRICT_STRAFE, YPORT, BHOP, VANILLA, ONGROUND
    }
}