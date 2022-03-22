package com.lemonchad.supercave.events;

import com.lemonchad.supercave.entities.LeanKing;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class LeanBoss implements Listener {
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!checkStructure(event.getBlock().getLocation())) {
            return;
        }
        event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), 4f, false);
        summonLeanZombie(event.getBlock().getLocation());
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Zombie) {
            Zombie entity = (Zombie) event.getEntity();
            if (entity.hasMetadata("leanZombie")) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                        event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                        event.getCause() == EntityDamageEvent.DamageCause.LAVA ||
                        event.getCause() == EntityDamageEvent.DamageCause.FALL ||
                        event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        if (event.getHitEntity() != null && event.getHitEntity().hasMetadata("leanZombie")) {
            event.setCancelled(true);
            event.getEntity().setVelocity(event.getHitEntity().getLocation().getDirection().multiply(3));
        }
    }

    public static void summonLeanZombie(Location location) {
        Zombie entity = (Zombie) location.getWorld().spawnEntity(location, org.bukkit.entity.EntityType.ZOMBIE);
        entity.canBreakDoors();
        entity.setShouldBurnInDay(false);
        entity.setCustomName("§5§lLean Zombie");
        entity.setCustomNameVisible(true);
        entity.setBaby(false);
        entity.setMaxHealth(80);
        entity.setHealth(80);
        entity.setRemoveWhenFarAway(false);
        location.getWorld().spawnParticle(org.bukkit.Particle.SMOKE_NORMAL, location, 50, 0.5, 0.5, 0.5, 0.1);
        new LeanKing(entity);
    }

    private boolean blockAt(Location location, Material material) {
        Block block = location.getBlock();
        return block.getType() == material;
    }

    private boolean checkStructure(Location location) {
        if (!blockAt(location.clone().add(0, -1, 0), Material.SOUL_SAND))
            return false;

        for (int i = 0; i < 9; i++)
            if (!blockAt(location.clone().add(i % 3 - 1, -2, Math.floor(i / 3f) - 1), Material.PURPLE_WOOL))
                return false;

        if (!blockAt(location.clone().add(-1, -1, -1), Material.SOUL_TORCH))
            return false;
        if (!blockAt(location.clone().add(1, -1, -1), Material.SOUL_TORCH))
            return false;
        if (!blockAt(location.clone().add(-1, -1, 1), Material.SOUL_TORCH))
            return false;
        return blockAt(location.clone().add(1, -1, 1), Material.SOUL_TORCH);
    }
}
