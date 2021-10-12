package me.sxmurai.inferno.client.features.modules.player;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.timing.Timer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

@Module.Define(name = "AntiAFK", description = "Stops you from being kicked for being AFK", category = Module.Category.PLAYER)
public class AntiAFK extends Module {
    private static final Random RNG = new Random();

    public final Value<Boolean> rotate = new Value<>("Rotate", true);
    public final Value<Boolean> punch = new Value<>("Punch", true);
    public final Value<Boolean> jump = new Value<>("Jump", true);
    public final Value<Boolean> sneak = new Value<>("Sneak", true);
    public final Value<Boolean> message = new Value<>("Message", false);
    public final Value<Integer> delay = new Value<>("Delay", 1, 2, 10);
    public final Value<Integer> randomDelay = new Value<>("RandomDelay", 5, 1, 30);

    private final Timer timer = new Timer();
    private int sneakTicks = 0;
    private int requiredSneakTicks = 0;

    @Override
    protected void onDeactivated() {
        this.timer.reset();
        if (this.sneakTicks != 0) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }

        this.sneakTicks = 0;
        this.requiredSneakTicks = 0;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.sneak.getValue()) {
            ++this.sneakTicks;
            if (this.sneakTicks >= this.requiredSneakTicks) {
                this.sneakTicks = 0;
                this.requiredSneakTicks = 0;
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
        }

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
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

                this.sneakTicks = 0;
                this.requiredSneakTicks = this.random(1, 3);
                break;
            }

            case 5: {
                mc.player.sendChatMessage(this.weDoABitOfTrolling("how fun, I have AntiAFK on and i'm sending this to not get kicked! fun!!" + this.random(2489, 92472)));
                break;
            }
        }

    }

    private String weDoABitOfTrolling(String text) {
        return Arrays.stream(text.split("")).map((str) -> {
            String s = RNG.nextBoolean() ? str.toUpperCase() : str.toLowerCase();

            if (RNG.nextBoolean()) {
                s = s.replaceAll("o", "0");
            }

            if (RNG.nextBoolean()) {
                s = s.replaceAll("i", "1");
            }

            return s;
        }).collect(Collectors.joining(""));
    }

    private int random(int min, int max) {
        return RNG.nextInt(max + min) - min;
    }
}
