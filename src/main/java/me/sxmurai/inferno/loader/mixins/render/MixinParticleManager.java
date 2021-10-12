package me.sxmurai.inferno.loader.mixins.render;

import me.sxmurai.inferno.client.features.modules.render.NoRender;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class MixinParticleManager {
    @Inject(method = "renderParticles", at = @At("HEAD"), cancellable = true)
    public void renderParticles(Entity entityIn, float partialTicks, CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.INSTANCE.particles.getValue()) {
            info.cancel();
        }
    }
}
