package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import net.tylermurphy.hideAndSeek.Main;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class SaveMap implements ICommand {

	public static boolean runningBackup = false;
	
	public void execute(CommandSender sender, String[] args) {
		if(!Main.plugin.status.equals("Standby")) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if(spawnPosition.getBlockX() == 0 && spawnPosition.getBlockY() == 0 && spawnPosition.getBlockZ() == 0) {
			sender.sendMessage(errorPrefix + message("ERROR_GAME_SPAWN"));
			return;
		}
		sender.sendMessage(messagePrefix + message("MAPSAVE_START"));
		sender.sendMessage(warningPrefix + message("MAPSAVE_WARNING"));
		Bukkit.getServer().getWorld(spawnWorld).save();
		BukkitRunnable runnable = new BukkitRunnable() {
			public void run() {
				File current = new File(Main.root+File.separator+spawnWorld);
				if(current.exists()) {
					File temp_destenation = new File(Main.root+File.separator+"temp_hideandseek_"+spawnWorld);
					File destenation = new File(Main.root+File.separator+"hideandseek_"+spawnWorld);
					copyFileStructure(current, temp_destenation);
					if(destenation.exists()) {
						deleteDirectory(destenation);
						destenation.mkdir();
					}
					temp_destenation.renameTo(destenation);
					sender.sendMessage(messagePrefix + message("MAPSAVE_END"));
					runningBackup = false;
				} else {
					sender.sendMessage(errorPrefix + message("MAPSAVE_ERROR"));
				}
			}
		};
		runnable.runTaskAsynchronously(Main.plugin);
		runningBackup = true;
	}
	
	private static void copyFileStructure(File source, File target){
	    try {
	        ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
	        if(!ignore.contains(source.getName())) {
	            if(source.isDirectory()) {
	                if(!target.exists())
	                    if (!target.mkdirs())
	                        throw new IOException("Couldn't create world directory!");
	                String files[] = source.list();
	                for (String file : files) {
	                    File srcFile = new File(source, file);
	                    File destFile = new File(target, file);
	                    copyFileStructure(srcFile, destFile);
	                }
	            } else {
	                InputStream in = new FileInputStream(source);
	                OutputStream out = new FileOutputStream(target);
	                byte[] buffer = new byte[1024];
	                int length;
	                while ((length = in.read(buffer)) > 0)
	                    out.write(buffer, 0, length);
	                in.close();
	                out.close();
	            }
	        }
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	private boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}

	public String getLabel() {
		return "saveMap";
	}

	public String getUsage() {
		return "";
	}

	public String getDescription() {
		return "Saves current map for the game. May lag server.";
	}
	
}
