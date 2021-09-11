package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.InventoryUtils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "FastUse", description = "Lets you use things fast", category = Module.Category.PLAYER)
public class FastUse extends Module {
    public final Setting<Double> speed = this.register(new Setting<>("Speed", 0.0, 0.0, 4.0));
    public final Setting<Boolean> xp = this.register(new Setting<>("XP", false));
    public final Setting<Boolean> crystals = this.register(new Setting<>("Crystals", false));
    public final Setting<Boolean> blocks = this.register(new Setting<>("Blocks", false));

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.xp.getValue() && InventoryUtils.isHolding(Items.EXPERIENCE_BOTTLE)) {
            mc.rightClickDelayTimer = ((int) ((double) speed.getValue()));
        }

        if (this.crystals.getValue() && InventoryUtils.isHolding(Items.END_CRYSTAL)) {
            mc.rightClickDelayTimer = ((int) ((double) speed.getValue()));
        }

        if (this.blocks.getValue() && InventoryUtils.isHolding(ItemBlock.class)) {
            mc.rightClickDelayTimer = ((int) ((double) speed.getValue()));
        }
    }
}
