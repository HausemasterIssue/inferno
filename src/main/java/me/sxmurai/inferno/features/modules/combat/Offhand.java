package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.EntityUtils;
import me.sxmurai.inferno.utils.InventoryUtils;
import me.sxmurai.inferno.utils.timing.TickTimer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "Offhand", description = "Manages your offhand", category = Module.Category.COMBAT)
public class Offhand extends Module {
    public final Setting<Type> item = new Setting<>("Item", Type.GAPPLE);
    public final Setting<Boolean> gapFallback = new Setting<>("GapFallback", true);
    public final Setting<Float> health = new Setting<>("Health", 16.0f, 1.0f, 20.0f);
    public final Setting<Float> fallDistance = new Setting<>("FallDistance", 10.0f, 1.0f, 256.0f);
    public final Setting<Boolean> gap = new Setting<>("Gap", true);
    public final Setting<Boolean> swordGap = new Setting<>("SwordGap", true);
    public final Setting<Integer> delay = new Setting<>("Delay", 1, 0, 20);
    public final Setting<Integer> actions = new Setting<>("Actions", 1, 1, 10);
    public final Setting<Boolean> update = new Setting<>("Update", true);
    public final Setting<Boolean> hotbar = new Setting<>("Hotbar", true);
    public final Setting<Boolean> guis = new Setting<>("Guis", false);

    private final Queue<InventoryUtils.TaskGroup> groups = new ConcurrentLinkedQueue<>();
    private final TickTimer timer = new TickTimer();

    private Item offhandItem;

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

        this.offhandItem = this.item.getValue().getItem();

        if (this.health.getValue() > EntityUtils.getHealth(mc.player) || this.fallDistance.getValue() < mc.player.fallDistance) {
            if (InventoryUtils.getCount(Items.TOTEM_OF_UNDYING, false, this.hotbar.getValue()) == 0) {
                if (this.gapFallback.getValue()) {
                    this.offhandItem = Items.GOLDEN_APPLE;
                } else {
                    // you're fucked basically, rip to you
                    return;
                }
            } else {
                this.offhandItem = Items.TOTEM_OF_UNDYING;
            }

            this.switchTo(); // force the switch
            return;
        }

        if (this.gap.getValue() && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            this.offhandItem = Items.GOLDEN_APPLE;
        }

        if (this.swordGap.getValue() && InventoryUtils.isHolding(ItemSword.class, false)) {
            this.offhandItem = Items.GOLDEN_APPLE;
        }

        this.switchTo();
    }

    private void switchTo() {
        if (mc.player.getHeldItemOffhand().getItem() == this.offhandItem) {
            return;
        }

        int slot = InventoryUtils.getInventoryItemSlot(this.offhandItem, this.hotbar.getValue());
        if (slot == -1) {
            return;
        }

        InventoryUtils.TaskGroup group = new InventoryUtils.TaskGroup();

        group.add(new InventoryUtils.Task(slot, this.update.getValue(), false));
        group.add(new InventoryUtils.Task(InventoryUtils.OFFHAND_SLOT, this.update.getValue(), false));

        if (!mc.player.getHeldItemOffhand().isEmpty()) {
            group.add(new InventoryUtils.Task(slot, this.update.getValue(), false));
        }

        this.groups.add(group);
    }

    public enum Type {
        TOTEM(Items.TOTEM_OF_UNDYING),
        GAPPLE(Items.GOLDEN_APPLE),
        CRYSTAL(Items.END_CRYSTAL),
        EXP(Items.EXPERIENCE_BOTTLE);

        private final Item item;
        Type(Item item) {
            this.item = item;
        }

        public Item getItem() {
            return item;
        }
    }
}
