/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation version 3.
 *
 * Kenshins Hide and Seek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.tylermurphy.hideAndSeek;

import java.io.File;
import java.util.List;

import net.tylermurphy.hideAndSeek.database.Database;
import net.tylermurphy.hideAndSeek.util.UUIDFetcher;
import org.bukkit.Bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.tylermurphy.hideAndSeek.game.CommandHandler;
import net.tylermurphy.hideAndSeek.game.EventListener;
import net.tylermurphy.hideAndSeek.util.TabCompleter;
import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.configuration.Config;
import net.tylermurphy.hideAndSeek.configuration.Localization;
import net.tylermurphy.hideAndSeek.configuration.Items;
import net.tylermurphy.hideAndSeek.game.Board;

public class Main extends JavaPlugin implements Listener {
	
	public static Main plugin;
	public static File root, data;
	private BukkitTask onTickTask;

	public void onEnable() {
		plugin = this;
		root = this.getServer().getWorldContainer();
		data = this.getDataFolder();
		getServer().getPluginManager().registerEvents(new EventListener(), this);

		Config.loadConfig();
		Localization.loadLocalization();
		Items.loadItems();

		CommandHandler.registerCommands();
		Board.reload();
		Database.init();
		UUIDFetcher.init();

		onTickTask = Bukkit.getServer().getScheduler().runTaskTimer(this, () -> {
			try{
				Game.onTick();
			} catch (Exception e) {
				e.printStackTrace();
			}
		},0,1);
	}
	
	public void onDisable() {
		if(onTickTask != null)
			onTickTask.cancel();
		UUIDFetcher.cleanup();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd,String label, String[] args) {
		return CommandHandler.handleCommand(sender, args);
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return TabCompleter.handleTabComplete(sender, args);
	}
	
}
