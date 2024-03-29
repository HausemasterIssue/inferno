package me.sxmurai.inferno;

import me.sxmurai.inferno.managers.EventManager;
import me.sxmurai.inferno.managers.FileManager;
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
    public static State state = State.NONE;

    public static final Logger LOGGER = LogManager.getLogger(Inferno.class);
    public static Minecraft mc;

    public static ModuleManager moduleManager;
    public static TextManager textManager;
    public static FriendManager friendManager;

    public static TotemPopManager totemPopManager;
    private static EventManager eventHelperManager;

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
        textManager = new TextManager();
        friendManager = new FriendManager();

        totemPopManager = new TotemPopManager();
        eventHelperManager = new EventManager();

        MinecraftForge.EVENT_BUS.register(eventHelperManager);
        MinecraftForge.EVENT_BUS.register(moduleManager);
        MinecraftForge.EVENT_BUS.register(totemPopManager);

        LOGGER.info("Loaded {} v{}!", MOD_NAME, MOD_VER);

        state = State.LOADED;
    }

    public static void unload() {
        if (state == State.UNLOADING || state == State.UNLOADED) {
            return;
        }

        state = State.UNLOADING;

        LOGGER.info("Unloading and saving configurations...");

        moduleManager.unload();
        MinecraftForge.EVENT_BUS.unregister(moduleManager);
        moduleManager = null;

        textManager = null;
        friendManager = null; // @todo save this

        MinecraftForge.EVENT_BUS.unregister(totemPopManager);
        totemPopManager = null;

        MinecraftForge.EVENT_BUS.unregister(eventHelperManager);
        eventHelperManager = null;

        state = State.UNLOADED;
    }

    public enum State {
        NONE, LOADING, LOADED, UNLOADING, UNLOADED
    }
}
