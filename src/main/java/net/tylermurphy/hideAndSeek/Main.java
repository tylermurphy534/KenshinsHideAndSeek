package net.tylermurphy.hideAndSeek;

import static net.tylermurphy.hideAndSeek.configuration.Config.spawnWorld;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.tylermurphy.hideAndSeek.bukkit.CommandHandler;
import net.tylermurphy.hideAndSeek.bukkit.EventListener;
import net.tylermurphy.hideAndSeek.bukkit.TabCompleter;
import net.tylermurphy.hideAndSeek.bukkit.Tick;
import net.tylermurphy.hideAndSeek.configuration.Config;
import net.tylermurphy.hideAndSeek.configuration.Localization;
import net.tylermurphy.hideAndSeek.events.Glow;
import net.tylermurphy.hideAndSeek.events.Taunt;
import net.tylermurphy.hideAndSeek.events.Worldborder;
import net.tylermurphy.hideAndSeek.util.Board;
import net.tylermurphy.hideAndSeek.world.WorldLoader;

public class Main extends JavaPlugin implements Listener {
	
	public static Main plugin;
	public static File root, data;
	
	public Taunt taunt;
	public Glow glow;
	public Worldborder worldborder;
	
	public Board board;
	
	public WorldLoader worldLoader;
	
	public Map<String,Player> playerList = new HashMap<String,Player>();
	
	public String status = "Standby";
	
	public int timeLeft = 0, gameId = 0;;
	
	private BukkitTask onTickTask;
	
	public void onEnable() {
		
		plugin = this;
		
		// Setup Initial Player Count
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		
		// Get Data Folder
		root = this.getServer().getWorldContainer();
		data = this.getDataFolder();
		
		// Init Configuration
		Config.loadConfig();
		Localization.loadLocalization();
		
		// Create World Loader
		worldLoader = new WorldLoader(spawnWorld);
		
		// Register Commands
		CommandHandler.registerCommands();
		
		//Board
		board = new Board();
		board.reload();
        
		// Start Tick Timer
		onTickTask = Bukkit.getServer().getScheduler().runTaskTimer(this, new Runnable(){
	        public void run(){
	            try{
	            	Tick.onTick();
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	        }
	    },0,1);
		
	}
	
	public void onDisable() {
		onTickTask.cancel();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandHandler.handleCommand(sender, cmd, label, args);
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return TabCompleter.handleTabComplete(sender, command, label, args);
	}
	
}
