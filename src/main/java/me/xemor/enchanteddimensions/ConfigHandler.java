package me.xemor.enchanteddimensions;

import java.io.File;

public class ConfigHandler {

    private EnchantedDimensions enchantedDimensions;
    private File structures;
    private File schematics;

    public ConfigHandler(EnchantedDimensions enchantedDimensions) {
        this.enchantedDimensions = enchantedDimensions;
        structures = new File(enchantedDimensions.getDataFolder(), "structures");
        structures.mkdir();
        schematics = new File(structures, "schematics");
        schematics.mkdir();
    }

}
