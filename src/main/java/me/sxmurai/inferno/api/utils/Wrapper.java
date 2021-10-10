package me.sxmurai.inferno.api.utils;

import net.minecraft.client.Minecraft;

public class Wrapper {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static boolean fullNullCheck() {
        return mc.world == null || mc.player == null;
    }
}
