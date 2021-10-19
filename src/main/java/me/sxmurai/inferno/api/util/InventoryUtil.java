package me.sxmurai.inferno.api.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;

public class InventoryUtil implements Util {
    public static final int OFFHAND_SLOT = 45;

    public static int getHotbarBlockSlot(Block block, boolean offhand) {
        Item off = getHeld(EnumHand.OFF_HAND).getItem();
        if (offhand && off instanceof ItemBlock && ((ItemBlock) off).getBlock() == block) {
            return OFFHAND_SLOT;
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == block) {
                return i;
            }
        }

        return -1;
    }

    public static boolean isHolding(Item item, boolean offhand) {
        return getHeld(EnumHand.MAIN_HAND).getItem() == item || (offhand && getHeld(EnumHand.OFF_HAND).getItem() == item);
    }

    public static boolean isHolding(Class<? extends Item> clazz, boolean offhand) {
        return clazz.isInstance(getHeld(EnumHand.MAIN_HAND).getItem()) || (offhand && clazz.isInstance(getHeld(EnumHand.OFF_HAND).getItem()));
    }

    public static ItemStack getHeld(EnumHand hand) {
        return mc.player.getHeldItem(hand);
    }

    public static void switchTo(int slot, boolean silent) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));

        if (!silent) {
            mc.player.inventory.currentItem = slot;
        }

        mc.playerController.updateController();
    }
}
