/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation version 3.
 *
 * Kenshins Hide and Seek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.tylermurphy.hideAndSeek.world;

import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.message;

public class WorldLoader {
	
	String mapname;
	String savename;
	
	public WorldLoader(String mapname) {
		this.mapname = mapname;
		this.savename = "hideandseek_"+mapname;
	}

	public World getWorld(){
		return Bukkit.getServer().getWorld(savename);
	}

	public void unloadMap(){
		World world = Bukkit.getServer().getWorld(savename);
		if(world == null){
			Main.getInstance().getLogger().warning(savename + " already unloaded.");
			return;
		}
        if(Bukkit.getServer().unloadWorld(world, false)){
            Main.getInstance().getLogger().info("Successfully unloaded " + savename);
        }else{
            Main.getInstance().getLogger().severe("COULD NOT UNLOAD " + savename);
        }
    }

    public void loadMap(){
		Bukkit.getServer().createWorld(new WorldCreator(savename).generator(new VoidGenerator()));
		World world = Bukkit.getServer().getWorld(savename);
		if(world == null){
			Main.getInstance().getLogger().severe("COULD NOT LOAD " + savename);
			return;
		}
		world.setAutoSave(false);
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
					if(!destenation.mkdir()){
						throw new RuntimeException("Failed to create directory: "+destenation.getPath());
					}
				}
				if(!temp_destenation.renameTo(destenation)){
					throw new RuntimeException("Failed to rename directory: "+temp_destenation.getPath());
				}
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
		System.out.println(region.getAbsolutePath());
		System.out.println(temp.getAbsolutePath());
    	if(region.exists() && region.isDirectory()) {
			System.out.println("passed");
    		if(!temp.exists())
    			if(!temp.mkdirs())
    				throw new IOException("Couldn't create region directory!");
    		String[] files = region.list();
			if(files == null){
				Main.getInstance().getLogger().severe("Region directory is null or cannot be accessed");
				return;
			}
    		for (String file : files) {
    			System.out.println("Testing file "+ file);
    			if(isMca) {
	    			int minX = (int)Math.floor(saveMinX / 512.0);
	    			int minZ = (int)Math.floor(saveMinZ / 512.0);
	    			int maxX = (int)Math.floor(saveMaxX / 512.0);
	    			int maxZ = (int)Math.floor(saveMaxZ / 512.0);
	    			
	    			String[] parts = file.split("\\.");
	    			if(parts.length > 1) {
		    			Main.getInstance().getLogger().info(file);
		    			if( Integer.parseInt(parts[1]) < minX || Integer.parseInt(parts[1]) > maxX || Integer.parseInt(parts[2]) < minZ || Integer.parseInt(parts[2]) > maxZ )
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
	
	private void deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
		if(!directoryToBeDeleted.delete()){
			throw new RuntimeException("Failed to delete directory: "+directoryToBeDeleted.getPath());
		}
	}
	
}
