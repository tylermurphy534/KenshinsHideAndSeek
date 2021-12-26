package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.configuration.Items;
import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.command.CommandSender;

import net.tylermurphy.hideAndSeek.configuration.Config;
import net.tylermurphy.hideAndSeek.configuration.Localization;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Reload implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		
		if(Game.status != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		Config.loadConfig();
		Localization.loadLocalization();
		Items.loadItems();
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
