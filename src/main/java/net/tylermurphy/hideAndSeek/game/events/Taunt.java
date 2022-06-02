package net.tylermurphy.hideAndSeek.game.events;

import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Config.tauntDelay;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class Taunt {

    private UUID tauntPlayer;
    private int delay;
    private boolean running;

    public Taunt() {
        this.delay = tauntDelay;
    }

    public void update() {
        if (delay == 0) {
            if (running) launchTaunt();
            else if (tauntLast || Main.getInstance().getBoard().sizeHider() > 1) executeTaunt();
        } else {
            delay--;
            delay = Math.max(delay, 0);
        }
    }

    private void executeTaunt() {
        Optional<Player> rand = Main.getInstance().getBoard().getHiders().stream().skip(new Random().nextInt(Main.getInstance().getBoard().size())).findFirst();
        if (!rand.isPresent()) {
            Main.getInstance().getLogger().warning("Failed to select random seeker.");
            return;
        }
        Player taunted = rand.get();
        taunted.sendMessage(message("TAUNTED").toString());
        Main.getInstance().getGame().broadcastMessage(tauntPrefix + message("TAUNT"));
        tauntPlayer = taunted.getUniqueId();
        running = true;
        delay = 30;
    }

    private void launchTaunt() {
        Player taunted = Main.getInstance().getBoard().getPlayer(tauntPlayer);
        if (taunted != null) {
            if (!Main.getInstance().getBoard().isHider(taunted)) {
                Main.getInstance().getLogger().info("Taunted played died and is now seeker. Skipping taunt.");
                tauntPlayer = null;
                running = false;
                delay = tauntDelay;
                return;
            }
            World world = taunted.getLocation().getWorld();
            if (world == null) {
                Main.getInstance().getLogger().severe("Game world is null while trying to launch taunt.");
                tauntPlayer = null;
                running = false;
                delay = tauntDelay;
                return;
            }
            Firework fw = (Firework) world.spawnEntity(taunted.getLocation(), EntityType.FIREWORK);
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
            Main.getInstance().getGame().broadcastMessage(tauntPrefix + message("TAUNT_ACTIVATE"));
        }
        tauntPlayer = null;
        running = false;
        delay = tauntDelay;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isRunning() {
        return running;
    }

}