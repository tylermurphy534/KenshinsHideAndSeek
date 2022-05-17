/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2022 Tyler Murphy.
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

package net.tylermurphy.hideAndSeek.game;

import com.cryptomorin.xseries.messages.Titles;
import net.md_5.bungee.api.ChatColor;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.configuration.Items;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Config.lobbyPosition;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class PlayerLoader {

    public static void loadHider(Player player, String gameWorld){
        player.teleport(new Location(Bukkit.getWorld(gameWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
        loadPlayer(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,1000000,5,false,false));
        Titles.sendTitle(player, 10, 70, 20, ChatColor.WHITE + "" + message("HIDER_TEAM_NAME"), ChatColor.WHITE + message("HIDERS_SUBTITLE").toString());
    }

    public static void loadSeeker(Player player, String gameWorld){
        player.teleport(new Location(Bukkit.getWorld(gameWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
        loadPlayer(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,1000000,127,false,false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1000000,127,false,false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,1000000,128,false,false));
        Titles.sendTitle(player, 10, 70, 20, ChatColor.WHITE + "" + message("SEEKER_TEAM_NAME"), ChatColor.WHITE + message("SEEKERS_SUBTITLE").toString());
    }

    public static void loadSpectator(Player player, String gameWorld){
        player.teleport(new Location(Bukkit.getWorld(gameWorld), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
        loadPlayer(player);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFallDistance(0.0F);
        player.getInventory().setItem(flightToggleItemPosition, flightToggleItem);
        player.getInventory().setItem(teleportItemPosition, teleportItem);
        Main.getInstance().getBoard().getPlayers().forEach(otherPlayer -> {
            otherPlayer.hidePlayer(player);
        });
        Titles.sendTitle(player, 10, 70, 20, ChatColor.GRAY + "" + ChatColor.BOLD + "SPECTATING", ChatColor.WHITE + message("SPECTATOR_SUBTITLE").toString());
    }

    public static void resetPlayer(Player player, Board board){
        loadPlayer(player);
        if (board.isSeeker(player)) {
            if (pvpEnabled)
                for(ItemStack item : Items.SEEKER_ITEMS)
                    player.getInventory().addItem(item);
            for(PotionEffect effect : Items.SEEKER_EFFECTS)
                player.addPotionEffect(effect);
        } else if (board.isHider(player)) {
            if (pvpEnabled)
                for(ItemStack item : Items.HIDER_ITEMS)
                    player.getInventory().addItem(item);
            for(PotionEffect effect : Items.HIDER_EFFECTS)
                player.addPotionEffect(effect);
            if (glowEnabled) {
                player.getInventory().addItem(glowPowerupItem);
            }
        }
    }

    public static void unloadPlayer(Player player){
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        for(PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (Main.getInstance().supports(9)) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) player.setHealth(attribute.getValue());
            for(Player temp : Main.getInstance().getBoard().getPlayers()) {
                Main.getInstance().getGame().getGlow().setGlow(player, temp, false);
            }
        } else {
            player.setHealth(player.getMaxHealth());
        }
        Main.getInstance().getBoard().getPlayers().forEach(temp -> {
            player.showPlayer(temp);
            temp.showPlayer(player);
        });
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFallDistance(0.0F);
    }

    public static void joinPlayer(Player player){
        player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(),lobbyPosition.getY(),lobbyPosition.getZ()));
        loadPlayer(player);
        if (lobbyStartItem != null && (!lobbyItemStartAdmin || player.hasPermission("hideandseek.start")))
            player.getInventory().setItem(lobbyItemStartPosition, lobbyStartItem);
        if (lobbyLeaveItem != null)
            player.getInventory().setItem(lobbyItemLeavePosition, lobbyLeaveItem);
    }

    private static void loadPlayer(Player player){
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        for(PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setFoodLevel(20);
        if (Main.getInstance().supports(9)) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) player.setHealth(attribute.getValue());
        } else {
            player.setHealth(player.getMaxHealth());
        }
    }

}
