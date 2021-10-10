package me.sxmurai.inferno.client.manager.managers.macros;

import me.sxmurai.inferno.api.utils.Wrapper;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class Macro extends Wrapper {
    private final String text;
    private final int key;

    public Macro(String text, int key) {
        this.text = text;
        this.key = key;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        int code = Keyboard.getEventKey();
        if (!Keyboard.getEventKeyState() && code != this.key) {
            mc.player.connection.sendPacket(new CPacketChatMessage(this.text));
        }
    }

    public String getText() {
        return text;
    }

    public int getKey() {
        return key;
    }
}
