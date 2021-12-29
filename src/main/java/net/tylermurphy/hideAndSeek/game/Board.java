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

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class Board {

    private static final List<String> Hider = new ArrayList<>(), Seeker = new ArrayList<>(), Spectator = new ArrayList<>();
    private static final Map<String, Player> playerList = new HashMap<>();
    private static final Map<String, CustomBoard> customBoards = new HashMap<>();

    public static boolean isPlayer(Player player) {
        return playerList.containsKey(player.getName());
    }

    public static boolean isPlayer(String name){
        return playerList.containsKey(name);
    }

    public static boolean isPlayer(CommandSender sender) {
        return playerList.containsKey(sender.getName());
    }

    public static boolean isHider(Player player) {
        return Hider.contains(player.getName());
    }

    public static boolean isSeeker(Player player) {
        return Seeker.contains(player.getName());
    }

    public static boolean isSpectator(Player player) {
        return Spectator.contains(player.getName());
    }

    public static int sizeHider() {
        return Hider.size();
    }

    public static int sizeSeeker() {
        return Seeker.size();
    }

    public static int size() {
        return playerList.values().size();
    }

    public static List<Player> getHiders(){
        return Hider.stream().map(playerList::get).collect(Collectors.toList());
    }

    public static List<Player> getSeekers(){
        return Seeker.stream().map(playerList::get).collect(Collectors.toList());
    }

    public static Player getFirstSeeker(){
        return playerList.get(Seeker.get(0));
    }

    public static List<Player> getSpectators(){
        return Spectator.stream().map(playerList::get).collect(Collectors.toList());
    }

    public static List<Player> getPlayers(){
        return new ArrayList<>(playerList.values());
    }

    public static Player getPlayer(String name) {
        return playerList.get(name);
    }

    public static void addHider(Player player) {
        Hider.add(player.getName());
        Seeker.remove(player.getName());
        Spectator.remove(player.getName());
        playerList.put(player.getName(), player);
    }

    public static void addSeeker(Player player) {
        Hider.remove(player.getName());
        Seeker.add(player.getName());
        Spectator.remove(player.getName());
        playerList.put(player.getName(), player);
    }

    public static void addSpectator(Player player) {
        Hider.remove(player.getName());
        Seeker.remove(player.getName());
        Spectator.add(player.getName());
        playerList.put(player.getName(), player);
    }

    public static void remove(Player player) {
        Hider.remove(player.getName());
        Seeker.remove(player.getName());
        Spectator.remove(player.getName());
        playerList.remove(player.getName());
    }

    public static boolean onSameTeam(Player player1, Player player2) {
        if(Hider.contains(player1.getName()) && Hider.contains(player2.getName())) return true;
        else if(Seeker.contains(player1.getName()) && Seeker.contains(player2.getName())) return true;
        else return Spectator.contains(player1.getName()) && Spectator.contains(player2.getName());
    }

    public static void reload() {
        Hider.clear();
        Seeker.clear();
        Spectator.clear();
    }

    public static void createLobbyBoard(Player player) {
        createLobbyBoard(player, true);
    }

    private static void createLobbyBoard(Player player, boolean recreate) {
        CustomBoard board = customBoards.get(player.getName());
        if(recreate) {
            board = new CustomBoard(player, "&l&eHIDE AND SEEK");
            board.updateTeams();
        }
        board.setLine("hiders", ChatColor.BOLD + "" + ChatColor.YELLOW + "HIDER %" + ChatColor.WHITE + getHiderPercent());
        board.setLine("seekers", ChatColor.BOLD + "" + ChatColor.RED + "SEEKER %" + ChatColor.WHITE + getSeekerPercent());
        board.addBlank();
        board.setLine("players", "Players: " + playerList.values().size());
        board.addBlank();
        if(lobbyCountdownEnabled){
            if(Game.countdownTime == -1){
                board.setLine("waiting", "Waiting for players...");
            } else {
                board.setLine("waiting", "Starting in: "+ChatColor.GREEN + Game.countdownTime+"s");
            }
        } else {
            board.setLine("waiting", "Waiting for gamemaster...");
        }
        board.display();
        customBoards.put(player.getName(), board);
    }

    public static void createGameBoard(Player player){
        createGameBoard(player, true);
    }

    private static void createGameBoard(Player player, boolean recreate){
        CustomBoard board = customBoards.get(player.getName());
        if(recreate) {
            board = new CustomBoard(player, "&l&eHIDE AND SEEK");
            board.updateTeams();
        }
        board.setLine("hiders", ChatColor.BOLD + "" + ChatColor.YELLOW + "HIDERS:" + ChatColor.WHITE + " " + Hider.size());
        board.setLine("seekers", ChatColor.BOLD + "" + ChatColor.RED + "SEEKERS:" + ChatColor.WHITE + " " + Seeker.size());
        board.addBlank();
        if(glowEnabled){
            if(Game.glow == null || Game.status == Status.STARTING || !Game.glow.isRunning())
                board.setLine("glow", "Glow: " + ChatColor.RED + "Inactive");
            else
                board.setLine("glow", "Glow: " + ChatColor.GREEN + "Active");
        }
        if(tauntEnabled && tauntCountdown){
            if(Game.taunt == null || Game.status == Status.STARTING)
                board.setLine("taunt", "Taunt: " + ChatColor.YELLOW + "0m0s");
            else if(!tauntLast && Hider.size() == 1){
                board.setLine("taunt", "Taunt: " + ChatColor.YELLOW + "Expired");
            } else if(!Game.taunt.isRunning())
                board.setLine("taunt", "Taunt: " + ChatColor.YELLOW + Game.taunt.getDelay()/60 + "m" + Game.taunt.getDelay()%60 + "s");
            else
                board.setLine("taunt", "Taunt: " + ChatColor.YELLOW + "Active");
        }
        if(worldborderEnabled){
            if(Game.worldBorder == null || Game.status == Status.STARTING){
                board.setLine("board", "WorldBorder: " + ChatColor.YELLOW + "0m0s");
            } else if(!Game.worldBorder.isRunning()) {
                board.setLine("board", "WorldBorder: " + ChatColor.YELLOW + Game.worldBorder.getDelay()/60 + "m" + Game.worldBorder.getDelay()%60 + "s");
            } else {
                board.setLine("board", "WorldBorder: " + ChatColor.YELLOW + "Decreasing");
            }
        }
        if(glowEnabled || (tauntEnabled && tauntCountdown) || worldborderEnabled)
            board.addBlank();
        board.setLine("time", "Time Left: " + ChatColor.GREEN + Game.timeLeft/60 + "m" + Game.timeLeft%60 + "s");
        board.addBlank();
        board.setLine("team", "Team: " + getTeam(player));
        board.display();
        customBoards.put(player.getName(), board);
    }

    public static void removeBoard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        player.setScoreboard(manager.getMainScoreboard());
        customBoards.remove(player.getName());
    }

    public static void reloadLobbyBoards() {
        for(Player player : playerList.values())
            createLobbyBoard(player, false);
    }

    public static void reloadGameBoards() {
        for(Player player : playerList.values())
            createGameBoard(player, false);
    }

    public static void reloadBoardTeams() {
        for(CustomBoard board : customBoards.values())
            board.updateTeams();
    }

    private static String getSeekerPercent() {
        if(playerList.values().size() < 2)
            return " --";
        else
            return " "+(int)(100*(1.0/playerList.size()));
    }

    private static String getHiderPercent() {
        if(playerList.size() < 2)
            return " --";
        else
            return " "+(int)(100-100*(1.0/playerList.size()));
    }

    private static String getTeam(Player player) {
        if(isHider(player)) return ChatColor.GOLD + "HIDER";
        else if(isSeeker(player)) return ChatColor.RED + "SEEKER";
        else if(isSpectator(player)) return ChatColor.GRAY + "SPECTATOR";
        else return ChatColor.WHITE + "UNKNOWN";
    }

}

