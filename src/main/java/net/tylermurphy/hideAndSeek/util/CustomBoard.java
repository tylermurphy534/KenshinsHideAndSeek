package net.tylermurphy.hideAndSeek.util;

import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

public class CustomBoard {

    private final Scoreboard board;
    private final Objective obj;
    private final Player player;
    private final Map<String,Line> LINES;
    private int blanks;

    public CustomBoard(Player player, String title){
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();
        this.LINES = new HashMap<String,Line>();
        this.player = player;
        this.obj = board.registerNewObjective(
                "Scoreboard", "dummy", ChatColor.translateAlternateColorCodes('&', title));
        this.blanks = 0;
    }

    public void updateTeams() {
        try{ board.registerNewTeam("Hider"); } catch (Exception e){}
        try{ board.registerNewTeam("Seeker"); } catch (Exception e){}
        Team hiderTeam = board.getTeam("Hider");
        for(String entry : hiderTeam.getEntries())
            hiderTeam.removeEntry(entry);
        for(Player player : Main.plugin.board.getHiders())
            hiderTeam.addEntry(player.getName());
        Team seekerTeam = board.getTeam("Seeker");
        for(String entry : seekerTeam.getEntries())
            seekerTeam.removeEntry(entry);
        for(Player player  : Main.plugin.board.getSeekers())
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

    public void addBlank(boolean value){
        if(!value) return;
        String temp = "";
        for(int i = 0; i <= blanks; i ++)
            temp += ChatColor.RESET;
        blanks++;
        addLine("blank"+blanks, temp);
    }

    private void updateLine(String key, String message){
        Line line = LINES.get(key);
        board.resetScores(line.getMessage());
        line.setMessage(message);
        Score newScore = obj.getScore(message);

        newScore.setScore(line.getScore());
    }

    public void display() {
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(board);
    }

}

class Line {

    private int score;
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
