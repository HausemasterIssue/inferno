package me.sxmurai.inferno.api.events.world;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class BlockDestroyEvent extends Event {
    private final BlockPos pos;

    public BlockDestroyEvent(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }
}
