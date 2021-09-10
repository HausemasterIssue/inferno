package me.sxmurai.inferno;

import me.sxmurai.inferno.managers.EventManager;
import me.sxmurai.inferno.managers.TextManager;
import me.sxmurai.inferno.managers.TotemPopManager;
import me.sxmurai.inferno.managers.friends.FriendManager;
import me.sxmurai.inferno.managers.modules.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid = Inferno.MOD_ID, name = Inferno.MOD_NAME, version = Inferno.MOD_VER)
public class Inferno {
    public static final String MOD_ID = "inferno";
    public static final String MOD_NAME = "Inferno";
    public static final String MOD_VER = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(Inferno.class);
    public static Minecraft mc;

    public static ModuleManager moduleManager;
    public static TextManager textManager;
    public static FriendManager friendManager;

    public static TotemPopManager totemPopManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("penis");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Initializing {} v{}", MOD_NAME, MOD_VER);

        Display.setTitle(MOD_NAME + " v" + MOD_VER);

        mc = Minecraft.getMinecraft();

        moduleManager = new ModuleManager();
        textManager = new TextManager();
        friendManager = new FriendManager();

        totemPopManager = new TotemPopManager();

        MinecraftForge.EVENT_BUS.register(new EventManager());
        MinecraftForge.EVENT_BUS.register(moduleManager);
        MinecraftForge.EVENT_BUS.register(totemPopManager);
    }
}
