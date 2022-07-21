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

package net.tylermurphy.hideAndSeek.configuration;

import com.cryptomorin.xseries.XItemStack;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.game.util.CountdownDisplay;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Config {

	private static ConfigManager config;

	public static String 
		messagePrefix,
		errorPrefix,
		tauntPrefix,
		worldBorderPrefix,
		abortPrefix,
		gameOverPrefix,
		warningPrefix,
		spawnWorld,
		exitWorld,
		lobbyWorld,
		locale,
		leaveServer,
		placeholderError,
		placeholderNoData,
		databaseType,
		databaseHost,
		databasePort,
		databaseUser,
		databasePass,
		databaseName;
	
	public static Vector
		spawnPosition,
		lobbyPosition,
		exitPosition,
		worldBorderPosition;
	
	public static boolean
		nameTagsVisible,
		permissionsRequired,
		announceMessagesToNonPlayers,
		worldBorderEnabled,
		tauntEnabled,
		tauntCountdown,
		tauntLast,
		alwaysGlow,
		glowEnabled,
		glowStackable,
		pvpEnabled,
		autoJoin,
		teleportToExit,
		lobbyCountdownEnabled,
		seekerPing,
		bungeeLeave,
		lobbyItemStartAdmin,
		leaveOnEnd,
		mapSaveEnabled,
		allowNaturalCauses,
		saveInventory;
	
	public static int 
		minPlayers,
		worldBorderSize,
		worldBorderDelay,
		currentWorldborderSize,
		worldBorderChange,
		gameLength,
		saveMinX,
		saveMinZ,
		saveMaxX,
		saveMaxZ,
		tauntDelay,
		glowLength,
		countdown,
		changeCountdown,
		lobbyMin,
		lobbyMax,
		seekerPingLevel1,
		seekerPingLevel2,
		seekerPingLevel3,
		lobbyItemLeavePosition,
		lobbyItemStartPosition,
		flightToggleItemPosition,
		teleportItemPosition;

	public static float
		seekerPingLeadingVolume,
		seekerPingVolume,
		seekerPingPitch;

	public static List<String>
		blockedCommands,
		blockedInteracts;

	public static String
		LOBBY_TITLE,
		GAME_TITLE,
		COUNTDOWN_WAITING,
		COUNTDOWN_COUNTING,
		COUNTDOWN_ADMINSTART,
		TAUNT_COUNTING,
		TAUNT_ACTIVE,
		TAUNT_EXPIRED,
		GLOW_ACTIVE,
		GLOW_INACTIVE,
		BORDER_COUNTING,
		BORDER_DECREASING;

	public static List<String>
		LOBBY_CONTENTS,
		GAME_CONTENTS;

	public static ItemStack
		lobbyLeaveItem,
		lobbyStartItem,
		glowPowerupItem,
		flightToggleItem,
		teleportItem;

	public static XSound
		ringingSound,
		heartbeatSound;

	public static CountdownDisplay
		countdownDisplay;
	
	public static void loadConfig() {

		config = ConfigManager.create("config.yml");
		config.saveConfig();
		ConfigManager leaderboard = ConfigManager.create("leaderboard.yml");

		//Spawn
		spawnPosition = new Vector(
				config.getDouble("spawns.game.x"),
				Math.max(Main.getInstance().supports(18) ? -64 : 0, Math.min(255, config.getDouble("spawns.game.y"))),
				config.getDouble("spawns.game.z")
		);
		spawnWorld = config.getString("spawns.game.world");

		///Lobby
		lobbyPosition = new Vector(
				config.getDouble("spawns.lobby.x"),
				Math.max(Main.getInstance().supports(18) ? -64 : 0, Math.min(255, config.getDouble("spawns.lobby.y"))),
				config.getDouble("spawns.lobby.z")
		);
		lobbyWorld = config.getString("spawns.lobby.world");

		announceMessagesToNonPlayers = config.getBoolean("announceMessagesToNonPlayers");

		exitPosition = new Vector(
				config.getDouble("spawns.exit.x"),
				Math.max(Main.getInstance().supports(18) ? -64 : 0, Math.min(255, config.getDouble("spawns.exit.y"))),
				config.getDouble("spawns.exit.z")
		);
		exitWorld = config.getString("spawns.exit.world");

		//World border
		worldBorderPosition = new Vector(
				config.getInt("worldBorder.x"),
				0,
				config.getInt("worldBorder.z")
		);
		worldBorderSize = Math.max(100, config.getInt("worldBorder.size"));
		worldBorderDelay = Math.max(1, config.getInt("worldBorder.delay"));
		worldBorderEnabled = config.getBoolean("worldBorder.enabled");
		worldBorderChange = config.getInt("worldBorder.moveAmount");

		//Prefix
		char SYMBOLE = '\u00A7';
		String SYMBOLE_STRING = String.valueOf(SYMBOLE);

		messagePrefix = config.getString("prefix.default").replace("&", SYMBOLE_STRING);
		errorPrefix = config.getString("prefix.error").replace("&", SYMBOLE_STRING);
		tauntPrefix = config.getString("prefix.taunt").replace("&", SYMBOLE_STRING);
		worldBorderPrefix = config.getString("prefix.border").replace("&", SYMBOLE_STRING);
		abortPrefix = config.getString("prefix.abort").replace("&", SYMBOLE_STRING);
		gameOverPrefix = config.getString("prefix.gameover").replace("&", SYMBOLE_STRING);
		warningPrefix = config.getString("prefix.warning").replace("&", SYMBOLE_STRING);

		//Map Bounds
		saveMinX = config.getInt("bounds.min.x");
		saveMinZ = config.getInt("bounds.min.z");
		saveMaxX = config.getInt("bounds.max.x");
		saveMaxZ = config.getInt("bounds.max.z");
		mapSaveEnabled = config.getBoolean("mapSaveEnabled");

		//Taunt
		tauntEnabled = config.getBoolean("taunt.enabled");
		tauntCountdown = config.getBoolean("taunt.showCountdown");
		tauntDelay = Math.max(60, config.getInt("taunt.delay"));
		tauntLast = config.getBoolean("taunt.whenLastPerson");

		//Glow
		alwaysGlow = config.getBoolean("alwaysGlow") && Main.getInstance().supports(9);
		glowLength = Math.max(1, config.getInt("glow.time"));
		glowStackable = config.getBoolean("glow.stackable");
		glowEnabled = config.getBoolean("glow.enabled") && Main.getInstance().supports(9) && !alwaysGlow;
		if (glowEnabled) {
			glowPowerupItem = createItemStack("glow");
		}

		//Lobby
		minPlayers = Math.max(2, config.getInt("minPlayers"));
		countdown = Math.max(10, config.getInt("lobby.countdown"));
		changeCountdown = Math.max(minPlayers, config.getInt("lobby.changeCountdown"));
		lobbyMin = Math.max(minPlayers, config.getInt("lobby.min"));
		lobbyMax = config.getInt("lobby.max");
		lobbyCountdownEnabled = config.getBoolean("lobby.enabled");

		//SeekerPing
		seekerPing = config.getBoolean("seekerPing.enabled");
		seekerPingLevel1 = config.getInt("seekerPing.distances.level1");
		seekerPingLevel2 = config.getInt("seekerPing.distances.level2");
		seekerPingLevel3 = config.getInt("seekerPing.distances.level3");
		seekerPingLeadingVolume = config.getFloat("seekerPing.sounds.leadingVolume");
		seekerPingVolume = config.getFloat("seekerPing.sounds.volume");
		seekerPingPitch = config.getFloat("seekerPing.sounds.pitch");
		Optional<XSound> heartbeatOptional = XSound.matchXSound(config.getString("seekerPing.sounds.heartbeatNoise"));
		heartbeatSound = heartbeatOptional.orElse(XSound.BLOCK_NOTE_BLOCK_BASEDRUM);
		Optional<XSound> ringingOptional = XSound.matchXSound(config.getString("seekerPing.sounds.ringingNoise"));
		ringingSound = ringingOptional.orElse(XSound.BLOCK_NOTE_BLOCK_PLING);

		//Other
		nameTagsVisible = config.getBoolean("nametagsVisible");
		permissionsRequired = config.getBoolean("permissionsRequired");
		gameLength = config.getInt("gameLength");
		pvpEnabled = config.getBoolean("pvp");
		allowNaturalCauses = config.getBoolean("allowNaturalCauses");
		autoJoin = config.getBoolean("autoJoin");
		teleportToExit = config.getBoolean("teleportToExit");
		locale = config.getString("locale", "local");
		blockedCommands = config.getStringList("blockedCommands");
		leaveOnEnd = config.getBoolean("leaveOnEnd");
		placeholderError = config.getString("placeholder.incorrect");
		placeholderNoData = config.getString("placeholder.noData");
		saveInventory = config.getBoolean("saveInventory");
		try {
			countdownDisplay = CountdownDisplay.valueOf(config.getString("hideCountdownDisplay"));
		} catch (IllegalArgumentException e) {
			countdownDisplay = CountdownDisplay.CHAT;
			Main.getInstance().getLogger().warning("hideCountdownDisplay: "+config.getString("hideCountdownDisplay")+" is not a valid configuration option!");
		}
		blockedInteracts = new ArrayList<>();
		List<String> tempInteracts = config.getStringList("blockedInteracts");
		for(String id : tempInteracts) {
			Optional<XMaterial> optional_mat = XMaterial.matchXMaterial(id);
			if (optional_mat.isPresent()) {
				Material mat = optional_mat.get().parseMaterial();
				if (mat != null) {
					blockedInteracts.add(mat.name());
				}
			}
		}
		bungeeLeave = config.getString("leaveType") == null || config.getString("leaveType").equalsIgnoreCase("proxy");
		leaveServer = config.getString("leaveServer");

		//Leaderboard
		LOBBY_TITLE = leaderboard.getString("lobby.title");
		GAME_TITLE = leaderboard.getString("game.title");
		LOBBY_CONTENTS = leaderboard.getStringList("lobby.content");
		Collections.reverse(LOBBY_CONTENTS);
		GAME_CONTENTS = leaderboard.getStringList("game.content");
		Collections.reverse(GAME_CONTENTS);
		COUNTDOWN_WAITING = leaderboard.getString("countdown.waiting");
		COUNTDOWN_COUNTING = leaderboard.getString("countdown.counting");
		COUNTDOWN_ADMINSTART = leaderboard.getString("countdown.adminStart");
		TAUNT_COUNTING = leaderboard.getString("taunt.counting");
		TAUNT_ACTIVE = leaderboard.getString("taunt.active");
		TAUNT_EXPIRED = leaderboard.getString("taunt.expired");
		GLOW_ACTIVE = leaderboard.getString("glow.active");
		GLOW_INACTIVE = leaderboard.getString("glow.inactive");
		BORDER_COUNTING = leaderboard.getString("border.counting");
		BORDER_DECREASING = leaderboard.getString("border.decreasing");

		//Lobby Items
		if (config.getBoolean("lobbyItems.leave.enabled")) {
			lobbyLeaveItem = createItemStack("lobbyItems.leave");
			lobbyItemLeavePosition = config.getInt("lobbyItems.leave.position");
		}
		if (config.getBoolean("lobbyItems.start.enabled")) {
			lobbyStartItem = createItemStack("lobbyItems.start");
			lobbyItemStartAdmin = config.getBoolean("lobbyItems.start.adminOnly");
			lobbyItemStartPosition = config.getInt("lobbyItems.start.position");
		}

		//Spectator Items
		flightToggleItem = createItemStack("spectatorItems.flight");
		flightToggleItemPosition = config.getInt("spectatorItems.flight.position");

		teleportItem = createItemStack("spectatorItems.teleport");
		teleportItemPosition = config.getInt("spectatorItems.teleport.position");

		//Database
		databaseHost = config.getString("databaseHost");
		databasePort = config.getString("databasePort");
		databaseUser = config.getString("databaseUser");
		databasePass = config.getString("databasePass");
		databaseName = config.getString("databaseName");

		databaseType = config.getString("databaseType").toUpperCase();
		if(!databaseType.equals("SQLITE") && !databaseType.equals("MYSQL")){
			Main.getInstance().getLogger().warning("databaseType: "+databaseType+" is not a valid configuration option!");
			databaseType = "SQLITE";
		}
	}
	
	public static void addToConfig(String path, Object value) {
		config.set(path, value);
	}

	public static void saveConfig() {
		config.saveConfig();
	}

	@Nullable
	private static ItemStack createItemStack(String path){
		ConfigurationSection item = new YamlConfiguration().createSection("temp");
		item.set("name", ChatColor.translateAlternateColorCodes('&',config.getString(path+".name")));
		item.set("material", config.getString(path+".material"));
		if (Main.getInstance().supports(14)) {
			if (config.contains(path+".model-data") && config.getInt(path+".model-data") != 0) {
				item.set("model-data", config.getInt(path+".model-data"));
			}
		}
		List<String> lore = config.getStringList(path+".lore");
		if (lore != null && !lore.isEmpty()) item.set("lore", lore);
		ItemStack temp = null;
		try{ temp = XItemStack.deserialize(item); } catch(Exception ignored) {}
		return temp;
	}
	
}