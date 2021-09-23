package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.EntityUtils;
import me.sxmurai.inferno.utils.InventoryUtils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "Offhand", description = "Manages your offhand", category = Module.Category.COMBAT)
public class Offhand extends Module {
    public final Setting<ItemType> item = this.register(new Setting<>("Item", ItemType.TOTEM));
    public final Setting<Float> healthSwitch = this.register(new Setting<>("HealthSwitch", 14.0f, 0.1f, 20.0f));
    public final Setting<Boolean> fallDistance = this.register(new Setting<>("FallDistance", true));
    public final Setting<Boolean> offhandGapple = this.register(new Setting<>("OffhandGapple", false));
    public final Setting<Boolean> hotbar = this.register(new Setting<>("UseHotbar", true));
    public final Setting<Boolean> update = this.register(new Setting<>("UpdateController", true));
    public final Setting<Integer> delay = this.register(new Setting<>("Delay", 5, 0, 50));

    private final Queue<ArrayList<InventoryUtils.Task>> tasks = new ConcurrentLinkedQueue<>();
    private int ticks = 0;
    private boolean switching = false;

    @Override
    protected void onDeactivated() {
        this.tasks.clear();
        this.ticks = 0;
        this.switching = false;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        this.handleTasks();

        Item item = this.item.getValue().item;
        if (this.healthSwitch.getValue() > EntityUtils.getHealth(mc.player) || (this.fallDistance.getValue() && mc.player.fallDistance >= 3)) {
            this.addTask(Items.TOTEM_OF_UNDYING);
            return;
        }

        if (Mouse.isButtonDown(1) && this.offhandGapple.getValue()) {
            item = Items.GOLDEN_APPLE;
        }

        this.addTask(item);
    }

    private void handleTasks() {
        ++this.ticks;
        if (this.ticks == 1 && this.switching) {
            this.switching = false;
            --this.ticks;
        }

        if (this.ticks >= this.delay.getValue()) {
            this.ticks = 0;

            ArrayList<InventoryUtils.Task> taskList = tasks.poll();
            if (taskList == null) {
                this.switching = false;
                return;
            }

            this.switching = true;
            taskList.forEach(InventoryUtils.Task::run);
        }
    }

    private void addTask(Item item) {
        ItemStack offhand = mc.player.getHeldItemOffhand();
        if (offhand.getItem() == item) {
            return;
        }

        int slot = InventoryUtils.getInventoryItemSlot(item, this.hotbar.getValue());
        if (slot == -1) {
            return;
        }

        final ArrayList<InventoryUtils.Task> taskList = new ArrayList<>();

        if (!offhand.isEmpty) {
            taskList.add(new InventoryUtils.Task(InventoryUtils.OFFHAND_SLOT, update.getValue()));
        }

        taskList.add(new InventoryUtils.Task(slot, update.getValue()));
        taskList.add(new InventoryUtils.Task(InventoryUtils.OFFHAND_SLOT, update.getValue()));

        this.tasks.add(taskList);
    }

    public enum ItemType {
        TOTEM(Items.TOTEM_OF_UNDYING),
        GAPPLE(Items.GOLDEN_APPLE),
        CRYSTAL(Items.END_CRYSTAL),
        EXP(Items.EXPERIENCE_BOTTLE);

        public Item item;
        ItemType(Item item) {
            this.item = item;
        }
    }
}
