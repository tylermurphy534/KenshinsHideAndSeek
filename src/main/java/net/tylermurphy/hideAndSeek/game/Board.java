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

package net.tylermurphy.hideAndSeek.game;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.game.events.Border;
import net.tylermurphy.hideAndSeek.game.events.Glow;
import net.tylermurphy.hideAndSeek.game.events.Taunt;
import net.tylermurphy.hideAndSeek.game.util.Status;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.stream.Collectors;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class Board {

    private final List<String> Hider = new ArrayList<>(), Seeker = new ArrayList<>(), Spectator = new ArrayList<>();
    private final Map<String, Player> playerList = new HashMap<>();
    private final Map<String, CustomBoard> customBoards = new HashMap<>();
    private final Map<String, Integer> hider_kills = new HashMap<>(), seeker_kills = new HashMap<>(), hider_deaths = new HashMap<>(), seeker_deaths = new HashMap<>();

    public boolean contains(Player player) {
        return playerList.containsKey(player.getUniqueId().toString());
    }

    public boolean isHider(Player player) {
        return Hider.contains(player.getUniqueId().toString());
    }

    public boolean isHider(UUID uuid) {
        return Hider.contains(uuid.toString());
    }

    public boolean isSeeker(Player player) {
        return Seeker.contains(player.getUniqueId().toString());
    }

    public boolean isSeeker(UUID uuid) {
        return Seeker.contains(uuid.toString());
    }

    public boolean isSpectator(Player player) {
        return Spectator.contains(player.getUniqueId().toString());
    }

    public int sizeHider() {
        return Hider.size();
    }

    public int sizeSeeker() {
        return Seeker.size();
    }

    public int size() {
        return playerList.values().size();
    }

    public List<Player> getHiders() {
        return Hider.stream().map(playerList::get).collect(Collectors.toList());
    }

    public List<Player> getSeekers() {
        return Seeker.stream().map(playerList::get).collect(Collectors.toList());
    }

    public Player getFirstSeeker() {
        return playerList.get(Seeker.get(0));
    }

    public List<Player> getSpectators() {
        return Spectator.stream().map(playerList::get).collect(Collectors.toList());
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(playerList.values());
    }

    public Player getPlayer(UUID uuid) {
        return playerList.get(uuid.toString());
    }

    public void addHider(Player player) {
        Hider.add(player.getUniqueId().toString());
        Seeker.remove(player.getUniqueId().toString());
        Spectator.remove(player.getUniqueId().toString());
        playerList.put(player.getUniqueId().toString(), player);
    }

    public void addSeeker(Player player) {
        Hider.remove(player.getUniqueId().toString());
        Seeker.add(player.getUniqueId().toString());
        Spectator.remove(player.getUniqueId().toString());
        playerList.put(player.getUniqueId().toString(), player);
    }

    public void addSpectator(Player player) {
        Hider.remove(player.getUniqueId().toString());
        Seeker.remove(player.getUniqueId().toString());
        Spectator.add(player.getUniqueId().toString());
        playerList.put(player.getUniqueId().toString(), player);
    }

    public void remove(Player player) {
        Hider.remove(player.getUniqueId().toString());
        Seeker.remove(player.getUniqueId().toString());
        Spectator.remove(player.getUniqueId().toString());
        playerList.remove(player.getUniqueId().toString());
    }

    public boolean onSameTeam(Player player1, Player player2) {
        if (Hider.contains(player1.getUniqueId().toString()) && Hider.contains(player2.getUniqueId().toString())) return true;
        else if (Seeker.contains(player1.getUniqueId().toString()) && Seeker.contains(player2.getUniqueId().toString())) return true;
        else return Spectator.contains(player1.getUniqueId().toString()) && Spectator.contains(player2.getUniqueId().toString());
    }

    public void reload() {
        Hider.clear();
        Seeker.clear();
        Spectator.clear();
        hider_kills.clear();
        seeker_kills.clear();
        hider_deaths.clear();
        seeker_deaths.clear();
    }

    public void addKill(UUID uuid) {
        if (Hider.contains(uuid.toString())) {
            if (hider_kills.containsKey(uuid.toString())) {
                hider_kills.put(uuid.toString(), hider_kills.get(uuid.toString())+1);
            } else {
                hider_kills.put(uuid.toString(), 1);
            }
        } else if (getFirstSeeker().getUniqueId().equals(uuid)) {
            if (seeker_kills.containsKey(uuid.toString())) {
                seeker_kills.put(uuid.toString(), seeker_kills.get(uuid.toString())+1);
            } else {
                seeker_kills.put(uuid.toString(), 1);
            }
        }
    }

    public void addDeath(UUID uuid) {
        if (Hider.contains(uuid.toString())) {
            if (hider_deaths.containsKey(uuid.toString())) {
                hider_deaths.put(uuid.toString(), hider_deaths.get(uuid.toString())+1);
            } else {
                hider_deaths.put(uuid.toString(), 1);
            }
        } else if (getFirstSeeker().getUniqueId().equals(uuid)) {
            if (seeker_deaths.containsKey(uuid.toString())) {
                seeker_deaths.put(uuid.toString(), seeker_deaths.get(uuid.toString())+1);
            } else {
                seeker_deaths.put(uuid.toString(), 1);
            }
        }
    }

    public Map<String, Integer> getHiderKills() {
        return new HashMap<>(hider_kills);
    }
    public Map<String, Integer> getSeekerKills() {
        return new HashMap<>(seeker_kills);
    }
    public Map<String, Integer> getHiderDeaths() {
        return new HashMap<>(hider_deaths);
    }
    public Map<String, Integer> getSeekerDeaths() {
        return new HashMap<>(seeker_deaths);
    }

    public void createLobbyBoard(Player player) {
        createLobbyBoard(player, true);
    }

    private void createLobbyBoard(Player player, boolean recreate) {
        CustomBoard board = customBoards.get(player.getUniqueId().toString());
        if (recreate || board == null) {
            board = new CustomBoard(player, LOBBY_TITLE);
            board.updateTeams();
        }
        int i=0;
        for(String line : LOBBY_CONTENTS) {
            if (line.equalsIgnoreCase("")) {
                board.addBlank();
            } else if (line.contains("{COUNTDOWN}")) {
                if (!lobbyCountdownEnabled) {
                    board.setLine(String.valueOf(i), line.replace("{COUNTDOWN}", COUNTDOWN_ADMINSTART));
                } else if (Main.getInstance().getGame().getLobbyTime() == -1) {
                    board.setLine(String.valueOf(i), line.replace("{COUNTDOWN}", COUNTDOWN_WAITING));
                } else {
                    board.setLine(String.valueOf(i), line.replace("{COUNTDOWN}", COUNTDOWN_COUNTING.replace("{AMOUNT}",Main.getInstance().getGame().getLobbyTime()+"")));
                }
            } else if (line.contains("{COUNT}")) {
                board.setLine(String.valueOf(i), line.replace("{COUNT}", getPlayers().size()+""));
            } else if (line.contains("{SEEKER%}")) {
                board.setLine(String.valueOf(i), line.replace("{SEEKER%}", getSeekerPercent()+""));
            } else if (line.contains("{HIDER%}")) {
                board.setLine(String.valueOf(i), line.replace("{HIDER%}", getHiderPercent()+""));
            } else {
                board.setLine(String.valueOf(i), line);
            }
            i++;
        }
        board.display();
        customBoards.put(player.getUniqueId().toString(), board);
    }

    public void createGameBoard(Player player) {
        createGameBoard(player, true);
    }

    private void createGameBoard(Player player, boolean recreate) {
        CustomBoard board = customBoards.get(player.getUniqueId().toString());
        if (recreate) {
            board = new CustomBoard(player, GAME_TITLE);
            board.updateTeams();
        }


        int timeLeft = Main.getInstance().getGame().getTimeLeft();
        Status status = Main.getInstance().getGame().getStatus();

        Taunt taunt = Main.getInstance().getGame().getTaunt();
        Border worldBorder = Main.getInstance().getGame().getBorder();
        Glow glow = Main.getInstance().getGame().getGlow();

        int i = 0;
        for(String line : GAME_CONTENTS) {
            if (line.equalsIgnoreCase("")) {
                board.addBlank();
            } else {
                if (line.contains("{TIME}")) {
                    String value = timeLeft/60 + "m" + timeLeft%60 + "s";
                    board.setLine(String.valueOf(i), line.replace("{TIME}", value));
                } else if (line.contains("{TEAM}")) {
                    String value = getTeam(player);
                    board.setLine(String.valueOf(i), line.replace("{TEAM}", value));
                } else if (line.contains("{BORDER}")) {
                    if (!worldBorderEnabled) continue;
                    if (worldBorder == null || status == Status.STARTING) {
                        board.setLine(String.valueOf(i), line.replace("{BORDER}", BORDER_COUNTING.replace("{AMOUNT}", "0")));
                    } else if (!worldBorder.isRunning()) {
                        board.setLine(String.valueOf(i), line.replace("{BORDER}", BORDER_COUNTING.replaceFirst("\\{AMOUNT}", worldBorder.getDelay()/60+"").replaceFirst("\\{AMOUNT}", worldBorder.getDelay()%60+"")));
                    } else {
                        board.setLine(String.valueOf(i), line.replace("{BORDER}", BORDER_DECREASING));
                    }
                } else if (line.contains("{TAUNT}")) {
                    if (!tauntEnabled) continue;
                    if (taunt == null || status == Status.STARTING) {
                        board.setLine(String.valueOf(i), line.replace("{TAUNT}", TAUNT_COUNTING.replace("{AMOUNT}", "0")));
                    } else if (!tauntLast && Hider.size() == 1) {
                        board.setLine(String.valueOf(i), line.replace("{TAUNT}", TAUNT_EXPIRED));
                    } else if (!taunt.isRunning()) {
                        board.setLine(String.valueOf(i), line.replace("{TAUNT}", TAUNT_COUNTING.replaceFirst("\\{AMOUNT}", taunt.getDelay() / 60 + "").replaceFirst("\\{AMOUNT}", taunt.getDelay() % 60 + "")));
                    } else {
                        board.setLine(String.valueOf(i), line.replace("{TAUNT}", TAUNT_ACTIVE));
                    }
                } else if (line.contains("{GLOW}")) {
                    if (!glowEnabled)  return;
                    if (glow == null || status == Status.STARTING || !glow.isRunning()) {
                        board.setLine(String.valueOf(i), line.replace("{GLOW}", GLOW_INACTIVE));
                    } else {
                        board.setLine(String.valueOf(i), line.replace("{GLOW}", GLOW_ACTIVE));
                    }
                } else if (line.contains("{#SEEKER}")) {
                    board.setLine(String.valueOf(i), line.replace("{#SEEKER}", getSeekers().size()+""));
                } else if (line.contains("{#HIDER}")) {
                    board.setLine(String.valueOf(i), line.replace("{#HIDER}", getHiders().size()+""));
                } else {
                    board.setLine(String.valueOf(i), line);
                }
            }
            i++;
        }
        board.display();
        customBoards.put(player.getUniqueId().toString(), board);
    }

    public void removeBoard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        player.setScoreboard(manager.getMainScoreboard());
        customBoards.remove(player.getUniqueId().toString());
    }

    public void reloadLobbyBoards() {
        for(Player player : playerList.values())
            createLobbyBoard(player, false);
    }

    public void reloadGameBoards() {
        for(Player player : playerList.values())
            createGameBoard(player, false);
    }

    public void reloadBoardTeams() {
        for(CustomBoard board : customBoards.values())
            board.updateTeams();
    }

    private String getSeekerPercent() {
        if (playerList.values().size() < 2)
            return " --";
        else
            return " "+(int)(100*(1.0/playerList.size()));
    }

    private String getHiderPercent() {
        if (playerList.size() < 2)
            return " --";
        else
            return " "+(int)(100-100*(1.0/playerList.size()));
    }

    private String getTeam(Player player) {
        if (isHider(player)) return message("HIDER_TEAM_NAME").toString();
        else if (isSeeker(player)) return message("SEEKER_TEAM_NAME").toString();
        else if (isSpectator(player)) return message("SPECTATOR_TEAM_NAME").toString();
        else return ChatColor.WHITE + "UNKNOWN";
    }

    public void cleanup() {
        playerList.clear();
        Hider.clear();
        Seeker.clear();
        Spectator.clear();
        customBoards.clear();
    }

}

