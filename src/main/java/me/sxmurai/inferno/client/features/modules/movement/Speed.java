package me.sxmurai.inferno.client.features.modules.movement;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.client.features.modules.movement.speed.OnGround;
import me.sxmurai.inferno.client.features.modules.movement.speed.Strafe;
import me.sxmurai.inferno.client.features.modules.movement.speed.StrafeStrict;
import me.sxmurai.inferno.client.features.modules.movement.speed.Vanilla;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Mode;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Optional;

@Module.Define(name = "Speed", description = "Speeds you up", category = Module.Category.MOVEMENT)
public class Speed extends Module {
    public final Value<M> mode = new Value<>("Mode", M.STRAFE);

    private final ArrayList<Mode> modes = new ArrayList<>();
    private Mode current = null;

    public Speed() {
        this.modes.add(new OnGround(this, M.ONGROUND));
        this.modes.add(new Strafe(this, M.STRAFE));
        this.modes.add(new StrafeStrict(this, M.STRAFE_STRICT));
        this.modes.add(new Vanilla(this, M.VANILLA));

        for (Mode m : this.modes) {
            for (Value value : m.getSettings()) {
                this.values.add(value.setVisibility((v) -> this.mode.getValue() == m.getMode()));
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