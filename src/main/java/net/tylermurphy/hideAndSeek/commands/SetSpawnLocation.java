package net.tylermurphy.hideAndSeek.commands;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.util.ICommand;

public class SetSpawnLocation implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		Vector newSpawnPosition = new Vector();
		Player player = (Player) sender;
		newSpawnPosition.setX(player.getLocation().getBlockX());
		newSpawnPosition.setY(player.getLocation().getBlockY());
		newSpawnPosition.setZ(player.getLocation().getBlockZ());
		if(worldborderEnabled && spawnPosition.distance(worldborderPosition) > 100) {
			sender.sendMessage(errorPrefix + "Spawn position must be 100 from worldborder center");
			return;
		}
		spawnPosition = newSpawnPosition;
		status = "Standby";
		sender.sendMessage(messagePrefix + "Set spawn position to current location");
		Map<String, Object> temp = new HashMap<String,Object>();
		temp.put("x", spawnPosition.getX());
		temp.put("y", spawnPosition.getY());
		temp.put("z", spawnPosition.getZ());
		temp.put("world", player.getLocation().getWorld().getName());
		addToSection("spawn",temp);
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
