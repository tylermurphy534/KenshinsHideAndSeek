package net.tylermurphy.hideAndSeek.commands;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.ICommand;

public class SetSeeker implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!status.equals("Standby") && !status.equals("Setup")) {
			sender.sendMessage(errorPrefix + "Game is currently in session");
			return;
		}
		String playerName;
		if(args.length < 1) {
			playerName = sender.getName();
		} else {
			playerName = args[0];
		}
		Player player = playerList.get(playerName);
		if(player == null) {
			sender.sendMessage(errorPrefix + "Invalid player: " + playerName);
			return;
		}
		for(Player temp : playerList.values()) {
			Hider.addEntry(temp.getName());
		}
		Seeker.addEntry(player.getName());
		sender.sendMessage(String.format("%s Set %s as the seaker.", messagePrefix, playerName));
	}

	public String getLabel() {
		return "setSeeker";
	}
	
	public String getUsage() {
		return "<player>";
	}

	public String getDescription() {
		return "Sets the current or select player as the seeker";
	}

}
