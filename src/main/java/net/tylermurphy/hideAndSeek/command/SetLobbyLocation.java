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
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class SetLobbyLocation implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) return;
		Player player = (Player) sender;

		Vector vec = Main.plugin.vectorFor(player);
		World world = player.getLocation().getWorld();
		if(world == null){
			throw new RuntimeException("Unable to get world: " + spawnWorld);
		}
		lobbyWorld = world.getName();
		lobbyPosition = vec;
		sender.sendMessage(messagePrefix + message("LOBBY_SPAWN"));
		addToConfig("spawns.lobby.x", lobbyPosition.getX());
		addToConfig("spawns.lobby.y", lobbyPosition.getY());
		addToConfig("spawns.lobby.z", lobbyPosition.getZ());
		addToConfig("spawns.lobby.world", player.getLocation().getWorld().getName());
		saveConfig();
	}

	public String getLabel() {
		return "setlobby";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Sets hide and seeks lobby location to current position";
	}

}
