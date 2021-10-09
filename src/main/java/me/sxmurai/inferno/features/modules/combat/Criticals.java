package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Criticals", description = "Applies critical hits", category = Module.Category.COMBAT)
public class Criticals extends Module {
    public final Setting<Mode> mode = new Setting<>("Mode", Mode.PACKET);
    public final Setting<Boolean> entityCheck = new Setting<>("EntityCheck", false);

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            Entity entity = packet.getEntityFromWorld(mc.world);

            if (packet.action == CPacketUseEntity.Action.ATTACK && (entityCheck.getValue() ? entity instanceof EntityLiving : entity != null) && mc.player.onGround) {
                switch (mode.getValue()) {
                    case JUMP: {
                        mc.player.jump();
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.055, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        break;
                    }

                    case MINIJUMP: {
                        mc.player.motionY = 0.2;
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.02, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        break;
                    }

                    case PACKET: {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.3, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        break;
                    }

                    case BYPASS: {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.062602401692772D, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0726023996066094D, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                        break;
                    }
                }
            }
        }
    }

    public enum Mode {
        PACKET, MINIJUMP, JUMP, BYPASS
    }
}
