package me.sxmurai.inferno.impl.features.module.modules.player;

import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.option.Option;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.RayTraceResult;

@Module.Define(name = "MultiTask", category = Module.Category.Player)
@Module.Info(description = "Lets you eat and mine at the same time")
public class MultiTask extends Module {
    public static MultiTask INSTANCE;

    public final Option<Boolean> bypass = new Option<>("Bypass", false);

    public MultiTask() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.bypass.getValue() && mc.player.isHandActive() && mc.playerController.isHittingBlock) {
            RayTraceResult result = mc.player.rayTrace(mc.playerController.getBlockReachDistance(), mc.getRenderPartialTicks());
            if (result != null && result.sideHit != null) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(mc.playerController.currentBlock, result.sideHit, mc.player.getActiveHand(), 0.0f, 0.0f, 0.0f));
            }
        }
    }
}
