package net.tylermurphy.hideAndSeek.manager;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class BoardManager {

	public static void loadScoreboard() {
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard mainBoard = manager.getMainScoreboard();
		
		try { mainBoard.registerNewTeam("Seeker");} catch(Exception e) {}
		Seeker = mainBoard.getTeam("Seeker");
		Seeker.setColor(ChatColor.RED);
		Seeker.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		Seeker.setAllowFriendlyFire(false);
		
		try { mainBoard.registerNewTeam("Hider");} catch(Exception e) {}
		Hider = mainBoard.getTeam("Hider");
		Hider.setColor(ChatColor.GOLD);
		Hider.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		Hider.setAllowFriendlyFire(false);
		
		try { mainBoard.registerNewTeam("Spectator");} catch(Exception e) {}
		Spectator = mainBoard.getTeam("Spectator");
		Spectator.setColor(ChatColor.GRAY);
		Spectator.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		Spectator.setAllowFriendlyFire(false);
		
		board = mainBoard;
	}
	
}
