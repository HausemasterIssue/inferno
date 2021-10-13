package me.sxmurai.inferno.client.features.modules.player;

import me.sxmurai.inferno.api.utils.BlockUtil;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.utils.timing.Timer;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Module.Define(name = "Scaffold", description = "Places blocks under you", category = Module.Category.PLAYER)
public class Scaffold extends Module {
    private static final BlockPos[] DIRECTION_OFFSETS = new BlockPos[] {
            new BlockPos(0, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, -1),
            new BlockPos(0, 0, 1)
    };

    public final Value<Boolean> tower = new Value<>("Tower", true);
    public final Value<Boolean> stopMotion = new Value<>("StopMotion", true);
    public final Value<Boolean> rotate = new Value<>("Rotate", true);
    public final Value<Boolean> packet = new Value<>("Packet", false);
    public final Value<Boolean> sneak = new Value<>("Sneak", false);
    public final Value<Boolean> swing = new Value<>("Swing", true);
    public final Value<Switch> switchTo = new Value<>("Switch", Switch.LEGIT);

    private final Timer timer = new Timer();

    @Override
    public void onTick() {
        if (!mc.gameSettings.keyBindJump.isKeyDown() && this.tower.getValue()) {
            this.timer.reset();
        }

        BlockPos base = new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ);
        if (mc.world.isAirBlock(base)) {
            EnumHand hand;
            int oldSlot = -1;

            if (this.switchTo.getValue() != Switch.NONE) {
                int slot = InventoryUtils.getHotbarItemSlot(ItemBlock.class, true);
                if (slot == -1) {
                    return;
                }

                hand = slot == 45 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
                if (hand == EnumHand.MAIN_HAND) {
                    oldSlot = mc.player.inventory.currentItem;
                    InventoryUtils.switchTo(slot, this.switchTo.getValue() == Switch.SILENT);
                }
            } else {
                if (!InventoryUtils.isHolding(ItemBlock.class, true)) {
                    return;
                }

                hand = mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock ?
                        EnumHand.MAIN_HAND :
                        mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock ?
                                EnumHand.OFF_HAND :
                                null;
            }

            if (hand == null) {
                return;
            }

            mc.player.setActiveHand(hand);

            BlockPos placePos = this.getPos(base);
            if (placePos == null) {
                return;
            }

            BlockUtil.place(placePos, hand, this.swing.getValue(), this.sneak.getValue(), this.packet.getValue(), this.rotate.getValue());
            if (placePos.equals(base)) {
                if (!mc.world.isAirBlock(placePos) && mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.motionX *= 0.3;
                    mc.player.motionZ *= 0.3;
                    mc.player.jump();

                    if (this.timer.passedMs(1200L)) {
                        this.timer.reset();
                        mc.player.motionY = -0.28;
                    }
                }
            } else {
                if (this.stopMotion.getValue()) {
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                    mc.player.movementInput.moveForward = 0.0f;
                }
            }

            if (oldSlot != -1) {
                InventoryUtils.switchTo(oldSlot, this.switchTo.getValue() == Switch.SILENT);
            }
        }
    }

    private BlockPos getPos(BlockPos base) {
        if (!mc.world.isAirBlock(base)) {
            return null;
        }

        for (BlockPos offset : Scaffold.DIRECTION_OFFSETS) {
            for (EnumFacing facing : EnumFacing.values()) {
                if (facing == EnumFacing.UP) {
                    continue;
                }

                if (facing == EnumFacing.DOWN) {
                    return base;
                } else {
                    return base.add(offset);
                }
            }
        }

        return null;
    }

    public enum Switch {
        NONE, LEGIT, SILENT
    }
}