package net.tylermurphy.hideAndSeek.configuration;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;

public class Localization {

	public static final Map<String,LocalizationString> LOCAL = new HashMap<>();

	private static String[][] CHANGES = {{"WORLDBORDER_DECREASING"}};

	public static void loadLocalization() {

		ConfigManager manager = new ConfigManager("localization.yml");

		int PLUGIN_VERSION = 2;
		int VERSION = manager.getInt("version");
		if(VERSION < PLUGIN_VERSION){
			for(int i = VERSION; i < PLUGIN_VERSION; i++){
				if(i < 1) continue;
				String[] changeList = CHANGES[i-1];
				for(String change : changeList)
					manager.reset("Localization." + change);
			}
			manager.reset("version");
		}

		manager.saveConfig();

		for(String key : manager.getConfigurationSection("Localization").getKeys(false)) {
			LOCAL.put(
					key, 
					new LocalizationString( ChatColor.translateAlternateColorCodes('&', manager.getString("Localization."+key) ) )
					);
		}
	}
	
	public static LocalizationString message(String key) {
		LocalizationString temp = LOCAL.get(key);
		if(temp == null) {
			return new LocalizationString(ChatColor.RED + "" + ChatColor.ITALIC + key + "is not found in localization.yml. This is a plugin issue, please report it.");
		}
		return new LocalizationString(temp.toString());
	}
}
