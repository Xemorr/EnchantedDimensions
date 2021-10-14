package me.xemor.enchanteddimensions;

import io.papermc.paper.world.generation.ProtoWorld;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public class MoonGenerator extends ChunkGenerator {

    Random random;
    MoonNoiseGenerator moonNoiseGenerator;
    SimplexNoiseGenerator bumps;
    double minimumSurfaceHeight = 0;
    double surfaceVariance = 100;

    public MoonGenerator(long seed) {
        random = new Random(seed);
        moonNoiseGenerator = new MoonNoiseGenerator(seed);
        bumps = new SimplexNoiseGenerator(seed);
    }

    @Override
    public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biomeGrid) {
        ChunkData chunkData =  createChunkData(world);
        double[][][] noiseChunk = moonNoiseGenerator.getNoiseChunk(chunkX, chunkZ);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    double noise = noiseChunk[x][y][z];
                    if (noise > 0) {
                        chunkData.setBlock(x, y, z, getTypeOfBlock(y, noise).createBlockData());
                    }
                }
            }
        }
        return chunkData;
    }

    @Override
    public void generateDecorations(@NotNull ProtoWorld protoWorld) {
        if (Math.random() < 0.5) {
            int xRng = random.nextInt(16) - 8;
            int zRng = random.nextInt(16) - 8;
            int x = protoWorld.getCenterBlockX() + xRng;
            int z = protoWorld.getCenterBlockZ() + zRng;
            int height = 50;
            int yRng = random.nextInt(35) + 10;
            int y = height - yRng;
            placeGeode(x, y, z, protoWorld, getRandomGeodeType());
        }
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

    @Override
    public boolean shouldGenerateCaves() { return true; }

    public Material getTypeOfBlock(int y, double rng) {
        if (y <= 35) {
            if (rng < 0.2) {
                return Material.DEAD_BRAIN_CORAL_BLOCK;
            } else if (rng < 0.4) {
                return Material.GRAY_CONCRETE_POWDER;
            } else if (rng < 0.6) {
                return Material.DEAD_TUBE_CORAL_BLOCK;
            } else if (rng < 0.8) {
                return Material.DEAD_BUBBLE_CORAL_BLOCK;
            } else {
                return Material.DEAD_FIRE_CORAL_BLOCK;
            }
        }
        else {
            if (rng < 0.2) {
                return Material.DEAD_BRAIN_CORAL_BLOCK;
            } else if (rng < 0.4) {
                return Material.LIGHT_GRAY_CONCRETE_POWDER;
            } else if (rng < 0.6) {
                return Material.DEAD_TUBE_CORAL_BLOCK;
            } else if (rng < 0.8) {
                return Material.DEAD_BUBBLE_CORAL_BLOCK;
            } else {
                return Material.DEAD_FIRE_CORAL_BLOCK;
            }
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


}
