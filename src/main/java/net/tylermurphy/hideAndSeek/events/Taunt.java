package net.tylermurphy.hideAndSeek.events;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.util.Util;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class Taunt {

	private final int temp;
	private String tauntPlayer;
	
	public Taunt(int temp) {
		this.temp = temp;
	}
	
	public void schedule() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				tryTaunt();
			}
		},20*60*5);
	}
	
	private void waitTaunt() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {
				tryTaunt();
			}
		},20*60);
	}
	
	private void tryTaunt() {
		if(temp != Main.plugin.gameId) return;
		if(Math.random() > .8) {
			executeTaunt();
		} else {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					tryTaunt();
				}
			},20*60);
		}
	}
	
	private void executeTaunt() {
		Player taunted = null;
		int rand = (int) (Math.random()*Main.plugin.board.sizeHider());
		for(Player player : Main.plugin.board.getPlayers()) {
			if(Main.plugin.board.isHider(player)) {
				rand--;
				if(rand==0) {
					taunted = player;
					break;
				}
			}
		}
		if(taunted != null) {
			taunted.sendMessage(message("TAUNTED").toString());
			Util.broadcastMessage(tauntPrefix + message("TAUNT"));
			tauntPlayer = taunted.getName();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
				public void run() {
					if(temp != Main.plugin.gameId) return;
					Player taunted = Main.plugin.board.getPlayer(tauntPlayer);
					if(taunted != null) {
						Firework fw = (Firework) taunted.getLocation().getWorld().spawnEntity(taunted.getLocation(), EntityType.FIREWORK);
						FireworkMeta fwm = fw.getFireworkMeta();
						fwm.setPower(4);
				        fwm.addEffect(FireworkEffect.builder()
				        		.withColor(Color.BLUE)
				        		.withColor(Color.RED)
				        		.withColor(Color.YELLOW)
				        		.with(FireworkEffect.Type.STAR)
				        		.with(FireworkEffect.Type.BALL)
				        		.with(FireworkEffect.Type.BALL_LARGE)
				        		.flicker(true)
				        		.withTrail()
				        		.build());
				        fw.setFireworkMeta(fwm);
				        Util.broadcastMessage(tauntPrefix + message("TAUNT_ACTIVATE"));
					}
					tauntPlayer = "";
					waitTaunt();
				}
			},20*30);
		} else {
			waitTaunt();
		}
	}
	
}
