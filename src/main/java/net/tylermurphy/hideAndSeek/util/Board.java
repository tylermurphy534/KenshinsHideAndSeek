package net.tylermurphy.hideAndSeek.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import net.tylermurphy.hideAndSeek.Main;

import static net.tylermurphy.hideAndSeek.Config.*;

public class Board {

	private List<String> Hider, Seeker, Spectator;
	private Map<String, Player> playerList = new HashMap<String,Player>();
	
	public boolean isPlayer(Player player) {
		return playerList.containsKey(player.getName());
	}
	
	public boolean isPlayer(CommandSender sender) {
		return playerList.containsKey(sender.getName());
	}
	
	public boolean isHider(Player player) {
		return Hider.contains(player.getName());
	}
	
	public boolean isSeeker(Player player) {
		return Seeker.contains(player.getName());
	}
	
	public boolean isSpectator(Player player) {
		return Spectator.contains(player.getName());
	}
	
	public int sizeHider() {
		return Hider.size();
	}
	
	public int sizeSeeker() {
		return Seeker.size();
	}
	
	public int sizeSpectator() {
		return Spectator.size();
	}
	
	public int size() {
		return playerList.values().size();
	}
	
	public List<Player> getHiders(){
		return Hider.stream().map(playerName -> playerList.get(playerName)).collect(Collectors.toList());
	}
	
	public List<Player> getSeekers(){
		return Seeker.stream().map(playerName -> playerList.get(playerName)).collect(Collectors.toList());
	}
	
	public List<Player> getSpectators(){
		return Spectator.stream().map(playerName -> playerList.get(playerName)).collect(Collectors.toList());
	}
	
	public List<Player> getPlayers(){
		return new ArrayList<Player>(playerList.values());
	}
	
	public Player getPlayer(String name) {
		return playerList.get(name);
	}
	
	public void addHider(Player player) {
		Hider.add(player.getName());
		Seeker.remove(player.getName());
		Spectator.remove(player.getName());
		playerList.put(player.getName(), player);
	}
	
	public void addSeeker(Player player) {
		Hider.remove(player.getName());
		Seeker.add(player.getName());
		Spectator.remove(player.getName());
		playerList.put(player.getName(), player);
	}
	
	public void addSpectator(Player player) {
		Hider.remove(player.getName());
		Seeker.remove(player.getName());
		Spectator.add(player.getName());
		playerList.put(player.getName(), player);
	}
	
	public void remove(Player player) {
		Hider.remove(player.getName());
		Seeker.remove(player.getName());
		Spectator.remove(player.getName());
		playerList.remove(player.getName());
	}
	
	public boolean onSameTeam(Player player1, Player player2) {
		if(Hider.contains(player1.getName()) && Hider.contains(player2.getName())) return true;
		else if(Seeker.contains(player1.getName()) && Seeker.contains(player2.getName())) return true;
		else if(Spectator.contains(player1.getName()) && Spectator.contains(player2.getName())) return true;
		else return false;
	}
	
	public void reload() {
		Hider = new ArrayList<String>();
		Seeker = new ArrayList<String>();
		Spectator = new ArrayList<String>();
	}
	
	public void reset() {
		Hider.clear();
		Seeker.clear();
		Spectator.clear();
	}
	
	private void createTeamsForBoard(Scoreboard board) {
		Team hiderTeam = board.registerNewTeam("Hider");
		for(String name : Hider)
			hiderTeam.addEntry(name);
		Team seekerTeam = board.registerNewTeam("Seeker");
		for(String name : Seeker)
			seekerTeam.addEntry(name);
		if(nametagsVisible) {
			hiderTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
			seekerTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
		} else {
			hiderTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
			seekerTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		}
	}
	
	private void createLobbyBoard(Player player) {
		
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = board.registerNewObjective("LobbyScoreboard", "dummy",
				ChatColor.translateAlternateColorCodes('&', "&l&eHIDE AND SEEK"));
		createTeamsForBoard(board);
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score waiting = obj.getScore("Waiting to start...");
		waiting.setScore(6);
		Score blank1 = obj.getScore(ChatColor.RESET.toString());
		blank1.setScore(5); 
		Score players = obj.getScore("Players: "+playerList.values().size());
		players.setScore(4);
		Score blank2 = obj.getScore(ChatColor.RESET.toString() + ChatColor.RESET.toString());
		blank2.setScore(3);
		Score seeker = obj.getScore(ChatColor.BOLD + "" + ChatColor.RED + "SEEKER%" + ChatColor.WHITE + getSeekerPercent());
		seeker.setScore(2);
		Score hider = obj.getScore(ChatColor.BOLD + "" + ChatColor.GOLD + "HIDER%" + ChatColor.WHITE + getHiderPercent());
		hider.setScore(1);
		player.setScoreboard(board);
	}
	
	private void createGameBoard(Player player) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = board.registerNewObjective("GameScoreboard", "dummy",
				ChatColor.translateAlternateColorCodes('&', "&l&eHIDE AND SEEK"));
		createTeamsForBoard(board);
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score team = obj.getScore("Team: " + getTeam(player));
		team.setScore(6);
		Score blank1 = obj.getScore(ChatColor.RESET.toString());
		blank1.setScore(5);
		if(gameLength > 0) {
			Score waiting = obj.getScore(ChatColor.GREEN + "Time Left: " + ChatColor.WHITE + Main.plugin.timeLeft/60 + "m" + Main.plugin.timeLeft%60 + "s");
			waiting.setScore(4);
			Score blank2 = obj.getScore(ChatColor.RESET.toString() + ChatColor.RESET.toString());
			blank2.setScore(3);
		}
		Score seeker = obj.getScore(ChatColor.BOLD + "" + ChatColor.RED + "SEEKERS:" + ChatColor.WHITE + " " + Seeker.size());
		seeker.setScore(2);
		Score hider = obj.getScore(ChatColor.BOLD + "" + ChatColor.GOLD + "HIDERS:" + ChatColor.WHITE + " " + Hider.size());
		hider.setScore(1);
		player.setScoreboard(board);
	}
	
	public void removeBoard(Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
	
	public void reloadLobbyBoards() {
		for(Player player : playerList.values())
			createLobbyBoard(player);
	}
	
	public void reloadGameBoards() {
		for(Player player : playerList.values())
			createGameBoard(player);
	}
	
	private String getSeekerPercent() {
		if(playerList.values().size() < 2)
			return " --";
		else
			return " "+(int)(100*(1.0/playerList.size()));
	}
	
	private String getHiderPercent() {
		if(playerList.size() < 2)
			return " --";
		else
			return " "+(int)(100-100*(1.0/playerList.size()));
	}
	
	private String getTeam(Player player) {
		if(isHider(player)) return ChatColor.GOLD + "HIDER";
		else if(isSeeker(player)) return ChatColor.RED + "SEEKER";
		else if(isSpectator(player)) return ChatColor.GRAY + "SPECTATOR";
		else return ChatColor.WHITE + "UNKNOWN";
	}
	
}
