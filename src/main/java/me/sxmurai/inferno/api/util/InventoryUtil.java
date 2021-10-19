package me.sxmurai.inferno.api.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class InventoryUtil implements Util {
    public static boolean isHolding(Item item, boolean offhand) {
        return getHeld(EnumHand.MAIN_HAND).getItem() == item || (offhand && getHeld(EnumHand.OFF_HAND).getItem() == item);
    }

    public static boolean isHolding(Class<? extends Item> clazz, boolean offhand) {
        return clazz.isInstance(getHeld(EnumHand.MAIN_HAND).getItem()) || (offhand && clazz.isInstance(getHeld(EnumHand.OFF_HAND).getItem()));
    }

    public static ItemStack getHeld(EnumHand hand) {
        return mc.player.getHeldItem(hand);
    }
}
