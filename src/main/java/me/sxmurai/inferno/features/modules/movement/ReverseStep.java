package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.modules.player.Freecam;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "ReverseStep", description = "Makes you fall down faster", category = Module.Category.MOVEMENT)
public class ReverseStep extends Module {
    public final Setting<Double> height = this.register(new Setting<>("Height", 2.0, 0.0, 5.0));
    public final Setting<Double> speed = this.register(new Setting<>("Speed", 1.0, 0.1, 2.0));
    public final Setting<Boolean> liquids = this.register(new Setting<>("Liquids", false));
    public final Setting<Boolean> strict = this.register(new Setting<>("Strict", false));
    public final Setting<Boolean> sneak = this.register(new Setting<>("Sneak", false));

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.onGround && !this.shouldStop()) {
            if (!this.liquids.getValue() && (mc.player.isInWater() || mc.player.isInLava())) {
                return;
            }

            if (this.sneak.getValue()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }

            if (this.strict.getValue()) {
                for (double y = 0.0; y < this.height.getValue() + 0.5; y += 0.01) {
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                        mc.player.motionY -= this.speed.getValue();
                        break;
                    }
                }
            } else {
                mc.player.motionY -= this.speed.getValue();
            }

            if (this.sneak.getValue()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
        }
    }

    private boolean shouldStop() {
        return Inferno.moduleManager.getModule(Freecam.class).isToggled();
    }
}
