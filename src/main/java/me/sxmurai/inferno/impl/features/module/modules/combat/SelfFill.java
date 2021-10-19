package me.sxmurai.inferno.impl.features.module.modules.combat;

import me.sxmurai.inferno.api.util.BlockUtil;
import me.sxmurai.inferno.api.util.InventoryUtil;
import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.option.Option;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

@Module.Define(name = "SelfFill", category = Module.Category.Combat)
@Module.Info(description = "Lags you back into a block", bind = Keyboard.KEY_B)
public class SelfFill extends Module {
    public final Option<Type> type = new Option<>("Type", Type.Obsidian);
    public final Option<Boolean> offhand = new Option<>("Offhand", true);
    public final Option<Switch> switchTo = new Option<>("Switch", Switch.Legit);
    public final Option<Double> rubberband = new Option<>("Rubberband", 3.0, -5.0, 5.0);
    public final Option<Boolean> rotate = new Option<>("Rotate", true);
    public final Option<Boolean> swing = new Option<>("Swing", true);
    public final Option<Boolean> sneak = new Option<>("Sneak", false);

    private BlockPos origin = null;
    private EnumHand hand;
    private int oldSlot = -1;

    @Override
    protected void onActivated() {
        if (!fullNullCheck()) {
            this.toggle();
            return;
        }

        this.origin = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        // already burrowed
        if (this.intersectsWith(this.origin)) {
            this.toggle();
            return;
        }

        // not enough headspace
        if (!mc.world.isAirBlock(this.origin.add(0.0, 1.0, 0.0)) || !mc.world.isAirBlock(this.origin.add(0.0, 2.0, 0.0))) {
            this.toggle();
            return;
        }

        if (this.switchTo.getValue() != Switch.None) {
            int slot = InventoryUtil.getHotbarBlockSlot(this.type.getValue().block, this.offhand.getValue());
            if (slot == -1) {
                this.toggle();
                return;
            }

            this.hand = slot == InventoryUtil.OFFHAND_SLOT ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
            mc.player.setActiveHand(this.hand);

            if (this.hand == EnumHand.MAIN_HAND) {
                this.oldSlot = mc.player.inventory.currentItem;
                InventoryUtil.switchTo(slot, this.switchTo.getValue() == Switch.Silent);
            }
        } else {
            // @todo
            return;
        }
    }

    @Override
    protected void onDeactivated() {
        this.origin = null;
        this.hand = null;

        if (this.oldSlot != -1 && fullNullCheck()) {
            InventoryUtil.switchTo(this.oldSlot, this.switchTo.getValue() == Switch.Silent);
        }

        this.oldSlot = -1;
    }

    @Override
    public void onUpdate() {
        if (this.origin != null) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214, mc.player.posZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821, mc.player.posZ, true));

            BlockUtil.place(this.origin, this.hand, true, this.sneak.getValue(), this.swing.getValue(), this.rotate.getValue());

            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + this.rubberband.getValue(), mc.player.posZ, true));

            mc.rightClickDelayTimer = 4;
            this.toggle();
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

    public enum Type {
        Obsidian(Blocks.OBSIDIAN),
        EChest(Blocks.ENDER_CHEST),
        EndRod(Blocks.END_ROD);

        private final Block block;
        Type(Block block) {
            this.block = block;
        }
    }

    public enum Switch {
        None, Legit, Silent
    }
}
