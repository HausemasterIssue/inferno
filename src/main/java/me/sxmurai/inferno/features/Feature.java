package me.sxmurai.inferno.features;

import net.minecraft.client.Minecraft;

public class Feature {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static boolean fullNullCheck() {
        return mc.world == null || mc.player == null;
    }
}
