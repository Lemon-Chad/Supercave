package com.lemonchad.supercave.entities;

import com.lemonchad.supercave.Supercave;
import com.lemonchad.supercave.items.SupercaveItems;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;

public class LeanMob<T extends Monster> {
    private final T entity;
    private final Attack<T> attack;
    private double gamma;
    private double decisionBoundary;
    private boolean engaged;
    private final int thread;
    private final float delay;

    public interface Attack<T extends Monster> {
        void attack(T entity);
    }

    public LeanMob(T entity, Attack<T> attack, float delay) {
        this.entity = entity;
        this.attack = attack;
        this.delay = delay;
        gamma = 1.0;
        decisionBoundary = Math.random();
        engaged = false;

        if (entity instanceof Skeleton) {
            ((Skeleton) entity).setShouldBurnInDay(false);
        } else if (entity instanceof Zombie) {
            ((Zombie) entity).setShouldBurnInDay(false);
        }

        thread = Bukkit.getScheduler().scheduleSyncRepeatingTask(Supercave.INSTANCE, this::tick, 0, 1);
    }

    private void engage() {
        entity.setMaxHealth(entity.getMaxHealth() * 5);
        entity.setHealth(entity.getMaxHealth());
        //noinspection ConstantConditions
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() * 3);

        StringBuilder name = new StringBuilder();
        name.append("§5§lLean ");

        String[] oldName = entity.getType().name().split("_");
        for (String s : oldName) {
            name.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase()).append(" ");
        }

        String newName = name.toString().trim();
        entity.setCustomName(newName);
        entity.setCustomNameVisible(true);

        AutoBossbar.createBossBar(entity, 20, entity.getCustomName(), BarColor.PURPLE, BarStyle.SEGMENTED_6);

        Bukkit.broadcast(new ComponentBuilder(ChatColor.YELLOW + "A " + newName + ChatColor.YELLOW + " has been engaged!").create());
    }

    private void tick() {
        if (entity.isDead()) {
            Bukkit.getScheduler().cancelTask(thread);
            entity.getWorld().dropItem(entity.getLocation(), SupercaveItems.LEAN);
            return;
        }

        entity.getWorld().spawnParticle(Particle.SPELL_WITCH, entity.getLocation(), 1, 0.5, 0.5, 0.5, 0);

        if (!engaged && entity.getHealth() < entity.getMaxHealth()) {
            engaged = true;
            engage();
        }

        if (!engaged) return;

        if (Math.random() > gamma * decisionBoundary) {
            attack.attack(entity);
            decisionBoundary = delay + Math.random();
            gamma = 1.0;
        }
        gamma *= 0.99;
    }

}
