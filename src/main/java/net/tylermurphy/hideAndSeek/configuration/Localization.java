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

import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Localization {

	public static final Map<String,LocalizationString> LOCAL = new HashMap<>();

	private static final Map<String,String[][]> CHANGES = new HashMap<String,String[][]>() {{
		put("en-US", new String[][]{{"WORLDBORDER_DECREASING"},{"START","TAUNTED"}});
		put("de-DE", new String[][]{{}});
	}};

	public static void loadLocalization() {

		ConfigManager manager = new ConfigManager("localization.yml", "lang/localization_"+Config.locale +".yml");

		int PLUGIN_VERSION = manager.getDefaultInt("version");
		int VERSION = manager.getInt("version");
		if(VERSION < PLUGIN_VERSION){
			for(int i = VERSION; i < PLUGIN_VERSION; i++){
				if(i < 1) continue;
				String[] changeList = CHANGES.get(Config.locale)[i-1];
				for(String change : changeList)
					manager.reset("Localization." + change);
			}
			manager.reset("version");
		}

		String SELECTED_LOCAL = manager.getString("type");
		if(SELECTED_LOCAL == null){
			manager.reset("type");
		} else if(!SELECTED_LOCAL.equals(Config.locale)){
			manager.resetFile("lang"+File.separator+"localization_"+Config.locale +".yml");
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
			return new LocalizationString(ChatColor.RED + "" + ChatColor.ITALIC + key + " is not found in localization.yml. This is a plugin issue, please report it.");
		}
		return new LocalizationString(temp.toString());
	}
}
