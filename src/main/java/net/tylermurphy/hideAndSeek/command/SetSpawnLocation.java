package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.Main;

import static net.tylermurphy.hideAndSeek.configuration.Config.addToConfig;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class SetSpawnLocation implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		Vector newSpawnPosition = new Vector();
		Player player = (Player) sender;
		newSpawnPosition.setX(player.getLocation().getBlockX());
		newSpawnPosition.setY(player.getLocation().getBlockY());
		newSpawnPosition.setZ(player.getLocation().getBlockZ());
		if(!Main.plugin.status.equals("Standby")) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if(worldborderEnabled && spawnPosition.distance(worldborderPosition) > 100) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_POSITION"));
			return;
		}
		spawnPosition = newSpawnPosition;
		sender.sendMessage(messagePrefix + message("GAME_SPAWN"));
		addToConfig("spawns.game.x", spawnPosition.getX());
		addToConfig("spawns.game.y", spawnPosition.getY());
		addToConfig("spawns.game.z", spawnPosition.getZ());
		addToConfig("spawns.game.world", player.getLocation().getWorld().getName());
		saveConfig();
	}

	public String getLabel() {
		return "setspawn";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Sets hide and seeks spawn location to current position";
	}

}
