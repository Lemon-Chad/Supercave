package com.lemonchad.supercave;

import com.lemonchad.supercave.commands.LeanCommands;
import com.lemonchad.supercave.entities.CobwebProjectile;
import com.lemonchad.supercave.events.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Spider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public final class Supercave extends JavaPlugin {
    public static Supercave INSTANCE;
    public static final Set<BossBar> bars = new HashSet<>();

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        INSTANCE = this;

        // Events
        Bukkit.getPluginManager().registerEvents(new BedTroll(), this);
        Bukkit.getPluginManager().registerEvents(new LeanEffects(), this);
        Bukkit.getPluginManager().registerEvents(new LeanBoss(), this);
        Bukkit.getPluginManager().registerEvents(new QualityOfLife(), this);

        // Commands
        LeanCommands commands = new LeanCommands();
        getCommand("givelean").setExecutor(commands);
        getCommand("giveleanarmor").setExecutor(commands);
        getCommand("summonleanzombie").setExecutor(commands);

        // Lean Mobs
        LeanMobGenerator.register(Spider.class, e -> {
            e.getWorld().playSound(e.getLocation(), Sound.ENTITY_SPIDER_HURT, 1, 0.5f);
            e.getWorld().spawnParticle(Particle.CLOUD, e.getLocation(), 15, 0.5, 0.5, 0.5, 0);
            Bukkit.getScheduler().runTaskLater(INSTANCE, () -> {
                CobwebProjectile projectile = new CobwebProjectile(e, e.getLocation());
                projectile.setVelocity(e.getLocation().getDirection().multiply(2.25).add(new Vector(0, 0.1, 0)));
            }, 20);
        }, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        bars.forEach(this::disableBossBar);
    }

    public void disableBossBar(BossBar bar) {
        bars.remove(bar);
        bar.removeAll();
        bar.setVisible(false);
    }
}
