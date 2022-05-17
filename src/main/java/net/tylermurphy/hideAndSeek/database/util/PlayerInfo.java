/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021-2022 Tyler Murphy.
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

package net.tylermurphy.hideAndSeek.database.util;

import java.util.UUID;

public class PlayerInfo {

    private final UUID uniqueId;
    private final int hiderWins;
    private final int seekerWins;
    private final int hiderGames;
    private final int seekerGames;
    private final int hiderKills;
    private final int seekerKills;
    private final int hiderDeaths;
    private final int seekerDeaths;

    public PlayerInfo(UUID uniqueId, int hiderWins, int seekerWins, int hiderGames, int seekerGames, int hiderKills, int seekerKills, int hiderDeaths, int seekerDeaths) {
        this.uniqueId = uniqueId;
        this.hiderWins = hiderWins;
        this.seekerWins = seekerWins;
        this.hiderGames = hiderGames;
        this.seekerGames = seekerGames;
        this.hiderKills = hiderKills;
        this.seekerKills = seekerKills;
        this.hiderDeaths = hiderDeaths;
        this.seekerDeaths = seekerDeaths;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public int getHiderWins() {
        return hiderWins;
    }

    public int getSeekerWins() {
        return seekerWins;
    }

    public int getHiderGames() {
        return hiderGames;
    }

    public int getSeekerGames() {
        return seekerGames;
    }

    public int getHiderKills() {
        return hiderKills;
    }

    public int getSeekerKills() {
        return seekerKills;
    }

    public int getHiderDeaths() {
        return hiderDeaths;
    }

    public int getSeekerDeaths() {
        return seekerDeaths;
    }

}
