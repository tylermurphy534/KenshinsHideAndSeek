/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation, either version 3 of the License.
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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

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
        String material_string = item.getString("material");
        if(material_string == null) return null;
        Material material = Material.valueOf(material_string.toUpperCase());
        int amount = item.getInt("amount");
        ItemStack stack = new ItemStack(material, amount);
        if(material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION){
            PotionMeta meta = getPotionMeta(stack, item);
            stack.setItemMeta(meta);
            
        } else {
            ConfigurationSection enchantments = item.getConfigurationSection("enchantments");
            if (enchantments != null)
                for (String enchantment_string : enchantments.getKeys(false)) {
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantment_string));
                    if (enchantment == null) continue;
                    stack.addUnsafeEnchantment(
                            enchantment,
                            enchantments.getInt(enchantment_string)
                    );
                }
            ItemMeta meta = getItemMeta(stack,item);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    private static ItemMeta getItemMeta(ItemStack stack, ConfigurationSection item){
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        String name = item.getString("name");
        if(name != null)
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setUnbreakable(item.getBoolean("unbreakable"));
        meta.setLore(item.getStringList("lore"));
        return meta;
    }

    private static PotionMeta getPotionMeta(ItemStack stack, ConfigurationSection item){
        String type = item.getString("type");
        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        if(type==null) return meta;
        assert meta != null;
        meta.setBasePotionData(new PotionData((PotionType.valueOf(type.toUpperCase()))));
        return meta;
    }

    private static PotionEffect getPotionEffect(ConfigurationSection item){
        String type = item.getString("type");
        if(type == null) return null;
        return new PotionEffect(
                Objects.requireNonNull(PotionEffectType.getByName(type.toUpperCase())),
                item.getInt("duration"),
                item.getInt("amplifier"),
                item.getBoolean("ambient"),
                item.getBoolean("particles")
        );
    }
}
