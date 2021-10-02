package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "AntiVoid", description = "Tries to get you out of the void", category = Module.Category.PLAYER)
public class AntiVoid extends Module {
    public final Setting<Mode> mode = new Setting<>("Mode", Mode.FLOAT);
    public final Setting<Boolean> onGround = new Setting<>("OnGround", false);
    public final Setting<Float> floatAmount = new Setting<>("FloatAmount", 0.2f, 0.1, 1.0f, (v) -> mode.getValue() == Mode.FLOAT);
    public final Setting<Boolean> packet = new Setting<>("Packet", true, (v) -> mode.getValue() == Mode.TELEPORT);
    public final Setting<Integer> teleportAmount = new Setting<>("TeleportAmount", 2, 1, 10, (v) -> mode.getValue() == Mode.TELEPORT);

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
