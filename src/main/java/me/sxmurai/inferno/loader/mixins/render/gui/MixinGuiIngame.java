package me.sxmurai.inferno.loader.mixins.render.gui;

import me.sxmurai.inferno.client.features.modules.render.NoRender;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    protected void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.INSTANCE.scoreboard.getValue()) {
            info.cancel();
        }
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    protected void renderPumpkinOverlay(ScaledResolution scaledRes, CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.INSTANCE.pumpkinOverlay.getValue()) {
            info.cancel();
        }
    }
}
