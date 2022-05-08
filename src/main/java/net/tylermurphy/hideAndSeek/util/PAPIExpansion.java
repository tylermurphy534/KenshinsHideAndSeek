package net.tylermurphy.hideAndSeek.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.tylermurphy.hideAndSeek.database.Database;
import net.tylermurphy.hideAndSeek.database.PlayerInfo;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.UUID;

public class PAPIExpansion extends PlaceholderExpansion  {

    @Override
    public @NotNull String getIdentifier() {
        return "kenshinshideandseek";
    }

    @Override
    public @NotNull String getAuthor() {
        return "KenshinEto";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.4.3";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        switch (params.toLowerCase(Locale.ROOT)) {
            case "hs_stats_total-wins":
                return hs_stats_total_wins(player.getUniqueId());
            case "hs_stats_hider-wins":
                return hs_stats_hider_wins(player.getUniqueId());
            case "hs_stats_seeker-wins":
                return hs_stats_seeker_wins(player.getUniqueId());
            case "hs_stats_total-games":
                return hs_stats_total_games(player.getUniqueId());
            case "hs_stats_hider-games":
                return hs_stats_hider_games(player.getUniqueId());
            case "hs_stats_seeker-games":
                return hs_stats_seeker_games(player.getUniqueId());
            case "hs_stats_total-kills":
                return hs_stats_total_kills(player.getUniqueId());
            case "hs_stats_hider-kills":
                return hs_stats_hider_kills(player.getUniqueId());
            case "hs_stats_seeker-kills":
                return hs_stats_seeker_kills(player.getUniqueId());
            case "hs_stats_total-deaths":
                return hs_stats_total_deaths(player.getUniqueId());
            case "hs_stats_hider-deaths":
                return hs_stats_hider_deaths(player.getUniqueId());
            case "hs_stats_seeker-deaths":
                return hs_stats_seeker_deaths(player.getUniqueId());
            default:
                return null;
        }
    }

    private String hs_stats_total_wins(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.hider_wins + info.seeker_wins);
    }

    private String hs_stats_hider_wins(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.hider_wins);
    }

    private String hs_stats_seeker_wins(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.seeker_wins);
    }

    private String hs_stats_total_games(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.hider_games + info.seeker_games);
    }

    private String hs_stats_hider_games(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.hider_games);
    }

    private String hs_stats_seeker_games(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.seeker_games);
    }

    private String hs_stats_total_kills(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.hider_kills + info.seeker_kills);
    }

    private String hs_stats_hider_kills(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.hider_kills);
    }

    private String hs_stats_seeker_kills(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.seeker_kills);
    }

    private String hs_stats_total_deaths(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.hider_deaths + info.seeker_deaths);
    }

    private String hs_stats_hider_deaths(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.hider_deaths);
    }

    private String hs_stats_seeker_deaths(UUID uuid){
        PlayerInfo info = Database.playerInfo.getInfo(uuid);
        return String.valueOf(info.seeker_deaths);
    }

}
