package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import org.bukkit.command.CommandSender;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.configuration.Config;
import net.tylermurphy.hideAndSeek.configuration.Localization;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Reload implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		
		if(!Main.plugin.status.equals("Standby")) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		Config.loadConfig();
		Localization.loadLocalization();
		sender.sendMessage(messagePrefix + message("CONFIG_RELOAD"));
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
