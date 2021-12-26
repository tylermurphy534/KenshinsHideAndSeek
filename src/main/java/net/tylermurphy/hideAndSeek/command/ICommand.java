package net.tylermurphy.hideAndSeek.command;

import org.bukkit.command.CommandSender;

public interface ICommand {

	void execute(CommandSender sender, String[] args);
	
	String getLabel();

	String getUsage();
	
	String getDescription();
	
}
