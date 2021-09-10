package me.sxmurai.inferno.mixin.mixins.input;

import me.sxmurai.inferno.features.modules.player.Reach;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {
    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    public void getBlockReachDistance(CallbackInfoReturnable<Float> info) {
        if (Reach.INSTANCE.isToggled()) {
            info.setReturnValue(Reach.INSTANCE.distance.getValue());
        }
    }
}
