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

import net.tylermurphy.hideAndSeek.configuration.Items;
import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.command.CommandSender;

import net.tylermurphy.hideAndSeek.configuration.Config;
import net.tylermurphy.hideAndSeek.configuration.Localization;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Reload implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		
		if(Game.status != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		Config.loadConfig();
		Localization.loadLocalization();
		Items.loadItems();
		sender.sendMessage(messagePrefix + message("CONFIG_RELOAD"));
	}

	public String getLabel() {
		return "reload";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Reloads the config";
	}
	
}
