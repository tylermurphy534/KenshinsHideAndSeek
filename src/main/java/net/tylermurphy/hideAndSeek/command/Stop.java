package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.game.Status;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Util;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Stop implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!Util.isSetup()) {
			sender.sendMessage(errorPrefix + "Game is not setup. Run /hs setup to see what you needed to do");
			return;
		}
		if(Main.plugin.status == Status.STARTING || Main.plugin.status == Status.PLAYING) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(abortPrefix + message("STOP"));
			else Util.broadcastMessage(abortPrefix + message("STOP"));
			Main.plugin.game.stop();
		} else {
			sender.sendMessage(errorPrefix + message("GAME_NOT_INPROGRESS"));
		}
	}

	public String getLabel() {
		return "stop";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Stops the game";
	}

}
