package com.lemonchad.supercave.entities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CobwebProjectile extends ParticleProjectile{
    public CobwebProjectile(@Nullable Entity shooter, @NotNull Location position) {
        super(shooter, position);
    }

    @Override
    void onHit(Entity entity) {
        if (entity instanceof Spider)
            return;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.setVelocity(livingEntity.getVelocity().multiply(0.5));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 2, 3));
            livingEntity.damage(1);
            livingEntity.setVelocity(new Vector());
            kill();
        }
    }

    @Override
    void onHit(Block block) {
        if (block.getType() != Material.COBWEB) {
            if (getPreviousPosition().getBlock().getType() == Material.AIR && Math.random() < 0.25) {
                getPreviousPosition().getBlock().setType(Material.COBWEB);
            }
            kill();
        }
    }

    @Override
    void render() {
        Location location = getPosition();
        location.getWorld().spawnParticle(Particle.CLOUD, location, 5, 0.1, 0.1, 0.1, 0);
    }
}
