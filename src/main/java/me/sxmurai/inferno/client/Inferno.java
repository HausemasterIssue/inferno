package me.sxmurai.inferno.client;

import me.sxmurai.inferno.client.manager.managers.alts.AltManager;
import me.sxmurai.inferno.client.manager.managers.commands.CommandManager;
import me.sxmurai.inferno.client.manager.managers.friends.FriendManager;
import me.sxmurai.inferno.client.manager.managers.hud.HudManager;
import me.sxmurai.inferno.client.manager.managers.macros.MacroManager;
import me.sxmurai.inferno.client.manager.managers.misc.*;
import me.sxmurai.inferno.client.manager.managers.modules.ModuleManager;
import me.sxmurai.inferno.client.manager.managers.notifications.NotificationManager;
import me.sxmurai.inferno.client.manager.managers.server.RotationManager;
import me.sxmurai.inferno.client.manager.managers.server.ServerManager;
import me.sxmurai.inferno.client.manager.managers.server.SpeedManager;
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
    public static final Build BUILD = Build.DEVELOPER;
    public static State state = State.NONE;

    public static final Logger LOGGER = LogManager.getLogger(Inferno.class);
    public static Minecraft mc;

    public static ModuleManager moduleManager;
    public static CommandManager commandManager;
    public static NotificationManager notificationManager;
    public static TextManager textManager;
    public static FriendManager friendManager;
    public static MacroManager macroManager;
    public static AltManager altManager;

    public static RotationManager rotationManager;
    public static ServerManager serverManager;
    public static SpeedManager speedManager;
    public static HoleManager holeManager;
    public static TotemPopManager totemPopManager;
    private static EventManager eventHelperManager;
    public static XrayManager xrayManager;

    public static FileManager fileManager;

    public static HudManager hudManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("penis");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Inferno.load();
    }

    public static void load() {
        if (state == State.LOADING || state == State.LOADED) {
            return;
        }

        state = State.LOADING;

        LOGGER.info("Initializing {} v{}", MOD_NAME, MOD_VER);

        Display.setTitle(MOD_NAME + " v" + MOD_VER);

        mc = Minecraft.getMinecraft();

        fileManager = FileManager.getInstance();
        if (!fileManager.exists(fileManager.getClientFolder())) {
            fileManager.mkDir(fileManager.getClientFolder(), false);
        }

        moduleManager = new ModuleManager();
        moduleManager.load();

        commandManager = new CommandManager();
        notificationManager = new NotificationManager();
        textManager = new TextManager();

        friendManager = new FriendManager();
        friendManager.load();

        macroManager = new MacroManager();
        macroManager.load();

        altManager = new AltManager();

        rotationManager = new RotationManager();
        serverManager = new ServerManager();
        speedManager = new SpeedManager();
        holeManager = new HoleManager();
        totemPopManager = new TotemPopManager();
        eventHelperManager = new EventManager();

        xrayManager = new XrayManager();
        xrayManager.load();

        hudManager = new HudManager();

        MinecraftForge.EVENT_BUS.register(eventHelperManager);
        MinecraftForge.EVENT_BUS.register(moduleManager);
        MinecraftForge.EVENT_BUS.register(commandManager);
        MinecraftForge.EVENT_BUS.register(totemPopManager);
        MinecraftForge.EVENT_BUS.register(serverManager);
        MinecraftForge.EVENT_BUS.register(holeManager);
        MinecraftForge.EVENT_BUS.register(rotationManager);

        MinecraftForge.EVENT_BUS.register(new UnloadManager());

        LOGGER.info("Loaded {} v{}!", MOD_NAME, MOD_VER);

        state = State.LOADED;
    }

    public static void unload() {
        if (state == State.UNLOADING || state == State.UNLOADED) {
            return;
        }

        state = State.UNLOADING;

        LOGGER.info("Unloading managers and saving configurations...");

        textManager = null;
        altManager = null; // @todo save this

        moduleManager.unload();
        MinecraftForge.EVENT_BUS.unregister(moduleManager);
        moduleManager = null;

        MinecraftForge.EVENT_BUS.unregister(commandManager);
        commandManager = null;

        friendManager.unload();
        MinecraftForge.EVENT_BUS.unregister(friendManager);
        friendManager = null;

        MinecraftForge.EVENT_BUS.unregister(totemPopManager);
        totemPopManager = null;

        MinecraftForge.EVENT_BUS.unregister(eventHelperManager);
        eventHelperManager = null;

        macroManager.unload();
        MinecraftForge.EVENT_BUS.unregister(macroManager);
        macroManager = null;

        MinecraftForge.EVENT_BUS.unregister(rotationManager);
        rotationManager = null;

        xrayManager.unload();
        xrayManager = null;

        state = State.UNLOADED;
    }

    public enum State {
        NONE, LOADING, LOADED, UNLOADING, UNLOADED
    }

    public enum Build {
        STABLE, BETA, DEVELOPER
    }
}