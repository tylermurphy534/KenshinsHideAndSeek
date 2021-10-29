package net.tylermurphy.hideAndSeek.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.Main;

public class Localization {

	public static final Map<String,LocalizationString> LOCAL = new HashMap<String,LocalizationString>();
	
	private static ConfigManager manager;
	
	public static boolean loadLocalization() {

		manager = new ConfigManager("localization.yml");
		
		for(String key : manager.getConfigurationSection("Localization").getKeys(false)) {
			LOCAL.put(
					key, 
					new LocalizationString( ChatColor.translateAlternateColorCodes('&', manager.getString("Localization."+key) ) )
					);
		}

		manager.saveConfig();

		return true;
	}
	
	public static LocalizationString message(String key) {
		LocalizationString temp = LOCAL.get(key);
		if(temp == null) {
			return new LocalizationString(ChatColor.RED + "" + ChatColor.ITALIC + key + "is not found in localization.yml. This is a plugin issue, please report it.");
		}
		return new LocalizationString(temp.toString());
	}
}
