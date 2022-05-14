package net.tylermurphy.hideAndSeek.game.listener;

import com.comphenix.protocol.PacketType;
import com.google.common.collect.Sets;
import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Set;
import java.util.UUID;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Config.saveMaxZ;

public class MovementHandler implements Listener {

    private final Set<UUID> prevPlayersOnGround = Sets.newHashSet();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null || event.getTo().getWorld() == null) return;

        checkJumping(event);
        checkBounds(event);
    }

    private void checkJumping(PlayerMoveEvent event){
        if (!Main.getInstance().getBoard().isSpectator(event.getPlayer())) return;
        if (event.getPlayer().getVelocity().getY() > 0) {
            if (event.getPlayer().getLocation().getBlock().getType() != Material.LADDER && prevPlayersOnGround.contains(event.getPlayer().getUniqueId())) {
                if (!event.getPlayer().isOnGround()) {
                    // JUMPING :o
                    if(event.getPlayer().getAllowFlight()) event.getPlayer().setFlying(true);
                }
            }
        }
        if (event.getPlayer().isOnGround()) {
            prevPlayersOnGround.add(event.getPlayer().getUniqueId());
        } else {
            prevPlayersOnGround.remove(event.getPlayer().getUniqueId());
        }
    }

    private void checkBounds(PlayerMoveEvent event){
        if (!Main.getInstance().getBoard().contains(event.getPlayer())) return;
        if (!event.getPlayer().getWorld().getName().equals(Main.getInstance().getGame().getGameWorld())) return;
        if (!event.getTo().getWorld().getName().equals(Main.getInstance().getGame().getGameWorld())) return;
        if (event.getPlayer().hasPermission("hideandseek.leavebounds")) return;
        if (event.getTo().getBlockX() < saveMinX || event.getTo().getBlockX() > saveMaxX || event.getTo().getBlockZ() < saveMinZ || event.getTo().getBlockZ() > saveMaxZ) {
            event.setCancelled(true);
        }
    }

}
