package me.sxmurai.inferno.client.features.modules.player;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "AntiVoid", description = "Tries to get you out of the void", category = Module.Category.PLAYER)
public class AntiVoid extends Module {
    public final Value<Mode> mode = new Value<>("Mode", Mode.FLOAT);
    public final Value<Boolean> onGround = new Value<>("OnGround", false);
    public final Value<Float> floatAmount = new Value<>("FloatAmount", 0.2f, 0.1, 1.0f, (v) -> mode.getValue() == Mode.FLOAT);
    public final Value<Boolean> packet = new Value<>("Packet", true, (v) -> mode.getValue() == Mode.TELEPORT);
    public final Value<Integer> teleportAmount = new Value<>("TeleportAmount", 2, 1, 10, (v) -> mode.getValue() == Mode.TELEPORT);

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.posY <= 0.0) {
            switch (this.mode.getValue()) {
                case FLOAT: {
                    mc.player.motionY = this.floatAmount.getValue();
                    break;
                }

                case SUSPEND: {
                    mc.player.motionY = 0.0;
                    break;
                }

                case TELEPORT: {
                    mc.player.setPosition(mc.player.posX, mc.player.posY + this.teleportAmount.getValue(), mc.player.posZ);
                    if (this.packet.getValue()) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + this.teleportAmount.getValue(), mc.player.posZ, this.onGround.getValue()));
                    }
                    break;
                }
            }

            if (this.onGround.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayer(true));
            }
        }
    }

    public enum Mode {
        FLOAT, SUSPEND, TELEPORT
    }
}
