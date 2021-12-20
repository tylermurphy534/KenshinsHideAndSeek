package net.tylermurphy.hideAndSeek.world;

import java.util.Collections;
import java.util.List;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class VoidGenerator extends ChunkGenerator{
	   
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.<BlockPopulator>emptyList();
    }
    
    @Override
    public boolean shouldGenerateNoise() {
    	return false;
    }
    
    @Override
    public boolean shouldGenerateSurface() {
    	return false;
    }
    
    @Override
    public boolean shouldGenerateBedrock() {
    	return false;
    }
    
    @Override
    public boolean shouldGenerateCaves() {
    	return false;
    }
    
    @Override
    public boolean shouldGenerateDecorations() {
    	return false;
    }
    
    @Override
    public boolean shouldGenerateMobs() {
    	return false;
    }
    
    @Override
    public boolean shouldGenerateStructures() {
    	return false;
    }
   
    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }
    
}
