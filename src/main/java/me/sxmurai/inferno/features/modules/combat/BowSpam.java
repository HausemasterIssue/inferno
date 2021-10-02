package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.InventoryUtils;
import me.sxmurai.inferno.utils.timing.TickTimer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Module.Define(name = "BowSpam", description = "Becomes annoying and spams your bow", category = Module.Category.COMBAT)
public class BowSpam extends Module {
    public final Setting<Integer> amount = new Setting<>("Amount", 4, 0, 20);
    public final Setting<Integer> delay = new Setting<>("Delay", 1, 0, 20);
    public final Setting<Boolean> offhand = new Setting<>("Offhand", true);

    private final TickTimer timer = new TickTimer();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Module.fullNullCheck()) {
            return;
        }

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
