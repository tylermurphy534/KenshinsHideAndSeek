package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.util.ICommand;

public class SetLobbyLocation implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		Vector newLobbyPosition = new Vector();
		Player player = (Player) sender;
		newLobbyPosition.setX(player.getLocation().getBlockX());
		newLobbyPosition.setY(player.getLocation().getBlockY());
		newLobbyPosition.setZ(player.getLocation().getBlockZ());
		if(!status.equals("Standby")) {
			sender.sendMessage(errorPrefix + "Game is currently in session");
			return;
		}
		lobbyPosition = newLobbyPosition;
		sender.sendMessage(messagePrefix + "Set lobby position to current location");
		Map<String, Object> temp = new HashMap<String,Object>();
		temp.put("x", lobbyPosition.getX());
		temp.put("y", lobbyPosition.getY());
		temp.put("z", lobbyPosition.getZ());
		addToSection("lobby.spawn",temp);
		saveConfig();
	}

	public String getLabel() {
		return "setlobby";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Sets hide and seeks lobby location to current position";
	}

}
