package net.tylermurphy.hideAndSeek.command;

import com.comphenix.protocol.PacketType;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.database.PlayerInfo;
import net.tylermurphy.hideAndSeek.util.CommandHandler;
import net.tylermurphy.hideAndSeek.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Wins implements ICommand {

    public void execute(CommandSender sender, String[] args) {
        Main.plugin.getServer().getScheduler().runTaskAsynchronously(Main.plugin, () -> {

            UUID uuid;
            String name;
            if(args.length == 0) {
                uuid = Main.plugin.getServer().getPlayer(sender.getName()).getUniqueId();
                name = sender.getName();
            }
            else {
                try {
                    name = args[0];
                    uuid = UUIDFetcher.getUUID(args[0]);
                } catch (Exception e){
                    sender.sendMessage(errorPrefix + message("START_INVALID_NAME").addPlayer(args[0]));
                    return;
                }
            }
            PlayerInfo info = Main.plugin.database.playerInfo.getInfo(uuid);
            if(info == null){
                sender.sendMessage(errorPrefix + message("NO_GAME_INFO"));
                return;
            }
            String message = ChatColor.WHITE + "" + ChatColor.BOLD + "==============================\n";
            message = message + message("INFORMATION_FOR").addPlayer(name) + "\n";
            message = message + "==============================\n";
            message = message + String.format("%sTOTAL WINS: %s%s\n%sHIDER WINS: %s%s\n%sSEEKER WINS: %s%s\n%sGAMES PLAYED: %s",
                    ChatColor.YELLOW, ChatColor.WHITE, info.wins, ChatColor.GOLD, ChatColor.WHITE, info.hider_wins,
                    ChatColor.RED, ChatColor.WHITE, info.seeker_wins, ChatColor.WHITE, info.games_played);
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
