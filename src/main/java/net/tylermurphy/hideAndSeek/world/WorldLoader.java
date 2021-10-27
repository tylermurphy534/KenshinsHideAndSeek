package net.tylermurphy.hideAndSeek.world;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

import net.tylermurphy.hideAndSeek.Main;

public class WorldLoader {
	
	String mapname;
	String savename;
	
	public WorldLoader(String mapname) {
		this.mapname = mapname;
		this.savename = "hideandseek_"+mapname;
	}

	public void unloadMap(){
        if(Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(savename), false)){
            Main.plugin.getLogger().info("Successfully unloaded " + savename);
        }else{
            Main.plugin.getLogger().severe("COULD NOT UNLOAD " + savename);
        }
    }

    public void loadMap(){
        Bukkit.getServer().createWorld(new WorldCreator(savename).generator(new VoidGenerator()));
        Bukkit.getServer().getWorld(savename).setAutoSave(false);
    }
 
    public void rollback(){
        unloadMap();
        loadMap();
    }
    
    public String save() {
    	File current = new File(Main.root+File.separator+mapname);
    	if(current.exists()) {
			try {
				File destenation = new File(Main.root+File.separator+savename);
				File temp_destenation = new File(Main.root+File.separator+"temp_"+savename);
				copyFileFolder("region",true);
				copyFileFolder("entities",true);
				copyFileFolder("datapacks",false);
				File srcFile = new File(current, "level.dat");
                File destFile = new File(temp_destenation, "level.dat");
				copyFile(srcFile,destFile);
				if(destenation.exists()) {
					deleteDirectory(destenation);
					destenation.mkdir();
				}
				temp_destenation.renameTo(destenation);
			} catch(IOException e) {
				e.printStackTrace();
				return errorPrefix + message("COMMAND_ERROR");
			}
			return messagePrefix + message("MAPSAVE_END");
		} else {
			return errorPrefix + message("MAPSAVE_ERROR");
		}
    }
    
    private void copyFileFolder(String name, Boolean isMca) throws IOException {
    	File region = new File(Main.root+File.separator+mapname+File.separator+name);
    	File temp = new File(Main.root+File.separator+"temp_"+savename+File.separator+name);
    	if(region.exists() && region.isDirectory()) {
    		if(!temp.exists())
    			if(!temp.mkdirs())
    				throw new IOException("Couldn't create region directory!");
    		String files[] = region.list();
    		for (String file : files) {
    			
    			if(isMca) {
	    			int minX = (int)Math.floor(saveMinX / 32.0);
	    			int minZ = (int)Math.floor(saveMinZ / 32.0);
	    			int maxX = (int)Math.floor(saveMaxX / 32.0);
	    			int maxZ = (int)Math.floor(saveMaxZ / 32.0);
	    			
	    			String[] parts = file.split(".");
	    			if(parts.length > 1) {
		    			Main.plugin.getLogger().info(file);
		    			if( Integer.parseInt(parts[1]) < minX || Integer.parseInt(parts[1]) > maxX ||Integer.parseInt(parts[2]) < minZ || Integer.parseInt(parts[2]) > maxZ )
		    				continue;
	    			}
    			}
    		
                File srcFile = new File(region, file);
                if(srcFile.isDirectory()) {
                	copyFileFolder(name+File.separator+file, false);
                } else {
                	File destFile = new File(temp, file);
                	copyFile(srcFile, destFile);
                }
            }
    	}
    }
    
    private void copyFile(File source, File target) throws IOException {
    	InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(target);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0)
            out.write(buffer, 0, length);
        in.close();
        out.close();
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
	
}
