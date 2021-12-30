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
import net.tylermurphy.hideAndSeek.database.Database;
import net.tylermurphy.hideAndSeek.database.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Top implements ICommand {

    public void execute(CommandSender sender, String[] args) {
        int page;
        if(args.length == 0) page = 1;
        else try{
            page = Integer.parseInt(args[0]);
        } catch(Exception e){
            sender.sendMessage(errorPrefix + message("WORLDBORDER_INVALID_INPUT").addAmount(args[0]));
            return;
        }
        if(page < 1){
            sender.sendMessage(errorPrefix + message("WORLDBORDER_INVALID_INPUT").addAmount(page));
            return;
        }
        StringBuilder message = new StringBuilder(String.format(
                "%s------- %sLEADERBOARD %s(Page %s) %s-------\n",
                ChatColor.WHITE, ChatColor.BOLD, ChatColor.GRAY, page, ChatColor.WHITE));
        List<PlayerInfo> infos = Database.playerInfo.getInfoPage(page);
        int i = 1 + (page-1)*10;
        for(PlayerInfo info : infos){
            String name = Main.plugin.getServer().getOfflinePlayer(info.uuid).getName();
            ChatColor color;
            switch (i){
                case 1: color = ChatColor.YELLOW; break;
                case 2: color = ChatColor.GRAY; break;
                case 3: color = ChatColor.GOLD; break;
                default: color = ChatColor.WHITE; break;
            }
            message.append(String.format("%s%s. %s%s %s%s\n",
                    color, i, ChatColor.RED, info.wins, ChatColor.WHITE, name));
            i++;
        }
        sender.sendMessage(message.toString());
    }

    public String getLabel() {
        return "top";
    }

    public String getUsage() {
        return "<page>";
    }

    public String getDescription() {
        return "Gets the top players in the server.";
    }
}
