package me.sxmurai.inferno.client.modules.player;

import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.utils.timing.TickTimer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Module.Define(name = "FastUse", description = "Lets you use things fast", category = Module.Category.PLAYER)
public class FastUse extends Module {
    public final Value<Integer> delay = new Value<>("Delay", 0, 0, 20);
    public final Value<Integer> speed = new Value<>("Speed", 0, 0, 4);
    public final Value<Boolean> offhand = new Value<>("Offhand", true);
    public final Value<Boolean> xp = new Value<>("XP", false);
    public final Value<Boolean> fireworks = new Value<>("Fireworks", false);
    public final Value<Boolean> crystals = new Value<>("Crystals", false);
    public final Value<Boolean> blocks = new Value<>("Blocks", false);

    private final TickTimer timer = new TickTimer();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Module.fullNullCheck()) {
            return;
        }

        this.timer.tick();
        if (!this.timer.passed(this.delay.getValue())) {
            return;
        }
        this.timer.reset();

        if (this.xp.getValue() && InventoryUtils.isHolding(Items.EXPERIENCE_BOTTLE, this.offhand.getValue())) {
            mc.rightClickDelayTimer = this.speed.getValue();
        }

        if (this.fireworks.getValue() && InventoryUtils.isHolding(Items.FIREWORKS, this.offhand.getValue())) {
            mc.rightClickDelayTimer = this.speed.getValue();
        }

        if (this.crystals.getValue() && InventoryUtils.isHolding(Items.END_CRYSTAL, this.offhand.getValue())) {
            mc.rightClickDelayTimer = this.speed.getValue();
        }

        if (this.blocks.getValue() && InventoryUtils.isHolding(ItemBlock.class, this.offhand.getValue())) {
            mc.rightClickDelayTimer = this.speed.getValue();
        }
    }
}
