package me.sxmurai.inferno.loader.mixins.render;

import me.sxmurai.inferno.client.features.modules.render.HandModifier;
import me.sxmurai.inferno.client.features.modules.render.NoRender;
import me.sxmurai.inferno.api.utils.ColorUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    private boolean a = true;

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    public void onRenderFireInFirstPerson(CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.INSTANCE.fireOverlay.getValue()) {
            info.cancel();
        }
    }

    @Shadow
    public abstract void renderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_);

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At("HEAD"), cancellable = true)
    public void renderItemInFirstPersonHook(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo info) {
        if (a) {
            HandModifier handModifier = HandModifier.INSTANCE;
            if (handModifier.isToggled() && handModifier.chams.getValue() != HandModifier.Chams.NONE && stack.isEmpty && hand == EnumHand.MAIN_HAND) {
                a = false;
                info.cancel();

                if (handModifier.chams.getValue() == HandModifier.Chams.WIREFRAME) {
                    this.renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_, stack, p_187457_7_);
                }

                GlStateManager.pushMatrix();
                if (handModifier.chams.getValue() == HandModifier.Chams.WIREFRAME) {
                    GL11.glPushAttrib(1048575);
                    GL11.glPolygonMode(1032, 6913);
                } else if (handModifier.chams.getValue() == HandModifier.Chams.COLORED) {
                    GlStateManager.pushAttrib();
                }

                GL11.glDisable(3553);
                GL11.glDisable(2896);
                if (handModifier.chams.getValue() == HandModifier.Chams.WIREFRAME) {
                    GL11.glEnable(2848);
                    GL11.glEnable(3042);
                }

                ColorUtils.Color color = handModifier.color.getValue();
                GL11.glColor4f(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, color.alpha / 255.0f);
                if (handModifier.chams.getValue() == HandModifier.Chams.WIREFRAME) {
                    GlStateManager.glLineWidth(handModifier.lineWidth.getValue());
                }

                this.renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_, stack, p_187457_7_);

                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }

            a = true;
        }
    }


    @Inject(method = "transformFirstPerson", at = @At("HEAD"))
    public void onTransformFirstPerson(EnumHandSide handSide, float p_187459_2_, CallbackInfo info) {
        if (HandModifier.INSTANCE.isToggled()) {
            GlStateManager.scale(HandModifier.INSTANCE.scaledX.getValue(), HandModifier.INSTANCE.scaledY.getValue(), HandModifier.INSTANCE.scaledZ.getValue());
            GlStateManager.translate(HandModifier.INSTANCE.translatedX.getValue(), HandModifier.INSTANCE.translatedY.getValue(), HandModifier.INSTANCE.translatedZ.getValue());

            GlStateManager.rotate(handSide == EnumHandSide.LEFT ? -HandModifier.INSTANCE.rotatedX.getValue() : HandModifier.INSTANCE.rotatedX.getValue(), 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(HandModifier.INSTANCE.rotatedY.getValue(), 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(HandModifier.INSTANCE.rotatedZ.getValue(), 0.0f, 0.0f, 1.0f);
        }
    }

    @Inject(method = "transformSideFirstPerson", at = @At("HEAD"), cancellable = true)
    public void onTransformSideFirstPerson(EnumHandSide handSide, float p_187459_2_, CallbackInfo info) {
        if (HandModifier.INSTANCE.isToggled()) {
            GlStateManager.scale(HandModifier.INSTANCE.scaledX.getValue(), HandModifier.INSTANCE.scaledY.getValue(), HandModifier.INSTANCE.scaledZ.getValue());
            GlStateManager.translate(HandModifier.INSTANCE.translatedX.getValue(), HandModifier.INSTANCE.translatedY.getValue(), HandModifier.INSTANCE.translatedZ.getValue());

            GlStateManager.rotate(handSide == EnumHandSide.LEFT ? -HandModifier.INSTANCE.rotatedX.getValue() : HandModifier.INSTANCE.rotatedX.getValue(), 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(HandModifier.INSTANCE.rotatedY.getValue(), 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(HandModifier.INSTANCE.rotatedZ.getValue(), 0.0f, 0.0f, 1.0f);
        }
    }
}
