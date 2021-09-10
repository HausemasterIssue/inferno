package me.sxmurai.inferno.mixin.mixins.entity;

import me.sxmurai.inferno.events.entity.PushEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {
    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "isPushedByWater", at = @At("HEAD"), cancellable = true)
    public void hookIsPushedByWater(CallbackInfoReturnable<Boolean> info) {
        PushEvent event = new PushEvent(PushEvent.Type.LIQUID);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.setReturnValue(false);
        }
    }
}
