package me.sxmurai.inferno.loader.mixins.input;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.utils.Wrapper;
import me.sxmurai.inferno.client.modules.movement.NoSlow;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {
    @Shadow
    public boolean pressed;

    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    public void hookIsKeyDown(CallbackInfoReturnable<Boolean> info) {
        if (!Wrapper.fullNullCheck()) {
            if (NoSlow.INSTANCE.isToggled() && !(Inferno.mc.currentScreen instanceof GuiChat)) {
                info.setReturnValue(pressed);
            }
        }
    }
}
