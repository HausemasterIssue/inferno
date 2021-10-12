package me.sxmurai.inferno.loader.mixins.input;

import me.sxmurai.inferno.api.events.world.BlockDestroyEvent;
import me.sxmurai.inferno.api.events.world.BlockHitEvent;
import me.sxmurai.inferno.client.features.modules.player.Reach;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {
    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"))
    public void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> info) {
        MinecraftForge.EVENT_BUS.post(new BlockHitEvent(posBlock, directionFacing));
    }

    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    public void getBlockReachDistance(CallbackInfoReturnable<Float> info) {
        if (Reach.INSTANCE.isToggled()) {
            info.setReturnValue(Reach.INSTANCE.distance.getValue());
        }
    }

    @Inject(method = "onPlayerDestroyBlock", at = @At("HEAD"))
    public void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        MinecraftForge.EVENT_BUS.post(new BlockDestroyEvent(pos));
    }
}