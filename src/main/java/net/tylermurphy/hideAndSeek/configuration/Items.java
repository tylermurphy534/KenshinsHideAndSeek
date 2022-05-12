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
import net.tylermurphy.hideAndSeek.util.Version;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Items {

    public static List<ItemStack> HIDER_ITEMS, SEEKER_ITEMS;
    public static List<PotionEffect> HIDER_EFFECTS, SEEKER_EFFECTS;

    public static void loadItems() {

        ConfigManager manager = new ConfigManager("items.yml");

        SEEKER_ITEMS = new ArrayList<>();
        ConfigurationSection SeekerItems = manager.getConfigurationSection("items.seeker");
        int i = 1;
        while (true) {
            ConfigurationSection section = SeekerItems.getConfigurationSection(String.valueOf(i));
            if(section == null) break;
            ItemStack item = createItem(section);
            if(item != null) SEEKER_ITEMS.add(item);
            i++;
        }

        HIDER_ITEMS = new ArrayList<>();
        ConfigurationSection HiderItems = manager.getConfigurationSection("items.hider");
        i = 1;
        while (true) {
            ConfigurationSection section = HiderItems.getConfigurationSection(String.valueOf(i));
            if(section == null) break;
            ItemStack item = createItem(section);
            if(item != null) HIDER_ITEMS.add(item);
            i++;
        }
        SEEKER_EFFECTS = new ArrayList<>();
        ConfigurationSection SeekerEffects = manager.getConfigurationSection("effects.seeker");
        i = 1;
        while (true) {
            ConfigurationSection section = SeekerEffects.getConfigurationSection(String.valueOf(i));
            if(section == null) break;
            PotionEffect effect = getPotionEffect(section);
            if(effect != null) SEEKER_EFFECTS.add(effect);
            i++;
        }

        HIDER_EFFECTS = new ArrayList<>();
        ConfigurationSection HiderEffects = manager.getConfigurationSection("effects.hider");
        i = 1;
        while (true) {
            ConfigurationSection section = HiderEffects.getConfigurationSection(String.valueOf(i));
            if(section == null) break;
            PotionEffect effect = getPotionEffect(section);
            if(effect != null) HIDER_EFFECTS.add(effect);
            i++;
        }

    }

    private static ItemStack createItem(ConfigurationSection item) {
        ConfigurationSection config = new YamlConfiguration().createSection("temp");
        String material = item.getString("material").toUpperCase();
        boolean splash = false;
        if(!Version.atLeast("1.9")){
            if(material.contains("POTION")){
                config.set("level", 1);
            }
            if(material.equalsIgnoreCase("SPLASH_POTION") || material.equalsIgnoreCase("LINGERING_POTION")){
                material = "POTION";
                splash = true;
            }
        }
        config.set("name", item.getString("name"));
        config.set("material", material);
        config.set("enchants", item.getConfigurationSection("enchantments"));
        config.set("unbreakable", item.getBoolean("unbreakable"));
        if(item.isSet("lore"))
            config.set("lore", item.getStringList("lore"));
        if (material.equalsIgnoreCase("POTION") || material.equalsIgnoreCase("SPLASH_POTION") || material.equalsIgnoreCase("LINGERING_POTION"))
            config.set("base-effect", String.format("%s,%s,%s", item.getString("type"), false, splash));
        ItemStack stack = XItemStack.deserialize(config);
        stack.setAmount(item.getInt("amount"));
        if(stack.getData().getItemType() == Material.AIR) return null;
        return stack;
    }

    private static PotionEffect getPotionEffect(ConfigurationSection item){
        String type = item.getString("type");
        if(type == null) return null;
        if(PotionEffectType.getByName(type.toUpperCase()) == null) return null;
        return new PotionEffect(
                Objects.requireNonNull(PotionEffectType.getByName(type.toUpperCase())),
                item.getInt("duration"),
                item.getInt("amplifier"),
                item.getBoolean("ambient"),
                item.getBoolean("particles")
        );
    }
}
