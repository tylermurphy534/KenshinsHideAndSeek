package net.tylermurphy.hideAndSeek.util;

import org.bukkit.command.CommandSender;

public interface ICommand {

	public void execute(CommandSender sender, String[] args);
	
	public String getLabel();
	
	public String getUsage();
	
	public String getDescription();
	
}
