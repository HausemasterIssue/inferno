package me.sxmurai.inferno.features.modules.client;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.inferno.ModuleToggledEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.managers.commands.text.TextBuilder;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.managers.notifications.Notification;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Notifications", description = "Manages the clients notification manager", category = Module.Category.CLIENT)
public class Notifications extends Module {
    public final Setting<Boolean> moduleMessages = new Setting<>("ModuleMessages", true);

    @SubscribeEvent
    public void onModuleToggled(ModuleToggledEvent event) {
        if (!Module.fullNullCheck() && this.moduleMessages.getValue()) {
            Module module = event.getModule();

            Inferno.notificationManager.create(
                    new Notification(
                        new TextBuilder()
                                .append(ChatColor.Dark_Gray, "Turned")
                                .append(" ")
                                .append(ChatColor.Dark_Gray, module.getName())
                                .append(" ")
                                .append(module.isToggled() ? ChatColor.Green : ChatColor.Red, module.isToggled() ? "on" : "off")
                                .append(ChatColor.Dark_Gray, ".")
                                .build(),
                        module.hashCode()
                    )
            );
        }
    }
}
