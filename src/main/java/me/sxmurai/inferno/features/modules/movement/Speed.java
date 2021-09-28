package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.modules.movement.speed.OnGround;
import me.sxmurai.inferno.features.modules.movement.speed.Strafe;
import me.sxmurai.inferno.features.modules.movement.speed.StrafeStrict;
import me.sxmurai.inferno.features.modules.movement.speed.Vanilla;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Mode;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Optional;

@Module.Define(name = "Speed", description = "Speeds you up", category = Module.Category.MOVEMENT)
public class Speed extends Module {
    public final Setting<M> mode = this.register(new Setting<>("Mode", M.STRAFE));

    private final ArrayList<Mode> modes = new ArrayList<>();
    private Mode current = null;

    public Speed() {
        this.modes.add(new OnGround(this, M.ONGROUND));
        this.modes.add(new Strafe(this, M.STRAFE));
        this.modes.add(new StrafeStrict(this, M.STRAFE_STRICT));
        this.modes.add(new Vanilla(this, M.VANILLA));

        for (Mode mode : this.modes) {
            for (Setting setting : mode.getSettings()) {
                this.register(setting.setVisibility((v) -> this.mode.getValue() == mode.getMode()));
            }
        }
    }

    @Override
    protected void onDeactivated() {
        if (this.current != null) {
            MinecraftForge.EVENT_BUS.unregister(this.current);
            this.current = null;
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.current == null || this.current.getMode() != this.mode.getValue()) {
            Optional<Mode> m = this.modes.stream().filter((mo) -> mo.getMode().equals(this.mode.getValue())).findFirst();
            if (m.isPresent()) {
                if (this.current != null) {
                    MinecraftForge.EVENT_BUS.unregister(this.current);
                }

                this.current = m.get();
                MinecraftForge.EVENT_BUS.register(this.current);
            }
        }
    }

    public enum M {
        ONGROUND, STRAFE, STRAFE_STRICT, VANILLA
    }
}