/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation, either version 3 of the License.
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
