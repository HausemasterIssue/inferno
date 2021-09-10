package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.events.network.SelfConnectionEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.Timer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "Blink", description = "Suspends movement packets until a criteria is met", category = Module.Category.PLAYER)
public class Blink extends Module {
    public final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.MANUAL));
    public final Setting<Boolean> spawnFakePlayer = this.register(new Setting<>("SpawnFake", true));
    public final Setting<Integer> time = this.register(new Setting<>("Time", 5, (v) -> mode.getValue() == Mode.TIME));
    public final Setting<Integer> distance = this.register(new Setting<>("Distance", 8, (v) -> mode.getValue() == Mode.DISTANCE));
    public final Setting<Integer> packets = this.register(new Setting<>("Packets", 20, (v) -> mode.getValue() == Mode.PACKETS));

    private EntityOtherPlayerMP fakePlayer;
    private final Timer timer = new Timer();
    private BlockPos pos;
    private final Queue<CPacketPlayer> movementPackets = new ConcurrentLinkedQueue<>();
    private boolean sending = false;

    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            toggle();
            return;
        }

        spawnEntity(false);
    }

    @Override
    protected void onDeactivated() {
        timer.reset();
        pos = null;
        if (Module.fullNullCheck()) {
            movementPackets.clear();
            spawnEntity(true);
        } else {
            emptyMovementQueue();
        }
    }

    @SubscribeEvent
    public void onLogout(SelfConnectionEvent event) {
        toggle();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof CPacketPlayer && !sending) {
            movementPackets.add((CPacketPlayer) event.getPacket());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        switch (mode.getValue()) {
            case TIME: {
                if (timer.passedS(time.getValue().doubleValue())) {
                    timer.reset();
                    spawnEntity(false);
                    emptyMovementQueue();
                }
                break;
            }

            case PACKETS: {
                if (movementPackets.size() >= packets.getValue()) {
                    spawnEntity(false);
                    emptyMovementQueue();
                }
                break;
            }

            case DISTANCE: {
                if (pos == null) {
                    pos = mc.player.getPosition();
                    return;
                }

                if (mc.player.getDistance(pos.x, pos.y, pos.z) > distance.getValue().floatValue()) {
                    pos = mc.player.getPosition();
                    spawnEntity(false);
                    emptyMovementQueue();
                }
                break;
            }
        }
    }

    private void spawnEntity(boolean onlyRemove) {
        if (fakePlayer != null) {
            mc.world.removeEntity(fakePlayer);
            mc.world.removeEntityDangerously(fakePlayer);
            fakePlayer = null;
        }

        if (onlyRemove) {
            return;
        }

        if (spawnFakePlayer.getValue()) {
            fakePlayer = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
            fakePlayer.copyLocationAndAnglesFrom(mc.player);
            fakePlayer.inventory.copyInventory(mc.player.inventory);
            fakePlayer.setEntityId(-694201338);

            mc.world.spawnEntity(fakePlayer);
        }
    }

    private void emptyMovementQueue() {
        sending = true;
        while (!movementPackets.isEmpty()) {
            CPacketPlayer packet = movementPackets.poll();
            if (packet == null || Module.fullNullCheck()) {
                break;
            }

            mc.player.connection.getNetworkManager().dispatchPacket(packet, null);
        }
        sending = false;
    }

    public enum Mode {
        MANUAL, TIME, DISTANCE, PACKETS
    }
}
