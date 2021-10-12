package me.sxmurai.inferno.client.features.modules.movement;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.client.features.modules.player.Freecam;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// @todo remove skidded shit
@Module.Define(name = "ReverseStep", description = "Makes you fall down faster", category = Module.Category.MOVEMENT)
public class ReverseStep extends Module {
    public final Value<Double> height = new Value<>("Height", 2.0, 0.0, 5.0);
    public final Value<Double> speed = new Value<>("Speed", 1.0, 0.1, 2.0);
    public final Value<Boolean> liquids = new Value<>("Liquids", false);
    public final Value<Boolean> strict = new Value<>("Strict", false);

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.onGround && !this.shouldStop()) {
            if (!this.liquids.getValue() && (mc.player.isInWater() || mc.player.isInLava())) {
                return;
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
        }
    }

    private boolean shouldStop() {
        return Inferno.moduleManager.getModule(Freecam.class).isToggled();
    }
}
