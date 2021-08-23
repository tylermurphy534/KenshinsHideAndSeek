package net.tylermurphy.hideAndSeek.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.util.Functions;
import net.tylermurphy.hideAndSeek.util.ICommand;

import static net.tylermurphy.hideAndSeek.Store.*;

public class SetBorder implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!status.equals("Standby") && !status.equals("Setup")) {
			sender.sendMessage(errorPrefix + "Game is currently in session");
			return;
		}
		if(spawnPosition == null) {
			sender.sendMessage(errorPrefix + "Please set spawn position first");
			return;
		}
		if(args.length < 2) {
			getConfig().set("borderEnabled", false);
			worldborderEnabled = false;
			saveConfig();
			sender.sendMessage(messagePrefix + "Disabled worldborder.");
			Functions.resetWorldborder();
			return;
		}
		int num,delay;
		try { num = Integer.parseInt(args[0]); } catch (Exception e) {
			sender.sendMessage(errorPrefix + "Invalid integer: "+args[0]);
			return;
		}
		try { delay = Integer.parseInt(args[1]); } catch (Exception e) {
			sender.sendMessage(errorPrefix + "Invalid integer: "+args[1]);
			return;
		}
		if(num < 100) {
			sender.sendMessage(errorPrefix + "Worldborder cannot be smaller than 100 blocks.");
			return;
		}
		Vector newWorldborderPosition = new Vector();
		Player player = (Player) sender;
		newWorldborderPosition.setX(player.getLocation().getBlockX());
		newWorldborderPosition.setY(0);
		newWorldborderPosition.setZ(player.getLocation().getBlockZ());
		if(spawnPosition.distance(newWorldborderPosition) > 100) {
			sender.sendMessage(errorPrefix + "Spawn position must be 100 from worldborder center");
			return;
		}
		worldborderPosition = newWorldborderPosition;
		worldborderSize = num;
		worldborderDelay = delay;
		sender.sendMessage(messagePrefix + "Set border center to current location, size to "+num+", and delay to "+delay);
		getConfig().set("borderPosition", newWorldborderPosition);
		getConfig().set("borderSize", num);
		getConfig().set("borderDelay", delay);
		getConfig().set("borderEnabled", false);
		worldborderEnabled = true;
		saveConfig();
		Functions.resetWorldborder();
	}

	public String getLabel() {
		return "setBorder";
	}
	
	public String getUsage() {
		return "<size> <delay>";
	}

	public String getDescription() {
		return "Sets worldboarder's center location, size in blocks, and delay in minutes per shrink. Add no arguments to disable.";
	}

}
