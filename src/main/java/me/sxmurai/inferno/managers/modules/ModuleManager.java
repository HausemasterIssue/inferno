package me.sxmurai.inferno.managers.modules;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.config.ModulesConfig;
import me.sxmurai.inferno.features.modules.client.ClickGUI;
import me.sxmurai.inferno.features.modules.client.CustomFont;
import me.sxmurai.inferno.features.modules.combat.*;
import me.sxmurai.inferno.features.modules.miscellaneous.*;
import me.sxmurai.inferno.features.modules.movement.*;
import me.sxmurai.inferno.features.modules.player.*;
import me.sxmurai.inferno.features.modules.render.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ModuleManager {
    private final ArrayList<Module> modules = new ArrayList<>();
    private final ModulesConfig config;

    public ModuleManager() {
        // client
        this.modules.add(new ClickGUI());
        this.modules.add(new CustomFont());

        // combat
        this.modules.add(new Aura());
        this.modules.add(new AutoCrystal());
        this.modules.add(new Burrow());
        this.modules.add(new Criticals());
        this.modules.add(new Offhand());
        this.modules.add(new TotemPopNotifier());

        // miscellaneous
        this.modules.add(new FakePlayer());
        this.modules.add(new MiddleClick());
        this.modules.add(new NoEntityTrace());
        this.modules.add(new NoHandshake());
        this.modules.add(new PingSpoof());

        // movement
        this.modules.add(new ElytraFly());
        this.modules.add(new NoSlow());
        this.modules.add(new PacketFly());
        this.modules.add(new ReverseStep());
        this.modules.add(new Speed());
        this.modules.add(new Sprint());
        this.modules.add(new Velocity());

        // player
        this.modules.add(new Blink());
        this.modules.add(new FastUse());
        this.modules.add(new Inventory());
        this.modules.add(new MultiTask());
        this.modules.add(new Reach());
        this.modules.add(new Scaffold());
        this.modules.add(new Timer());

        // render
        this.modules.add(new Brightness());
        this.modules.add(new Chams());
        this.modules.add(new CrystalModifier());
        this.modules.add(new HandModifier());
        this.modules.add(new Tracers());
        this.modules.add(new Trails());
        this.modules.add(new ViewClip());
        this.modules.add(new Wireframes());

        Inferno.LOGGER.info("Loaded {} modules!", this.modules.size());

        Inferno.LOGGER.info("Loading configurations for {} modules...", this.modules.size());
        config = new ModulesConfig(this);
        config.load();
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        int key = Keyboard.getEventKey();
        if (!Keyboard.getEventKeyState() && key != Keyboard.KEY_NONE) {
            for (Module module : modules) {
                if (module.getBind() == key) {
                    module.toggle();
                }
            }
        }
    }

    public <T extends Module> T getModule(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return (T) module;
            }
        }

        return null;
    }

    public <T extends Module> T getModule(Class<? extends Module> clazz) {
        for (Module module : modules) {
            if (clazz.isInstance(module)) {
                return (T) module;
            }
        }

        return null;
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public void unload() {
        this.config.stop();
    }
}
