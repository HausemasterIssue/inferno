package me.sxmurai.inferno.mixin.mixins.render.entity.tile;

import me.sxmurai.inferno.features.modules.render.NoRender;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntitySign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TileEntitySignRenderer.class)
public abstract class MixinTileEntitySignRenderer extends TileEntitySpecialRenderer<TileEntitySign> {
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"), index = 0)
    public String getLineText(String text) {
        return NoRender.INSTANCE.isToggled() && NoRender.INSTANCE.signText.getValue() ? "" : text;
    }
}
