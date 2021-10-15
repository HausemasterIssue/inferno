package me.sxmurai.inferno.client.features.modules.miscellaneous;

import me.sxmurai.inferno.api.events.mc.GuiChangeEvent;
import me.sxmurai.inferno.api.utils.timing.Timer;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.commands.Command;
import me.sxmurai.inferno.client.manager.managers.commands.text.ChatColor;
import me.sxmurai.inferno.client.manager.managers.commands.text.TextBuilder;
import me.sxmurai.inferno.client.manager.managers.misc.FileManager;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Module.Define(name = "AutoRespawn", description = "Automatically respawns you")
public class AutoRespawn extends Module {
    private static final FileManager FILES = FileManager.getInstance();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss z");

    public final Value<Boolean> noDeathScreen = new Value<>("NoDeathScreen", true);
    public final Value<Float> delay = new Value<>("Delay", 0.0f, 0.0f, 20.0f, (v) -> !noDeathScreen.getValue());
    public final Value<Boolean> deathCoords = new Value<>("DeathCoords", false);
    public final Value<Boolean> chat = new Value<>("Chat", true, (v) -> deathCoords.getValue());
    public final Value<Boolean> saveToFile = new Value<>("SaveToFile", false, (v) -> deathCoords.getValue());

    private final Path deathCoordsFolder = FILES.getClientFolder().resolve("death_coords");
    private final Timer timer = new Timer();

    public AutoRespawn() {
        if (!FILES.exists(this.deathCoordsFolder)) {
            FILES.mkDir(this.deathCoordsFolder, false);
        }
    }

    @SubscribeEvent
    public void onGuiScreenChange(GuiChangeEvent event) {
        if (event.getCurrentGui() instanceof GuiGameOver) {
            if (this.deathCoords.getValue()) {
                if (this.chat.getValue()) {
                    Command.send(new TextBuilder()
                            .append(ChatColor.Dark_Gray, "You died at")
                            .append(" ")
                            .append(ChatColor.Red, "X: ")
                            .append(ChatColor.Red, String.valueOf(mc.player.posX))
                            .append(ChatColor.Dark_Gray, ",")
                            .append(" ")
                            .append(ChatColor.Red, "Y: ")
                            .append(ChatColor.Red, String.valueOf(mc.player.posY))
                            .append(ChatColor.Dark_Gray, ",")
                            .append(" ")
                            .append(ChatColor.Red, "Z: ")
                            .append(ChatColor.Red, String.valueOf(mc.player.posZ))
                            .append(ChatColor.Dark_Gray, ".")
                    );
                }

                if (this.saveToFile.getValue()) {
                    this.write(mc.player.getPosition());
                }
            }

            if (this.noDeathScreen.getValue()) {
                mc.player.respawnPlayer();
            }
        }
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiGameOver && !this.noDeathScreen.getValue() && this.timer.passedMs(this.delay.getValue().longValue())) {
            this.timer.reset();
            mc.player.respawnPlayer();
        }
    }

    private void write(BlockPos pos) {
        String ip = mc.isSingleplayer() ? "singleplayer" : Objects.requireNonNull(mc.getCurrentServerData()).serverIP;
        Path file = this.deathCoordsFolder.resolve(ip + ".txt");

        String text = FILES.readFile(file);
        text = text == null ?
                ("Generated " + DATE_FORMAT.format(new Date(System.currentTimeMillis())) + "\n") :
                text;

        FILES.writeFile(file, text + "\n Died at " + pos.toString() + " on " + DATE_FORMAT.format(new Date(System.currentTimeMillis())));
    }
}
