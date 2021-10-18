package net.tylermurphy.hideAndSeek.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.util.Functions;
import net.tylermurphy.hideAndSeek.util.ICommand;

import static net.tylermurphy.hideAndSeek.Store.*;

public class Leave implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!Functions.setup()) {
			sender.sendMessage(errorPrefix + "Game is not setup. Run /hs setup to see what you needed to do");
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
		if(!Seeker.contains(player.getName())) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(messagePrefix + sender.getName() + " has left the HideAndSeek lobby");
			else Functions.broadcastMessage(messagePrefix + sender.getName() + " has left the HideAndSeek lobby");
		}
		playerList.remove(player.getName());
		Hider.remove(player.getName());
		Seeker.remove(player.getName());
		HiderTeam.removeEntry(player.getName());
		SeekerTeam.removeEntry(player.getName());
		player.teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
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