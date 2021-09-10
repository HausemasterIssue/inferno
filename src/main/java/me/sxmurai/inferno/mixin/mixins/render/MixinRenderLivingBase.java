package me.sxmurai.inferno.mixin.mixins.render;

import me.sxmurai.inferno.events.render.RenderModelEvent;
import me.sxmurai.inferno.features.modules.render.Chams;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> {
    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Redirect(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void onRenderModel(ModelBase modelBase, Entity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        if (Chams.INSTANCE.isToggled() && Chams.INSTANCE.mode.getValue() == Chams.Mode.WALLHACK) {
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4f(Chams.INSTANCE.color.getValue().red / 255.0f, Chams.INSTANCE.color.getValue().green / 255.0f, Chams.INSTANCE.color.getValue().blue / 255.0f, Chams.INSTANCE.color.getValue().alpha / 255.0f);
            modelBase.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }

        modelBase.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        MinecraftForge.EVENT_BUS.post(new RenderModelEvent(modelBase, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor));
    }

    @Inject(method = "doRender", at = @At("HEAD"))
    public void doRenderPre(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (Chams.INSTANCE.isToggled() && Chams.INSTANCE.mode.getValue() == Chams.Mode.NORMAL) {
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0f, -1000000.0f);
        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    public void doRenderPost(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (Chams.INSTANCE.isToggled() && Chams.INSTANCE.mode.getValue() == Chams.Mode.NORMAL) {
            GL11.glPolygonOffset(1.0f, 1000000.0f);
            GL11.glDisable(32823);
        }
    }
}
