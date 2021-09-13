package me.sxmurai.inferno.managers;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.utils.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// @todo i'll do double holes later
public class HoleManager extends Feature {
    public static final BlockPos[] SURROUND_POSITIONS = new BlockPos[] {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(0, -1, 0)
    };

    private ArrayList<Hole> holes = new ArrayList<>();
    private int ticks = 0;

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        ++this.ticks;
        if (this.ticks >= 20) { // 1s, so we're not constantly trying to get all blocks around a player
            final ArrayList<Hole> newHoles = new ArrayList<>();

            // @todo the radius & height should become settings in a module.
            List<BlockPos> blocks = BlockUtil.getSphere(new BlockPos(mc.player.getPositionVector()), 5, 5, false, true, 0);
            for (BlockPos pos : blocks) {
                if (BlockUtil.getBlockFromPos(pos) != Blocks.AIR) {
                    continue;
                }

                if (BlockUtil.getBlockFromPos(pos.add(0, 1, 0)) != Blocks.AIR) {
                    continue;
                }

                if (BlockUtil.getBlockFromPos(pos.add(0, 2, 0)) != Blocks.AIR) {
                    continue;
                }

                List<BlockPos> surrounding = Arrays.stream(SURROUND_POSITIONS).map(pos::add).collect(Collectors.toList());
                if (surrounding.stream().noneMatch((p) -> BlockUtil.getBlockFromPos(p) == Blocks.BEDROCK || BlockUtil.getBlockFromPos(p) == Blocks.OBSIDIAN)) {
                    continue;
                }

                int unsafe = 0, safe = 0;
                for (BlockPos blockPos : surrounding) {
                    Block block = BlockUtil.getBlockFromPos(blockPos);
                    if (block == Blocks.OBSIDIAN) {
                        ++unsafe;
                    } else if (block == Blocks.BEDROCK) {
                        ++safe;
                    }
                }

                if (unsafe + safe != 5) {
                    continue;
                }

                newHoles.add(new Hole(pos, unsafe == 0));
            }

            if (!newHoles.isEmpty()) {
                this.holes = newHoles;
            }
        }
    }

    public ArrayList<Hole> getHoles() {
        return holes;
    }

    public static class Hole {
        private final BlockPos pos;
        private final boolean safe;

        public Hole(BlockPos pos, boolean safe) {
            this.pos = pos;
            this.safe = safe;
        }

        public BlockPos getPos() {
            return pos;
        }

        public boolean isSafe() {
            return safe;
        }
    }
}
