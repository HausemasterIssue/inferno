package me.sxmurai.inferno.loader.mixins.entity.ridden;

import me.sxmurai.inferno.client.features.modules.movement.EntityControl;
import net.minecraft.entity.passive.EntityLlama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLlama.class)
public class MixinEntityLlama {
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(CallbackInfoReturnable<Boolean> info) {
        if (EntityControl.INSTANCE.isToggled() && EntityControl.INSTANCE.llama.getValue()) {
            info.setReturnValue(true);
        }
    }
}
