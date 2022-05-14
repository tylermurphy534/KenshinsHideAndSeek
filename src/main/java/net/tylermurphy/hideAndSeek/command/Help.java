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

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.util.CommandHandler;
import org.bukkit.command.CommandSender;

public class Help implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		StringBuilder message = new StringBuilder();
		for(ICommand command : CommandHandler.COMMAND_REGISTER.values()) {
			message.append(String.format("%s/hs %s%s %s%s\n  %s%s%s", ChatColor.AQUA, ChatColor.WHITE, command.getLabel().toLowerCase(), ChatColor.BLUE, command.getUsage(), ChatColor.GRAY, ChatColor.ITALIC, command.getDescription() + "\n"));
		}
		message = new StringBuilder(message.substring(0, message.length() - 1));
		sender.sendMessage(message.toString());
	}

	public String getLabel() {
		return "help";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Get the commands for the plugin";
	}

}
