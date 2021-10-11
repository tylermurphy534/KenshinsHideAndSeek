package net.tylermurphy.hideAndSeek.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.util.ICommand;

import static net.tylermurphy.hideAndSeek.Store.*;

public class Join implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!lobbyManualJoin) {
			sender.sendMessage(errorPrefix + "Manual join/leave isnt enabled in this server");
			return;
		}
		if(!status.equals("Standby")) {
			sender.sendMessage(errorPrefix + "Game is currently in session");
			return;
		}
		if(!lobbyStarted) {
			sender.sendMessage(errorPrefix + "There is currently no lobby in session");
			return;
		}
		Player player = Bukkit.getServer().getPlayer(sender.getName());
		if(player == null) {
			sender.sendMessage(errorPrefix + "An internal error has occured");
			return;
		}
		if(playerList.containsKey(player.getName())){
			sender.sendMessage(errorPrefix + "You are already in the lobby");
			return;
		}
		playerList.put(player.getName(), player);
		Hider.add(player.getName());
		HiderTeam.addEntry(player.getName());
		playerLastLocationList.put(player.getName(), player.getLocation());
		player.teleport(new Location(Bukkit.getWorld(spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
		if(lobbyAnnounced) Bukkit.broadcastMessage(messagePrefix + sender.getName() + " has joined the HideAndSeek lobby");
	}

	public String getLabel() {
		return "join";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Joins the lobby if game is set to manual join/leave";
	}

}
