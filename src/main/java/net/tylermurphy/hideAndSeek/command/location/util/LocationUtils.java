package net.tylermurphy.hideAndSeek.command.location.util;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.game.util.Status;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

/**
 * @author bobby29831
 */
public class LocationUtils {

    /**
     * Provides a vector for a player
     * @param player the player to create the vector for
     * @return the vector
     */
    private static @Nullable Vector vector(Player player) {
        if (Main.getInstance().getGame().getStatus() != Status.STANDBY) {
            player.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
            return null;
        }

        if (player.getLocation().getBlockX() == 0 || player.getLocation().getBlockZ() == 0 || player.getLocation().getBlockY() == 0){
            player.sendMessage(errorPrefix + message("NOT_AT_ZERO"));
            return null;
        }

        Location loc = player.getLocation();
        return new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static void setLocation(Player player, Locations place, @Nullable Consumer<Vector> consumer) {
        Vector vec = vector(player);

        World world = player.getLocation().getWorld();
        if(world == null) {
            throw new RuntimeException("Unable to get world: " + spawnWorld);
        }

        consumer.accept(vec);

        player.sendMessage(messagePrefix + message(place.message()));
        addToConfig(place.path("x"), vec.getX());
        addToConfig(place.path("y"), vec.getY());
        addToConfig(place.path("z"), vec.getZ());
        addToConfig(place.path("world"), player.getLocation().getWorld().getName());
        saveConfig();
    }

}