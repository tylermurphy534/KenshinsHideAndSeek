package net.tylermurphy.hideAndSeek;

import static net.tylermurphy.hideAndSeek.configuration.Config.spawnWorld;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tylermurphy.hideAndSeek.game.Status;
import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.tylermurphy.hideAndSeek.util.CommandHandler;
import net.tylermurphy.hideAndSeek.game.EventListener;
import net.tylermurphy.hideAndSeek.util.TabCompleter;
import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.configuration.Config;
import net.tylermurphy.hideAndSeek.configuration.Localization;
import net.tylermurphy.hideAndSeek.configuration.Items;
import net.tylermurphy.hideAndSeek.util.Board;
import net.tylermurphy.hideAndSeek.world.WorldLoader;

public class Main extends JavaPlugin implements Listener {
	
	public static Main plugin;
	public static File root, data;
	
	public Game game;
	public Board board;
	public WorldLoader worldLoader;
	public Status status = Status.STANDBY;
	private BukkitTask onTickTask;

	public void onEnable() {
		
		plugin = this;
		
		// Setup Event Listener
		getServer().getPluginManager().registerEvents(new EventListener(), this);
		
		// Get Data Folder
		root = this.getServer().getWorldContainer();
		data = this.getDataFolder();
		
		// Init Configuration
		Config.loadConfig();
		Localization.loadLocalization();
		Items.loadItems();
		
		// Create World Loader
		worldLoader = new WorldLoader(spawnWorld);
		
		// Register Commands
		CommandHandler.registerCommands();
		
		//Board
		board = new Board();
		board.reload();
        
		// Start Tick Timer
		onTickTask = Bukkit.getServer().getScheduler().runTaskTimer(this, () -> {
			try{
				game = new Game();
				game.onTick();
			} catch (Exception e) {
				e.printStackTrace();
			}
		},0,1);
		
	}
	
	public void onDisable() {
		if(onTickTask != null)
			onTickTask.cancel();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd,String label, String[] args) {
		return CommandHandler.handleCommand(sender, cmd, label, args);
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return TabCompleter.handleTabComplete(sender, command, label, args);
	}
	
}
