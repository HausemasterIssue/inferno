package me.sxmurai.inferno.client.manager.managers.notifications;

import me.sxmurai.inferno.client.manager.AbstractManager;
import me.sxmurai.inferno.client.manager.managers.commands.Command;
import net.minecraft.util.text.TextComponentString;

public class NotificationManager extends AbstractManager<Notification> {
    public void handle() {
        for (Notification notification : this.items) {
            if (notification.shouldDelete()) {
                mc.ingameGUI.getChatGUI().deleteChatLine(notification.getId());
                this.items.remove(notification);
            }
        }
    }

    public void create(Notification notification) {
        this.items.add(notification);
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Command.watermark() + notification.getText()), notification.getId());
    }
}
