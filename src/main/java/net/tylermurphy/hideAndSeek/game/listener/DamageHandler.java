package net.tylermurphy.hideAndSeek.game.listener;

import com.cryptomorin.xseries.XSound;
import net.tylermurphy.hideAndSeek.game.Board;
import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Config.spawnPosition;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class DamageHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        // If you are not a player, get out of here
        if(!(event.getEntity() instanceof Player)) return;
        // Define variables
        Player player = (Player) event.getEntity();
        Player attacker = null;
        // If player pvp is enabled, and player doesn't die, we do not care
        if(pvpEnabled && player.getHealth() - event.getFinalDamage() >= 0.5){ return; }
        // If no spawn position, we won't be able to manage their death :o
        if(spawnPosition == null){ return; }
        // If there is an attacker, find them
        if (event instanceof EntityDamageByEntityEvent) {
            if(((EntityDamageByEntityEvent) event).getDamager() instanceof Player)
                attacker = (Player) ((EntityDamageByEntityEvent) event).getDamager();
            else if(((EntityDamageByEntityEvent) event).getDamager() instanceof Projectile)
                if(((Projectile) ((EntityDamageByEntityEvent) event).getDamager()).getShooter() instanceof Player)
                    attacker = (Player) ((Projectile) ((EntityDamageByEntityEvent) event).getDamager()).getShooter();
        }
        // Makes sure that if there was an attacking player, that the event is allowed for the game
        if(attacker != null){
            // Cancel if one player is in the game but other isn't
            if((Board.contains(player) && !Board.contains(attacker)) || (!Board.contains(player) && Board.contains(attacker))){
                event.setCancelled(true);
                return;
                // Ignore event if neither player are in the game
            } else if(!Board.contains(player) && !Board.contains(attacker)){
                return;
                // Ignore event if players are on the same team, or one of them is a spectator
            } else if(Board.onSameTeam(player, attacker) || Board.isSpectator(player) || Board.isSpectator(attacker)){
                event.setCancelled(true);
                return;
                // Ignore the event if pvp is disabled, and a hider is trying to attack a seeker
            } else if(!pvpEnabled && Board.isHider(attacker) && Board.isSeeker(player)){
                event.setCancelled(true);
                return;
            }
            // If there is no attacker, it must of been by natural causes. If pvp is disabled, and config doesn't allow natural causes, cancel event.
        } else if(!pvpEnabled && !allowNaturalCauses){
            event.setCancelled(true);
            return;
            // Spectators cannot take damage
        } else if(Board.isSpectator(player)){
            event.setCancelled(true);
            if(Version.atLeast("1.18") && player.getLocation().getY() < -64){
                player.teleport(new Location(Bukkit.getWorld(Game.getGameWorld()), spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()));
            } else if(player.getLocation().getY() < 0){
                player.teleport(new Location(Bukkit.getWorld(Game.getGameWorld()), spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()));
            }
            return;
        }
        // Handle death event
        event.setCancelled(true);
        // Reset health and play death effect
        if(Version.atLeast("1.9")) {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) player.setHealth(attribute.getValue());
            XSound.ENTITY_PLAYER_DEATH.play(player, 1, 1);
        } else {
            player.setHealth(player.getMaxHealth());
            XSound.ENTITY_PLAYER_HURT.play(player, 1, 1);
        }
        // Teleport player to seeker spawn
        player.teleport(new Location(Bukkit.getWorld(Game.getGameWorld()), spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()));
        // Broadcast player death message
        if (Board.isSeeker(player)) {
            Game.broadcastMessage(message("GAME_PLAYER_DEATH").addPlayer(player).toString());
            if(Board.getFirstSeeker().getName().equals(player.getName())){
                Board.addDeath(player.getUniqueId());
            }
        } else if (Board.isHider(player)) {
            if (attacker == null) {
                Game.broadcastMessage(message("GAME_PLAYER_FOUND").addPlayer(player).toString());
            } else {
                Game.broadcastMessage(message("GAME_PLAYER_FOUND_BY").addPlayer(player).addPlayer(attacker).toString());
            }
            Board.addDeath(player.getUniqueId());
            Board.addSeeker(player);
        }
        // Add leaderboard kills if attacker
        if(attacker != null && ( Board.isHider(attacker) || Board.getFirstSeeker().getName().equals(attacker.getName()) ) )
            Board.addKill(attacker.getUniqueId());
        Game.resetPlayer(player);
        Board.reloadBoardTeams();
    }

}
