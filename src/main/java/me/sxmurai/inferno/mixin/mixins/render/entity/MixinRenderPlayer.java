package me.sxmurai.inferno.mixin.mixins.render.entity;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.modules.render.Nametags;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    private float renderPitch;
    private float renderYaw;
    private float renderHeadYaw;
    private float prevRenderHeadYaw;
    private float lastRenderHeadYaw = 0.0f;
    private float prevRenderPitch;
    private float lastRenderPitch = 0.0f;

    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
    public void onRenderEntityName(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo info) {
        if (Nametags.INSTANCE.isToggled()) {
            info.cancel();
        }
    }

    @Inject(method = "doRender", at = @At("HEAD"))
    public void doRenderPre(AbstractClientPlayer player, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (player == Inferno.mc.player) {
            this.renderPitch = player.rotationPitch;
            this.renderYaw = player.rotationYaw;

            this.renderHeadYaw = player.rotationYawHead;

            this.prevRenderHeadYaw = player.prevRotationYawHead;
            this.prevRenderPitch = player.prevRotationPitch;

            player.rotationPitch = Inferno.rotationManager.getPitch();
            player.rotationYaw = Inferno.rotationManager.getYaw();

            player.rotationYawHead = Inferno.rotationManager.getYaw();

            player.prevRotationYawHead = this.lastRenderHeadYaw;
            player.prevRotationPitch = this.lastRenderPitch;
        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    public void doRenderPost(AbstractClientPlayer player, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info) {
        if (player == Inferno.mc.player) {
            this.lastRenderHeadYaw = player.rotationYawHead;
            this.lastRenderPitch = player.rotationPitch;

            player.rotationPitch = this.renderPitch;
            player.rotationYaw = this.renderYaw;

            player.prevRotationPitch = this.prevRenderPitch;

            player.rotationYawHead = this.renderHeadYaw;
            player.prevRotationYawHead = this.prevRenderHeadYaw;
        }
    }
}
