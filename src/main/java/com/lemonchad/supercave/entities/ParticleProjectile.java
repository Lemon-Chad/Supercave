package com.lemonchad.supercave.entities;

import com.lemonchad.supercave.Supercave;
import com.lemonchad.supercave.events.ParticleProjectileHitEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;

public abstract class ParticleProjectile {
    private @Nullable final Entity shooter;
    private @NotNull Vector velocity;
    private @NotNull Location position;
    private @NotNull final BukkitRunnable task;

    public ParticleProjectile(@Nullable Entity shooter, @NotNull Location position) {
        this.shooter = shooter;
        this.velocity = new Vector();
        this.position = position;
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                ParticleProjectile.this.update();
            }
        };
        this.task.runTaskTimer(Supercave.INSTANCE, 0, 1);
    }

    abstract void onHit(Entity entity);

    abstract void onHit(Block block);

    abstract void render();

    private void update() {
        // Velocity + position
        position.add(velocity);
        velocity.multiply(0.95);
        velocity.setY(velocity.getY() - 0.05);

        // Render
        render();

        // Collisions
        Collection<Entity> entities = position.getWorld().getNearbyEntities(position, 0.5, 0.5, 0.5);
        Block block = position.getBlock();
        if (block.getType() != Material.AIR) {
            // Initiate collision event
            ParticleProjectileHitEvent event = new ParticleProjectileHitEvent(this, block);
            if (!event.isCancelled()) {
                onHit(block);
            }
        } else if (entities.size() > 0) {
            // Get closest entity
            Entity closest = entities.stream().reduce((a, b) -> {
                if (a.getLocation().distance(position) < b.getLocation().distance(position)) {
                    return a;
                }
                return b;
            }).get();
            // Initiate collision event
            ParticleProjectileHitEvent event = new ParticleProjectileHitEvent(this, closest);
            if (!event.isCancelled()) {
                onHit(closest);
            }
        }
    }

    public @Nullable Entity getShooter() {
        return shooter;
    }

    public @NotNull Vector getVelocity() {
        return velocity;
    }

    public @NotNull Location getPosition() {
        return position;
    }

    public void setPosition(@NotNull Location position) {
        this.position = position;
    }

    public void setVelocity(@NotNull Vector velocity) {
        this.velocity = velocity;
    }

    public void kill() {
        task.cancel();
    }
}
