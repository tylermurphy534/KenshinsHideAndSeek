package net.tylermurphy.hideAndSeek.commands;

import org.bukkit.Bukkit;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.tylermurphy.hideAndSeek.ICommand;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.manager.TauntManager;
import net.tylermurphy.hideAndSeek.manager.WorldborderManager;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.util.HashMap;

public class Start implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(status.equals("Setup")) {
			sender.sendMessage(errorPrefix + "Please set spawn location first");
			return;
		}
		if(!status.equals("Standby")) {
			sender.sendMessage(errorPrefix + "Game is already in session");
			return;
		}
		if(Hider.getSize() < 1) {
			sender.sendMessage(errorPrefix + "No Hiders were found");
			return;
		}
		if(Seeker.getSize() < 1) {
			sender.sendMessage(errorPrefix + "No Seekers were found");
			return;
		}
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule sendCommandFeedback false");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule doImmediateRespawn true");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule logAdminCommands false");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule naturalRegeneration false");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule keepInventory false");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule showDeathMessages false");
		
		playerData = new HashMap<String,Integer>();
		for(Player player : playerList.values()) {
			resetPlayerData(player.getName(),true);
			player.getInventory().clear();
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(new Location(player.getWorld(), spawnPosition.getX(),spawnPosition.getY(),spawnPosition.getZ()));
			for(PotionEffect effect : player.getActivePotionEffects()){
			    player.removePotionEffect(effect.getType());
			}
		}
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("spawnpoint @a %s %s %s", spawnPosition.getBlockX(), spawnPosition.getBlockY(), spawnPosition.getBlockZ()));
		for(String playerName : Seeker.getEntries()) {
			Player player = playerList.get(playerName);
			if(player != null) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,1000000,127,false,false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,1000000,127,false,false));
			}
		}
		for(String playerName : Hider.getEntries()) {
			Player player = playerList.get(playerName);
			if(player != null) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,1000000,5,false,false));
			}
		}
		WorldborderManager.reset();
		status = "Starting";
		startTaskId = Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){
			public void run() {
				int temp = gameId;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 30 seconds to hide!");
				try { Thread.sleep(10*1000); } catch (InterruptedException e) {}
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 20 seconds to hide!");
				try { Thread.sleep(10*1000); } catch (InterruptedException e) {}
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 10 seconds to hide!");
				try { Thread.sleep(5*1000); } catch (InterruptedException e) {}
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 5 seconds to hide!");
				try { Thread.sleep(2*1000); } catch (InterruptedException e) {}
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 3 seconds to hide!");
				try { Thread.sleep(1*1000); } catch (InterruptedException e) {}
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 2 seconds to hide!");
				try { Thread.sleep(1*1000); } catch (InterruptedException e) {}
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Hiders have 1 seconds to hide!");
				try { Thread.sleep(1*1000); } catch (InterruptedException e) {}
				if(temp != gameId) return;
				Bukkit.getServer().broadcastMessage(messagePrefix + "Attetion SEEKERS, its time to find the hiders!");
				status = "Playing";
			}
		}).getTaskId();
		if(worldborderEnabled) {
			WorldborderManager.schedule();
		}
		TauntManager.schedule();
	}

	public String getLabel() {
		return "start";
	}
	
	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Starts the game";
	}

}
