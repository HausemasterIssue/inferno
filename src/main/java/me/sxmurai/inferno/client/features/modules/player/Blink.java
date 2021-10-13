package me.sxmurai.inferno.client.features.modules.player;

import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.api.events.network.SelfConnectionEvent;
import me.sxmurai.inferno.api.utils.timing.Timer;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "Blink", description = "Suspends movement packets until a criteria is met", category = Module.Category.PLAYER)
public class Blink extends Module {
    public final Value<Mode> mode = new Value<>("Mode", Mode.MANUAL);
    public final Value<Boolean> spawnFakePlayer = new Value<>("SpawnFake", true);
    public final Value<Integer> time = new Value<>("Time", 5, 1, 100, (v) -> mode.getValue() == Mode.TIME);
    public final Value<Integer> distance = new Value<>("Distance", 8, 1, 100, (v) -> mode.getValue() == Mode.DISTANCE);
    public final Value<Integer> packets = new Value<>("Packets", 20, 1, 100, (v) -> mode.getValue() == Mode.PACKETS);

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

    @Override
    public void onUpdate() {
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
