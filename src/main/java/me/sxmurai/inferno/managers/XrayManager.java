package me.sxmurai.inferno.managers;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;

// @todo config saving
public class XrayManager {
    private final ArrayList<Block> blocks = new ArrayList<>();

    public XrayManager() {
        blocks.add(Blocks.COAL_ORE);
        blocks.add(Blocks.GOLD_ORE);
        blocks.add(Blocks.IRON_ORE);
        blocks.add(Blocks.REDSTONE_ORE);
        blocks.add(Blocks.LAPIS_ORE);
        blocks.add(Blocks.DIAMOND_ORE);

        blocks.add(Blocks.COAL_BLOCK);
        blocks.add(Blocks.GOLD_BLOCK);
        blocks.add(Blocks.IRON_BLOCK);
        blocks.add(Blocks.REDSTONE_BLOCK);
        blocks.add(Blocks.LAPIS_BLOCK);
        blocks.add(Blocks.DIAMOND_BLOCK);

        blocks.add(Blocks.END_PORTAL_FRAME);
        blocks.add(Blocks.END_PORTAL);
        blocks.add(Blocks.IRON_BARS);
        blocks.add(Blocks.IRON_DOOR);
        blocks.add(Blocks.OBSIDIAN);
    }

    public boolean isXrayBlock(Block block) {
        return blocks.contains(block);
    }
}
