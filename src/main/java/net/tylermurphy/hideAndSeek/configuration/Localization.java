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
	
	static YamlConfiguration config, defaultConfig;
	static File location;
	
	public static boolean init() {
		
		Main.plugin.saveResource("localization.yml", false);
		String path = Main.data.getAbsolutePath()+File.separator + "localization.yml";
		location = new File(path);
		config = YamlConfiguration.loadConfiguration(location);
		
		InputStream is = Main.plugin.getResource("localization.yml");
		InputStreamReader isr = new InputStreamReader(is);
		defaultConfig = YamlConfiguration.loadConfiguration(isr);
		
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
		if(temp == null) {
			config.set("Localization."+key, defaultConfig.getString("Localization."+key));
			try {
				config.save(location);
			} catch (IOException e) {
				Main.plugin.getLogger().severe(e.getMessage());
			}
			LOCAL.put(key, 
					new LocalizationString( ChatColor.translateAlternateColorCodes('&', defaultConfig.getString("Localization."+key) ) )
					);
			return new LocalizationString(LOCAL.get(key).toString());
		}
		return new LocalizationString(temp.toString());
		
	}
}
