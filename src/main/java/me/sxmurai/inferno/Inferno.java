package me.sxmurai.inferno;

import me.sxmurai.inferno.impl.manager.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(name = Inferno.NAME, modid = Inferno.ID, version = Inferno.VERSION)
public class Inferno {
    public static final String NAME = "Inferno";
    public static final String ID = "inferno";
    public static final String VERSION = "b1.1.0";

    @Mod.Instance
    public static Inferno INSTANCE;
    public static Logger LOGGER = LogManager.getLogger(Inferno.class);

    public static ConfigManager configManager;
    public static ModuleManager moduleManager;
    public static NotificationManager notificationManager;
    public static RotationManager rotationManager;
    public static TotemPopManager totemPopManager;
    public static FontManager fontManager;

    @SubscribeEvent
    public void onPreInit(FMLPreInitializationEvent event) {
        LOGGER.info("get out of my logs cunt - aesthetical");
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        LOGGER.info("Initializing {} {}", Inferno.NAME, Inferno.VERSION);

        Display.setTitle(Inferno.NAME + " " + Inferno.VERSION);

        moduleManager = new ModuleManager();
        notificationManager = new NotificationManager();
        configManager = ConfigManager.getInstance();
        rotationManager = new RotationManager();
        totemPopManager = new TotemPopManager();
        fontManager = new FontManager();

        MinecraftForge.EVENT_BUS.register(new EventManager());
        MinecraftForge.EVENT_BUS.register(Inferno.moduleManager);
        MinecraftForge.EVENT_BUS.register(Inferno.notificationManager);
        MinecraftForge.EVENT_BUS.register(Inferno.rotationManager);
        MinecraftForge.EVENT_BUS.register(Inferno.totemPopManager);

        LOGGER.info("Initialized {} {}. Welcome!", Inferno.NAME, Inferno.VERSION);
    }
}
