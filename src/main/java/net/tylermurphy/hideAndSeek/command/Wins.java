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
import net.tylermurphy.hideAndSeek.database.util.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.tylermurphy.hideAndSeek.configuration.Config.errorPrefix;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class Wins implements ICommand {

    public void execute(Player sender, String[] args) {
        Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {

            UUID uuid;
            String name;
            if (args.length == 0) {
                uuid = sender.getUniqueId();
                name = sender.getName();
            }
            else {
                name = args[0];
                if(Main.getInstance().getServer().getOfflinePlayer(args[0]) == null){
                    uuid = Main.getInstance().getDatabase().getNameData().getUUID(args[0]);
                } else {
                    uuid = Main.getInstance().getServer().getOfflinePlayer(args[0]).getUniqueId();
                }
            }
            if(uuid == null){
                sender.sendMessage(errorPrefix + message("START_INVALID_NAME").addPlayer(args[0]));
                return;
            }
            PlayerInfo info = Main.getInstance().getDatabase().getGameData().getInfo(uuid);
            if (info == null) {
                sender.sendMessage(errorPrefix + message("NO_GAME_INFO"));
                return;
            }
            String message = ChatColor.WHITE + "" + ChatColor.BOLD + "==============================\n";
            message = message + message("INFORMATION_FOR").addPlayer(name) + "\n";
            message = message + "==============================\n";
            message = message + String.format("%sTOTAL WINS: %s%s\n%sHIDER WINS: %s%s\n%sSEEKER WINS: %s%s\n%sGAMES PLAYED: %s",
                    ChatColor.YELLOW, ChatColor.WHITE, info.getSeekerWins() +info.getHiderWins(), ChatColor.GOLD, ChatColor.WHITE, info.getHiderWins(),
                    ChatColor.RED, ChatColor.WHITE, info.getSeekerWins(), ChatColor.WHITE, info.getSeekerGames() +info.getHiderGames());
            message = message + ChatColor.WHITE + "" + ChatColor.BOLD + "\n==============================";
            sender.sendMessage(message);

        });
    }

    public String getLabel() {
        return "wins";
    }

    public String getUsage() {
        return "<player>";
    }

    public String getDescription() {
        return "Get the win information for yourself or another player.";
    }
}
