package me.sxmurai.inferno.mixin.mixins.render.gui;

import me.sxmurai.inferno.features.modules.miscellaneous.ExtraTab;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {
    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "java/util/List.subList(II)Ljava/util/List;", remap = false))
    public List<NetworkPlayerInfo> hookRenderPlayerList(List<NetworkPlayerInfo> list, int from, int to) {
        return list.subList(from, ExtraTab.INSTANCE.isToggled() ? ExtraTab.INSTANCE.players.getValue() : to);
    }
}
