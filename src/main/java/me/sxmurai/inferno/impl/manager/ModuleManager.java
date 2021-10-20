package me.sxmurai.inferno.impl.manager;

import com.google.common.collect.Lists;
import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.features.module.modules.client.GUI;
import me.sxmurai.inferno.impl.features.module.modules.client.Notifier;
import me.sxmurai.inferno.impl.features.module.modules.combat.AutoBowRelease;
import me.sxmurai.inferno.impl.features.module.modules.combat.Critcals;
import me.sxmurai.inferno.impl.features.module.modules.combat.SelfFill;
import me.sxmurai.inferno.impl.features.module.modules.miscellaneous.FakePlayer;
import me.sxmurai.inferno.impl.features.module.modules.movement.NoSlow;
import me.sxmurai.inferno.impl.features.module.modules.movement.Sprint;
import me.sxmurai.inferno.impl.features.module.modules.movement.Velocity;
import me.sxmurai.inferno.impl.features.module.modules.player.FastUse;
import me.sxmurai.inferno.impl.features.module.modules.player.Reach;
import me.sxmurai.inferno.impl.features.module.modules.player.Scaffold;
import me.sxmurai.inferno.impl.features.module.modules.visual.Brightness;
import me.sxmurai.inferno.impl.features.module.modules.visual.NoRender;
import me.sxmurai.inferno.impl.features.module.modules.visual.ViewClip;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class ModuleManager {
    private final List<Module> modules;

    public ModuleManager() {
        this.modules = Lists.newArrayList(
                // client
                new GUI(),
                new Notifier(),

                // combat
                new AutoBowRelease(),
                new Critcals(),
                new SelfFill(),

                // miscellaneous
                new FakePlayer(),

                // movement
                new NoSlow(),
                new Sprint(),
                new Velocity(),

                // player
                new FastUse(),
                new Reach(),
                new Scaffold(),

                // visual
                new Brightness(),
                new NoRender(),
                new ViewClip()
        );

        this.modules.forEach(Module::registerAllOptions);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        int code = Keyboard.getEventKey();
        if (code != Keyboard.KEY_NONE && !Keyboard.getEventKeyState()) { // if the key is not unknown and the button is not down
            this.modules.forEach((module) -> {
                if (module.getBind() == code) {
                    module.toggle();
                }
            });
        }
    }

    public <T extends Module> T getModule(String name) {
        for (Module module : this.modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return (T) module;
            }
        }

        return null;
    }

    public <T extends Module> T getModule(Class<? extends Module> clazz) {
        for (Module module : this.modules) {
            if (clazz.isInstance(module)) {
                return (T) module;
            }
        }

        return null;
    }

    public List<Module> getModules() {
        return modules;
    }
}
