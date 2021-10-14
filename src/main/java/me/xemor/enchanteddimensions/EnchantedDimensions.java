package me.xemor.enchanteddimensions;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class EnchantedDimensions extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        WorldCreator moon = new WorldCreator(new NamespacedKey(this, "moon"));
        moon.environment(World.Environment.THE_END);
        moon.generator(new MoonGenerator(moon.seed()));
        World moonWorld = Bukkit.createWorld(moon);
        WorldCreator aether = new WorldCreator(new NamespacedKey(this, "aether"));
        aether.environment(World.Environment.NORMAL);
        aether.generator(new AetherGenerator(aether.seed()));
        World aetherWorld = Bukkit.createWorld(aether);
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(moonWorld)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 10, 1));
                }
            }
        }, 1L, 1L);
    }

}
