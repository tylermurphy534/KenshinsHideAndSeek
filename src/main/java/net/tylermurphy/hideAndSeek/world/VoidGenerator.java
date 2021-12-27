/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation, either version 3 of the License.
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
