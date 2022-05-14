package net.tylermurphy.hideAndSeek.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.database.Database;
import net.tylermurphy.hideAndSeek.database.PlayerInfo;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.tylermurphy.hideAndSeek.configuration.Config.placeholderError;
import static net.tylermurphy.hideAndSeek.configuration.Config.placeholderNoData;

public class PAPIExpansion extends PlaceholderExpansion  {

    @Override
    public @NotNull String getIdentifier() {
        return "hs";
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
        Database database = Main.getInstance().getDatabase();
        String[] args = params.split("_");
        if (args.length < 1) return null;
        if (args[0].equals("stats") && args.length == 2) {
            PlayerInfo info = database.getGameData().getInfo(player.getUniqueId());
            return getValue(info, args[1]);
        } else if (args[0].equals("stats") && args.length == 3) {
            UUID uuid;
            try { uuid = Main.getInstance().getServer().getOfflinePlayer(args[2]).getUniqueId(); } catch (Exception e) { return placeholderError; }
            PlayerInfo info = database.getGameData().getInfo(uuid);
            return getValue(info, args[1]);
        } else if ((args[0].equals("rank-score") || args[0].equals("rank-name") ) && args.length == 3) {
            int place;
            try { place = Integer.parseInt(args[2]); } catch (NumberFormatException e) { return placeholderError; }
            if (place < 1) { return placeholderError; }
            if (getRanking(args[1]) == null) { return placeholderError; }
            PlayerInfo info = database.getGameData().getInfoRanking(getRanking(args[1]), place);
            if (info == null) return placeholderNoData;
            if (args[0].equals("rank-score")) {
                return getValue(info, args[1]);
            } else {
                return Main.getInstance().getServer().getOfflinePlayer(info.uuid).getName();
            }
        } else if (args[0].equals("rank-place") && args.length == 2) {
            if (getRanking(args[1]) == null) { return placeholderError; }
            PlayerInfo info = database.getGameData().getInfo(player.getUniqueId());
            if (getValue(info, args[1]).equals("0")) { return "-"; }
            Integer count = database.getGameData().getRanking(getRanking(args[1]), player.getUniqueId());
            if (count == null) { return placeholderNoData; }
            return count.toString();
        } else if (args[0].equals("rank-place") && args.length == 3) {
            UUID uuid;
            try { uuid = Main.getInstance().getServer().getOfflinePlayer(args[2]).getUniqueId(); } catch (Exception e) { return placeholderError; }
            if (getRanking(args[1]) == null) { return placeholderError; }
            PlayerInfo info = database.getGameData().getInfo(player.getUniqueId());
            if (getValue(info, args[1]).equals("0")) { return "-"; }
            Integer count = database.getGameData().getRanking(getRanking(args[1]), uuid);
            if (count == null) { return placeholderNoData; }
            return count.toString();
        }
        return null;
    }

    private String getValue(PlayerInfo info, String query) {
        if (query == null) return null;
        switch (query) {
            case "total-wins":
                return String.valueOf(info.hider_wins + info.seeker_wins);
            case "hider-wins":
                return String.valueOf(info.hider_wins);
            case "seeker-wins":
                return String.valueOf(info.seeker_wins);
            case "total-games":
                return String.valueOf(info.hider_games + info.seeker_games);
            case "hider-games":
                return String.valueOf(info.hider_games);
            case "seeker-games":
                return String.valueOf(info.seeker_games);
            case "total-kills":
                return String.valueOf(info.hider_kills + info.seeker_kills);
            case "hider-kills":
                return String.valueOf(info.hider_kills);
            case "seeker-kills":
                return String.valueOf(info.seeker_kills);
            case "total-deaths":
                return String.valueOf(info.hider_deaths + info.seeker_deaths);
            case "hider-deaths":
                return String.valueOf(info.hider_deaths);
            case "seeker-deaths":
                return String.valueOf(info.seeker_deaths);
            default:
                return null;
        }
    }

    private String getRanking(String query) {
        if (query == null) return null;
        switch (query) {
            case "total-wins":
                return "(hider_wins + seeker_wins)";
            case "hider-wins":
                return "hider_wins";
            case "seeker-wins":
                return "seeker_wins";
            case "total-games":
                return "(hider_games + seeker_games)";
            case "hider-games":
                return "hider_games";
            case "seeker-games":
                return "seeker_games";
            case "total-kills":
                return "(hider_kills + seeker_kills)";
            case "hider-kills":
                return "hider_kills";
            case "seeker-kills":
                return "seeker_kills";
            case "total-deaths":
                return "(hider_deaths + seeker_deaths)";
            case "hider-deaths":
                return "hider_deaths";
            case "seeker-deaths":
                return "seeker_deaths";
            default:
                return null;
        }
    }

}
