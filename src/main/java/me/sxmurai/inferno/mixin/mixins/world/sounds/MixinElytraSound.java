package me.sxmurai.inferno.mixin.mixins.world.sounds;

import me.sxmurai.inferno.features.modules.movement.ElytraFly;
import net.minecraft.client.audio.ElytraSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraSound.class)
public class MixinElytraSound {
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void onUpdate(CallbackInfo info) {
        if (ElytraFly.INSTANCE.isToggled() && !ElytraFly.INSTANCE.sounds.getValue()) {
            info.cancel();
        }
    }
}
