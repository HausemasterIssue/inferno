package me.sxmurai.inferno.mixin.mixins.world.blocks;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.modules.render.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockStateContainer.StateImplementation.class)
public class MixinBlockStateImplementation {
    @Shadow
    @Final
    private Block block;

    public void getAmbientOcclusionLightValue(CallbackInfoReturnable<Float> info) {
        if (Xray.INSTANCE.isToggled() && Inferno.xrayManager.isXrayBlock(this.block)) {
            info.setReturnValue(10000000000.0f);
        }
    }
}
