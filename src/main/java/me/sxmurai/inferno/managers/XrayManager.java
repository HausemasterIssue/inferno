package me.sxmurai.inferno.managers;

import com.google.common.collect.Lists;
import me.sxmurai.inferno.config.XrayConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.List;

// @todo config saving
public class XrayManager {
    private final ArrayList<Block> blocks = new ArrayList<>();
    private final XrayConfig config;

    public XrayManager() {
        this.config = new XrayConfig(this);
        this.config.load();
    }

    public static List<Block> defaultBlocks() {
        return Lists.newArrayList(
                // ores
                Blocks.COAL_ORE,
                Blocks.GOLD_ORE,
                Blocks.IRON_ORE,
                Blocks.REDSTONE_ORE,
                Blocks.LAPIS_ORE,
                Blocks.DIAMOND_ORE,

                // ore blocks
                Blocks.COAL_BLOCK,
                Blocks.GOLD_BLOCK,
                Blocks.IRON_BLOCK,
                Blocks.REDSTONE_BLOCK,
                Blocks.LAPIS_BLOCK,
                Blocks.DIAMOND_BLOCK,

                // other useful shit
                Blocks.END_PORTAL_FRAME,
                Blocks.END_PORTAL,
                Blocks.IRON_BARS,
                Blocks.IRON_DOOR,
                Blocks.OBSIDIAN
        );
    }

    public void add(Block block) {
        this.blocks.add(block);
    }

    public void remove(Block block) {
        this.blocks.remove(block);
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public boolean isXrayBlock(Block block) {
        return this.blocks.contains(block);
    }

    public void unload() {
        this.config.stop();
    }
}
