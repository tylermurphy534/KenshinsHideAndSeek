package net.tylermurphy.hideAndSeek.commands;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Functions;
import net.tylermurphy.hideAndSeek.util.ICommand;

public class SaveMap implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) {
			sender.sendMessage(errorPrefix + "Please set spawn location first");
			return;
		}
		sender.sendMessage(warningPrefix + "This command may lag the server");
		Bukkit.getServer().getWorld(spawnWorld).save();
		File current = new File(Main.root+File.separator+spawnWorld);
		if(current.exists()) {
			File destenation = new File(Main.root+File.separator+"hideandseek_"+spawnWorld);
			if(destenation.exists()) {
				deleteDirectory(destenation);
				destenation.mkdir();
			}
			Functions.copyFileStructure(current, destenation);
			sender.sendMessage(messagePrefix + "Map save complete");
		} else {
			sender.sendMessage(errorPrefix + "Coudnt find current map");
		}
	}
	
	boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}

	public String getLabel() {
		return "saveMap";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Saves current map for the game. May lag server.";
	}
	
}
