package me.sxmurai.inferno.client.features.modules.movement;

import me.sxmurai.inferno.api.events.entity.PushEvent;
import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Velocity", description = "Stops knockback and shit", category = Module.Category.MOVEMENT)
public class Velocity extends Module {
    public final Value<Float> vertical = new Value<>("Vertical", 0.0f, 0.0f, 100.0f);
    public final Value<Float> horizontal = new Value<>("Horizontal", 0.0f, 0.0f, 100.0f);
    public final Value<Boolean> explosions = new Value<>("Explosions", false);
    public final Value<Boolean> knockback = new Value<>("Knockback", false);
    public final Value<Boolean> push = new Value<>("Push", false);
    public final Value<Boolean> blocks = new Value<>("Blocks", false);
    public final Value<Boolean> liquid = new Value<>("Liquids", false);
    public final Value<Boolean> bobbers = new Value<>("Bobbers", false);

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Module.fullNullCheck()) {
            if (knockback.getValue() && event.getPacket() instanceof SPacketEntityVelocity) {
                SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();
                if (packet.getEntityID() == mc.player.entityId) {
                    packet.motionX *= horizontal.getValue().intValue();
                    packet.motionY *= vertical.getValue().intValue();
                    packet.motionZ *= horizontal.getValue().intValue();
                }
            }

            if (explosions.getValue() && event.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion packet = (SPacketExplosion) event.getPacket();
                packet.motionX *= horizontal.getValue();
                packet.motionY *= vertical.getValue();
                packet.motionZ *= horizontal.getValue();
            }

            if (bobbers.getValue() && event.getPacket() instanceof SPacketEntityStatus) {
                SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
                if (packet.getOpCode() == 31 && packet.getEntity(mc.world) instanceof EntityFishHook) {
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
        if (!Module.fullNullCheck()) {
            switch (event.getType()) {
                case LIQUID: {
                    event.setCanceled(liquid.getValue());
                    break;
                }

                case BLOCKS: {
                    event.setCanceled(blocks.getValue());
                    break;
                }

                case ENTITY: {
                    if (push.getValue()) {
                        // @todo
                    }
                    break;
                }
            }
        }
    }
}
