package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.commands.Command;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;

@Module.Define(name = "ExtraTab", description = "Expands the amount of players you can see in tab")
public class ExtraTab extends Module {
	
	public final Setting<Integer> players = this.register(new Setting<Integer>("Players", 250, 1, 1000));
	
	 public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
	        String name;
	        String string = name = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName((Team)networkPlayerInfoIn.getPlayerTeam(), (String)networkPlayerInfoIn.getGameProfile().getName());
	        return name;
	    }

}
