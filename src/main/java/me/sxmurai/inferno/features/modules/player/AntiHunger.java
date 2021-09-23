package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "AntiHunger", description = "Spoofs your onGround state", category = Module.Category.PLAYER)
public class AntiHunger extends Module {
    public final Setting<Boolean> noSprint = this.register(new Setting<>("NoSprint", false));

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck()) {
            if (event.getPacket() instanceof CPacketPlayer) {
                ((CPacketPlayer) event.getPacket()).onGround = false;
            } else if (event.getPacket() instanceof CPacketEntityAction && ((CPacketEntityAction) event.getPacket()).getAction() == CPacketEntityAction.Action.START_SPRINTING && !this.noSprint.getValue()) {
                event.setCanceled(true);
            }
        }
    }
}
