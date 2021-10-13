package me.sxmurai.inferno.client.features.modules.combat;

import me.sxmurai.inferno.api.utils.BlockUtil;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.commands.Command;
import me.sxmurai.inferno.client.manager.managers.commands.text.ChatColor;
import me.sxmurai.inferno.client.manager.managers.commands.text.TextBuilder;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Define(name = "Burrow", description = "becomes cringe and burrows", category = Module.Category.COMBAT)
public class Burrow extends Module {
    public final Value<Mode> mode = new Value<>("Mode", Mode.INSTANT);
    public final Value<Boolean> silentSwitch = new Value<>("SilentSwitch", false);
    public final Value<BlockType> block = new Value<>("Block", BlockType.OBSIDIAN);
    public final Value<Boolean> packetPlace = new Value<>("PacketPlace", false);
    public final Value<Boolean> swing = new Value<>("Swing", true);
    public final Value<Float> rubberband = new Value<>("Rubberband", 3.0f, -5.0f, 5.0f);
    public final Value<Boolean> rotate = new Value<>("Rotate", true);

    private BlockPos origin;
    private int oldSlot;
    private EnumHand hand;

    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            toggle();
            return;
        }

        BlockPos pos = new BlockPos(mc.player.getPositionVector());
        if (intersectsWith(pos)) {
            Command.send(new TextBuilder().append(ChatColor.Dark_Gray, "Already burrowed. Turning off Burrow"));
            toggle();
            return;
        }

        if (mc.world.getBlockState(pos.add(0.0, 1.0, 0.0)).getBlock() != Blocks.AIR || mc.world.getBlockState(pos.add(0.0, 2.0, 0.0)).getBlock() != Blocks.AIR) {
            Command.send(new TextBuilder().append(ChatColor.Dark_Gray, "Not enough room. Turning off Burrow"));
            toggle();
            return;
        }

        int slot = InventoryUtils.getHotbarBlockSlot(block.getValue().getBlock(), true);
        if (slot == -1) {
            Command.send(new TextBuilder().append(ChatColor.Dark_Gray, "No block found. Turning off Burrow"));
            toggle();
            return;
        }

        if (slot == 45) {
            hand = EnumHand.OFF_HAND;
        } else {
            hand = EnumHand.MAIN_HAND;
        }

        oldSlot = mc.player.inventory.currentItem;
        InventoryUtils.switchTo(slot, silentSwitch.getValue());

        origin = pos;
    }

    @Override
    protected void onDeactivated() {
        origin = null;
        oldSlot = -1;
        hand = EnumHand.MAIN_HAND;
    }

    @Override
    public void onUpdate() {
        if (origin != null) {
            switch (mode.getValue()) {
                case INSTANT: {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698, mc.player.posZ, true));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997, mc.player.posZ, true));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214, mc.player.posZ, true));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821, mc.player.posZ, true));

                    BlockUtil.place(origin, hand, swing.getValue(), true, packetPlace.getValue(), this.rotate.getValue());

                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + rubberband.getValue(), mc.player.posZ, true));
                    break;
                }

                case MOTION: {
                    mc.player.jump();
                    BlockUtil.place(origin, hand, swing.getValue(), true, packetPlace.getValue(), this.rotate.getValue());
                    mc.player.motionY = rubberband.getValue();
                    break;
                }

                case TELEPORT: {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(origin.getX(), origin.getY() + rubberband.getValue().doubleValue(), origin.getZ(), true));
                    mc.player.setPosition(origin.getX(), origin.getY() + rubberband.getValue().doubleValue(), origin.getZ());

                    BlockUtil.place(origin, hand, swing.getValue(), true, packetPlace.getValue(), this.rotate.getValue());
                    mc.player.setPosition(origin.getX(), origin.getY(), origin.getZ());
                    break;
                }
            }

            InventoryUtils.switchTo(oldSlot, silentSwitch.getValue());
            mc.rightClickDelayTimer = 4;
            toggle();
        }
    }

    private boolean intersectsWith(BlockPos pos) {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity == mc.player || entity instanceof EntityItem) {
                continue;
            }

            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox())) {
                return true;
            }
        }

        return false;
    }

    public enum Mode {
        INSTANT, MOTION, TELEPORT
    }

    public enum BlockType {
        OBSIDIAN(Blocks.OBSIDIAN),
        ECHEST(Blocks.ENDER_CHEST),
        ENDROD(Blocks.END_ROD);

        private Block block;
        BlockType(Block block) {
            this.block = block;
        }

        public Block getBlock() {
            return block;
        }
    }
}
