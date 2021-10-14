package me.sxmurai.inferno.client.manager.managers.modules;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.config.ModulesConfig;
import me.sxmurai.inferno.client.manager.ConfigurableManager;
import me.sxmurai.inferno.client.features.modules.client.*;
import me.sxmurai.inferno.client.features.modules.combat.*;
import me.sxmurai.inferno.client.features.modules.miscellaneous.*;
import me.sxmurai.inferno.client.features.modules.movement.*;
import me.sxmurai.inferno.client.features.modules.player.*;
import me.sxmurai.inferno.client.features.modules.render.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ModuleManager extends ConfigurableManager<Module> {
    public ModuleManager() {
        // client
        this.items.add(new ClickGUI());
        this.items.add(new Colors());
        this.items.add(new CustomFont());
        this.items.add(new HUD());
        this.items.add(new HudEditor());
        this.items.add(new Notifications());

        // combat
        this.items.add(new Aura());
        this.items.add(new AutoCrystal());
        this.items.add(new AutoLog());
        this.items.add(new BowSpam());
        this.items.add(new SelfFill());
        this.items.add(new ChorusPredict());
        this.items.add(new Criticals());
        this.items.add(new HoleFiller());
        this.items.add(new Offhand());
        this.items.add(new Surround());
        this.items.add(new Trap());

        // miscellaneous
        this.items.add(new AntiSignFuck());
        this.items.add(new AutoEChestMiner());
        this.items.add(new Avoid());
        this.items.add(new ExtraTab());
        this.items.add(new FakePlayer());
        this.items.add(new MiddleClick());
        this.items.add(new MountBypass());
        this.items.add(new NoEntityTrace());
        this.items.add(new NoHandshake());
        this.items.add(new PingSpoof());
        this.items.add(new AutoReconnect());

        // movement
        this.items.add(new AutoWalk());
        this.items.add(new ElytraFly());
        this.items.add(new EntityControl());
        this.items.add(new NoSlow());
        this.items.add(new ReverseStep());
        this.items.add(new SafeWalk());
        this.items.add(new Speed());
        this.items.add(new Sprint());
        this.items.add(new Velocity());

        // player
        this.items.add(new AntiAFK());
        this.items.add(new AntiHunger());
        this.items.add(new AntiLevitation());
        this.items.add(new AntiVoid());
        this.items.add(new AutoRespawn());
        this.items.add(new Blink());
        this.items.add(new FastUse());
        this.items.add(new Freecam());
        this.items.add(new Inventory());
        this.items.add(new MultiTask());
        this.items.add(new NoFall());
        this.items.add(new Reach());
        this.items.add(new Replenish());
        this.items.add(new Scaffold());
        this.items.add(new Speedmine());
        this.items.add(new Timer());
        this.items.add(new Yaw());

        // render
        this.items.add(new Chams());
        this.items.add(new CrystalModifier());
        this.items.add(new Fullbright());
        this.items.add(new HandModifier());
        this.items.add(new HoleESP());
        this.items.add(new Nametags());
        this.items.add(new NoRender());
        this.items.add(new Tracers());
        this.items.add(new ViewClip());
        this.items.add(new Wireframes());
        this.items.add(new Xray());

        Inferno.LOGGER.info("Loaded {} modules.", this.items.size());
        this.items.forEach(Module::registerSettings);
    }

    @Override
    public void load() {
        if (!this.items.isEmpty()) {
            Inferno.LOGGER.info("Loading configurations for {} modules...", this.items.size());
            this.configuration = new ModulesConfig(this);
            this.configuration.load();
        }
    }

    @Override
    public void unload() {
        this.configuration.stop();
        this.items.forEach(mod -> {
            if (mod.isToggled()) {
                mod.toggle(true);
            }
        });
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        int key = Keyboard.getEventKey();
        if (!Keyboard.getEventKeyState() && key != Keyboard.KEY_NONE) {
            for (Module module : this.items) {
                if (module.getBind() == key) {
                    module.toggle();
                }
            }
        }
    }

    public <T extends Module> T getModule(String name) {
        for (Module module : this.items) {
            if (module.getName().equalsIgnoreCase(name)) {
                return (T) module;
            }
        }

        return null;
    }

    public <T extends Module> T getModule(Class<? extends Module> clazz) {
        for (Module module : this.items) {
            if (clazz.isInstance(module)) {
                return (T) module;
            }
        }

        return null;
    }

    public ArrayList<Module> getModules() {
        return this.items;
    }
}
