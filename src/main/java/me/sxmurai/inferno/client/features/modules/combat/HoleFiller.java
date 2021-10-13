package me.sxmurai.inferno.client.features.modules.combat;

import me.sxmurai.inferno.api.utils.BlockUtil;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.manager.managers.misc.HoleManager;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "HoleFiller", description = "(cringe) Fills in safe holes", category = Module.Category.COMBAT)
public class HoleFiller extends Module {
    public final Value<FillBlock> block = new Value<>("Block", FillBlock.OBSIDIAN);
    public final Value<Boolean> offhand = new Value<>("Offhand", true);
    public final Value<Integer> range = new Value<>("Range", 5, 1, 10);
    public final Value<Boolean> rotate = new Value<>("Rotate", true);
    public final Value<Boolean> packet = new Value<>("Packet", false);
    public final Value<Boolean> sneak = new Value<>("Sneak", false);
    public final Value<Boolean> swing = new Value<>("Swing", true);
    public final Value<Boolean> silent = new Value<>("Silent", false);
    public final Value<Integer> blocks = new Value<>("Blocks", 1, 1, 5);

    private final Queue<BlockPos> positions = new ConcurrentLinkedQueue<>();
    private EnumHand hand = EnumHand.MAIN_HAND;
    private int oldSlot = -1;

    @Override
    public void onTick() {
        if (this.positions.isEmpty()) {
            for (HoleManager.Hole hole : Inferno.holeManager.getHoles()) {
                BlockPos pos = hole.getPos();
                if (mc.player.getDistance(pos.x, pos.y, pos.z) > this.range.getValue()) {
                    continue;
                }

                this.positions.add(pos);
            }

            if (this.positions.isEmpty()) {
                if (this.oldSlot == -1) {
                    return;
                }

                InventoryUtils.switchTo(this.oldSlot, false);
                this.oldSlot = -1;
                
                return;
            }

            if (this.block.getValue() == FillBlock.WEB ?
                    !InventoryUtils.isHoldingBlock(BlockWeb.class, this.offhand.getValue()) :
                    !InventoryUtils.isHolding(this.block.getValue().block, this.offhand.getValue())
            ) {
                    int slot = this.block.getValue() == FillBlock.WEB ?
                            InventoryUtils.getHotbarBlockSlot(BlockWeb.class, this.offhand.getValue()) :
                            InventoryUtils.getHotbarBlockSlot(this.block.getValue().block, this.offhand.getValue());

                    if (slot == -1) {
                        return;
                    }

                    if (slot == 45) {
                        this.hand = EnumHand.OFF_HAND;
                    } else {
                        this.oldSlot = mc.player.inventory.currentItem;
                        InventoryUtils.switchTo(slot, this.silent.getValue());
                    }

                    mc.player.setActiveHand(this.hand);
            }
        } else {
            int b = 0;
            while (!this.positions.isEmpty()) {
                BlockPos pos = this.positions.poll();
                if (pos == null) {
                    break;
                }

                BlockUtil.place(pos, this.hand, this.swing.getValue(), this.sneak.getValue(), this.packet.getValue(), this.rotate.getValue());
                ++b;

                if (b >= this.blocks.getValue()) {
                    return;
                }
            }
        }
    }

    public enum FillBlock {
        OBSIDIAN(Blocks.OBSIDIAN),
        ECHEST(Blocks.ENDER_CHEST),
        WEB(null);

        private final Block block;

        FillBlock(Block block) {
            this.block = block;
        }
    }
}
