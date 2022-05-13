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
import org.bukkit.command.CommandSender;

import java.io.File;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class Setup implements ICommand {
	
	public void execute(CommandSender sender, String[] args) {
		
		String msg = message("SETUP").toString();
		int count = 0;
		
		if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) {
			msg = msg + "\n" + message("SETUP_GAME");
			count++;
		}
		if(lobbyPosition.getBlockX() == 0 && lobbyPosition.getBlockY() == 0 && lobbyPosition.getBlockZ() == 0) {
			msg = msg + "\n" + message("SETUP_LOBBY");
			count++;
		}
		if(exitPosition.getBlockX() == 0 && exitPosition.getBlockY() == 0 && exitPosition.getBlockZ() == 0) {
			msg = msg + "\n" + message("SETUP_EXIT");
			count++;
		}
		if(saveMinX == 0 || saveMinZ == 0 || saveMaxX == 0 || saveMaxZ == 0) {
			msg = msg + "\n" + message("SETUP_BOUNDS");
			count++;
		}
		if(mapSaveEnabled) {
			File destenation = new File(Main.root + File.separator + Game.getGameWorld());
			if (!destenation.exists()) {
				msg = msg + "\n" + message("SETUP_SAVEMAP");
				count++;
			}
		}
		if(count < 1) {
			sender.sendMessage(messagePrefix + message("SETUP_COMPLETE"));
		} else {
			sender.sendMessage(msg);
		}
	}

	public String getLabel() {
		return "setup";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Shows what needs to be setup";
	}

}