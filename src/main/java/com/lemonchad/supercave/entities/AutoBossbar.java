package com.lemonchad.supercave.entities;

import com.lemonchad.supercave.Supercave;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoBossbar {
    public static <T extends Damageable> void createBossBar(T entity, int radius, String title, BarColor color, BarStyle style) {
        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.setVisible(true);
        Supercave.bars.add(bossBar);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isDead()) {
                    cancel();
                    Supercave.INSTANCE.disableBossBar(bossBar);
                    return;
                }

                bossBar.setProgress(entity.getHealth() / entity.getMaxHealth());
                bossBar.removeAll();
                entity.getNearbyEntities(radius, radius, radius).forEach(e -> {
                    if (e instanceof Player)
                        bossBar.addPlayer((Player) e);
                });
            }
        }.runTaskTimer(Supercave.INSTANCE, 0, 1);
    }
}
