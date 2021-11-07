package me.sxmurai.inferno.impl.features.module.modules.miscellaneous;

import me.sxmurai.inferno.impl.event.network.PacketEvent;
import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.option.Option;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "TickShift")
@Module.Info(description = "Shifts around your ticks xd")
public class TickShift extends Module {
    public final Option<Float> rate = new Option<>("Rate", 2.0f, 1.1f, 20.0f);
    public final Option<Integer> ticks = new Option<>("Ticks", 10, 1, 100);
    public final Option<Boolean> autoDisable = new Option<>("AutoDisable", true);
    public final Option<Boolean> moveEnable = new Option<>("MoveEnable", true);

    private final Queue<CPacketPlayer> packets = new ConcurrentLinkedQueue<>();
    private boolean stop = false; // this is because of my autism
    private int passed = 0;

    @Override
    protected void onDeactivated() {
        if (fullNullCheck()) {
            mc.timer.tickLength = 50.0f;

            if (!this.packets.isEmpty()) {
                this.empty();
            }
        }

        this.passed = 0;
    }

    @Override
    public void onTick() {
        ++this.passed;
        if (this.passed >= this.ticks.getValue()) {
            this.passed = 0;
            if (this.autoDisable.getValue()) {
                this.toggle();
            } else {
                mc.timer.tickLength = 50.0f;
                this.empty();
            }

            return;
        }
        if(moveEnable.getValue()) {
            if(mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) {
                mc.timer.tickLength = 50.0f / this.rate.getValue();
            }
        } else {
            mc.timer.tickLength = 50.0f / this.rate.getValue();   
        }
        
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            if (!this.stop) {
                this.packets.add((CPacketPlayer) event.getPacket());
            }

            event.setCanceled(true);
        }
    }

    private void empty() {
        if (this.stop) {
            return;
        }

        this.stop = true;
        while (!this.packets.isEmpty()) {
            CPacketPlayer packet = this.packets.poll();
            if (packet == null) {
                break;
            }

            mc.player.connection.sendPacket(packet);
        }

        this.stop = false;
    }
}
