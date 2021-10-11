package net.tylermurphy.hideAndSeek.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.util.ICommand;

import static net.tylermurphy.hideAndSeek.Store.*;

public class Leave implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!lobbyManualJoin) {
			sender.sendMessage(errorPrefix + "Manual join/leave isnt set to manual in this server");
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
		if(!playerList.containsKey(player.getName())) {
			sender.sendMessage(errorPrefix + "You are currently not in the lobby");
			return;
		}
		playerList.remove(player.getName());
		Hider.remove(player.getName());
		Seeker.remove(player.getName());
		HiderTeam.removeEntry(player.getName());
		SeekerTeam.removeEntry(player.getName());
		player.teleport(playerLastLocationList.get(player.getName()));
		if(lobbyAnnounced) Bukkit.broadcastMessage(messagePrefix + sender.getName() + " has left the HideAndSeek lobby");
	}

	public String getLabel() {
		return "leave";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Leaves the lobby if game is set to manual join/leave";
	}

}