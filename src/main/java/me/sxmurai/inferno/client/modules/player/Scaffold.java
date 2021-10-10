package me.sxmurai.inferno.client.modules.player;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.BlockUtil;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.utils.data.Pair;
import me.sxmurai.inferno.api.utils.timing.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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

    public final Value<Boolean> offhand = new Value<>("Offhand", true);
    public final Value<Boolean> packet = new Value<>("Packet", true);
    public final Value<Boolean> tower = new Value<>("Tower", false);
    public final Value<Boolean> rotate = new Value<>("Rotate", true);
    public final Value<Boolean> swing = new Value<>("Swing", true);
    public final Value<Boolean> silentSwitch = new Value<>("SilentSwitch", false);

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

        this.pos = new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ);
        if (BlockUtil.getBlockFromPos(pos) == Blocks.AIR) {
            int oldSlot = mc.player.inventory.currentItem;
            EnumHand hand = EnumHand.MAIN_HAND;
            if (!InventoryUtils.isHolding(ItemBlock.class)) {
                int slot = InventoryUtils.getHotbarItemSlot(ItemBlock.class, this.offhand.getValue());
                if (slot == -1) {
                    return;
                }

                if (slot == 45) {
                    hand = EnumHand.OFF_HAND;
                } else {
                    InventoryUtils.switchTo(slot, silentSwitch.getValue());
                }
            }

            Pair<BlockPos, EnumFacing> place = this.getPlacePos(pos);
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

                    if (timer.passedMs(1500L)) {
                        mc.player.motionY -= 0.28;
                        timer.reset();
                    }
                }

                BlockUtil.place(p, hand, this.swing.getValue(), false, this.packet.getValue(), this.rotate.getValue());
            }

            if (hand == EnumHand.MAIN_HAND) {
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
}