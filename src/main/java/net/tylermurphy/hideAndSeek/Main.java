package net.tylermurphy.hideAndSeek;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.tylermurphy.hideAndSeek.manager.CommandManager;
import net.tylermurphy.hideAndSeek.manager.EventManager;
import net.tylermurphy.hideAndSeek.manager.TickManager;
import net.tylermurphy.hideAndSeek.util.Functions;

public class Main extends JavaPlugin implements Listener {
	
	public static Main plugin;
	
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
		
		// Register Commands
		CommandManager.registerCommands();
		
		// Start Tick Timer
		Bukkit.getServer().getScheduler().runTaskTimer(this, new Runnable(){
	        public void run(){
	            try{
	            	TickManager.onTick();
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	        }
	    },0,1);
		
	}
	
	public void onDisable() {
		saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandManager.handleCommand(sender, cmd, label, args);
	}
	
}
