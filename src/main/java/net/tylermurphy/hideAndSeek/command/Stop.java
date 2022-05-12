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

import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import net.tylermurphy.hideAndSeek.util.WinType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class Stop implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(Game.isNotSetup()) {
			sender.sendMessage(errorPrefix + "Game is not setup. Run /hs setup to see what you needed to do");
			return;
		}
		if(Game.status == Status.STARTING || Game.status == Status.PLAYING) {
			if(announceMessagesToNonPlayers) Bukkit.broadcastMessage(abortPrefix + message("STOP"));
			else Game.broadcastMessage(abortPrefix + message("STOP"));
			Game.stop(WinType.NONE);
		} else {
			sender.sendMessage(errorPrefix + message("GAME_NOT_INPROGRESS"));
		}
	}

	public String getLabel() {
		return "stop";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Stops the game";
	}

}
