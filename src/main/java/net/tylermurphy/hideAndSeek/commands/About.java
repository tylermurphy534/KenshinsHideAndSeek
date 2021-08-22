package net.tylermurphy.hideAndSeek.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.tylermurphy.hideAndSeek.ICommand;

public class About implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage(
				String.format("%s%sHide and Seek %s(1.1.0%s)\n", ChatColor.AQUA, ChatColor.BOLD, ChatColor.GRAY,ChatColor.WHITE,ChatColor.GRAY) + 
				String.format("%sAuthor: %s[KenshinEto]\n", ChatColor.GRAY, ChatColor.WHITE) + 
				String.format("%sHelp Command: %s/hs %shelp", ChatColor.GRAY, ChatColor.AQUA, ChatColor.WHITE)
			);
	}

	public String getLabel() {
		return "about";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Get information about the plugin";
	}

}
