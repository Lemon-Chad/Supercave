package com.lemonchad.supercave.entities;

import com.lemonchad.supercave.Supercave;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Monster;

public class LeanMob<T extends Monster> {
    private final T entity;
    private final Attack<T> attack;
    private double gamma;
    private double decisionBoundary;
    private boolean engaged;

    public interface Attack<T extends Monster> {
        void attack(T entity);
    }

    public LeanMob(T entity, Attack<T> attack) {
        this.entity = entity;
        this.attack = attack;
        gamma = 1.0;
        decisionBoundary = Math.random();
        engaged = false;

        entity.setMaxHealth(entity.getMaxHealth() * 2);
        entity.setHealth(entity.getMaxHealth());

        StringBuilder name = new StringBuilder();
        name.append("§5§lLean ");

        String[] oldName = entity.getType().name().split("_");
        for (String s : oldName) {
            name.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase()).append(" ");
        }

        entity.setCustomName(name.substring(0, name.length() - 1));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Supercave.INSTANCE, this::tick, 0, 1);
    }

    private void tick() {
        if (!engaged && entity.getHealth() < entity.getMaxHealth()) {
            engaged = true;
            AutoBossbar.createBossBar(entity, 20, entity.getCustomName(), BarColor.PURPLE, BarStyle.SEGMENTED_6);
            entity.setCustomNameVisible(true);
        }
        if (Math.random() > gamma * decisionBoundary) {
            attack.attack(entity);
            decisionBoundary = Math.random();
            gamma = 1.0;
        }
        gamma *= 0.99;
    }

}
