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
import net.tylermurphy.hideAndSeek.database.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.tylermurphy.hideAndSeek.configuration.Config.errorPrefix;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class Wins implements ICommand {

    public void execute(CommandSender sender, String[] args) {
        OfflinePlayer player = args.length == 0 ? (Player) sender : Main.getInstance().getServer().getOfflinePlayer(args[0]);

        PlayerInfo info = Main.getInstance().getDatabase().getPlayerInfo().getInfo(player.getUniqueId());
        if (info == null) {
            sender.sendMessage(errorPrefix + message("NO_GAME_INFO"));
            return;
        }

        String message = String.valueOf(ChatColor.WHITE) + ChatColor.BOLD + "==============================" + "\n" +
                message("INFORMATION_FOR").addPlayer(player.getName()) + "\n" +
                "==============================" + "\n" +
                String.format("%sTOTAL WINS: %s%s\n%sHIDER WINS: %s%s\n%sSEEKER WINS: %s%s\n%sGAMES PLAYED: %s",
                        ChatColor.YELLOW, ChatColor.WHITE, info.wins, ChatColor.GOLD, ChatColor.WHITE, info.hider_wins,
                        ChatColor.RED, ChatColor.WHITE, info.seeker_wins, ChatColor.WHITE, info.games_played) +
                "\n" +
                ChatColor.WHITE + ChatColor.BOLD + "==============================";
        sender.sendMessage(message);
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
