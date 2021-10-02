package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "AntiHunger", description = "Attempts to stop you from loosing hunger", category = Module.Category.PLAYER)
public class AntiHunger extends Module {
    public final Setting<Boolean> ground = new Setting<>("Ground", true);
    public final Setting<Boolean> fallCheck = new Setting<>("FallCheck", true, (v) -> ground.getValue());
    public final Setting<Boolean> sprint = new Setting<>("Sprint", false);

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck()) {
            if (event.getPacket() instanceof CPacketPlayer) {
                if (this.ground.getValue()) {
                    if (this.fallCheck.getValue() && mc.player.fallDistance > 3) {
                        return;
                    }

                    ((CPacketPlayer) event.getPacket()).onGround = false;
                }
            } else if (event.getPacket() instanceof CPacketEntityAction) {
                CPacketEntityAction packet = (CPacketEntityAction) event.getPacket();
                if (packet.getAction() == CPacketEntityAction.Action.START_SPRINTING && this.sprint.getValue()) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
