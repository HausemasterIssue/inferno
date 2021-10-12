package me.sxmurai.inferno.loader.mixins.world.blocks.states;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.features.modules.render.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockStateContainer.StateImplementation.class)
public class MixinBlockStateImplementation {
    @Shadow
    @Final
    private Block block;

    @Inject(method = "getAmbientOcclusionLightValue", at = @At("HEAD"), cancellable = true)
    public void getAmbientOcclusionLightValue(CallbackInfoReturnable<Float> info) {
        if (Xray.INSTANCE.isToggled() && Inferno.xrayManager.isXrayBlock(this.block)) {
            info.setReturnValue(10000000000.0f);
        }
    }

    @Inject(method = "getLightValue", at = @At("HEAD"), cancellable = true)
    public void getLightValue(CallbackInfoReturnable<Integer> info) {
        if (Xray.INSTANCE.isToggled() && Inferno.xrayManager.isXrayBlock(this.block)) {
            info.setReturnValue(100);
        }
    }
}
