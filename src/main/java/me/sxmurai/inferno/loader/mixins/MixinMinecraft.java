package me.sxmurai.inferno.loader.mixins;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.events.mc.GuiChangeEvent;
import me.sxmurai.inferno.client.modules.player.MultiTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow
    public GuiScreen currentScreen;

    @Inject(method = "displayGuiScreen", at = @At("HEAD"), cancellable = true)
    public void displayGuiScreen(@Nullable GuiScreen guiScreenIn, CallbackInfo info) {
        GuiChangeEvent event = new GuiChangeEvent(this.currentScreen, guiScreenIn);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

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
