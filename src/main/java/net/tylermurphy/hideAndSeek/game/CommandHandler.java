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

package net.tylermurphy.hideAndSeek.game;

import net.tylermurphy.hideAndSeek.command.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.tylermurphy.hideAndSeek.configuration.Config.errorPrefix;
import static net.tylermurphy.hideAndSeek.configuration.Config.permissionsRequired;
import static net.tylermurphy.hideAndSeek.configuration.Localization.LOCAL;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class CommandHandler {

	public static final Map<String,ICommand> COMMAND_REGISTER = new LinkedHashMap<>();
	
	private static void registerCommand(ICommand command) {
		if (!COMMAND_REGISTER.containsKey(command.getLabel())) {
			COMMAND_REGISTER.put(command.getLabel().toLowerCase(), command);
		}
	}
	
	public static void registerCommands() {
		registerCommand(new About());
		registerCommand(new Help());
		registerCommand(new Setup());
		registerCommand(new Start());
		registerCommand(new Stop());
		registerCommand(new SetSpawnLocation());
		registerCommand(new SetLobbyLocation());
		registerCommand(new SetExitLocation());
		registerCommand(new SetBorder());
		registerCommand(new Reload());
		registerCommand(new SaveMap());
		registerCommand(new SetBounds());
		registerCommand(new Join());
		registerCommand(new Leave());
		registerCommand(new Top());
		registerCommand(new Wins());
	}
	
	public static boolean handleCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(errorPrefix + message("COMMAND_PLAYER_ONLY"));
		} else if (args.length < 1 || !COMMAND_REGISTER.containsKey(args[0].toLowerCase()) ) {
			if (permissionsRequired && !sender.hasPermission("hideandseek.about")) {
				sender.sendMessage(errorPrefix + LOCAL.get(""));
			} else {
				COMMAND_REGISTER.get("about").execute(sender, null);
			}
		} else {
			if (!args[0].equalsIgnoreCase("about") && !args[0].equalsIgnoreCase("help") && SaveMap.runningBackup) {
				sender.sendMessage(errorPrefix + message("MAPSAVE_INPROGRESS"));
			} else if (permissionsRequired && !sender.hasPermission("hideandseek."+args[0].toLowerCase())) {
				sender.sendMessage(errorPrefix + message("COMMAND_NOT_ALLOWED"));
			} else {
				try {
					COMMAND_REGISTER.get(args[0].toLowerCase()).execute(sender,Arrays.copyOfRange(args, 1, args.length));
				} catch (Exception e) {
					sender.sendMessage(errorPrefix + "An error has occured.");
					e.printStackTrace();
				}
			}
		}
		return true;
	}

}
