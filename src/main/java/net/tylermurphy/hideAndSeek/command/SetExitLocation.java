package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.Config.*;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.Main;

public class SetExitLocation implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		Vector newExitPosition = new Vector();
		Player player = (Player) sender;
		newExitPosition.setX(player.getLocation().getBlockX());
		newExitPosition.setY(player.getLocation().getBlockY());
		newExitPosition.setZ(player.getLocation().getBlockZ());
		if(!Main.plugin.status.equals("Standby")) {
			sender.sendMessage(errorPrefix + "Game is currently in session");
			return;
		}
		exitPosition = newExitPosition;
		sender.sendMessage(messagePrefix + "Set exit position to current location");
		Map<String, Object> temp = new HashMap<String,Object>();
		temp.put("x", exitPosition.getX());
		temp.put("y", exitPosition.getY());
		temp.put("z", exitPosition.getZ());
		temp.put("world", player.getLocation().getWorld().getName());
		addToSection("spawns.exit",temp);
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
