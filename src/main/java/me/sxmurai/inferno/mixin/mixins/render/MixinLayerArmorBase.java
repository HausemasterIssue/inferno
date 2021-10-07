package me.sxmurai.inferno.mixin.mixins.render;

import me.sxmurai.inferno.features.modules.render.NoRender;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {
    @Inject(method = "doRenderLayer", at = @At("HEAD"), cancellable = true)
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.INSTANCE.armor.getValue() == NoRender.Armor.ALL) {
            info.cancel();
        }
    }

    @Inject(method = "renderEnchantedGlint", at = @At("HEAD"), cancellable = true)
    private static void renderEnchantedGlint(RenderLivingBase<?> p_188364_0_, EntityLivingBase p_188364_1_, ModelBase model, float p_188364_3_, float p_188364_4_, float p_188364_5_, float p_188364_6_, float p_188364_7_, float p_188364_8_, float p_188364_9_, CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.INSTANCE.armor.getValue() == NoRender.Armor.GLINT) {
            info.cancel();
        }
    }
}