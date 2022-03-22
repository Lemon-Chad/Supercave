package com.lemonchad.supercave.entities;

import com.lemonchad.supercave.Supercave;
import com.lemonchad.supercave.items.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LeanZombie {
    private final int thread;
    private final Zombie entity;
    private final BossBar bossBar;
    private final Map<Action, Double> actionChances;
    private double decisionBoundary;
    private double gamma;

    public LeanZombie(Zombie entity) {
        this.entity = entity;
        entity.setMetadata("leanZombie", new MetadataValue() {
            @Override
            public @Nullable Object value() {
                return true;
            }

            @Override
            public int asInt() {
                return 1;
            }

            @Override
            public float asFloat() {
                return 1;
            }

            @Override
            public double asDouble() {
                return 1;
            }

            @Override
            public long asLong() {
                return 1;
            }

            @Override
            public short asShort() {
                return 1;
            }

            @Override
            public byte asByte() {
                return 1;
            }

            @Override
            public boolean asBoolean() {
                return true;
            }

            @Override
            public @NotNull String asString() {
                return "true";
            }

            @Override
            public @Nullable Plugin getOwningPlugin() {
                return Supercave.INSTANCE;
            }

            @Override
            public void invalidate() {

            }
        });

        bossBar = Bukkit.createBossBar("§5§lLean Zombie", BarColor.PURPLE, BarStyle.SOLID);
        bossBar.setVisible(true);
        Supercave.bars.add(bossBar);

        decisionBoundary = 1;
        gamma = 1;

        this.actionChances = new HashMap<>();
        actionChances.put(this::barrier, 50.0);
        actionChances.put(this::fireRing, 100.0);
        actionChances.put(this::bedBarrage, 150.0);
        actionChances.put(this::stealth, 25.0);
        actionChances.put(this::heal, 25.0);

        thread = Bukkit.getScheduler().scheduleSyncRepeatingTask(Supercave.INSTANCE, this::tick, 0, 1);
    }

    public void tick() {
        if (entity == null) {
            cleanup();
            return;
        }
        if (entity.isDead()) {
            ItemStack[] drops = { SupercaveItems.LEAN_HELMET, SupercaveItems.LEAN_CHESTPLATE, SupercaveItems.LEAN_LEGGINGS, SupercaveItems.LEAN_BOOTS };
            entity.getWorld().dropItem(entity.getLocation(), drops[(int) (Math.random() * drops.length)]);
            cleanup();
            return;
        }

        actionChances.forEach((action, chance) -> actionChances.put(action, chance * 2));
        Bukkit.getLogger().info(actionChances.toString());

        bossBar.setProgress(entity.getHealth() / entity.getMaxHealth());
        bossBar.removeAll();
        entity.getNearbyEntities(20, 20, 20).forEach(e -> {
            if (e instanceof Player) {
                bossBar.addPlayer((Player) e);
            }
        });

        gamma *= 0.99;
        if (gamma * decisionBoundary > Math.random()) {
            return;
        }
        randomAction();
        decisionBoundary = Math.random() * 0.75 + entity.getHealth() / entity.getMaxHealth();
        gamma = 1;
    }

    public void cleanup() {
        Bukkit.getScheduler().cancelTask(thread);
        bossBar.removeAll();
        bossBar.setVisible(false);
        Supercave.bars.remove(bossBar);
    }

    interface Action {
        void perform();
    }

    private void randomAction() {
        NavigableMap<Double, Action> actions = new TreeMap<>();
        double total = 0;
        for (Map.Entry<Action, Double> entry : actionChances.entrySet()) {
            total += entry.getValue();
            actions.put(total, entry.getKey());
        }
        Action action = actions.higherEntry(Math.random() * total).getValue();
        action.perform();
        actionChances.put(action, actionChances.get(action) / 50);
    }

    private void barrier() {
        entity.getNearbyEntities(10, 10, 10).forEach(e -> {
            Vector launchVector = entity.getLocation().toVector().subtract(e.getLocation().toVector()).normalize().multiply(-1);
            launchVector.setY(1.5);
            e.setVelocity(launchVector.multiply(0.25));
        });
    }

    private void fireRing() {
        for (int i = 1; i <= 10; i++) {
            int radius = i;
            Bukkit.getScheduler().runTaskLater(Supercave.INSTANCE, () -> {
                for (int j = 0; j < 360; j += 10) {
                    double x = radius * Math.cos(Math.toRadians(j));
                    double z = radius * Math.sin(Math.toRadians(j));
                    entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, entity.getLocation().add(x, 0, z), 10, 0.5, 0.5, 0.5, 0);
                }
                entity.getNearbyEntities(radius, radius, radius).forEach(e -> {
                    if (e.getFireTicks() <= 0 && e.getLocation().getY() - entity.getLocation().getY() < 1 && e.getLocation().toVector().distance(entity.getLocation().toVector()) - radius < 1) {
                        e.setFireTicks(100);
                    }
                });
            }, 10 * i);
        }
    }

    private void bedBarrage() {
        entity.getNearbyEntities(10, 10, 10).forEach(e -> {
            if (!(e instanceof Player)) {
                return;
            }
            Fireball fireball = entity.getWorld().spawn(entity.getLocation(), Fireball.class);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (fireball.isDead()) {
                        cancel();
                        return;
                    }
                    Vector aimVector = e.getLocation().toVector().subtract(fireball.getLocation().toVector()).normalize();
                    fireball.setVelocity(aimVector.multiply(1));
                }
            }.runTaskTimer(Supercave.INSTANCE, 0, 1);
        });
    }

    private void stealth() {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 2, 1));
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 2, 5));

        for (int x = -5; x <= 5; x++) for (int z = -5; z <= 5; z++) {
            Location location = entity.getLocation().add(x, 1, z);
            location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location, 10, 1.5, 1.5, 1.5, 0);
        }

        List<Entity> entities = entity.getNearbyEntities(4, 4, 4);
        entities.forEach(e -> {
            if (e instanceof Player) {
                Player player = (Player) e;
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 3));
            }
        });
        Location newLocation;
        if (entities.isEmpty()) {
            newLocation = entity.getLocation().add(Math.random() * 10 - 5, 0, Math.random() * 10 - 5);
        } else {
            newLocation = entities.get((int) Math.floor(Math.random() * entities.size())).getLocation();
        }
        entity.teleport(newLocation);
    }

    private void heal() {
        Player closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;
        for (Entity e : entity.getNearbyEntities(5, 5, 5)) {
            if (!(e instanceof Player)) {
                continue;
            }
            Player player = (Player) e;
            if (player.getLocation().distance(entity.getLocation()) < closestDistance) {
                closestDistance = player.getLocation().distance(entity.getLocation());
                closestPlayer = player;
            }
        }
        if (closestPlayer == null)
            return;

        for (int i = 0; i < 3 * closestDistance; i++) {
            double t = i / (3 * closestDistance);
            Location lerp = entity.getLocation().multiply(t).add(closestPlayer.getLocation().multiply(1 - t));
            entity.getWorld().spawnParticle(Particle.COMPOSTER, lerp, 1);
        }

        double newHealth = closestPlayer.getHealth() * 0.5;
        if (closestPlayer.getHealth() < 2) {
            newHealth = 2;
        }
        entity.setHealth(entity.getHealth() + newHealth);
        closestPlayer.damage(newHealth);
    }

}
