package me.sxmurai.inferno.managers;

import me.sxmurai.inferno.features.Feature;
import net.minecraft.util.math.BlockPos;

public class HoleManager extends Feature {
    public static final BlockPos[] SURROUND_POSITIONS = new BlockPos[] {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(0, -1, 0)
    };
}
