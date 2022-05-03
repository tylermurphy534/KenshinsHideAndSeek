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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class About implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage(
				String.format("%s%sHide and Seek %s(%s1.4.3%s)\n", ChatColor.AQUA, ChatColor.BOLD, ChatColor.GRAY,ChatColor.WHITE,ChatColor.GRAY) +
				String.format("%sAuthor: %s[KenshinEto]\n", ChatColor.GRAY, ChatColor.WHITE) + 
				String.format("%sHelp Command: %s/hs %shelp", ChatColor.GRAY, ChatColor.AQUA, ChatColor.WHITE)
			);
	}

	public String getLabel() {
		return "about";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Get information about the plugin";
	}

}
