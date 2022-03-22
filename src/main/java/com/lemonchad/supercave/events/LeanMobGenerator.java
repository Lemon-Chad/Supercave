package com.lemonchad.supercave.events;

import com.lemonchad.supercave.Supercave;
import com.lemonchad.supercave.entities.LeanMob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class LeanMobGenerator<T extends Monster> implements Listener {
    public static final float CHANCE = 500;

    private final Class<T> clazz;
    private final LeanMob.Attack<T> attack;
    private final float delay;

    public LeanMobGenerator(Class<T> clazz, LeanMob.Attack<T> attack, float delay) {
        this.clazz = clazz;
        this.attack = attack;
        this.delay = delay;
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Monster) {
            if (event.getEntityType().getEntityClass() == clazz &&
                    event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL &&
                    Math.random() < 1f / CHANCE) {
                //noinspection unchecked
                new LeanMob<>((T) event.getEntity(), attack, delay);
            }
        }
    }

    public static <T extends Monster> void register(Class<T> clazz, LeanMob.Attack<T> attack) {
        register(clazz, attack, 1);
    }

    public static <T extends Monster> void register(Class<T> clazz, LeanMob.Attack<T> attack, float delay) {
        Bukkit.getPluginManager().registerEvents(new LeanMobGenerator<>(clazz, attack, delay), Supercave.INSTANCE);
    }

}
