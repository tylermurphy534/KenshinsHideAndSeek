package net.tylermurphy.hideAndSeek.command;

import com.cryptomorin.xseries.XMaterial;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.game.util.PlayerUtil;
import net.tylermurphy.hideAndSeek.game.util.Status;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Config.exitPosition;

public class Debug implements ICommand {

    private static final Map<Integer, Consumer<Player>> debugMenuFunctions = new HashMap<>();
    private Inventory debugMenu;

    public void execute(Player sender, String[] args) {
        if(debugMenu == null) createMenu();
        sender.openInventory(debugMenu);
    }

    private void createMenu(){
        debugMenu = Main.getInstance().getServer().createInventory(null, 9, "Debug Menu");
        debugMenu.setItem(0, createOption(0, XMaterial.LEATHER_CHESTPLATE.parseMaterial(), "&6Become a &lHider", 1, player -> {
            if(mapSaveEnabled) {
                if(Bukkit.getWorld(Main.getInstance().getGame().getGameWorld()) == null) Main.getInstance().getGame().getWorldLoader().loadMap();
            }
            Main.getInstance().getBoard().addHider(player);
            PlayerUtil.loadHider(player, Main.getInstance().getGame().getGameWorld());
            PlayerUtil.resetPlayer(player, Main.getInstance().getBoard());
        }));
        debugMenu.setItem(1, createOption(1, XMaterial.GOLDEN_CHESTPLATE.parseMaterial(), "&cBecome a &lSeeker", 1, player -> {
            if(mapSaveEnabled) {
                if(Bukkit.getWorld(Main.getInstance().getGame().getGameWorld()) == null) Main.getInstance().getGame().getWorldLoader().loadMap();
            }
            Main.getInstance().getBoard().addSeeker(player);
            PlayerUtil.loadSeeker(player, Main.getInstance().getGame().getGameWorld());
            PlayerUtil.resetPlayer(player, Main.getInstance().getBoard());
        }));
        debugMenu.setItem(2, createOption(2, XMaterial.IRON_CHESTPLATE.parseMaterial(), "&8Become a &lSpectator", 1, player -> {
            if(mapSaveEnabled) {
                if(Bukkit.getWorld(Main.getInstance().getGame().getGameWorld()) == null) Main.getInstance().getGame().getWorldLoader().loadMap();
            }
            Main.getInstance().getBoard().addSpectator(player);
            PlayerUtil.loadSpectator(player, Main.getInstance().getGame().getGameWorld());
        }));
        debugMenu.setItem(3, createOption(3, XMaterial.BARRIER.parseMaterial(), "&cUnload from Game", 1, player -> {
            Main.getInstance().getBoard().remove(player);
            PlayerUtil.unloadPlayer(player);
            player.teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
        }));
        debugMenu.setItem(4, createOption(4, XMaterial.BARRIER.parseMaterial(), "&cDie In Game", 2, player -> {
            if((Main.getInstance().getBoard().isSeeker(player) || Main.getInstance().getBoard().isHider(player)) && Main.getInstance().getGame().getStatus() == Status.PLAYING){
                player.setHealth(0.1);
            }
        }));
        debugMenu.setItem(6, createOption(6, Material.ENDER_PEARL, "&d&lTeleport: &fGame spawn", 1, player -> {
            if(mapSaveEnabled) {
                if(Bukkit.getWorld(Main.getInstance().getGame().getGameWorld()) == null) Main.getInstance().getGame().getWorldLoader().loadMap();
            }
            player.teleport(new Location(Bukkit.getWorld(Main.getInstance().getGame().getGameWorld()), spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ()));
        }));
        debugMenu.setItem(7, createOption(7, Material.ENDER_PEARL, "&d&lTeleport: &fLobby", 2, player -> {
            player.teleport(new Location(Bukkit.getWorld(lobbyWorld), lobbyPosition.getX(), lobbyPosition.getY(), lobbyPosition.getZ()));
        }));
        debugMenu.setItem(8, createOption(8, Material.ENDER_PEARL, "&d&lTeleport: &fExit", 3, player -> {
            player.teleport(new Location(Bukkit.getWorld(exitWorld), exitPosition.getX(), exitPosition.getY(), exitPosition.getZ()));
        }));
    }

    private ItemStack createOption(int slow, Material material, String name, int amount, Consumer<Player> callback){
        ItemStack temp = new ItemStack(material, amount);
        ItemMeta meta = temp.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        temp.setItemMeta(meta);
        debugMenuFunctions.put(slow, callback);
        return temp;
    }

    public static void handleOption(Player player, int slotId){
        Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            Consumer<Player> callback = debugMenuFunctions.get(slotId);
            if(callback != null) callback.accept(player);
        }, 0);
    }

    public String getLabel() {
        return "debug";
    }

    public String getUsage() {
        return "";
    }

    public String getDescription() {
        return "Run debug commands";
    }

}
