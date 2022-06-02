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
import net.tylermurphy.hideAndSeek.game.util.Status;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class SaveMap implements ICommand {

	public static boolean runningBackup = false;
	
	public void execute(Player sender, String[] args) {
		if (!mapSaveEnabled) {
			sender.sendMessage(errorPrefix + message("MAPSAVE_DISABLED"));
			return;
		}
		if (Main.getInstance().getGame().getStatus() != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if (spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) {
			sender.sendMessage(errorPrefix + message("ERROR_GAME_SPAWN"));
			return;
		}
		sender.sendMessage(messagePrefix + message("MAPSAVE_START"));
		sender.sendMessage(warningPrefix + message("MAPSAVE_WARNING"));
		World world = Bukkit.getServer().getWorld(spawnWorld);
		if (world == null) {
			throw new RuntimeException("Unable to get world: " + spawnWorld);
		}
		world.save();
		BukkitRunnable runnable = new BukkitRunnable() {
			public void run() {
				sender.sendMessage(
						Main.getInstance().getGame().getWorldLoader().save()
						);
				runningBackup = false;
			}
		};
		runnable.runTaskAsynchronously(Main.getInstance());
		runningBackup = true;
	}

	public String getLabel() {
		return "saveMap";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Saves current map for the game. May lag server.";
	}
	
}
