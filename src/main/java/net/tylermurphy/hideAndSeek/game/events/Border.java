package net.tylermurphy.hideAndSeek.game.events;

import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class Border {

    private int delay;
    private boolean running;

    public Border() {
        delay = 60 * worldborderDelay;
    }

    public void update() {
        if (delay == 30 && !running) {
            Main.getInstance().getGame().broadcastMessage(worldborderPrefix + message("WORLDBORDER_WARN"));
        } else if (delay == 0) {
            if (running) {
                delay = 60 * worldborderDelay;
                running = false;
            }
            else decreaceWorldborder();
        }
        delay--;
    }

    private void decreaceWorldborder() {
        if (currentWorldborderSize == 100) return;
        int change = worldborderChange;
        if (currentWorldborderSize-worldborderChange < 100) {
            change = currentWorldborderSize-100;
        }
        running = true;
        Main.getInstance().getGame().broadcastMessage(worldborderPrefix + message("WORLDBORDER_DECREASING").addAmount(change));
        currentWorldborderSize -= worldborderChange;
        World world = Bukkit.getWorld(Main.getInstance().getGame().getGameWorld());
        assert world != null;
        org.bukkit.WorldBorder border = world.getWorldBorder();
        border.setSize(border.getSize()-change,30);
        delay = 30;
    }

    public void resetWorldBorder(String worldName) {
        World world = Bukkit.getWorld(worldName);
        assert world != null;
        org.bukkit.WorldBorder border = world.getWorldBorder();
        if (worldborderEnabled) {
            border.setSize(worldborderSize);
            border.setCenter(worldborderPosition.getX(), worldborderPosition.getZ());
            currentWorldborderSize = worldborderSize;
        } else {
            border.setSize(30000000);
            border.setCenter(0, 0);
        }
        delay = 60 * worldborderDelay;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isRunning() {
        return running;
    }

}