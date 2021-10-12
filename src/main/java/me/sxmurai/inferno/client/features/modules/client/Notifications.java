package me.sxmurai.inferno.client.features.modules.client;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.events.inferno.ModuleToggledEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.commands.text.ChatColor;
import me.sxmurai.inferno.client.manager.managers.commands.text.TextBuilder;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.client.manager.managers.notifications.Notification;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Notifications", description = "Manages the clients notification manager", category = Module.Category.CLIENT)
public class Notifications extends Module {
    public static Notifications INSTANCE;

    public final Value<Boolean> moduleMessages = new Value<>("ModuleMessages", false);
    public final Value<Boolean> totemPops = new Value<>("TotemPops", false);

    public Notifications() {
        INSTANCE = this;
    }

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
