package net.tylermurphy.hideAndSeek.commands;

import org.bukkit.command.CommandSender;

import net.tylermurphy.hideAndSeek.Store;
import net.tylermurphy.hideAndSeek.util.ICommand;

import static net.tylermurphy.hideAndSeek.Store.*;

public class Reload implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		Store.loadConfig();
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
