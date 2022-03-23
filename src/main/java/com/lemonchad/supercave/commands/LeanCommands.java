package com.lemonchad.supercave.commands;

import com.lemonchad.supercave.Supercave;
import com.lemonchad.supercave.entities.LeanMob;
import com.lemonchad.supercave.items.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static com.lemonchad.supercave.events.LeanBoss.summonLeanZombie;

public class LeanCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        switch (command.getName()) {
            case "givelean": {
                Inventory inventory = player.getInventory();
                inventory.addItem(SupercaveItems.LEAN);
                break;
            }
            case "giveleanarmor": {
                Inventory inventory = player.getInventory();
                inventory.addItem(SupercaveItems.LEAN_HELMET, SupercaveItems.LEAN_CHESTPLATE, SupercaveItems.LEAN_LEGGINGS, SupercaveItems.LEAN_BOOTS);
                break;
            }
            case "summonleanking": {
                summonLeanZombie(player.getLocation());
                break;
            }
            case "summonleanzombie": {
                Zombie zombie = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
                new LeanMob<>(zombie, e -> {
                    e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ZOMBIE_HURT, 1, 0.5f);
                    e.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, e.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0.1);

                    for (int r = 1; r <= 10; r++) {
                        int radius = r;
                        //noinspection DuplicatedCode
                        Bukkit.getScheduler().runTaskLater(Supercave.INSTANCE, () -> {
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
            }
            default:
                return false;
        }

        return true;
    }
}
