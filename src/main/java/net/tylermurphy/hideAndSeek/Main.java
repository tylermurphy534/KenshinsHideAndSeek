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

import net.tylermurphy.hideAndSeek.configuration.Config;
import net.tylermurphy.hideAndSeek.configuration.Items;
import net.tylermurphy.hideAndSeek.configuration.Localization;
import net.tylermurphy.hideAndSeek.database.Database;
import net.tylermurphy.hideAndSeek.game.Board;
import net.tylermurphy.hideAndSeek.game.CommandHandler;
import net.tylermurphy.hideAndSeek.game.EventListener;
import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import net.tylermurphy.hideAndSeek.util.TabCompleter;
import net.tylermurphy.hideAndSeek.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

import static net.tylermurphy.hideAndSeek.configuration.Config.errorPrefix;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class Main extends JavaPlugin implements Listener {
	
	public static Main plugin;
	public static File root, data;
	private int onTickTask;

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
		},0,1).getTaskId();

		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	}
	
	public void onDisable() {
		Main.plugin.getServer().getScheduler().cancelTask(onTickTask);
		Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
		UUIDFetcher.cleanup();
		Board.cleanup();
	}
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		return CommandHandler.handleCommand(sender, args);
	}
	
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		return TabCompleter.handleTabComplete(sender, args);
	}

	/**
	 * Provides a vector for a player
	 * @param player the player to create the vector for
	 * @return the vector
	 */
	public @Nullable Vector vectorFor(Player player) {
		if (Game.status != Status.STANDBY) {
			player.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return null;
		}

		if (player.getLocation().getBlockX() == 0 || player.getLocation().getBlockZ() == 0 || player.getLocation().getBlockY() == 0){
			player.sendMessage(errorPrefix + message("NOT_AT_ZERO"));
			return null;
		}

		Location loc = player.getLocation();
		return new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
}