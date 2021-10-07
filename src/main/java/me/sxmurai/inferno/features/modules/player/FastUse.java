package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.InventoryUtils;
import me.sxmurai.inferno.utils.timing.TickTimer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Module.Define(name = "FastUse", description = "Lets you use things fast", category = Module.Category.PLAYER)
public class FastUse extends Module {
    public final Setting<Integer> delay = new Setting<>("Delay", 0, 0, 20);
    public final Setting<Integer> speed = new Setting<>("Speed", 0, 0, 4);
    public final Setting<Boolean> offhand = new Setting<>("Offhand", true);
    public final Setting<Boolean> xp = new Setting<>("XP", false);
    public final Setting<Boolean> fireworks = new Setting<>("Fireworks", false);
    public final Setting<Boolean> crystals = new Setting<>("Crystals", false);
    public final Setting<Boolean> blocks = new Setting<>("Blocks", false);

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
