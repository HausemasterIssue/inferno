package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.Timer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

@Module.Define(name = "AntiAFK", description = "Stops you from being kicked for being AFK", category = Module.Category.PLAYER)
public class AntiAFK extends Module {
    private static final Random RNG = new Random();

    public final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
    public final Setting<Boolean> punch = this.register(new Setting<>("Punch", true));
    public final Setting<Boolean> jump = this.register(new Setting<>("Jump", true));
    public final Setting<Boolean> sneak = this.register(new Setting<>("Sneak", true));
    public final Setting<Boolean> message = this.register(new Setting<>("Message", false));
    public final Setting<Integer> delay = this.register(new Setting<>("Delay", 1, 2, 10));
    public final Setting<Integer> randomDelay = this.register(new Setting<>("RandomDelay", 5, 1, 30));

    private final Timer timer = new Timer();

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.timer.passedS(this.delay.getValue() + RNG.nextInt(this.randomDelay.getValue()))) {
            this.timer.reset();

            int action = this.random(1, 5);
            for (int i = 0; i < 6; ++i) {
                if (!this.check(action)) {
                    action = action + 1 > 5 ? 1 : action + 1;
                } else {
                    this.doAction(action);
                    break;
                }
            }
        }
    }

    private boolean check(int action) {
        switch (action) {
            case 1: return this.rotate.getValue();
            case 2: return this.punch.getValue();
            case 3: return this.jump.getValue();
            case 4: return this.sneak.getValue();
            case 5: return this.message.getValue();
        }

        return true;
    }

    private void doAction(int action) {
        switch (action) {
            case 1: {
                mc.player.rotationYaw = (float) this.random(1, 360);

                int pitch = this.random(1, 90);
                mc.player.rotationPitch = RNG.nextBoolean() ? -pitch : pitch;
                break;
            }

            case 2: {
                mc.player.swingArm(RNG.nextBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                break;
            }

            case 3: {
                mc.player.jump();
                break;
            }

            case 4: {
                mc.player.setSneaking(true);
                mc.player.setSneaking(false);
                break;
            }

            case 5: {
                mc.player.sendChatMessage(this.random(4872, 9357653) + " im sending this message because I have AntiAFK on, interesting isnt it " + this.random(100, 74643));
                break;
            }
        }

    }

    private int random(int min, int max) {
        return RNG.nextInt(max + min) - min;
    }
}
