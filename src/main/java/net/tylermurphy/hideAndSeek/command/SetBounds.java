/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation, either version 3 of the License.
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

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class SetBounds implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(Game.status != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if(spawnPosition == null) {
			sender.sendMessage(errorPrefix + message("ERROR_GAME_SPAWN"));
			return;
		}
		Player player = (Player) sender;
		if(!player.getWorld().getName().equals(spawnWorld)){
			sender.sendMessage(errorPrefix + message("BOUNDS_WRONG_WORLD"));
			return;
		}
		if(player.getLocation().getBlockX() == 0 || player.getLocation().getBlockZ() == 0){
			sender.sendMessage(errorPrefix + message("NOT_AT_ZERO"));
			return;
		}
		boolean first = true;
		if(saveMinX != 0 && saveMinZ != 0 && saveMaxX != 0 && saveMaxZ != 0) {
			saveMinX = 0; saveMinZ= 0; saveMaxX = 0; saveMaxZ = 0;
		}
		if(saveMaxX == 0) {
			addToConfig("bounds.max.x", player.getLocation().getBlockX());
			saveMaxX = player.getLocation().getBlockX();
		} else if(saveMaxX < player.getLocation().getBlockX()) {
			first = false;
			addToConfig("bounds.max.x", player.getLocation().getBlockX());
			addToConfig("bounds.min.x", saveMaxX);
			saveMinX = saveMaxX;
			saveMaxX = player.getLocation().getBlockX();
		} else {
			first = false;
			addToConfig("bounds.min.x", player.getLocation().getBlockX());
			saveMinX = player.getLocation().getBlockX();
		}
		if(saveMaxZ == 0) {
			addToConfig("bounds.max.z", player.getLocation().getBlockZ());
			saveMaxZ = player.getLocation().getBlockZ();
		} else if(saveMaxZ < player.getLocation().getBlockZ()) {
			first = false;
			addToConfig("bounds.max.z", player.getLocation().getBlockZ());
			addToConfig("bounds.min.z", saveMaxZ);
			saveMinZ = saveMaxZ;
			saveMaxZ = player.getLocation().getBlockZ();
		} else {
			first = false;
			addToConfig("bounds.min.z", player.getLocation().getBlockZ());
			saveMinZ = player.getLocation().getBlockZ();
		}
		sender.sendMessage(messagePrefix + message("BOUNDS").addAmount(first ? 1 : 2));
		saveConfig();
	}

	public String getLabel() {
		return "setBounds";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Sets the map bounds for the game.";
	}

}
