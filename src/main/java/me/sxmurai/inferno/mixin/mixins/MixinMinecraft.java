package me.sxmurai.inferno.mixin.mixins;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.modules.player.MultiTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Redirect(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    private boolean isHandActive(EntityPlayerSP player) {
        return !MultiTask.INSTANCE.isToggled() && player.isHandActive();
    }

    @Redirect(method = "rightClickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z", ordinal = 0))
    public boolean getIsHittingBlock(PlayerControllerMP controller) {
        return !MultiTask.INSTANCE.isToggled() && controller.getIsHittingBlock();
    }

    @Inject(method = "shutdown", at = @At("HEAD"), cancellable = true)
    public void onShutdown(CallbackInfo info) {
        Inferno.unload();
    }
}
