package net.tylermurphy.hideAndSeek.util;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.tylermurphy.hideAndSeek.game.Status;
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

public class Board {

	private List<String> Hider, Seeker, Spectator;
	private Map<String, Player> playerList = new HashMap<>();
	private Map<String, CustomBoard> customBoards = new HashMap<>();

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

	public Player getFirstSeeker(){
		return playerList.get(Seeker.get(0));
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
		Hider = new ArrayList<>();
		Seeker = new ArrayList<>();
		Spectator = new ArrayList<>();
	}

	public void createLobbyBoard(Player player) {
		createLobbyBoard(player, true);
	}

	private void createLobbyBoard(Player player, boolean recreate) {
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
			if(Main.plugin.game.countdownTime == -1){
				board.setLine("waiting", "Waiting for players...");
			} else {
				board.setLine("waiting", "Starting in: "+ChatColor.GREEN + Main.plugin.game.countdownTime+"s");
			}
		} else {
			board.setLine("waiting", "Waiting for gamemaster...");
		}
		board.display();
		customBoards.put(player.getName(), board);
	}

	public void createGameBoard(Player player){
		createGameBoard(player, true);
	}

	private void createGameBoard(Player player, boolean recreate){
		CustomBoard board = customBoards.get(player.getName());
		if(recreate) {
			board = new CustomBoard(player, "&l&eHIDE AND SEEK");
			board.updateTeams();
		}
		board.setLine("hiders", ChatColor.BOLD + "" + ChatColor.YELLOW + "HIDERS:" + ChatColor.WHITE + " " + Hider.size());
		board.setLine("seekers", ChatColor.BOLD + "" + ChatColor.RED + "SEEKERS:" + ChatColor.WHITE + " " + Seeker.size());
		board.addBlank();
		if(glowEnabled){
			if(Main.plugin.game.glow == null || Main.plugin.status == Status.STARTING || !Main.plugin.game.glow.isRunning())
				board.setLine("glow", "Glow: " + ChatColor.RED + "Inactive");
			else
				board.setLine("glow", "Glow: " + ChatColor.GREEN + "Active");
		}
		if(tauntEnabled && tauntCountdown){
			if(Main.plugin.game.taunt == null || Main.plugin.status == Status.STARTING)
				board.setLine("taunt", "Taunt: " + ChatColor.YELLOW + "0m0s");
			else if(!tauntLast && Hider.size() == 1){
				board.setLine("taunt", "Taunt: " + ChatColor.YELLOW + "Expired");
			} else if(!Main.plugin.game.taunt.isRunning())
				board.setLine("taunt", "Taunt: " + ChatColor.YELLOW + Main.plugin.game.taunt.getDelay()/60 + "m" + Main.plugin.game.taunt.getDelay()%60 + "s");
			else
				board.setLine("taunt", "Taunt: " + ChatColor.YELLOW + "Active");
		}
		if(worldborderEnabled){
			if(Main.plugin.game.worldborder == null || Main.plugin.status == Status.STARTING){
				board.setLine("board", "WorldBorder: " + ChatColor.YELLOW + "0m0s");
			} else if(!Main.plugin.game.worldborder.isRunning()) {
				board.setLine("board", "WorldBorder: " + ChatColor.YELLOW + Main.plugin.game.worldborder.getDelay()/60 + "m" + Main.plugin.game.worldborder.getDelay()%60 + "s");
			} else {
				board.setLine("board", "WorldBorder: " + ChatColor.YELLOW + "Decreasing");
			}
		}
		if(glowEnabled || (tauntEnabled && tauntCountdown) || worldborderEnabled)
			board.addBlank();
		board.setLine("time", "Time Left: " + ChatColor.GREEN + Main.plugin.game.timeLeft/60 + "m" + Main.plugin.game.timeLeft%60 + "s");
		board.addBlank();
		board.setLine("team", "Team: " + getTeam(player));
		board.display();
		customBoards.put(player.getName(), board);
	}
	
	public void removeBoard(Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		customBoards.remove(player.getName());
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
