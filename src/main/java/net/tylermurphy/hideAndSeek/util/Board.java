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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import static net.tylermurphy.hideAndSeek.Config.*;

public class Board {

	private Team HiderTeam, SeekerTeam, SpectatorTeam;
	private List<String> Hider, Seeker, Spectator;
	private Map<String, Player> playerList = new HashMap<String,Player>();
	
	private boolean setup = false;
	
	public boolean isReady() {
		return setup;
	}
	
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
		return playerList.size();
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
		HiderTeam.addEntry(player.getName());
		if(!playerList.containsKey(player.getName()))
				playerList.put(player.getName(), player);
	}
	
	public void addSeeker(Player player) {
		Hider.remove(player.getName());
		Seeker.add(player.getName());
		Spectator.remove(player.getName());
		SeekerTeam.addEntry(player.getName());
		if(!playerList.containsKey(player.getName()))
				playerList.put(player.getName(), player);
	}
	
	public void addSpectator(Player player) {
		Hider.remove(player.getName());
		Seeker.remove(player.getName());
		Spectator.add(player.getName());
		SpectatorTeam.addEntry(player.getName());
		if(!playerList.containsKey(player.getName()))
				playerList.put(player.getName(), player);
	}
	
	public void remove(Player player) {
		Hider.remove(player.getName());
		Seeker.remove(player.getName());
		Spectator.remove(player.getName());
		HiderTeam.removeEntry(player.getName());
		SeekerTeam.removeEntry(player.getName());
		SpectatorTeam.removeEntry(player.getName());
		playerList.remove(player.getName());
	}
	
	public boolean onSameTeam(Player player1, Player player2) {
		if(Hider.contains(player1.getName()) && Hider.contains(player2.getName())) return true;
		else if(Seeker.contains(player1.getName()) && Seeker.contains(player2.getName())) return true;
		else if(Spectator.contains(player1.getName()) && Spectator.contains(player2.getName())) return true;
		else return false;
	}
	
	public void init() {
		Hider = new ArrayList<String>();
		Seeker = new ArrayList<String>();
		Spectator = new ArrayList<String>();
		reload();
	}
	
	public void reload() {
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		if(manager == null) return;
		Scoreboard board = manager.getMainScoreboard();
		
		try { board.registerNewTeam("Seeker"); } catch(Exception e) {}
		SeekerTeam = board.getTeam("Seeker");
		SeekerTeam.setColor(ChatColor.RED);
		if(nametagsVisible) SeekerTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
		else SeekerTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		
		try { board.registerNewTeam("Hider"); } catch(Exception e) {}
		HiderTeam = board.getTeam("Hider");
		HiderTeam.setColor(ChatColor.GOLD);
		if(nametagsVisible) HiderTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
		else HiderTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		
		try { board.registerNewTeam("Spectator"); } catch(Exception e) {}
		SpectatorTeam = board.getTeam("Spectator");
		SpectatorTeam.setColor(ChatColor.GRAY);
		SpectatorTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		
		setup = true;
	}
	
	public void reset() {
		Hider.clear();
		Seeker.clear();
		Spectator.clear();
		for(String entry : HiderTeam.getEntries())
			HiderTeam.removeEntry(entry);
		for(String entry : SeekerTeam.getEntries())
			SeekerTeam.removeEntry(entry);
		for(String entry : SpectatorTeam.getEntries())
			SpectatorTeam.removeEntry(entry);
	}
	
}
