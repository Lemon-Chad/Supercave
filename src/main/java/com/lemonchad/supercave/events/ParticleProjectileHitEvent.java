package com.lemonchad.supercave.events;

import com.lemonchad.supercave.entities.ParticleProjectile;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ParticleProjectileHitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private @Nullable final Entity sender;
    private @NotNull  final ParticleProjectile projectile;
    private @Nullable final Entity hitEntity;
    private @Nullable final Block hitBlock;

    public ParticleProjectileHitEvent(@NotNull ParticleProjectile projectile, @Nullable Entity hitEntity) {
        this.projectile = projectile;
        this.sender = projectile.getShooter();
        this.hitEntity = hitEntity;
        this.hitBlock = null;
    }

    public ParticleProjectileHitEvent(@NotNull ParticleProjectile projectile, @Nullable Block hitBlock) {
        this.projectile = projectile;
        this.sender = projectile.getShooter();
        this.hitEntity = null;
        this.hitBlock = hitBlock;
    }

    public void call() {
        Bukkit.getServer().getPluginManager().callEvent(this);
    }

    public @NotNull ParticleProjectile getProjectile() {
        return projectile;
    }

    public @Nullable Entity getHitEntity() {
        return hitEntity;
    }

    public @Nullable Block getHitBlock() {
        return hitBlock;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
