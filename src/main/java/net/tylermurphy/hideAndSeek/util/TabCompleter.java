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

package net.tylermurphy.hideAndSeek.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import net.tylermurphy.hideAndSeek.game.CommandHandler;
import org.bukkit.command.CommandSender;

public class TabCompleter{

	public static List<String> handleTabComplete(CommandSender sender, String[] args) {
		if(args.length == 1) {
			return new ArrayList<>(CommandHandler.COMMAND_REGISTER.keySet())
					.stream()
					.filter(handle -> sender.hasPermission("hideandseek."+handle.toLowerCase()) && handle.toLowerCase().startsWith(args[0].toLowerCase(Locale.ROOT)))
					.collect(Collectors.toList());
		} else if(args.length > 1) {
			if(!CommandHandler.COMMAND_REGISTER.containsKey(args[0].toLowerCase())) {
				return null;
			} else {
				String[] usage = CommandHandler.COMMAND_REGISTER.get(args[0].toLowerCase()).getUsage().split(" ");
				if(args.length - 2 < usage.length) {
					String parameter = usage[args.length-2];
					if(parameter.equals("<player>")) {
						return null;
					} else {
						List<String> temp = new ArrayList<>();
						temp.add(parameter.replace("<", "").replace(">", ""));
						return temp;
					}
				} else {
					return null;
				}
			}
		}
		return null;
	}

}
