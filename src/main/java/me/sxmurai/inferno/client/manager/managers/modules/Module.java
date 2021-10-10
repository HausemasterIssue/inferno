package me.sxmurai.inferno.client.manager.managers.modules;

import me.sxmurai.inferno.api.events.inferno.ModuleToggledEvent;
import me.sxmurai.inferno.api.values.Bind;
import me.sxmurai.inferno.api.values.Configurable;
import me.sxmurai.inferno.api.values.Value;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Module extends Configurable {
    private final String name;
    private final String description;
    private final Category category;

    private final Bind bind = new Bind("Bind", Keyboard.KEY_NONE);
    private final Value<Boolean> visible = new Value<>("Visible", true);

    private boolean toggled;

    public Module() {
        Define definition = this.getClass().getDeclaredAnnotation(Define.class);

        this.name = definition.name();
        this.description = definition.description();
        this.category = definition.category();

        this.bind.setValue(definition.bind());
        this.values.add(this.bind);
        this.values.add(this.visible);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getBind() {
        return bind.getValue();
    }

    public void setBind(int key) {
        this.bind.setValue(key);
    }

    public boolean isVisible() {
        return visible.getValue();
    }

    public void setVisible(boolean visible) {
        this.visible.setValue(visible);
    }

    protected void onActivated() { }
    protected void onDeactivated() { }

    public void toggle() {
        this.toggle(false);
    }

    public void toggle(boolean silent) {
        this.toggled = !this.toggled;

        if (this.toggled) {
            MinecraftForge.EVENT_BUS.register(this);
            onActivated();
        } else {
            MinecraftForge.EVENT_BUS.unregister(this);
            onDeactivated();
        }

        if (!silent) {
            MinecraftForge.EVENT_BUS.post(new ModuleToggledEvent(this));
        }
    }

    public boolean isToggled() {
        return toggled;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Define {
        String name();
        String description();
        Category category() default Category.MISCELLANEOUS;
        int bind() default Keyboard.KEY_NONE;
    }

    public enum Category {
        CLIENT("Client"),
        COMBAT("Combat"),
        MISCELLANEOUS("Miscellaneous"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        RENDER("Render");

        private String displayName;
        Category(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
