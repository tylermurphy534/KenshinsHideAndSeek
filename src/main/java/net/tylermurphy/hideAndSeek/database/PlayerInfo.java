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

package net.tylermurphy.hideAndSeek.database;

import java.util.UUID;

public class PlayerInfo {

    public final UUID uuid;
    public final int hider_wins;
    public final int seeker_wins;
    public final int hider_games;
    public final int seeker_games;
    public final int hider_kills;
    public final int seeker_kills;
    public final int hider_deaths;
    public final int seeker_deaths;

    public PlayerInfo(UUID uuid, int hider_wins, int seeker_wins, int hider_games, int seeker_games, int hider_kills, int seeker_kills, int hider_deaths, int seeker_deaths) {
        this.uuid = uuid;
        this.hider_wins = hider_wins;
        this.seeker_wins = seeker_wins;
        this.hider_games = hider_games;
        this.seeker_games = seeker_games;
        this.hider_kills = hider_kills;
        this.seeker_kills = seeker_kills;
        this.hider_deaths = hider_deaths;
        this.seeker_deaths = seeker_deaths;
    }
}
