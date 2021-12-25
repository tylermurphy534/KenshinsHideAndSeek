package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.game.Status;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import net.tylermurphy.hideAndSeek.Main;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class SaveMap implements ICommand {

	public static boolean runningBackup = false;
	
	public void execute(CommandSender sender, String[] args) {
		if(Main.plugin.status != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) {
			sender.sendMessage(errorPrefix + message("ERROR_GAME_SPAWN"));
			return;
		}
		sender.sendMessage(messagePrefix + message("MAPSAVE_START"));
		sender.sendMessage(warningPrefix + message("MAPSAVE_WARNING"));
		Bukkit.getServer().getWorld(spawnWorld).save();
		BukkitRunnable runnable = new BukkitRunnable() {
			public void run() {
				sender.sendMessage(
						Main.plugin.worldLoader.save()
						);
				runningBackup = false;
			}
		};
		runnable.runTaskAsynchronously(Main.plugin);
		runningBackup = true;
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