class CustomBoard {

    private final Scoreboard board;
    private final Objective obj;
    private final Player player;
    private final Map<String,Line> LINES;
    private int blanks;
    private boolean displayed;

    public CustomBoard(Player player, String title){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        this.board = manager.getNewScoreboard();
        this.LINES = new HashMap<>();
        this.player = player;
        this.obj = board.registerNewObjective(
                "Scoreboard", "dummy", ChatColor.translateAlternateColorCodes('&', title));
        this.blanks = 0;
        this.displayed = false;
        this.updateTeams();
    }

    public void updateTeams() {
        try{ board.registerNewTeam("Hider"); } catch (Exception ignored){}
        try{ board.registerNewTeam("Seeker"); } catch (Exception ignored){}
        Team hiderTeam = board.getTeam("Hider");
        assert hiderTeam != null;
        for(String entry : hiderTeam.getEntries())
            hiderTeam.removeEntry(entry);
        for(Player player : Board.getHiders())
            hiderTeam.addEntry(player.getName());
        Team seekerTeam = board.getTeam("Seeker");
        assert seekerTeam != null;
        for(String entry : seekerTeam.getEntries())
            seekerTeam.removeEntry(entry);
        for(Player player  : Board.getSeekers())
            seekerTeam.addEntry(player.getName());
        if(nametagsVisible) {
            hiderTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
            seekerTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        } else {
            hiderTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            seekerTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }
        hiderTeam.setColor(ChatColor.GOLD);
        seekerTeam.setColor(ChatColor.RED);
    }

    public void setLine(String key, String message){
        Line line = LINES.get(key);
        if(line == null)
            addLine(key, message);
        else
            updateLine(key, message);
    }

    private void addLine(String key, String message){
        Score score = obj.getScore(message);
        score.setScore(LINES.values().size()+1);
        Line line = new Line(LINES.values().size()+1, message);
        LINES.put(key, line);
    }

    public void addBlank(){
        if(displayed) return;
        StringBuilder temp = new StringBuilder();
        for(int i = 0; i <= blanks; i ++)
            temp.append(ChatColor.RESET);
        blanks++;
        addLine("blank"+blanks, temp.toString());
    }

    private void updateLine(String key, String message){
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

    public Line(int score, String message){
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