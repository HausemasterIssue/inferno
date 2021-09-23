package me.sxmurai.inferno.managers.notifications;

import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.managers.commands.Command;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;

public class NotificationManager extends Feature {
    private final ArrayList<Notification> notifications = new ArrayList<>();

    public void handle() {
        for (Notification notification : this.notifications) {
            if (notification.shouldDelete()) {
                mc.ingameGUI.getChatGUI().deleteChatLine(notification.getId());
                this.notifications.remove(notification);
            }
        }
    }

    public void create(Notification notification) {
        this.notifications.add(notification);
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(Command.watermark() + notification.getText()), notification.getId());
    }
}
