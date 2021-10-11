package net.tylermurphy.hideAndSeek.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.util.ICommand;

import static net.tylermurphy.hideAndSeek.Store.*;

public class Join implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!manualJoin) {
			sender.sendMessage(errorPrefix + "Manual join isnt enabled in this server");
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
		playerList.put(player.getName(), player);
		Hider.add(player.getName());
		HiderTeam.addEntry(player.getName());
		player.teleport(new Location(Bukkit.getWorld(spawnWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
		Bukkit.broadcastMessage(messagePrefix + sender.getName() + " has joined the game lobby");
	}

	public String getLabel() {
		return null;
	}

	public String getUsage() {
		return null;
	}

	public String getDescription() {
		return null;
	}

}
