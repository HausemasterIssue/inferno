package me.sxmurai.inferno.loader.mixins.entity.ridden;

import me.sxmurai.inferno.client.features.modules.movement.EntityControl;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public class MixinAbstractHorse {
    @Inject(method = "isHorseSaddled", at = @At("HEAD"), cancellable = true)
    public void isHorseSaddled(CallbackInfoReturnable<Boolean> info) {
        if (EntityControl.INSTANCE.isToggled()) {
            if (!this.isValidTarget((AbstractHorse) (Object) this)) {
                return;
            }

            info.setReturnValue(true);
        }
    }

    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(CallbackInfoReturnable<Boolean> info) {
        if (EntityControl.INSTANCE.isToggled()) {
            if (!this.isValidTarget((AbstractHorse) (Object) this)) {
                return;
            }

            info.setReturnValue(true);
        }
    }

    private boolean isValidTarget(AbstractHorse horse) {
        EntityControl instance = EntityControl.INSTANCE;

        if (!instance.horse.getValue() && ((AbstractHorse) (Object) this instanceof EntityHorse)) {
            return false;
        }

        if (!instance.mules.getValue() && ((AbstractHorse) (Object) this instanceof EntityMule)) {
            return false;
        }

        if (!instance.donkey.getValue() && ((AbstractHorse) (Object) this instanceof EntityDonkey)) {
            return false;
        }

        return true;
    }
}
