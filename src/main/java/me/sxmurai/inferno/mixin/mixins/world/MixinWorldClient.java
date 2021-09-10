package me.sxmurai.inferno.mixin.mixins.world;

import me.sxmurai.inferno.events.entity.EntityRemoveEvent;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public class MixinWorldClient {
    @Inject(method = "onEntityRemoved", at = @At("HEAD"))
    public void onEntityRemoved(Entity entityIn, CallbackInfo info) {
        MinecraftForge.EVENT_BUS.post(new EntityRemoveEvent(entityIn));
    }
}
