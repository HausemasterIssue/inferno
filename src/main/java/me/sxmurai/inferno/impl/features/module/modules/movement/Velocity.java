package me.sxmurai.inferno.impl.features.module.modules.movement;

import me.sxmurai.inferno.impl.event.entity.PushEvent;
import me.sxmurai.inferno.impl.event.network.PacketEvent;
import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.option.Option;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Velocity", category = Module.Category.Movement)
@Module.Info(description = "Modifies the velocity you take")
public class Velocity extends Module {
    public final Option<Float> vertical = new Option<>("Vertical", 0.0f, 0.0f, 100.0f);
    public final Option<Float> horizontal = new Option<>("Horizontal", 0.0f, 0.0f, 100.0f);

    public final Option<Boolean> knockback = new Option<>("Knockback", true);
    public final Option<Boolean> explosions = new Option<>("Explosions", true);
    public final Option<Boolean> blocks = new Option<>("Blocks", false);
    public final Option<Boolean> liquid = new Option<>("Liquid", false);
    public final Option<Boolean> push = new Option<>("NoPush", true);
    public final Option<Boolean> bobbers = new Option<>("Bobbers", false);

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (fullNullCheck()) {
            if (event.getPacket() instanceof SPacketEntityVelocity) {
                if (!this.knockback.getValue()) {
                    return;
                }

                SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();
                if (packet.getEntityID() == mc.player.entityId) {
                    packet.motionX *= this.horizontal.getValue().intValue();
                    packet.motionY *= this.vertical.getValue().intValue();
                    packet.motionZ *= this.horizontal.getValue().intValue();
                }
            } else if (event.getPacket() instanceof SPacketExplosion) {
                if (!this.explosions.getValue()) {
                    return;
                }

                SPacketExplosion packet = (SPacketExplosion) event.getPacket();
                packet.motionX *= this.horizontal.getValue();
                packet.motionY *= this.vertical.getValue();
                packet.motionZ *= this.horizontal.getValue();
            } else if (event.getPacket() instanceof SPacketEntityStatus) {
                if (!this.bobbers.getValue()) {
                    return;
                }

                SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
                if (packet.getEntity(mc.world) instanceof EntityFishHook && packet.getOpCode() == 31) {
                    EntityFishHook hook = (EntityFishHook) packet.getEntity(mc.world);
                    if (hook.caughtEntity == mc.player) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPush(PushEvent event) {
        if (event.getEntity() == mc.player) {
            if (event.getMaterial() == PushEvent.Type.ENTITY) {
                if (!this.push.getValue()) {
                    return;
                }

                PushEvent.Entity actualEvent = (PushEvent.Entity) event;
                if (this.vertical.getValue() == 0.0f && this.horizontal.getValue() == 0.0f) {
                    actualEvent.setCanceled(true);
                    return;
                }

                actualEvent.setX(actualEvent.getX() * this.horizontal.getValue().doubleValue());
                actualEvent.setY(actualEvent.getY() * this.vertical.getValue().doubleValue());
                actualEvent.setZ(actualEvent.getZ() * this.horizontal.getValue().doubleValue());
            } else {
                if (event.getMaterial() == PushEvent.Type.BLOCKS) {
                    event.setCanceled(this.blocks.getValue());
                } else if (event.getMaterial() == PushEvent.Type.LIQUID) {
                    event.setCanceled(this.liquid.getValue());
                }
            }
        }
    }
}
