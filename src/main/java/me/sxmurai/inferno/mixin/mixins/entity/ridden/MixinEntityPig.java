package me.sxmurai.inferno.mixin.mixins.entity.ridden;

import me.sxmurai.inferno.features.modules.movement.EntityControl;
import net.minecraft.entity.passive.EntityPig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPig.class)
public class MixinEntityPig {
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(CallbackInfoReturnable<Boolean> info) {
        if (EntityControl.INSTANCE.isToggled() && EntityControl.INSTANCE.pig.getValue()) {
            info.setReturnValue(true);
        }
    }
}
