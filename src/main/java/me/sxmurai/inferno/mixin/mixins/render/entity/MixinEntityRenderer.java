package me.sxmurai.inferno.mixin.mixins.render.entity;

import com.google.common.base.Predicate;
import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.modules.miscellaneous.NoEntityTrace;
import me.sxmurai.inferno.features.modules.render.NoRender;
import me.sxmurai.inferno.features.modules.render.ViewClip;
import me.sxmurai.inferno.utils.InventoryUtils;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Shadow
    private ItemStack itemActivationItem;

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void onHurtCameraEffect(float partialTicks, CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.INSTANCE.hurtcam.getValue()) {
            info.cancel();
        }
    }

    @Inject(method = "renderItemActivation", at = @At("HEAD"), cancellable = true)
    private void renderItemActivation(int p_190563_1_, int p_190563_2_, float p_190563_3_, CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.INSTANCE.totemAnimation.getValue() && itemActivationItem != null && !itemActivationItem.isEmpty() && itemActivationItem.getItem() == Items.TOTEM_OF_UNDYING) {
            info.cancel();
        }
    }

    @Inject(method = "addRainParticles", at = @At("HEAD"), cancellable = true)
    public void onAddRainParticles(CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.INSTANCE.weather.getValue()) {
            info.cancel();
        }
    }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true)
    public void onRenderRainSnow(CallbackInfo info) {
        if (NoRender.INSTANCE.isToggled() && NoRender.INSTANCE.weather.getValue()) {
            info.cancel();
        }
    }

    @ModifyVariable(method = "orientCamera", at = @At("STORE"), ordinal = 3, require = 1)
    private double preOrientCamera(double range) {
        return ViewClip.INSTANCE.isToggled() ? ViewClip.INSTANCE.distance.getValue() : range;
    }

    @ModifyVariable(method = "orientCamera", at = @At("STORE"), ordinal = 7, require = 1)
    private double postOrientCamera(double range) {
        return ViewClip.INSTANCE.isToggled() ? ViewClip.INSTANCE.distance.getValue() : range;
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> hookGetMouseOver(WorldClient client, Entity entity, AxisAlignedBB box, Predicate predicate) {
        if (NoEntityTrace.INSTANCE.isToggled() &&
                (NoEntityTrace.INSTANCE.mining.getValue() && Inferno.mc.playerController.getIsHittingBlock() ||
                        NoEntityTrace.INSTANCE.pickaxe.getValue() && InventoryUtils.isHolding(ItemPickaxe.class))) {
            return new ArrayList<>();
        }

        return client.getEntitiesInAABBexcluding(entity, box, predicate);
    }
}
