package me.sxmurai.inferno.mixin.mixins.input;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.features.modules.movement.NoSlow;
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
        if (!Feature.fullNullCheck()) {
            if (NoSlow.INSTANCE.isToggled() && !(Inferno.mc.currentScreen instanceof GuiChat)) {
                info.setReturnValue(pressed);
            }
        }
    }
}
