package me.sxmurai.inferno.mixin.mixins.render;

import me.sxmurai.inferno.features.modules.render.ViewClip;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @ModifyVariable(method = "orientCamera", at = @At("STORE"), ordinal = 3, require = 1)
    private double preOrientCamera(double range) {
        return ViewClip.INSTANCE.isToggled() ? ViewClip.INSTANCE.distance.getValue() : range;
    }

    @ModifyVariable(method = "orientCamera", at = @At("STORE"), ordinal = 7, require = 1)
    private double postOrientCamera(double range) {
        return ViewClip.INSTANCE.isToggled() ? ViewClip.INSTANCE.distance.getValue() : range;
    }
}
