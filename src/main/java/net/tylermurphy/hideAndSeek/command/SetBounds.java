package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class SetBounds implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!Main.plugin.status.equals("Standby")) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if(spawnPosition == null) {
			sender.sendMessage(errorPrefix + message("ERROR_GAME_SPAWN"));
			return;
		}
		Player player = (Player) sender;
		if(!player.getWorld().getName().equals(spawnWorld)){
			sender.sendMessage(errorPrefix + message("BOUNDS_WRONG_WORLD"));
			return;
		}
		if(saveMaxX == 0) {
			addToConfig("bounds.max.x", player.getLocation().getBlockX());
			saveMaxX = player.getLocation().getBlockX();
		} else if(saveMaxX < player.getLocation().getBlockX()) {
			addToConfig("bounds.max.x", player.getLocation().getBlockX());
			addToConfig("bounds.min.x", saveMaxX);
			saveMinX = saveMaxX;
			saveMaxX = player.getLocation().getBlockX();
		} else {
			addToConfig("bounds.min.x", player.getLocation().getBlockX());
			saveMinX = player.getLocation().getBlockX();
		}
		if(saveMaxZ == 0) {
			addToConfig("bounds.max.z", player.getLocation().getBlockZ());
			saveMaxZ = player.getLocation().getBlockZ();
		} else if(saveMaxZ < player.getLocation().getBlockZ()) {
			addToConfig("bounds.max.z", player.getLocation().getBlockZ());
			addToConfig("bounds.min.z", saveMaxZ);
			saveMinZ = saveMaxZ;
			saveMaxZ = player.getLocation().getBlockZ();
		} else {
			addToConfig("bounds.min.z", player.getLocation().getBlockZ());
			saveMinZ = player.getLocation().getBlockZ();
		}
		sender.sendMessage(messagePrefix + message("BOUNDS"));
		saveConfig();
	}

	public String getLabel() {
		return "setBounds";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Sets the map bounds for the game.";
	}

}
