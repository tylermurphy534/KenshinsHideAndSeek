package net.tylermurphy.hideAndSeek.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.Main;

public class Localization {

	public static final Map<String,LocalizationString> LOCAL = new HashMap<String,LocalizationString>();
	
	static YamlConfiguration config;
	
	public static boolean init() {
		Main.plugin.saveResource("localization.yml", false);
		String path = Main.data.getAbsolutePath()+File.separator + "localization.yml";
		config = YamlConfiguration.loadConfiguration(new File(path));
		for(String key : config.getConfigurationSection("Localization").getKeys(false)) {
			LOCAL.put(
					key, 
					new LocalizationString( ChatColor.translateAlternateColorCodes('&', config.getString("Localization."+key) ) )
					);
		}
		return true;
	}
	
	public static LocalizationString message(String key) {
		LocalizationString temp = LOCAL.get(key);
		if(temp == null)
			return new LocalizationString(key+" missing from localization.yml");
		return temp;
	}
}
