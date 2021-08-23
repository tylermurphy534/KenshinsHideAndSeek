package net.tylermurphy.hideAndSeek.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.util.ICommand;

import static net.tylermurphy.hideAndSeek.Store.*;

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
		getConfig().set("spawnPosition", newSpawnPosition);
		saveConfig();
//		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("setworldspawn %s %s %s", player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
//		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("spawnpoint @a %s %s %s", player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
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
