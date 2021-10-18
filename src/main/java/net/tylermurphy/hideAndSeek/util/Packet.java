package net.tylermurphy.hideAndSeek.util;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.EnumWrappers.SoundCategory;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;

public class Packet {
	
	private static ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

	public static void playSound(Player player, Sound sound, float volume, float pitch) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.NAMED_SOUND_EFFECT);
		packet.getSoundCategories().write(0, SoundCategory.MASTER);
		packet.getSoundEffects().write(0, sound);
		packet.getIntegers().write(0, (int)(player.getLocation().getX() * 8.0));
		packet.getIntegers().write(1, (int)(player.getLocation().getY() * 8.0));
		packet.getIntegers().write(2, (int)(player.getLocation().getZ() * 8.0));
		packet.getFloat().write(0, volume);
		packet.getFloat().write(1, pitch);
		try {
			protocolManager.sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static void setGlow(Player player, Player target, boolean glowing) {
	    PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
	    packet.getIntegers().write(0, target.getEntityId());
	    WrappedDataWatcher watcher = new WrappedDataWatcher();
	    Serializer serializer = Registry.get(Byte.class);
	    watcher.setEntity(target);
	    if(glowing) {
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
