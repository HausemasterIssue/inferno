package me.sxmurai.inferno.loader.mixins.render.entity;

import me.sxmurai.inferno.api.events.render.RenderModelEvent;
import me.sxmurai.inferno.client.modules.render.CrystalModifier;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderEnderCrystal.class)
public class MixinRenderEnderCrystal {
    @Shadow
    @Final
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;
    private static ResourceLocation glint;

    @Redirect(method = "doRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void onRender(ModelBase modelBase, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (CrystalModifier.INSTANCE.isToggled()) {
            GlStateManager.scale(CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue());

            boolean didSomething = false;

            if (CrystalModifier.INSTANCE.wireframe.getValue()) {
                MinecraftForge.EVENT_BUS.post(new RenderModelEvent(modelBase, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale));
            }

            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);

            if (CrystalModifier.INSTANCE.colored.getValue()) {
                if (CrystalModifier.INSTANCE.throughWalls.getValue()) {
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                }

                GL11.glEnable(10754);
                GL11.glColor4f(CrystalModifier.INSTANCE.color.getValue().red / 255.0f, CrystalModifier.INSTANCE.color.getValue().green / 255.0f, CrystalModifier.INSTANCE.color.getValue().blue / 255.0f, CrystalModifier.INSTANCE.color.getValue().alpha / 255.0f);
                modelBase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

                if (CrystalModifier.INSTANCE.throughWalls.getValue()) {
                    GL11.glDepthMask(true);
                    GL11.glEnable(2929);
                }

                didSomething = true;
            }

            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();

            if (CrystalModifier.INSTANCE.glint.getValue()) {
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GlStateManager.enableAlpha();
                GlStateManager.color(1.0f, 0.0f, 0.0f, 0.13f);
                modelBase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                GlStateManager.disableAlpha();
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
            }

            if (!didSomething) {
                modelBase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }
        } else {
            modelBase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }

        if (CrystalModifier.INSTANCE.isToggled()) {
            GlStateManager.scale(1.0f / CrystalModifier.INSTANCE.scale.getValue(), 1.0f / CrystalModifier.INSTANCE.scale.getValue(), 1.0f / CrystalModifier.INSTANCE.scale.getValue());
        }
    }

    static {
        glint = new ResourceLocation("textures/glint.png");
    }
}
