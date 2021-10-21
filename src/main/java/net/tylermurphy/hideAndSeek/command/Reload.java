package net.tylermurphy.hideAndSeek.command;

import org.bukkit.command.CommandSender;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.Config;

import static net.tylermurphy.hideAndSeek.Config.*;

public class Reload implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		
		if(!Main.plugin.status.equals("Standby")) {
			sender.sendMessage(errorPrefix + "Game is currently in session");
			return;
		}
		Config.loadConfig();
		Main.plugin.board.reload();
		sender.sendMessage(messagePrefix + "Reloaded the config");
	}

	public String getLabel() {
		return "reload";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Reloads the config";
	}
	
}
