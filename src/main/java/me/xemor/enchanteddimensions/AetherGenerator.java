package me.xemor.enchanteddimensions;

import io.papermc.paper.world.generation.ProtoWorld;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public class AetherGenerator extends ChunkGenerator {

    Random random;
    AetherNoiseGenerator aetherNoiseGenerator;
    SimplexNoiseGenerator bumps;
    double minimumSurfaceHeight = 0;
    double surfaceVariance = 100;

    public AetherGenerator(long seed) {
        random = new Random(seed);
        aetherNoiseGenerator = new AetherNoiseGenerator(seed);
        bumps = new SimplexNoiseGenerator(seed);
    }

    @Override
    public ChunkGenerator.ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.BiomeGrid biomeGrid) {
        ChunkGenerator.ChunkData chunkData = createChunkData(world);
        try {
            double[][][] noiseChunk = aetherNoiseGenerator.getNoiseChunk(chunkX, chunkZ);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    boolean inGround = false;
                    for (int y = 255; y >= 0; y--) {
                        double noise = noiseChunk[x][y][z];
                        if (noise > 0) {
                            if (!inGround) {
                                chunkData.setBlock(x, y, z, Material.GRASS_BLOCK);
                                if (y - 1 >= 0 && noiseChunk[x][y - 1][z] > 0) {
                                    chunkData.setBlock(x, y - 1, z, Material.DIRT);
                                    if (y - 2 >= 0 && noiseChunk[x][y - 2][z] > 0) {
                                        chunkData.setBlock(x, y - 2, z, Material.DIRT);
                                    }
                                }
                                inGround = true;
                                y -= 2;
                            }
                            chunkData.setBlock(x, y, z, Material.STONE);
                        } else if (inGround) {
                            inGround = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chunkData;
    }

    @Override
    public void generateDecorations(@NotNull ProtoWorld protoWorld) {
        /*if (Math.random() < 0.5) {
            int xRng = random.nextInt(16) - 8;
            int zRng = random.nextInt(16) - 8;
            int x = protoWorld.getCenterBlockX() + xRng;
            int z = protoWorld.getCenterBlockZ() + zRng;
            int height = 50;
            int yRng = random.nextInt(35) + 10;
            int y = height - yRng;
            placeGeode(x, y, z, protoWorld, getRandomGeodeType());
        }*/
    }

    public void placeGeode(int x, int y, int z, ProtoWorld protoWorld, Supplier<Material> geodeSelection) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    protoWorld.setBlockData(x + i, y + j, z + k, geodeSelection.get().createBlockData());
                }
            }
        }
    }

    @Override
    public boolean isParallelCapable() {
        return true;
    }

    public Material getTypeOfBlock(double rng) {
        if (rng < 0.2) {
            return Material.GRASS_BLOCK;
        } else if (rng < 0.4) {
            return Material.DIRT;
        } else {
            return Material.STONE;
        }
    }

    public Supplier<Material> getRandomGeodeType() {
        Supplier<Material>[] suppliers = new Supplier[]{this::soulGeode, this::lavaGeode, this::blackstoneGeode};
        int rng = random.nextInt(suppliers.length);
        return suppliers[rng];
    }


    public Material soulGeode() {
        double rng = random.nextDouble();
        if (rng < 0.25) {
            return Material.ROOTED_DIRT;
        }
        else if (rng < 0.5) {
            return Material.SOUL_SOIL;
        }
        else if (rng < 0.75) {
            return Material.SOUL_SAND;
        }
        else {
            return Material.COARSE_DIRT;
        }
    }

    public Material lavaGeode() {
        double rng = random.nextDouble();
        if (rng < 0.25) {
            return Material.LAVA;
        }
        else if (rng < 0.75) {
            return Material.MAGMA_BLOCK;
        }
        else {
            return Material.SHROOMLIGHT;
        }
    }

    public Material blackstoneGeode() {
        double rng = random.nextDouble();
        if (rng < 0.25) {
            return Material.OBSIDIAN;
        }
        else if (rng < 0.75) {
            return Material.BLACKSTONE;
        }
        else {
            return Material.GILDED_BLACKSTONE;
        }
    }

    private static final class AetherNoiseGenerator extends NoiseGenerator {

        SimplexOctaveGenerator islandSurface;

        public AetherNoiseGenerator(long seed) {
            islandSurface = new SimplexOctaveGenerator(seed, 8);
            islandSurface.setScale((double) 1 / 300);
        }

        public double calculateDensity(int worldX, int worldY, int worldZ) {
            double surfaceNoise = (smooth(islandSurface.noise(worldX, worldY, worldZ, 1.3D, 0.6D, true))) * 128 - worldY * 0.5; //gets the noise for surface
            return surfaceNoise; //picks the height for the top of islands
        }

        private double smooth(double noise) {
            return noise > 0 ? (1 - Math.pow(1 - noise, 2)) : (-1 + Math.pow(1 + noise, 2));
        }
    }

}
