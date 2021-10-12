package me.sxmurai.inferno.client.features.modules.combat;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.EntityUtils;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.utils.timing.TickTimer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "Offhand", description = "Manages your offhand", category = Module.Category.COMBAT)
public class Offhand extends Module {
    public final Value<Type> item = new Value<>("Item", Type.GAPPLE);
    public final Value<Boolean> gapFallback = new Value<>("GapFallback", true);
    public final Value<Float> health = new Value<>("Health", 16.0f, 1.0f, 20.0f);
    public final Value<Float> fallDistance = new Value<>("FallDistance", 10.0f, 1.0f, 256.0f);
    public final Value<Boolean> gap = new Value<>("Gap", true);
    public final Value<Boolean> swordGap = new Value<>("SwordGap", true);
    public final Value<Integer> delay = new Value<>("Delay", 1, 0, 20);
    public final Value<Integer> actions = new Value<>("Actions", 1, 1, 10);
    public final Value<Boolean> update = new Value<>("Update", true);
    public final Value<Boolean> hotbar = new Value<>("Hotbar", true);
    public final Value<Boolean> guis = new Value<>("Guis", false);

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
