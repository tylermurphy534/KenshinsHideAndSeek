package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import net.tylermurphy.hideAndSeek.util.WinType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Stop implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(Game.isNotSetup()) {
			sender.sendMessage(errorPrefix + "Game is not setup. Run /hs setup to see what you needed to do");
			return;
		}
		if(Game.status == Status.STARTING || Game.status == Status.PLAYING) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(abortPrefix + message("STOP"));
			else Game.broadcastMessage(abortPrefix + message("STOP"));
			Game.stop(WinType.NONE);
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
