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

public class LegacyPlayerInfo {

    private final byte[] uniqueId;
    private final int totalWins;
    private final int hiderWins;
    private final int seekerWins;
    private final int gamesPlayed;

    public LegacyPlayerInfo(byte[] uniqueId, int totalWins, int hiderWins, int seekerWins, int gamesPlayed) {
        this.uniqueId = uniqueId;
        this.totalWins = totalWins;
        this.hiderWins = hiderWins;
        this.seekerWins = seekerWins;
        this.gamesPlayed = gamesPlayed;
    }

    public byte[] getUniqueId() {
        return uniqueId;
    }

    public int getTotalWins() { return totalWins; }

    public int getHiderWins() {
        return hiderWins;
    }

    public int getSeekerWins() {
        return seekerWins;
    }

    public int getGamesPlayer() { return gamesPlayed; }

}