class CustomBoard {

    private final Scoreboard board;
    private final Objective obj;
    private final Player player;
    private final Map<String,Line> LINES;
    private int blanks;
    private boolean displayed;

    public CustomBoard(Player player, String title) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        this.board = manager.getNewScoreboard();
        this.LINES = new HashMap<>();
        this.player = player;
        if (Main.getInstance().supports(13)) {
            this.obj = board.registerNewObjective(
                    "Scoreboard", "dummy", ChatColor.translateAlternateColorCodes('&', title));
        } else {
            this.obj = board.registerNewObjective("Scoreboard", "dummy");
            this.obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
        }
        this.blanks = 0;
        this.displayed = false;
        this.updateTeams();
    }

    public void updateTeams() {
        try{ board.registerNewTeam("Hider"); } catch (Exception ignored) {}
        try{ board.registerNewTeam("Seeker"); } catch (Exception ignored) {}
        Team hiderTeam = board.getTeam("Hider");
        assert hiderTeam != null;
        for(String entry : hiderTeam.getEntries())
            hiderTeam.removeEntry(entry);
        for(Player player : Main.getInstance().getBoard().getHiders())
            hiderTeam.addEntry(player.getName());
        Team seekerTeam = board.getTeam("Seeker");
        assert seekerTeam != null;
        for(String entry : seekerTeam.getEntries())
            seekerTeam.removeEntry(entry);
        for(Player player  : Main.getInstance().getBoard().getSeekers())
            seekerTeam.addEntry(player.getName());
        if (Main.getInstance().supports(9)) {
            if (nameTagsVisible) {
                hiderTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
                seekerTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
            } else {
                hiderTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                seekerTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }
        } else {
            if (nameTagsVisible) {
                hiderTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
                seekerTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OWN_TEAM);
            } else {
                hiderTeam.setNameTagVisibility(NameTagVisibility.NEVER);
                seekerTeam.setNameTagVisibility(NameTagVisibility.NEVER);
            }
        }
        if (Main.getInstance().supports(12)) {
            hiderTeam.setColor(ChatColor.GOLD);
            seekerTeam.setColor(ChatColor.RED);
        } else {
            hiderTeam.setPrefix(ChatColor.translateAlternateColorCodes('&', "&6"));
            seekerTeam.setPrefix(ChatColor.translateAlternateColorCodes('&', "&c"));
        }
    }

    public void setLine(String key, String message) {
        Line line = LINES.get(key);
        if (line == null)
            addLine(key, ChatColor.translateAlternateColorCodes('&',message));
        else
            updateLine(key, ChatColor.translateAlternateColorCodes('&',message));
    }

    private void addLine(String key, String message) {
        Score score = obj.getScore(message);
        score.setScore(LINES.values().size()+1);
        Line line = new Line(LINES.values().size()+1, message);
        LINES.put(key, line);
    }

    public void addBlank() {
        if (displayed) return;
        StringBuilder temp = new StringBuilder();
        for(int i = 0; i <= blanks; i ++)
            temp.append(ChatColor.RESET);
        blanks++;
        addLine("blank"+blanks, temp.toString());
    }

    private void updateLine(String key, String message) {
        Line line = LINES.get(key);
        board.resetScores(line.getMessage());
        line.setMessage(message);
        Score newScore = obj.getScore(message);

        newScore.setScore(line.getScore());
    }

    public void display() {
        displayed = true;
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(board);
    }

}

class Line {

    private final int score;
    private String message;

    public Line(int score, String message) {
        this.score = score;
        this.message = message;
    }

    public int getScore() {
        return score;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}