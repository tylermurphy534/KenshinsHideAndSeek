package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.events.Worldborder;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class SetBorder implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(!Main.plugin.status.equals("Standby")) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if(spawnPosition == null) {
			sender.sendMessage(errorPrefix + message("ERROR_GAME_SPAWN"));
			return;
		}
		if(args.length < 2) {
			worldborderEnabled = false;
			addToConfig("worldBorder.enabled",false);
			saveConfig();
			sender.sendMessage(messagePrefix + message("WORLDBORDER_DISABLE"));
			Worldborder.resetWorldborder(spawnWorld);
			return;
		}
		int num,delay;
		try { num = Integer.parseInt(args[0]); } catch (Exception e) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_INVALID_INPUT").addAmount(args[0]));
			return;
		}
		try { delay = Integer.parseInt(args[1]); } catch (Exception e) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_INVALID_INPUT").addAmount(args[1]));
			return;
		}
		if(num < 100) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_MIN_SIZE"));
			return;
		}
		Vector newWorldborderPosition = new Vector();
		Player player = (Player) sender;
		newWorldborderPosition.setX(player.getLocation().getBlockX());
		newWorldborderPosition.setY(0);
		newWorldborderPosition.setZ(player.getLocation().getBlockZ());
		if(spawnPosition.distance(newWorldborderPosition) > 100) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_POSITION"));
			return;
		}
		worldborderPosition = newWorldborderPosition;
		worldborderSize = num;
		worldborderDelay = delay;
		worldborderEnabled = true;
		addToConfig("worldBorder.x", worldborderPosition.getBlockX());
		addToConfig("worldBorder.z", worldborderPosition.getBlockZ());
		addToConfig("worldBorder.delay", worldborderDelay);
		addToConfig("worldBorder.size", worldborderSize);
		addToConfig("worldBorder.enabled", true);
		sender.sendMessage(messagePrefix + message("WORLDBORDER_ENABLE").addAmount(num).addAmount(delay));
		saveConfig();
		Worldborder.resetWorldborder(spawnWorld);
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
