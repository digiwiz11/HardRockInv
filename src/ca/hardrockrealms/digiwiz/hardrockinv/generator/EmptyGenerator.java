package ca.hardrockrealms.hardrockinv.generator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: DAD
 * Date: 11/21/13
 * Time: 8:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmptyGenerator extends ChunkGenerator {
    void setBlock(byte[][] result, int x, int y, int z, byte blkid) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new byte[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
    }

    public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<BlockPopulator>();
    }

    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 3, 0);
    }

    /**
     * Generate a base of bed rock for the empty world.
     * @param world
     * @param random
     * @param chunkX
     * @param chunkZ
     * @param biomes
     * @return
     */
    @Override
    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
        byte[][] blocks = new byte[world.getMaxHeight() / 16][];
        int x = 0;
        int y = 0;
        int z = 0;

        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                setBlock(blocks, x, 0, z, (byte) Material.BEDROCK.getId());
            }
        }

        return blocks;
    }

}
