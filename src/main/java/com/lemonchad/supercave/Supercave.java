package com.lemonchad.supercave;

import com.lemonchad.supercave.commands.LeanCommands;
import com.lemonchad.supercave.entities.CobwebProjectile;
import com.lemonchad.supercave.events.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.Collection;
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
                if (e.isDead()) return;
                CobwebProjectile projectile = new CobwebProjectile(e, e.getLocation());
                projectile.setVelocity(e.getLocation().getDirection().multiply(2.25).add(new Vector(0, 0.1, 0)));
            }, 20);
        });
        LeanMobGenerator.register(Skeleton.class, e -> {
            e.getWorld().playSound(e.getLocation(), Sound.ENTITY_SKELETON_HURT, 1, 0.5f);
            e.getWorld().spawnParticle(Particle.CRIT, e.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);

            Bukkit.getScheduler().runTaskLater(INSTANCE, () -> {
                if (e.isDead()) return;
                int arrowCount = (int) (Math.random() * 10) + 10;
                for (int i = 0; i < arrowCount; i++) {
                    double angle = e.getLocation().getYaw() + 2 * i * Math.PI / arrowCount;
                    Vector direction = new Vector(Math.cos(angle), 0, Math.sin(angle));
                    Arrow arrow = e.launchProjectile(Arrow.class, direction.multiply(2.25));
                    arrow.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, true));
                }
            }, 20);
        }, 3);
        LeanMobGenerator.register(Zombie.class, e -> {
            e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ZOMBIE_HURT, 1, 0.5f);
            e.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, e.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0.1);

            for (int r = 1; r <= 10; r++) {
                int radius = r;
                Bukkit.getScheduler().runTaskLater(INSTANCE, () -> {
                    if (e.isDead()) return;
                    int particleCount = 4 * radius;
                    for (int i = 0; i < particleCount; i++) {
                        double angle = i * 2 * Math.PI / particleCount;
                        Vector direction = new Vector(Math.cos(angle), 0.25, Math.sin(angle));
                        e.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, e.getLocation().clone().add(direction.multiply(radius)), 3, 0.25, 0.25, 0.25, 0);
                    }
                    e.getNearbyEntities(radius, radius, radius).forEach(entity -> {
                        if (entity.getLocation().distance(e.getLocation()) - radius <= 1 && entity.getLocation().getY() - e.getLocation().getY() <= 0.5) {
                            entity.setVelocity(entity.getVelocity().setY(1.5));
                        }
                    });
                }, 20 + 2 * r);
            }
        }, 5);
        LeanMobGenerator.register(Enderman.class, e -> {
            e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1, 0.5f);
            e.getWorld().spawnParticle(Particle.END_ROD, e.getLocation().clone().add(0, 1.5, 0), 15, 0.5, 1.5, 0.5, 0);

            Bukkit.getScheduler().runTaskLater(INSTANCE, () -> {
                if (e.isDead()) return;
                Collection<Entity> entities = e.getNearbyEntities(8, 8, 8);
                entities.forEach(entity -> {
                    e.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                    e.getWorld().spawnParticle(Particle.PORTAL, entity.getLocation(), 15, 0.5, 0.5, 0.5, 0);
                    entity.teleport(e.getLocation());
                    e.getWorld().spawnParticle(Particle.PORTAL, entity.getLocation(), 15, 0.5, 0.5, 0.5, 0);
                });
                e.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 3));
            }, 20);
        }, 5);
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
