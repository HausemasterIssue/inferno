package me.sxmurai.inferno.mixin.mixins.render.gui;

import me.sxmurai.inferno.features.modules.miscellaneous.Reconnect;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.utils.timing.Timer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiDisconnected.class)
public class MixinGuiDisconnected extends GuiScreen {
    @Shadow
    public int textHeight;

    private final Timer timer = new Timer();
    private GuiButton reconnectToggle = null;

    @Inject(method = "initGui", at = @At("RETURN"))
    public void onInit(CallbackInfo info) {
        if (Reconnect.INSTANCE.serverData != null) {
            int y = Math.min(this.height / 2 + this.textHeight / 2 + this.fontRenderer.FONT_HEIGHT + 25, this.height - 10);
            this.timer.reset();

            this.addButton(this.reconnectToggle = new GuiButton(1, this.width / 2 - 100, y,  "Reconnect " + (Reconnect.INSTANCE.isToggled() ? ChatColor.Green.text(this.toReadable()) : ChatColor.Red.text("N/A"))));
            this.addButton(new GuiButton(2, this.width / 2 - 100, y + 25, "Reconnect Now"));
        }
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void onActionPerformed(GuiButton button, CallbackInfo info) {
        if (button.id == 1) {
            this.timer.reset();
            Reconnect.INSTANCE.toggle();
        } else if (button.id == 2) {
            mc.displayGuiScreen(new GuiConnecting((GuiDisconnected) (Object) this, mc, Reconnect.INSTANCE.serverData));
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
        if (Reconnect.INSTANCE.serverData != null) {
            this.reconnectToggle.displayString = "Reconnect " + (Reconnect.INSTANCE.isToggled() ? ChatColor.Green.text(this.toReadable()) : ChatColor.Red.text("N/A"));

            if (Reconnect.INSTANCE.isToggled()) {
                if (this.timer.passedS(Reconnect.INSTANCE.delay.getValue())) {
                    mc.displayGuiScreen(new GuiConnecting((GuiDisconnected) (Object) this, mc, Reconnect.INSTANCE.serverData));
                }
            }
        }
    }

    private String toReadable() {
        return Math.round((10.0 * ((Reconnect.INSTANCE.delay.getValue() * 1000.0) - this.timer.getPassedTimeMs())) / 1000.0) / 10.0 + "s";
    }
}
