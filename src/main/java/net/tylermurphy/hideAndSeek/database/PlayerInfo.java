package net.tylermurphy.hideAndSeek.database;

import java.util.UUID;

public class PlayerInfo {

    public UUID uuid;
    public int wins, hider_wins, seeker_wins, games_played;

    public PlayerInfo(UUID uuid, int wins, int hider_wins, int seeker_wins, int games_played){
        this.uuid = uuid;
        this.wins = wins;
        this.hider_wins = hider_wins;
        this.seeker_wins = seeker_wins;
        this.games_played = games_played;
    }

}
