package me.sxmurai.inferno.mixin.mixins.render.gui;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.modules.miscellaneous.ExtraTab;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay {
    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "java/util/List.subList(II)Ljava/util/List;", remap = false))
    public List<NetworkPlayerInfo> hookRenderPlayerList(List<NetworkPlayerInfo> list, int from, int to) {
        return list.subList(from, Math.min(ExtraTab.INSTANCE.isToggled() ? ExtraTab.INSTANCE.players.getValue() : to, list.size()));
    }

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    public void getPlayerName(NetworkPlayerInfo plrInfo, CallbackInfoReturnable<String> info) {
        if (ExtraTab.INSTANCE.isToggled() && ExtraTab.INSTANCE.friendHighlight.getValue()) {
            if (plrInfo.getGameProfile().getId() != null) {
                info.setReturnValue((Inferno.friendManager.isFriend(plrInfo.getGameProfile().getId()) ? ChatColor.Blue.toString() : "") + info.getReturnValue() + ChatColor.Reset);
            }
        }
    }
}