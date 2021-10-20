package me.sxmurai.inferno.impl.features.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.impl.features.Wrapper;
import net.minecraft.util.text.TextComponentString;

public class Command implements Wrapper {
    public static void send(String text) {
        mc.player.sendMessage(new TextComponentString(Command.getPrefix() + text));
    }

    public static String getPrefix() {
        return ChatFormatting.DARK_GRAY + "[" + ChatFormatting.RED + Inferno.NAME + ChatFormatting.DARK_GRAY + "]" + ChatFormatting.RESET + " ";
    }
}
