package me.sxmurai.inferno.client.features.modules.player;

import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.utils.timing.TickTimer;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "Replenish", description = "Replenishes your hotbar", category = Module.Category.PLAYER)
public class Replenish extends Module {
    public final Value<Integer> threshold = new Value<>("Threshold", 45, 0, 63);
    public final Value<Integer> delay = new Value<>("Delay", 1, 0, 20);
    public final Value<Integer> actions = new Value<>("Actions", 1, 1, 10);
    public final Value<Boolean> update = new Value<>("Update", true);
    public final Value<Boolean> guis = new Value<>("Guis", false);

    private final Queue<InventoryUtils.TaskGroup> groups = new ConcurrentLinkedQueue<>();
    private final Map<Integer, ItemStack> hotbar = new ConcurrentHashMap<>();
    private final TickTimer timer = new TickTimer();

    @Override
    protected void onDeactivated() {
        this.hotbar.clear();
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        this.hotbar.clear();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Module.fullNullCheck()) {
            return;
        }

        this.timer.tick();
        if (this.timer.passed(this.delay.getValue()) && !this.groups.isEmpty()) {
            if (!this.guis.getValue() && mc.currentScreen != null) {
                return;
            }

            this.timer.reset();

            for (int i = 0; i < this.actions.getValue(); ++i) {
                InventoryUtils.TaskGroup group = this.groups.poll();
                if (group == null) {
                    break;
                }

                group.handle();
            }
        }

        if (this.hotbar.isEmpty()) {
            this.recordHotbar();
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getCount() <= this.threshold.getValue()) {
                this.replenish(i);
            }
        }
    }

    private void replenish(int id) {
        ItemStack stack = mc.player.inventory.getStackInSlot(id);

        int slot;
        if (stack.getItem() instanceof ItemBlock) {
            slot = InventoryUtils.getInventoryBlockSlot(((ItemBlock) stack.getItem()).getBlock(), false);
        } else {
            slot = InventoryUtils.getInventoryItemSlot(stack.getItem(), false);
        }

        if (slot == -1) {
            return;
        }

        if (!stack.getDisplayName().equals(mc.player.inventory.getStackInSlot(slot).getDisplayName())) {
            return;
        }

        InventoryUtils.TaskGroup group = new InventoryUtils.TaskGroup();
        group.add(new InventoryUtils.Task(slot, this.update.getValue()));
        group.add(new InventoryUtils.Task(id + 36, this.update.getValue()));
        group.add(new InventoryUtils.Task(slot, this.update.getValue()));

        this.groups.add(group);
    }

    private void recordHotbar() {
        this.hotbar.clear();
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            this.hotbar.put(i, stack);
        }
    }
}
