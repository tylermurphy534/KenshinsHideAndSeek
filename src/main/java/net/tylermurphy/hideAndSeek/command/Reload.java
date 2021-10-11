package net.tylermurphy.hideAndSeek.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Store;
import net.tylermurphy.hideAndSeek.util.Functions;
import net.tylermurphy.hideAndSeek.util.ICommand;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.util.HashMap;

public class Reload implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!status.equals("Standby")) {
			sender.sendMessage(errorPrefix + "Game is currently in session");
			return;
		}
		Store.loadConfig();
		try {
			Functions.loadScoreboard();
		} catch(Exception e) {}
		sender.sendMessage(messagePrefix + "Reloaded the config");
		playerList = new HashMap<String,Player>();
		if(!lobbyManualJoin) {
			for(Player p : Bukkit.getOnlinePlayers())
				playerList.put(p.getName(), p);
		}
	}

	public String getLabel() {
		return "reload";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Reloads the config";
	}
	
}
