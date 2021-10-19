package me.sxmurai.inferno.asm.mixins.input;

import me.sxmurai.inferno.impl.features.module.modules.player.Reach;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {
    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    public void getBlockReachDistance(CallbackInfoReturnable<Float> info) {
        if (Reach.INSTANCE.isOn()) {
            info.setReturnValue(Reach.range.getValue());
        }
    }
}
