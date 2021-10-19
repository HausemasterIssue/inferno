package me.sxmurai.inferno.impl.features.module;

import me.sxmurai.inferno.impl.features.Wrapper;
import me.sxmurai.inferno.impl.option.Bind;
import me.sxmurai.inferno.impl.option.Option;
import net.minecraftforge.common.MinecraftForge;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Module implements Wrapper {
    private final String name;
    private final Category category;

    private final String description;

    private final Bind bind = new Bind("Bind", -1);
    private final Option<Boolean> drawn = new Option<>("Drawn", true);

    private boolean toggled = false;

    public Module() {
        Define definition = this.getClass().getDeclaredAnnotation(Define.class);

        this.name = definition.name();
        this.category = definition.category();

        Info info = this.getClass().getDeclaredAnnotation(Info.class);

        this.description = info == null ? "No description provided." : info.description();
        this.bind.setValue(info == null ? -1 : info.bind());
        this.drawn.setValue(info == null || info.drawn());
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDrawn() {
        return this.drawn.getValue();
    }

    public void setDrawn(boolean drawn) {
        this.drawn.setValue(drawn);
    }

    public int getBind() {
        return this.bind.getValue();
    }

    public void setBind(int bind) {
        this.bind.setValue(bind);
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
            this.onActivated();
        } else {
            MinecraftForge.EVENT_BUS.unregister(this);
            this.onDeactivated();
        }

        if (!silent) {
            // @todo
        }
    }

    public boolean isOn() {
        return this.toggled;
    }

    public boolean isOff() {
        return !this.toggled;
    }

    public void onUpdate() { }
    public void onTick() { }
    public void onRenderWorld() { }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Define {
        String name();
        Category category() default Category.Miscellaneous;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Info {
        String description() default "No description provided.";
        int bind() default -1;
        boolean drawn() default true;
    }

    public enum Category {
        Combat, Miscellaneous, Movement, Player, Visual, Client
    }
}
