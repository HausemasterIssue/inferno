package me.sxmurai.inferno.client.features.modules.movement;

import me.sxmurai.inferno.api.utils.EntityUtils;
import me.sxmurai.inferno.api.utils.RotationUtils;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;

@Module.Define(name = "EntityControl", description = "Controls ridden entities", category = Module.Category.MOVEMENT)
public class EntityControl extends Module {
    public static EntityControl INSTANCE;

    public final Value<Boolean> betterLook = new Value<>("BetterLook", false);
    public final Value<Boolean> maxJumpPower = new Value<>("MaxJumpPower", false);
    public final Value<Boolean> speedUp = new Value<>("SpeedUp", false);
    public final Value<Double> speed = new Value<>("Speed", 4.5, 0.1, 100.0);

    // entities
    public final Value<Boolean> horse = new Value<>("Horse", true);
    public final Value<Boolean> donkey = new Value<>("Donkey", true);
    public final Value<Boolean> mules = new Value<>("Mules", false);
    public final Value<Boolean> llama = new Value<>("Llama", true);
    public final Value<Boolean> pig = new Value<>("Pig", false);

    public EntityControl() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.maxJumpPower.getValue()) {
            mc.player.horseJumpPower = 1.0f;
            mc.player.horseJumpPowerCounter = -10;
        }

        if (this.speedUp.getValue() && EntityUtils.isRiding(true)) {
            RotationUtils.Rotation rotation = RotationUtils.getDirectionalSpeed(this.speed.getValue() / 10.0);

            mc.player.ridingEntity.motionX = rotation.getYaw();
            mc.player.ridingEntity.motionZ = rotation.getPitch();
        }

        if (this.betterLook.getValue() && EntityUtils.isRiding(true)) {
            mc.player.ridingEntity.rotationYaw = mc.player.rotationYaw;
        }
    }
}
