package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.EntityUtils;
import me.sxmurai.inferno.utils.RotationUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "EntityControl", description = "Controls ridden entities", category = Module.Category.MOVEMENT)
public class EntityControl extends Module {
    public static EntityControl INSTANCE;

    public final Setting<Boolean> betterLook = new Setting<>("BetterLook", false);
    public final Setting<Boolean> maxJumpPower = new Setting<>("MaxJumpPower", false);
    public final Setting<Boolean> speedUp = new Setting<>("SpeedUp", false);
    public final Setting<Double> speed = new Setting<>("Speed", 4.5, 0.1, 100.0);

    // entities
    public final Setting<Boolean> horse = new Setting<>("Horse", true);
    public final Setting<Boolean> donkey = new Setting<>("Donkey", true);
    public final Setting<Boolean> mules = new Setting<>("Mules", false);
    public final Setting<Boolean> llama = new Setting<>("Llama", true);
    public final Setting<Boolean> pig = new Setting<>("Pig", false);

    public EntityControl() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.maxJumpPower.getValue()) {
            mc.player.horseJumpPower = 1.0f;
            mc.player.horseJumpPowerCounter = -10;
        }

        if (this.speedUp.getValue() && EntityUtils.isRiding(true)) {
            RotationUtils.Rotation rotation = RotationUtils.getDirectionalSpeed(this.speed.getValue() / 10.0);

            mc.player.motionX = rotation.getYaw();
            mc.player.motionZ = rotation.getPitch();
        }

        if (this.betterLook.getValue() && EntityUtils.isRiding(true)) {
            mc.player.ridingEntity.rotationYaw = mc.player.rotationYaw;
        }
    }
}
