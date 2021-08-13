package net.tylermurphy.hideAndSeek.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.ICommand;
import net.tylermurphy.hideAndSeek.manager.WorldborderManager;

import static net.tylermurphy.hideAndSeek.Store.*;

public class SetBorder implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(spawnPosition == null) {
			sender.sendMessage(errorPrefix + "Please set spawn position first");
			return;
		}
		if(args.length < 2) {
			sender.sendMessage(errorPrefix + "Please enter worldborder size and delay");
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
		saveConfig();
		WorldborderManager.reset();
	}

	public String getLabel() {
		return "setBorder";
	}
	
	public String getUsage() {
		return "<size> <delay>";
	}

	public String getDescription() {
		return "Sets worldboarder's center location, size in blocks, and delay in minutes";
	}

}
