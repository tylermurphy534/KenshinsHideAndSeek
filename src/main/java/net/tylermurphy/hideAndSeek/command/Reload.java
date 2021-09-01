package net.tylermurphy.hideAndSeek.command;

import org.bukkit.command.CommandSender;

import net.tylermurphy.hideAndSeek.Store;
import net.tylermurphy.hideAndSeek.util.Functions;
import net.tylermurphy.hideAndSeek.util.ICommand;

import static net.tylermurphy.hideAndSeek.Store.*;

public class Reload implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		Store.loadConfig();
		try {
			Functions.loadScoreboard();
		} catch(Exception e) {}
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
