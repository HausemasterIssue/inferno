package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.*;
import me.sxmurai.inferno.utils.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@Module.Define(name = "Scaffold", description = "Places blocks under you", category = Module.Category.PLAYER)
public class Scaffold extends Module {
    private static final BlockPos[] DIRECTION_OFFSETS = new BlockPos[] {
            new BlockPos(0, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, -1),
            new BlockPos(0, 0, 1)
    };

    public final Setting<Boolean> offhand = this.register(new Setting<>("Offhand", true));
    public final Setting<Boolean> packet = this.register(new Setting<>("Packet", true));
    public final Setting<Boolean> tower = this.register(new Setting<>("Tower", false));
    public final Setting<Boolean> stop = this.register(new Setting<>("Stop", true, (v) -> tower.getValue()));
    public final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
    public final Setting<Boolean> normalize = this.register(new Setting<>("Normalize", true, (v) -> rotate.getValue()));
    public final Setting<Boolean> swing = this.register(new Setting<>("Swing", true));
    public final Setting<Boolean> silentSwitch = this.register(new Setting<>("SilentSwitch", false));
    public final Setting<SwitchBack> switchBack = this.register(new Setting<>("SwitchBack", SwitchBack.LOOPEND));

    private final Timer timer = new Timer();
    private final Queue<Pair<BlockPos, EnumFacing>> blocks = new ConcurrentLinkedDeque<>();
    private BlockPos pos;

    @Override
    protected void onActivated() {
        timer.reset();
        pos = null;
        blocks.clear();
    }

    @Override
    protected void onDeactivated() {
        timer.reset();
        pos = null;
        blocks.clear();
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (!mc.gameSettings.keyBindJump.pressed) {
            timer.reset();
        }

        pos = new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ);
        if (BlockUtil.getBlockFromPos(pos) == Blocks.AIR) {
            int oldSlot = mc.player.inventory.currentItem;
            EnumHand hand = EnumHand.MAIN_HAND;
            if (!InventoryUtils.isHolding(ItemBlock.class)) {
                int slot = InventoryUtils.getHotbarItemSlot(ItemBlock.class, offhand.getValue());
                if (slot == -1) {
                    return;
                }

                if (slot == 45) {
                    hand = EnumHand.OFF_HAND;
                } else {
                    InventoryUtils.switchTo(slot, silentSwitch.getValue());
                }
            }

            Pair<BlockPos, EnumFacing> place = getPlacePos(pos);
            if (place == null) {
                return;
            }

            while (!blocks.isEmpty()) {
                Pair<BlockPos, EnumFacing> data = blocks.poll();
                BlockPos p = data.getKey();
                if (p == null) {
                    break;
                }

                if (mc.player.getDistance(p.x, p.y, p.z) > 2.0f) {
                    continue;
                }

                if (mc.gameSettings.keyBindJump.isKeyDown() && tower.getValue()) {
                    mc.player.motionX *= 0.3;
                    mc.player.motionZ *= 0.3;
                    mc.player.jump();

                    if (stop.getValue() && timer.passedMs(1200L)) {
                        mc.player.motionY -= 0.28;
                        timer.reset();
                    }
                }

                if (rotate.getValue()) {
                    RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.x + 0.5, pos.y - 0.5, pos.z + 0.5));
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotation.getYaw(), rotation.getPitch(), mc.player.onGround));
                    mc.player.rotationYawHead = rotation.getYaw();
                }

                BlockUtil.place(p, hand, swing.getValue(), false, packet.getValue());
                if (switchBack.getValue() == SwitchBack.PLACED) {
                    InventoryUtils.switchTo(oldSlot, silentSwitch.getValue());
                }
            }

            if (switchBack.getValue() == SwitchBack.LOOPEND) {
                InventoryUtils.switchTo(oldSlot, silentSwitch.getValue());
            }
        }
    }

    private Pair<BlockPos, EnumFacing> getPlacePos(BlockPos origin) {
        if (BlockUtil.getBlockFromPos(origin) != Blocks.AIR) {
            return null;
        }

        for (BlockPos offset : DIRECTION_OFFSETS) {
            for (EnumFacing facing : EnumFacing.values()) {
                if (facing == EnumFacing.DOWN) {
                    continue;
                }

                BlockPos pos = origin.add(offset);
                if (facing == EnumFacing.UP) {
                    return add(new Pair<>(pos, EnumFacing.UP));
                } else {
                    return add(new Pair<>(pos.add(facing.getDirectionVec()), facing));
                }
            }
        }

        return null;
    }

    public Pair<BlockPos, EnumFacing> add(Pair<BlockPos, EnumFacing> pair) {
        blocks.add(pair);
        return pair;
    }

    public enum SwitchBack {
        LOOPEND, PLACED
    }
}