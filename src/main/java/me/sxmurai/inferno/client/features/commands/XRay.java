package me.sxmurai.inferno.client.features.commands;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.features.modules.render.Xray;
import me.sxmurai.inferno.client.manager.managers.commands.Command;
import me.sxmurai.inferno.client.manager.managers.commands.exceptions.InvalidArgumentException;
import me.sxmurai.inferno.client.manager.managers.commands.exceptions.InvalidUsageException;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.List;

@Command.Define(handles = {"xray", "x", "xrayblocks", "xblock"}, description = "Manages the blocks used by the Xray module")
public class XRay extends Command {
    @Override
    public void execute(List<String> args) throws Exception {
        if (args.isEmpty()) {
            throw new InvalidUsageException("Please provide a block name");
        }

        Block block = Block.getBlockFromName(String.join("_", args));
        if (block == null || block == Blocks.AIR) {
            throw new InvalidArgumentException("Please provide a valid block");
        }

        if (Inferno.xrayManager.isXrayBlock(block)) {
            Inferno.xrayManager.remove(block);
            Command.send("Removed the block " + block + ".");
        } else {
            Inferno.xrayManager.add(block);
            Command.send("Added the block " + block + ".");
        }

        if (Xray.INSTANCE.isToggled()) {
            mc.renderGlobal.loadRenderers();
        }
    }
}
