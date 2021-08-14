package net.tylermurphy.hideAndSeek;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.manager.BoardManager;
import net.tylermurphy.hideAndSeek.manager.CommandManager;
import net.tylermurphy.hideAndSeek.manager.EventManager;
import net.tylermurphy.hideAndSeek.manager.TickManager;

public class Main extends JavaPlugin implements Listener {
	
	public static Main plugin;
	private int tickTaskId;
	
	public void onEnable() {
		
		plugin = this;
		
		// Setup Initial Player Count
		getServer().getPluginManager().registerEvents(new EventManager(), this);
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
		    playerList.put(player.getName(), player);
		}
		
		// Init Configuration
		Vector spawnPositionVector = getConfig().getVector("spawnPosition");
		if(spawnPositionVector != null) {
			spawnPosition = spawnPositionVector;
			status = "Standby";
		}
		Vector worldborderPositionVector = getConfig().getVector("borderPosition");
		if(worldborderPositionVector != null) {
			worldborderPosition = worldborderPositionVector;
			worldborderSize = getConfig().getInt("borderSize");
			worldborderDelay = getConfig().getInt("borderDelay");
		}
		worldborderEnabled = getConfig().getBoolean("borderEnabled");
		
		// Init Gamerules
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule sendCommandFeedback false");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule doImmediateRespawn true");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule logAdminCommands false");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule naturalRegeneration false");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule keepInventory false");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule showDeathMessages false");
		
		// Register Commands
		CommandManager.registerCommands();
		
		// Init Scoreboard
		if(Bukkit.getScoreboardManager() != null) {
			BoardManager.loadScoreboard();
		}
		
		// Start Tick Timer
		tickTaskId = Bukkit.getServer().getScheduler().runTaskTimer(this, new Runnable(){
	        public void run(){
	            TickManager.onTick();
	        }
	    },0,1).getTaskId();
		
	}
	
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTask(tickTaskId);
		saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandManager.handleCommand(sender, cmd, label, args);
	}
	
}
