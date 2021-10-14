package me.xemor.enchanteddimensions;

import org.bukkit.util.noise.SimplexOctaveGenerator;

public class MoonNoiseGenerator extends NoiseGenerator {

    SimplexOctaveGenerator islandSurface;

    public MoonNoiseGenerator(long seed) {
        islandSurface = new SimplexOctaveGenerator(seed, 8);
        islandSurface.setScale((double) 1 / 300);
    }

    public double calculateDensity(int worldX, int worldY, int worldZ) {
        double surfaceNoise = (smooth(islandSurface.noise(worldX, worldY, worldZ, 1.3D, 0.5D, true)) + 1) * 64 - worldY; //gets the noise for surface
        return surfaceNoise; //picks the height for the top of islands
    }

    @Override
    public double[] calculateDensity(int[] worldXs, int[] worldYs, int[] worldZs) {
        return new double[0];
    }

    private double smooth(double noise) {
        return noise > 0 ? (1 - Math.pow(1 - noise, 2)) : (-1 + Math.pow(1 + noise, 2));
    }
}
