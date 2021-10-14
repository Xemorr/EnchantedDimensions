package me.xemor.enchanteddimensions;

public abstract class NoiseGenerator {

    public abstract double calculateDensity(int worldX, int worldY, int worldZ);
    public abstract double[] calculateDensity(int[] worldXs, int[] worldYs, int[] worldZs);

    public double[][][] getNoiseChunk(int chunkX, int chunkZ) {
        double[][][] noise = new double[17][257][17];
        try {
            int worldX = chunkX << 4;
            int worldZ = chunkZ << 4;

            for (int x = 0; x <= 16; x += 8) {
                int currentWorldX = worldX + x;
                for (int z = 0; z <= 16; z += 8) {
                    int currentWorldZ = worldZ + z;
                    for (int y = 0; y <= 256; y += 4) {
                        noise[x][y][z] = calculateDensity(currentWorldX, y, currentWorldZ);
                    }
                }
            }
            int[][] vertices = null;
            for (int x = 0; x < 16; x++) {
                int dx = x % 8;
                for (int z = 0; z < 16; z++) {
                    int dz = z % 8;
                    for (int y = 0; y < 256; y++) {
                        int dy = y % 4;
                        if (dy == 0) {
                            vertices = getVertices(x, y, z, dx, dy, dz);
                        }
                        if (noise[x][y][z] == 0D) {
                            noise[x][y][z] = interpolatedNoise(noise, vertices, dx / 8D, dy / 4D, dz / 8D);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return noise;
    }

    private int[][] getVertices(int x, int y, int z, int dx, int dy, int dz) {
        int flippedDX = 8 - dx;
        int flippedDY = 4 - dy;
        int flippedDZ = 8 - dz;
        int lowerX = x - dx;
        int lowerY = y - dy;
        int lowerZ = z - dz;
        int upperX = x + flippedDX;
        int upperY = y + flippedDY;
        int upperZ = z + flippedDZ;
        return new int[][]{new int[]{lowerX, lowerY, lowerZ}, new int[]{upperX, lowerY, lowerZ},
                new int[]{lowerX, upperY, lowerZ}, new int[]{upperX, upperY, lowerZ},
                new int[]{lowerX, lowerY, upperZ}, new int[]{upperX, lowerY, upperZ},
                new int[]{lowerX, upperY, upperZ}, new int[]{upperX, upperY, upperZ}};
    }

    private double interpolation1D(double value1, double value2, double position) {
        return value1 * (1 - position) + value2 * position;
    }

    private double interpolation2D(double value1, double value2, double value3, double value4, double positionX, double positionY) {
        double interp1 = interpolation1D(value1, value2, positionX);
        double interp2 = interpolation1D(value3, value4, positionX);
        return interpolation1D(interp1, interp2, positionY);
    }

    private double interpolatedNoise(double[][][] noise, int[][] vertices, double x, double y, double z) {
        double noise1 = getNoiseForVertex(0, noise, vertices);
        double noise2 = getNoiseForVertex(1, noise, vertices);
        double noise3 = getNoiseForVertex(2, noise, vertices);
        double noise4 = getNoiseForVertex(3, noise, vertices);
        double noise5 = getNoiseForVertex(4, noise, vertices);
        double noise6 = getNoiseForVertex(5, noise, vertices);
        double noise7 = getNoiseForVertex(6, noise, vertices);
        double noise8 = getNoiseForVertex(7, noise, vertices);
        double interp1 = interpolation2D(noise1, noise2, noise3, noise4, x, y);
        double interp2 = interpolation2D(noise5, noise6, noise7, noise8, x, y);
        return interpolation1D(interp1, interp2, z);
    }

    private double getNoiseForVertex(int index, double[][][] noise, int[][] vertices) {
        int[] vertex = vertices[index];
        return noise[vertex[0]][vertex[1]][vertex[2]];
    }

}
