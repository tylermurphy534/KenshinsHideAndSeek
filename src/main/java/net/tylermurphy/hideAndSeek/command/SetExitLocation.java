package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.Main;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class SetExitLocation implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!Main.plugin.status.equals("Standby")) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		Vector newExitPosition = new Vector();
		Player player = (Player) sender;
		if(player.getLocation().getBlockX() == 0 || player.getLocation().getBlockZ() == 0 || player.getLocation().getBlockY() == 0){
			sender.sendMessage(errorPrefix + message("NOT_AT_ZERO"));
			return;
		}
		newExitPosition.setX(player.getLocation().getBlockX());
		newExitPosition.setY(player.getLocation().getBlockY());
		newExitPosition.setZ(player.getLocation().getBlockZ());
		exitWorld = player.getLocation().getWorld().getName();
		exitPosition = newExitPosition;
		sender.sendMessage(messagePrefix + message("EXIT_SPAWN"));
		addToConfig("spawns.exit.x", exitPosition.getX());
		addToConfig("spawns.exit.y", exitPosition.getY());
		addToConfig("spawns.exit.z", exitPosition.getZ());
		addToConfig("spawns.exit.world", player.getLocation().getWorld().getName());
		saveConfig();
	}

	public String getLabel() {
		return "setexit";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Sets hide and seeks exit location to current position and world";
	}

}
