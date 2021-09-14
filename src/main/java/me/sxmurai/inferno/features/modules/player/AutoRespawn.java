package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.GuiChangeEvent;
import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.FileManager;
import me.sxmurai.inferno.managers.commands.Command;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.managers.commands.text.TextBuilder;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.timing.Timer;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.nio.file.Path;

@Module.Define(name = "AutoRespawn", description = "Does shit upon ur death", category = Module.Category.PLAYER)
public class AutoRespawn extends Module {
    private static final FileManager FILES = FileManager.getInstance();

    public final Setting<Boolean> noDeathScreen = this.register(new Setting<>("NoDeathScreen", true));
    public final Setting<Float> delay = this.register(new Setting<>("Delay", 0.0f, 0.0f, 20.0f, (v) -> !noDeathScreen.getValue()));
    public final Setting<Boolean> deathCoords = this.register(new Setting<>("DeathCoords", false));
    public final Setting<Boolean> chat = this.register(new Setting<>("Chat", true, (v) -> deathCoords.getValue()));
    public final Setting<Boolean> saveToFile = this.register(new Setting<>("SaveToFile", false, (v) -> deathCoords.getValue()));

    private final Path deathCoordsFolder = FILES.getClientFolder().resolve("death_coords");
    private final Timer timer = new Timer();

    public AutoRespawn() {
        if (!FILES.exists(this.deathCoordsFolder)) {
            FILES.mkDir(this.deathCoordsFolder, false);
        }
    }

    @SubscribeEvent
    public void onGuiScreenChange(GuiChangeEvent event) {
        if (event.getCurrentGui() instanceof GuiGameOver && mc.player.isDead) {
            if (this.deathCoords.getValue()) {
                if (this.chat.getValue()) {
                    Command.send(new TextBuilder()
                            .append(ChatColor.Dark_Gray, "You died at")
                            .append(" ")
                            .append(ChatColor.Red, "X: ")
                            .append(ChatColor.Red, String.valueOf(mc.player.posX))
                            .append(" ")
                            .append(ChatColor.Dark_Gray, ",")
                            .append(" ")
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {

    }
}
