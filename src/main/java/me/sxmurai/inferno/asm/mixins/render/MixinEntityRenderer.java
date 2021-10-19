package me.sxmurai.inferno.asm.mixins.render;

import me.sxmurai.inferno.impl.features.module.modules.visual.ViewClip;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @ModifyVariable(method = "orientCamera", at = @At("STORE"), ordinal = 3)
    public double orientCameraX(double distance) {
        return ViewClip.INSTANCE.isOn() ? ViewClip.distance.getValue() : distance;
    }

    @ModifyVariable(method = "orientCamera", at = @At("STORE"), ordinal = 7)
    public double orientCameraZ(double distance) {
        return ViewClip.INSTANCE.isOn() ? ViewClip.distance.getValue() : distance;
    }
}
