package me.sxmurai.inferno.client.features.modules.combat;

import me.sxmurai.inferno.api.utils.BlockUtil;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Define(name = "SelfFill", description = "Lags you back into a block", category = Module.Category.COMBAT)
public class SelfFill extends Module {
    public final Value<Mode> mode = new Value<>("Mode", Mode.INSTANT);
    public final Value<BlockType> type = new Value<>("Type", BlockType.OBSIDIAN);
    public final Value<Double> rubberband = new Value<>("Rubberband", 3.0, -5.0, 5.0);
    public final Value<Boolean> offhand = new Value<>("Offhand", true);
    public final Value<Boolean> rotate = new Value<>("Rotate", true);
    public final Value<Boolean> sneak = new Value<>("Sneak", false);
    public final Value<Boolean> swing = new Value<>("Swing", true);
    public final Value<Switch> switchTo = new Value<>("Switch", Switch.LEGIT);

    private BlockPos origin = null;
    private EnumHand hand = EnumHand.MAIN_HAND;
    private int oldSlot = -1;

    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            this.toggle();
            return;
        }

        this.origin = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (this.intersectsWith(this.origin)) {
            this.toggle();
            return;
        }

        if (!mc.world.isAirBlock(this.origin.add(0.0, 1.0, 0.0)) || !mc.world.isAirBlock(this.origin.add(0.0, 2.0, 0.0))) {
            this.toggle();
            return;
        }

        int slot = InventoryUtils.getHotbarBlockSlot(this.type.getValue().getBlock(), this.offhand.getValue());
        if (slot == -1) {
            this.toggle();
            return;
        }

        this.hand = slot == 45 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        mc.player.setActiveHand(this.hand);

        if (this.hand == EnumHand.MAIN_HAND) {
            this.oldSlot = mc.player.inventory.currentItem;
            InventoryUtils.switchTo(slot, this.switchTo.getValue() == Switch.SILENT);
        }
    }

    @Override
    protected void onDeactivated() {
        this.origin = null;
        this.oldSlot = -1;
        this.hand = null;
    }

    @Override
    public void onUpdate() {
        if (this.origin != null) {
            switch (this.mode.getValue()) {
                case INSTANT: {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698, mc.player.posZ, true));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997, mc.player.posZ, true));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214, mc.player.posZ, true));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821, mc.player.posZ, true));

                    BlockUtil.place(this.origin, this.hand, this.swing.getValue(), this.sneak.getValue(), true, this.rotate.getValue());

                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + this.rubberband.getValue(), mc.player.posZ, true));
                    break;
                }

                case MOTION: {
                    mc.player.jump();
                    BlockUtil.place(this.origin, this.hand, this.swing.getValue(), this.sneak.getValue(), true, this.rotate.getValue());
                    mc.player.motionY = this.rubberband.getValue();
                    break;
                }

                case TELEPORT: {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(this.origin.x, this.origin.y + 1.0, this.origin.z, true));

                    mc.player.jump();

                    BlockUtil.place(this.origin, this.hand, this.swing.getValue(), this.sneak.getValue(), true, this.rotate.getValue());

                    mc.player.connection.sendPacket(new CPacketPlayer.Position(this.origin.x, this.origin.y, this.origin.z, true));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + this.rubberband.getValue(), mc.player.posZ, true));
                    break;
                }
            }

            if (this.oldSlot != -1) {
                InventoryUtils.switchTo(this.oldSlot, this.switchTo.getValue() == Switch.SILENT);
                mc.rightClickDelayTimer = 4;
                this.toggle();
            }
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

    public enum Switch {
        LEGIT, SILENT
    }
}
