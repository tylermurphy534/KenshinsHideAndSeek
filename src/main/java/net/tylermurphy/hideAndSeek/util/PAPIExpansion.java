package net.tylermurphy.hideAndSeek.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.database.Database;
import net.tylermurphy.hideAndSeek.database.util.PlayerInfo;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
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
        if ((args.length == 2 || args.length == 3) && (args[0].equals("stats") || args[0].equals("rank-place"))) {
            Optional<PlayerInfo> info = this.getPlayerInfo(args.length == 2 ? player.getUniqueId() : database.getNameData().getUUID(args[2]));
            if (info.isPresent()) {
                switch (args[0]) {
                    case "stats":
                    case "rank-place":
                        return getValue(info.get(), args[1]);
                        if (getRanking(args[1]) == null) return placeholderError;
                        Integer count = database.getGameData().getRanking(getRanking(args[1]), player.getUniqueId());
                        if (getValue(info.get(), args[1]).equals("0")) return "-";
                        if (count == null) return placeholderNoData;
                        return count.toString();
                }
            }
        }

        if ((args[0].equals("rank-score") || args[0].equals("rank-name")) && args.length == 3) {
            int place = Integer.parseInt(args[2]);
            if (place < 1 || getRanking(args[1]) == null) return placeholderError;

            PlayerInfo info = database.getGameData().getInfoRanking(getRanking(args[1]), place);
            if (info == null) return placeholderNoData;

            return args[0].equals("rank-score") ? getValue(info, args[1]) : Main.getInstance().getServer().getOfflinePlayer(info.getUniqueId()).getName();
        }
        return null;
    }

    private String getValue(PlayerInfo info, String query) {
        if (query == null) return null;
        switch (query) {
            case "total-wins":
                return String.valueOf(info.getHiderWins() + info.getSeekerWins());
            case "hider-wins":
                return String.valueOf(info.getHiderWins());
            case "seeker-wins":
                return String.valueOf(info.getSeekerWins());
            case "total-games":
                return String.valueOf(info.getHiderGames() + info.getSeekerGames());
            case "hider-games":
                return String.valueOf(info.getHiderGames());
            case "seeker-games":
                return String.valueOf(info.getSeekerGames());
            case "total-kills":
                return String.valueOf(info.getHiderKills() + info.getSeekerKills());
            case "hider-kills":
                return String.valueOf(info.getHiderKills());
            case "seeker-kills":
                return String.valueOf(info.getSeekerKills());
            case "total-deaths":
                return String.valueOf(info.getHiderDeaths() + info.getSeekerDeaths());
            case "hider-deaths":
                return String.valueOf(info.getHiderDeaths());
            case "seeker-deaths":
                return String.valueOf(info.getSeekerDeaths());
            default:
                return null;
        }
    }

    private String getRanking(@NotNull String query) {
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

    private Optional<PlayerInfo> getPlayerInfo(@Nullable UUID uniqueId) {
        return Optional.ofNullable(Main.getInstance().getDatabase().getGameData().getInfo(uniqueId));
    }

}
