package me.sxmurai.inferno.utils;

import me.sxmurai.inferno.features.Feature;
import net.minecraft.block.Block;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryUtils extends Feature {
    public static int OFFHAND_SLOT = 45;

    public static boolean isHolding(Item item) {
        return mc.player.getHeldItemMainhand().item == item || mc.player.getHeldItemOffhand().item == item;
    }

    public static boolean isHolding(Class<? extends Item> clazz) {
        return clazz.isInstance(mc.player.getHeldItemMainhand().getItem()) || clazz.isInstance(mc.player.getHeldItemOffhand().getItem());
    }

    public static int getHotbarItemSlot(Item item, boolean offhand) {
        if (offhand && mc.player.getHeldItemOffhand().item == item) {
            return 45;
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty && stack.item == item) {
                return i;
            }
        }

        return -1;
    }

    public static int getHotbarItemSlot(Class<? extends Item> clazz, boolean offhand) {
        if (offhand && clazz.isInstance(mc.player.getHeldItemOffhand().item)) {
            return 45;
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty && clazz.isInstance(stack.item)) {
                return i;
            }
        }

        return -1;
    }

    public static int getHotbarBlockSlot(Block block, boolean offhand) {
        if (offhand && mc.player.getHeldItemOffhand().item instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().item).block == block) {
            return 45;
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty && stack.item instanceof ItemBlock && ((ItemBlock) stack.item).block == block) {
                return i;
            }
        }

        return -1;
    }

    public static int getHotbarBlockSlot(Class<? extends Block> block, boolean offhand) {
        if (offhand && mc.player.getHeldItemOffhand().item instanceof ItemBlock && block.isInstance(((ItemBlock) mc.player.getHeldItemOffhand().item).block)) {
            return 45;
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty && stack.item instanceof ItemBlock && block.isInstance(((ItemBlock) stack.item).block)) {
                return i;
            }
        }

        return -1;
    }

    public static int getInventoryItemSlot(Item item, boolean hotbar) {
        for (int i = hotbar ? 0 : 9; i < 39; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty && stack.getItem() == item) {
                return i < 9 ? i + 36 : i;
            }
        }

        return -1;
    }

    public static void switchTo(int slot, boolean silent) {
        if (silent) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        } else {
            mc.player.inventory.currentItem = slot;
        }

        mc.playerController.updateController();
    }

    public static class Task {
        private final int slot;
        private final boolean updateController;
        private final boolean shiftClick;

        public Task(int slot, boolean updateController, boolean shiftClick) {
            this.slot = slot;
            this.updateController = updateController;
            this.shiftClick = shiftClick;
        }

        public Task(int slot, boolean updateController) {
            this(slot, updateController, false);
        }

        public void run() {
            mc.playerController.windowClick(0, this.slot, 0, this.shiftClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, mc.player);
            if (updateController) {
                mc.playerController.updateController();
            }
        }
    }
}
