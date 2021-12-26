package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class SetExitLocation implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(Game.status != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		Vector newExitPosition = new Vector();
		Player player = (Player) sender;
		if(player.getLocation().getBlockX() == 0 || player.getLocation().getBlockZ() == 0 || player.getLocation().getBlockY() == 0){
			sender.sendMessage(errorPrefix + message("NOT_AT_ZERO"));
			return;
		}
		newExitPosition.setX(player.getLocation().getBlockX());
		newExitPosition.setY(player.getLocation().getBlockY());
		newExitPosition.setZ(player.getLocation().getBlockZ());
		World world = player.getLocation().getWorld();
		if(world == null){
			throw new RuntimeException("Unable to get world: " + spawnWorld);
		}
		exitWorld = world.getName();
		exitPosition = newExitPosition;
		sender.sendMessage(messagePrefix + message("EXIT_SPAWN"));
		addToConfig("spawns.exit.x", exitPosition.getX());
		addToConfig("spawns.exit.y", exitPosition.getY());
		addToConfig("spawns.exit.z", exitPosition.getZ());
		addToConfig("spawns.exit.world", player.getLocation().getWorld().getName());
		saveConfig();
	}

	public String getLabel() {
		return "setexit";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Sets hide and seeks exit location to current position and world";
	}

}
