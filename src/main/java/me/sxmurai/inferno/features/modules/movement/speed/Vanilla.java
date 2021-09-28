package me.sxmurai.inferno.features.modules.movement.speed;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.modules.movement.Speed;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Mode;
import me.sxmurai.inferno.utils.RotationUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Vanilla extends Mode<Speed> {
    public final Setting<Double> vanillaSpeed = this.register(new Setting<>("VanillaSpeed", 2.3, 0.1, 10.0));
    public final Setting<Boolean> vanillaHop = this.register(new Setting<>("VanillaHop", false));

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
