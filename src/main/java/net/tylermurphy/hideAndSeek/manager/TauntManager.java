package net.tylermurphy.hideAndSeek.manager;

import static net.tylermurphy.hideAndSeek.Store.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;

public class TauntManager {

public static void schedule() {
		
		Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){

			public void run() {
				int temp = gameId;
				while(true) {
					if(tauntPlayer != null && !tauntPlayer.equals("")) {
						try { Thread.sleep(1000); } catch (InterruptedException e) {}
						if(gameId != temp) break;
						continue;
					}
					try { Thread.sleep(1000*60); } catch (InterruptedException e) {}
					if(gameId != temp) break;
					if(Math.random() > .9) {
						Player taunted = null;
						int rand = (int) (Math.random()*Hider.getEntries().size());
						for(Player player : playerList.values()) {
							if(Hider.hasEntry(player.getName())) {
								rand--;
								if(rand==0) {
									taunted = player;
									break;
								}
							}
						}
						if(taunted != null) {
							taunted.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Oh no! You have been chosed to be taunted.");
							Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + "Taunt >" + ChatColor.WHITE + " A random hider will be taunted in the next 30s");
							try { Thread.sleep(1000*30); } catch (InterruptedException e) {}
							if(gameId != temp) break;
							tauntPlayer = taunted.getName();
						}
					}
				}
			}
		});
	}
	
}
