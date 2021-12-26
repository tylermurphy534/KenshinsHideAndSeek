package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import net.tylermurphy.hideAndSeek.Main;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class SaveMap implements ICommand {

	public static boolean runningBackup = false;
	
	public void execute(CommandSender sender, String[] args) {
		if(Game.status != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) {
			sender.sendMessage(errorPrefix + message("ERROR_GAME_SPAWN"));
			return;
		}
		sender.sendMessage(messagePrefix + message("MAPSAVE_START"));
		sender.sendMessage(warningPrefix + message("MAPSAVE_WARNING"));
		World world = Bukkit.getServer().getWorld(spawnWorld);
		if(world == null){
			throw new RuntimeException("Unable to get world: " + spawnWorld);
		}
		world.save();
		BukkitRunnable runnable = new BukkitRunnable() {
			public void run() {
				sender.sendMessage(
						Game.worldLoader.save()
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
