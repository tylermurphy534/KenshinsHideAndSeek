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

package net.tylermurphy.hideAndSeek.command.location;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.command.ICommand;
import net.tylermurphy.hideAndSeek.command.location.util.LocationUtils;
import net.tylermurphy.hideAndSeek.command.location.util.Locations;
import org.bukkit.entity.Player;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class SetSpawnLocation implements ICommand {

	public void execute(Player sender, String[] args) {
		LocationUtils.setLocation(sender, Locations.GAME, vector -> {
			if (worldBorderEnabled && vector.distance(worldBorderPosition) > 100) {
				sender.sendMessage(errorPrefix + message("WORLDBORDER_POSITION"));
				throw new RuntimeException("World border not enabled or not in valid position!");
			}

			if (!sender.getLocation().getWorld().getName().equals(spawnWorld)) {
				Main.getInstance().getGame().getWorldLoader().unloadMap();
				Main.getInstance().getGame().getWorldLoader().setNewMap(sender.getLocation().getWorld().getName());
			}

			spawnWorld = sender.getLocation().getWorld().getName();
			spawnPosition = vector;
		});
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
