package net.tylermurphy.hideAndSeek;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.tylermurphy.hideAndSeek.events.EventListener;
import net.tylermurphy.hideAndSeek.events.EventTick;

public class Main extends JavaPlugin implements Listener {
	
	public static Main plugin;
	public static File root;
	
	public void onEnable() {
		
		plugin = this;
		
		// Setup Initial Player Count
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
		    playerList.put(player.getName(), player);
		}
		
		// Init Configuration
		Store.loadConfig();
		
		// Register Commands
		CommandHandler.registerCommands();
		
		// Get Data Folder
		root = this.getServer().getWorldContainer();
        
		// Start Tick Timer
		Bukkit.getServer().getScheduler().runTaskTimer(this, new Runnable(){
	        public void run(){
	            try{
	            	EventTick.onTick();
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	        }
	    },0,1);
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return CommandHandler.handleCommand(sender, cmd, label, args);
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return CommandTabCompleter.handleTabComplete(sender, command, label, args);
	}
	
}
