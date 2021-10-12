package me.sxmurai.inferno.client.features.modules.player;

import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "AntiHunger", description = "Attempts to stop you from loosing hunger", category = Module.Category.PLAYER)
public class AntiHunger extends Module {
    public final Value<Boolean> ground = new Value<>("Ground", true);
    public final Value<Boolean> fallCheck = new Value<>("FallCheck", true, (v) -> ground.getValue());
    public final Value<Boolean> sprint = new Value<>("Sprint", false);

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck()) {
            if (event.getPacket() instanceof CPacketPlayer) {
                if (this.ground.getValue()) {
                    if (this.fallCheck.getValue() && mc.player.fallDistance > 3) {
                        return;
                    }

                    ((CPacketPlayer) event.getPacket()).onGround = true;
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
