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
	private int delay;
	private boolean running;
	
	public Taunt(int temp) {
		this.temp = temp;
		this.delay = 0;
	}
	
	public void schedule() {
		delay = tauntDelay;
		waitTaunt();
	}

	private void waitTaunt() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
			if(delay == 0) {
				if(!tauntLast && Main.plugin.board.size() < 2) return;
				else executeTaunt();
			} else {
				delay--;
				waitTaunt();
			}
		},20);
	}
	
	private void executeTaunt() {
		if(temp != Main.plugin.game.gameId) return;
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
			running = true;
			taunted.sendMessage(message("TAUNTED").toString());
			Util.broadcastMessage(tauntPrefix + message("TAUNT"));
			tauntPlayer = taunted.getName();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> {
				if(temp != Main.plugin.game.gameId) return;
				Player taunted1 = Main.plugin.board.getPlayer(tauntPlayer);
				if(taunted1 != null) {
					Firework fw = (Firework) taunted1.getLocation().getWorld().spawnEntity(taunted1.getLocation(), EntityType.FIREWORK);
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
				running = false;
				schedule();
			},20*30);
		} else {
			schedule();
		}
	}

	public int getDelay(){
		return delay;
	}

	public boolean isRunning() {
		return running;
	}
	
}
