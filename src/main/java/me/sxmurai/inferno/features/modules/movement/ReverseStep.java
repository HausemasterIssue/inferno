package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "ReverseStep", description = "Makes you fall down faster", category = Module.Category.MOVEMENT)
public class ReverseStep extends Module {
    public final Setting<Double> speed = this.register(new Setting<>("Speed", 1.0, 0.1, 2.0));
    public final Setting<Boolean> strict = this.register(new Setting<>("Strict", false));
    public final Setting<Boolean> sneak = this.register(new Setting<>("Sneak", false));

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.onGround) {
            // @todo strict
            if (sneak.getValue()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }

            mc.player.motionY -= speed.getValue();

            if (sneak.getValue()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
        }
    }
}
