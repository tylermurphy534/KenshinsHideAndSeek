package net.tylermurphy.hideAndSeek.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class VoidGenerator extends ChunkGenerator{

    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.<BlockPopulator>emptyList();
    }

    public boolean shouldGenerateNoise() {
    	return false;
    }

    public boolean shouldGenerateSurface() {
    	return false;
    }

    public boolean shouldGenerateBedrock() {
    	return false;
    }

    public boolean shouldGenerateCaves() {
    	return false;
    }

    public boolean shouldGenerateDecorations() {
    	return false;
    }

    public boolean shouldGenerateMobs() {
    	return false;
    }

    public boolean shouldGenerateStructures() {
    	return false;
    }

    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) { return createChunkData(world); }
    
}
