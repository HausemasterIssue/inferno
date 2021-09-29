package me.sxmurai.inferno.mixin.mixins.render;

import me.sxmurai.inferno.features.modules.render.GlintColor;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {
    @ModifyArg(method = "renderEnchantedGlint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 1), index = 0)
    private static float getRed(float r) {
        return GlintColor.instance.isToggled() ? GlintColor.instance.color.getValue().red / 255.0f : r;
    }

    @ModifyArg(method = "renderEnchantedGlint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 1), index = 1)
    private static float getGreen(float g) {
        return GlintColor.instance.isToggled() ? GlintColor.instance.color.getValue().green / 255.0f : g;
    }

    @ModifyArg(method = "renderEnchantedGlint", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 1), index = 2)
    private static float getBlue(float b) {
        return GlintColor.instance.isToggled() ? GlintColor.instance.color.getValue().blue / 255.0f : b;
    }
}
