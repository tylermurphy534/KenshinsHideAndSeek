package net.tylermurphy.hideAndSeek.command;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.bukkit.CommandHandler;

public class Help implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		String message = "";
		for(ICommand command : CommandHandler.COMMAND_REGISTER.values()) {
			message += String.format("%s/hs %s%s %s%s\n  %s%s%s", ChatColor.AQUA, ChatColor.WHITE, command.getLabel().toLowerCase(), ChatColor.BLUE, command.getUsage(), ChatColor.GRAY, ChatColor.ITALIC, command.getDescription()+"\n");
		}
		message = message.substring(0, message.length()-1);
		sender.sendMessage(message);
	}

	public String getLabel() {
		return "help";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Get the commands for the plugin";
	}

}
