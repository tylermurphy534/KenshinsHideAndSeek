/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation version 3.
 *
 * Kenshins Hide and Seek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.tylermurphy.hideAndSeek.command;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.world.WorldLoader;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class SetSpawnLocation implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;
		Player player = (Player) sender;

		Vector vec = Main.plugin.vectorFor(player);
		if (vec == null) return;

		if (worldborderEnabled && vec.distance(worldborderPosition) > 100) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_POSITION"));
			return;
		}

		World world = player.getLocation().getWorld();
		if (world == null) {
			throw new RuntimeException("Unable to get world: " + spawnWorld);
		}

		if (!world.getName().equals(spawnWorld)) {
			Game.worldLoader.unloadMap();
			Game.worldLoader = new WorldLoader(world.getName());
		}

		spawnWorld = world.getName();
		spawnPosition = vec;
		sender.sendMessage(messagePrefix + message("GAME_SPAWN"));
		addToConfig("spawns.game.x", vec.getX());
		addToConfig("spawns.game.y", vec.getY());
		addToConfig("spawns.game.z", vec.getZ());
		addToConfig("spawns.game.world", player.getLocation().getWorld().getName());
		saveConfig();
	}

	public String getLabel() {
		return "setspawn";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Sets hide and seeks spawn location to current position";
	}

}
