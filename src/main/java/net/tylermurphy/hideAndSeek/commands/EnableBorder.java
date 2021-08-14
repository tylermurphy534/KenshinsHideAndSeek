package net.tylermurphy.hideAndSeek.commands;

import org.bukkit.command.CommandSender;

import net.tylermurphy.hideAndSeek.ICommand;
import net.tylermurphy.hideAndSeek.manager.WorldborderManager;

import static net.tylermurphy.hideAndSeek.Store.*;

public class EnableBorder implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!status.equals("Standby") && !status.equals("Setup")) {
			sender.sendMessage(errorPrefix + "Game is currently in session");
			return;
		}
		if(worldborderPosition == null) {
			sender.sendMessage(errorPrefix + "Please setup worldborder info before enabling");
			return;
		}
		boolean bool;
		try { bool = Boolean.parseBoolean(args[0]); } catch (Exception e) {
			sender.sendMessage(errorPrefix + "Please enter true or false");
			return;
		}
		if(spawnPosition != null && worldborderPosition != null && spawnPosition.distance(worldborderPosition) > 100) {
			sender.sendMessage(errorPrefix + "Cannot enable worldborder, spawn position is outside 100 blocks from worldborder");
			return;
		}
		sender.sendMessage(messagePrefix + "Set worldborder to "+args[0]);
		getConfig().set("borderEnabled", bool);
		worldborderEnabled = bool;
		saveConfig();
		WorldborderManager.reset();
	}

	public String getLabel() {
		return "enableBorder";
	}
	
	public String getUsage() {
		return "<true/false>";
	}

	public String getDescription() {
		return "Enables or disables worldborder";
	}

}
