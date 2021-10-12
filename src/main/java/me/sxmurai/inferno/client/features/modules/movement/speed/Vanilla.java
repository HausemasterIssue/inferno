package me.sxmurai.inferno.client.features.modules.movement.speed;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.client.features.modules.movement.Speed;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Mode;
import me.sxmurai.inferno.api.utils.RotationUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Vanilla extends Mode<Speed> {
    public final Value<Double> vanillaSpeed = new Value<>("VanillaSpeed", 2.3, 0.1, 10.0);
    public final Value<Boolean> vanillaHop = new Value<>("VanillaHop", false);

    public Vanilla(Speed module, Enum<?> mode) {
        super(module, mode);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
            if (this.vanillaHop.getValue() && mc.player.onGround) {
                mc.player.jump();
            }

            RotationUtils.Rotation rotation = RotationUtils.getDirectionalSpeed(this.vanillaSpeed.getValue() / 10.0);

            mc.player.motionX = rotation.getYaw();
            mc.player.motionZ = rotation.getPitch();
        }
    }
}
