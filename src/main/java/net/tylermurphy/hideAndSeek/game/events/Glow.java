package net.tylermurphy.hideAndSeek.game.events;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.game.util.Packet;
import org.bukkit.entity.Player;

import static net.tylermurphy.hideAndSeek.configuration.Config.glowLength;
import static net.tylermurphy.hideAndSeek.configuration.Config.glowStackable;

public class Glow {

    private int glowTime;
    private boolean running;

    public Glow() {
        this.glowTime = 0;
    }

    public void onProjectile() {
        if (glowStackable) glowTime += glowLength;
        else glowTime = glowLength;
        running = true;
    }

    private void sendPackets() {
        for (Player hider : Main.getInstance().getBoard().getHiders())
            for (Player seeker : Main.getInstance().getBoard().getSeekers())
                Packet.setGlow(hider, seeker, true);
    }

    public void update() {
        if (running) {
            sendPackets();
            glowTime--;
            glowTime = Math.max(glowTime, 0);
            if (glowTime == 0) {
                stopGlow();
            }
        }
    }

    private void stopGlow() {
        running = false;
        for (Player hider : Main.getInstance().getBoard().getHiders()) {
            for (Player seeker : Main.getInstance().getBoard().getSeekers()) {
                Packet.setGlow(hider, seeker, false);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

}
