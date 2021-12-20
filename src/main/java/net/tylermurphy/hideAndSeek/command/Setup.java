package net.tylermurphy.hideAndSeek.command;

import org.bukkit.command.CommandSender;

import net.tylermurphy.hideAndSeek.Main;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.io.File;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Setup implements ICommand {
	
	public void execute(CommandSender sender, String[] args) {
		
		String msg = message("SETUP").toString();
		int count = 0;
		
		if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) {
			msg = msg + "\n" + message("SETUP_GAME").toString();
			count++;
		}
		if(lobbyPosition.getBlockX() == 0 && lobbyPosition.getBlockY() == 0 && lobbyPosition.getBlockZ() == 0) {
			msg = msg + "\n" + message("SETUP_LOBBY").toString();
			count++;
		}
		if(exitPosition.getBlockX() == 0 && exitPosition.getBlockY() == 0 && exitPosition.getBlockZ() == 0) {
			msg = msg + "\n" + message("SETUP_EXIT").toString();
			count++;
		}
		if(saveMinX == 0 || saveMinZ == 0 || saveMaxX == 0 || saveMaxZ == 0) {
			msg = msg + "\n" + message("SETUP_BOUNDS").toString();
			count++;
		}
		File destenation = new File(Main.root+File.separator+"hideandseek_"+spawnWorld);
		if(!destenation.exists()) {
			msg = msg + "\n" + message("SETUP_SAVEMAP").toString();
			count++;
		}
		if(count < 1) {
			sender.sendMessage(messagePrefix + message("SETUP_COMPLETE"));
		} else {
			sender.sendMessage(msg);
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