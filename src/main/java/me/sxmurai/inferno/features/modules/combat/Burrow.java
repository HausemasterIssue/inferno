package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.commands.Command;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.managers.commands.text.TextBuilder;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.BlockUtil;
import me.sxmurai.inferno.utils.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Burrow", description = "becomes cringe and burrows", category = Module.Category.COMBAT)
public class Burrow extends Module {
    public final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.INSTANT));
    public final Setting<Boolean> silentSwitch = this.register(new Setting<>("SilentSwitch", false));
    public final Setting<BlockType> block = this.register(new Setting<>("Block", BlockType.OBSIDIAN));
    public final Setting<Boolean> packetPlace = this.register(new Setting<>("PacketPlace", false));
    public final Setting<Boolean> swing = this.register(new Setting<>("Swing", true));
    public final Setting<Float> rubberband = this.register(new Setting<>("Rubberband", 3.0f, -5.0f, 5.0f));
    public final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));

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

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
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
