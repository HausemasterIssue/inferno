package me.sxmurai.inferno;

import me.sxmurai.inferno.managers.*;
import me.sxmurai.inferno.managers.alts.AltManager;
import me.sxmurai.inferno.managers.commands.CommandManager;
import me.sxmurai.inferno.managers.friends.FriendManager;
import me.sxmurai.inferno.managers.macros.MacroManager;
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
    public static final Build BUILD = Build.DEVELOPER;
    public static State state = State.NONE;

    public static final Logger LOGGER = LogManager.getLogger(Inferno.class);
    public static Minecraft mc;

    public static ModuleManager moduleManager;
    public static CommandManager commandManager;
    public static TextManager textManager;
    public static FriendManager friendManager;
    public static MacroManager macroManager;
    public static AltManager altManager;

    public static RotationManager rotationManager;
    public static ServerManager serverManager;
    public static TotemPopManager totemPopManager;
    private static EventManager eventHelperManager;
    public static XrayManager xrayManager;

    public static FileManager fileManager;

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
        commandManager = new CommandManager();
        textManager = new TextManager();
        friendManager = new FriendManager();
        macroManager = new MacroManager();
        altManager = new AltManager();

        rotationManager = new RotationManager();

        serverManager = new ServerManager();
        totemPopManager = new TotemPopManager();
        eventHelperManager = new EventManager();
        xrayManager = new XrayManager();

        MinecraftForge.EVENT_BUS.register(eventHelperManager);
        MinecraftForge.EVENT_BUS.register(moduleManager);
        MinecraftForge.EVENT_BUS.register(commandManager);
        MinecraftForge.EVENT_BUS.register(totemPopManager);
        MinecraftForge.EVENT_BUS.register(serverManager);
        MinecraftForge.EVENT_BUS.register(rotationManager);

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
        friendManager = null; // @todo save this
        altManager = null; // @todo save this
        xrayManager = null; // @todo save this

        moduleManager.unload();
        MinecraftForge.EVENT_BUS.unregister(moduleManager);
        moduleManager = null;

        MinecraftForge.EVENT_BUS.unregister(commandManager);
        commandManager = null;

        MinecraftForge.EVENT_BUS.unregister(totemPopManager);
        totemPopManager = null;

        MinecraftForge.EVENT_BUS.unregister(eventHelperManager);
        eventHelperManager = null;

        MinecraftForge.EVENT_BUS.unregister(macroManager);
        macroManager.unload();
        macroManager = null;

        MinecraftForge.EVENT_BUS.unregister(rotationManager);
        rotationManager = null;

        state = State.UNLOADED;
    }

    public enum State {
        NONE, LOADING, LOADED, UNLOADING, UNLOADED
    }

    public enum Build {
        STABLE, BETA, DEVELOPER
    }
}
