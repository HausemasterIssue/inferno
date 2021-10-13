package me.sxmurai.inferno.client.features.modules.combat;

import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.utils.timing.TickTimer;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;

@Module.Define(name = "BowSpam", description = "Becomes annoying and spams your bow", category = Module.Category.COMBAT)
public class BowSpam extends Module {
    public final Value<Integer> amount = new Value<>("Amount", 4, 0, 20);
    public final Value<Integer> delay = new Value<>("Delay", 1, 0, 20);
    public final Value<Boolean> offhand = new Value<>("Offhand", true);

    private final TickTimer timer = new TickTimer();

    @Override
    public void onTick() {
        this.timer.tick();

        ItemStack active = mc.player.getActiveItemStack();
        if (active.getItem() == Items.BOW) {
            if (!this.offhand.getValue() && InventoryUtils.isHolding(Items.BOW)) {
                return;
            }

            if (mc.player.getItemInUseMaxCount() >= this.amount.getValue() && this.timer.passed(this.delay.getValue())) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                mc.player.stopActiveHand();
                this.timer.reset();
            }
        }
    }
}
