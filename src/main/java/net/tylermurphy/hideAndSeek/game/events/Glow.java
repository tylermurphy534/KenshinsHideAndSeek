package net.tylermurphy.hideAndSeek.game.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

import static net.tylermurphy.hideAndSeek.configuration.Config.glowLength;
import static net.tylermurphy.hideAndSeek.configuration.Config.glowStackable;

public class Glow {

    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

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
                setGlow(hider, seeker, true);
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
                setGlow(hider, seeker, false);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setGlow(Player player, Player target, boolean glowing) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, target.getEntityId());
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setEntity(target);
        if (glowing) {
            watcher.setObject(0, serializer, (byte) (0x40));
        } else {
            watcher.setObject(0, serializer, (byte) (0x0));
        }
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
