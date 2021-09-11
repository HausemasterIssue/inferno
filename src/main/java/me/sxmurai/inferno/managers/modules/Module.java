package me.sxmurai.inferno.managers.modules;

import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.features.settings.Bind;
import me.sxmurai.inferno.features.settings.Setting;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class Module extends Feature {
    private final String name;
    private final String description;
    private final Category category;

    private final ArrayList<Setting> settings = new ArrayList<>();
    private final Bind bind = new Bind("Bind", Keyboard.KEY_NONE);
    private final Setting<Boolean> visible = new Setting<>("Visible", true);

    private boolean toggled;

    public Module() {
        Define definition = this.getClass().getDeclaredAnnotation(Define.class);

        this.name = definition.name();
        this.description = definition.description();
        this.category = definition.category();

        bind.setValue(definition.bind());
        settings.add(bind);
        settings.add(visible);
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

    public <T> T register(Setting setting) {
        this.settings.add(setting);
        return (T) setting;
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public <T> Setting<T> getSetting(String name) {
        for (Setting setting : this.settings) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return setting;
            }
        }

        return null;
    }

    protected void onActivated() { }
    protected void onDeactivated() { }

    public void toggle() {
        this.toggled = !this.toggled;

        if (this.toggled) {
            MinecraftForge.EVENT_BUS.register(this);
            onActivated();
        } else {
            MinecraftForge.EVENT_BUS.unregister(this);
            onDeactivated();
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
