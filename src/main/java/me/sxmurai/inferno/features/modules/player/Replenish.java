package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.InventoryUtils;
import me.sxmurai.inferno.utils.timing.TickTimer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "Replenish", description = "Replenishes your hotbar", category = Module.Category.PLAYER)
public class Replenish extends Module {
    public final Setting<Boolean> update = this.register(new Setting<>("Update", true));
    public final Setting<Boolean> shiftClick = this.register(new Setting<>("ShiftClick", false));
    public final Setting<Integer> threshold = this.register(new Setting<>("Threshold", 45, 0, 63));
    public final Setting<Integer> delay = this.register(new Setting<>("Delay", 2, 0, 30));
    public final Setting<Integer> actions = this.register(new Setting<>("Actions", 2, 1, 10));

    private final TickTimer timer = new TickTimer();
    private final Queue<InventoryUtils.TaskGroup> groups = new ConcurrentLinkedQueue<>();
    private final Map<Integer, ItemStack> hotbar = new ConcurrentHashMap<>();
    private boolean needToFill = true;

    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            this.needToFill = true;
        } else {
            this.recordHotbar();
        }
    }

    @Override
    protected void onDeactivated() {
        this.timer.reset();
        this.groups.clear();
        this.hotbar.clear();
        this.needToFill = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Module.fullNullCheck()) {
            return;
        }

        this.timer.tick();

        if (this.needToFill) {
            this.recordHotbar();
        }

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getCount() <= this.threshold.getValue()) {
                this.refill(i);
            }
        }

        if (this.timer.passed(this.delay.getValue())) {
            this.timer.reset();

            for (int i = 0; i < this.actions.getValue(); ++i) {
                InventoryUtils.TaskGroup group = this.groups.poll();
                if (group == null) {
                    return;
                }

                group.handle();
            }
        }
    }

    private void refill(int id) {
        int slot = InventoryUtils.getInventoryItemSlot(this.hotbar.get(id).getItem(), false);
        if (slot == -1) {
            return;
        }

        InventoryUtils.TaskGroup group = new InventoryUtils.TaskGroup();

        group.add(new InventoryUtils.Task(slot, this.update.getValue(), this.shiftClick.getValue()));
        if (!this.shiftClick.getValue()) {
            group.add(new InventoryUtils.Task(id < 9 ? id + 36 : id, this.update.getValue()));
            group.add(new InventoryUtils.Task(slot, this.update.getValue()));
        }

        this.groups.add(group);
    }

    private void recordHotbar() {
        this.needToFill = false;
        for (int i = 0; i < 9; ++i) {
            hotbar.put(i, mc.player.inventory.getStackInSlot(i));
        }
    }
}
