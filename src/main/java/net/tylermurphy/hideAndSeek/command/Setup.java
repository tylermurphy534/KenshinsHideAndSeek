package net.tylermurphy.hideAndSeek.command;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.Main;

import static net.tylermurphy.hideAndSeek.Config.*;

import java.io.File;

public class Setup implements ICommand {
	
	public void execute(CommandSender sender, String[] args) {
		
		String message = String.format("%s%sThe following is needed for setup...", ChatColor.WHITE, ChatColor.BOLD);
		int count = 0;
		
		if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) {
			message = String.format("%s\n%s%s-%s%s", message, ChatColor.RED, ChatColor.BOLD, ChatColor.WHITE, "Game spawn isnt set, /hs setspawn");
			count++;
		}
		if(lobbyPosition.getBlockX() == 0 && lobbyPosition.getBlockY() == 0 && lobbyPosition.getBlockZ() == 0) {
			message = String.format("%s\n%s%s-%s%s", message, ChatColor.RED, ChatColor.BOLD, ChatColor.WHITE, "Lobby spawn isnt set, /hs setlobby");
			sender.sendMessage(errorPrefix + "Please set lobby location first");
			count++;
		}
		if(exitPosition.getBlockX() == 0 && exitPosition.getBlockY() == 0 && exitPosition.getBlockZ() == 0) {
			message = String.format("%s\n%s%s-%s%s", message, ChatColor.RED, ChatColor.BOLD, ChatColor.WHITE, "Quit/exit teleport location isnt set, /hs setexit");
			count++;
		}
		File destenation = new File(Main.root+File.separator+"hideandseek_"+spawnWorld);
		if(!destenation.exists()) {
			message = String.format("%s\n%s%s-%s%s", message, ChatColor.RED, ChatColor.BOLD, ChatColor.WHITE, "Hide and seek map isnt saved, /hs savemap (after /hs setspawn)");
			count++;
		}
		if(count < 1) {
			sender.sendMessage(messagePrefix + "Everything is setup and ready to go!");
		} else {
			sender.sendMessage(message);
		}
	}

	public String getLabel() {
		return "setup";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Shows what needs to be setup";
	}

}