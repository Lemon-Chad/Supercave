package com.lemonchad.supercave.events;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.lemonchad.supercave.Supercave;
import com.lemonchad.supercave.items.Lean;
import com.lemonchad.supercave.items.SupercaveItems;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.w3c.dom.Attr;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeanEffects implements Listener {
    public static final float CHANCE = 300;
    public static final UUID MODUUID = UUID.fromString("c8f9f8e0-e9b9-11e9-b210-d663bd873d93");

    public final Map<Player, Integer> leanLevel;
    public int tick;

    public LeanEffects() {
        leanLevel = new HashMap<>();

        tick = 0;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Supercave.INSTANCE, () -> tick++, 0, 1);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster && Math.random() < 1f / CHANCE) {
            // Summon lean on death spot
            World world = event.getEntity().getWorld();
            world.dropItem(event.getEntity().getLocation(), SupercaveItems.LEAN);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getItemMeta().getDisplayName().equals("§5Lean")) {
            drinkLean(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        resetLean(event.getPlayer());
        applyLeanLevel(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        wobble(player);
        if (wearing(player, "lean_boots", 0)) for (int x = 0; x < 3; x++) for (int z = 0; z < 3; z++) {
            Location location = player.getLocation().add(x - 1, -1, z - 1);
            if (location.getBlock().getType() == Material.LAVA) {
                location.getBlock().setType(Material.BASALT);
                location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 5, 0.2, 0.2, 0.2, 0.1);
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
            }
            location.add(0, 1, 0);
            if (location.getBlock().getType() == Material.FIRE || location.getBlock().getType() == Material.SOUL_FIRE) {
                location.getBlock().setType(Material.AIR);
                location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 5, 0.2, 0.2, 0.2, 0.1);
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitEntity() instanceof Player) {
            Player player = (Player) event.getHitEntity();
            if (wearing(player, "lean_chestplate", 2)) {
                event.getEntity().setBounce(true);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && wearing(event.getCurrentItem(), "lean_helmet")) {
            Player p = (Player) event.getWhoClicked();
            fireCrown(p);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (wearing(player, "lean_helmet", 3)) {
                event.getEntity().getLocation().getWorld().strikeLightning(event.getEntity().getLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        if (wearing(player, "lean_leggings", 1)) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        launchPlayer(player);
        player.setAllowFlight(false);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (wearing(player, "lean_leggings", 1) && !player.isOnGround() && !player.isFlying()
                && event.isSneaking() && !player.isGliding()) {
            player.setVelocity(player.getVelocity().setY(-1.5));
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        switch (event.getCause()) {
            case FALL:
                if (wearing(player, "lean_leggings", 1) && player.isSneaking()) {
                    boolean isFire = wearing(player, "lean_boots", 0);
                    event.setCancelled(true);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_DAMAGE, 1f, 0.5f);
                    for (int r = 1; r <= 5; r++) for (int i = 0; i < 360; i += 36) {
                        double angle = Math.toRadians(i + r * 10);
                        double x = Math.cos(angle) * r;
                        double z = Math.sin(angle) * r;
                        Location loc = player.getLocation().clone().add(x, 7f / (8 * r), z);
                        player.getWorld().spawnParticle(isFire ? Particle.SOUL_FIRE_FLAME : Particle.CLOUD, loc, r, r / 25f, r / 25f, r / 25f, 0);
                        if (loc.getBlock().getBlockData() instanceof Ageable) {
                            loc.getBlock().breakNaturally();
                        }
                    }
                    player.getNearbyEntities(5, 5, 5).forEach(entity -> {
                        if (entity instanceof Damageable && entity.getLocation().distance(player.getLocation()) <= 5) {
                            Damageable damageable = (Damageable) entity;
                            if (isFire) {
                                damageable.setFireTicks((int) (event.getDamage() * 10));
                                damageable.damage(event.getDamage() / 5);
                            } else {
                                damageable.damage(event.getDamage());
                            }
                        }
                    });
                }
                break;

            case FIRE:
            case FIRE_TICK:
            case LAVA:
            case HOT_FLOOR:
                if (wearing(player, "lean_boots", 0)) {
                    event.setCancelled(true);
                }
                break;

            case ENTITY_EXPLOSION:
                if (wearing(player, "lean_chestplate", 2)) {
                    event.setCancelled(true);
                    break;
                }
            case LIGHTNING:
            case BLOCK_EXPLOSION:
                if (wearing(player, "lean_helmet", 3)) {
                    event.setCancelled(true);
                }
                break;

            case PROJECTILE:
                if (wearing(player, "lean_chestplate", 2)) {
                    event.setCancelled(true);
                }
                break;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (wearing(item, "lean_helmet") && event.getHand() != null) {
            event.setCancelled(true);
            PlayerInventory inventory = event.getPlayer().getInventory();
            if (inventory.getArmorContents()[3] == null) {
                // Equip helmet
                inventory.setHelmet(item);
                // Remove helmet from inventory
                inventory.setItem(event.getHand(), null);
                fireCrown(event.getPlayer());
            }
        }
    }

    private void launchPlayer(Player player) {
        player.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));
    }

    private void fireCrown(Player p) {
        new BukkitRunnable() {
            Player player = p;
            boolean wasOffline;

            @Override
            public void run() {
                if (!wearing(player, "lean_helmet", 3)) {
                    cancel();
                    return;
                }
                if (player.isOnline()) {
                    if (wasOffline) {
                        player = Bukkit.getPlayer(player.getUniqueId());
                        assert player != null;
                    }
                    for (int i = 0; i < 360; i += 36) {
                        double angle = Math.toRadians(i);
                        double x = Math.cos(angle) * 0.5;
                        double z = Math.sin(angle) * 0.5;
                        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(x, 2, z), 1, 0, 0, 0, 0);
                    }
                    wasOffline = false;
                } else {
                    wasOffline = true;
                }
            }
        }.runTaskTimer(Supercave.INSTANCE, 5, 2);
    }

    private boolean wearing(Player player, String name, int slot) {
        ItemStack itemSlot = player.getInventory().getArmorContents()[slot];
        return wearing(itemSlot, name);
    }

    private boolean wearing(ItemStack itemSlot, String name) {
        if (itemSlot == null) {
            return false;
        }
        if (itemSlot.getItemMeta() == null) {
            return false;
        }
        NamespacedKey key = new NamespacedKey(Supercave.INSTANCE, name);
        return itemSlot.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }

    private void wobble(Player player) {
        float level = initLean(player);
        if (level == 0 || level > 4 || Math.random() < level / 10) {
            return;
        }

        double wobbleIntensity = Math.pow(0.5, level - 1) * 0.25;
        double angle = Math.toRadians(player.getLocation().getYaw()) + Math.PI / 2;

        double[] endpoint1 = { Math.cos(angle + Math.PI / 2) * wobbleIntensity, Math.sin(angle + Math.PI / 2) * wobbleIntensity }; // Right
        double[] endpoint2 = { Math.cos(angle - Math.PI / 2) * wobbleIntensity, Math.sin(angle - Math.PI / 2) * wobbleIntensity }; // Left

        double i = Math.random();
        double[] endpoint = { endpoint1[0] * i + endpoint2[0] * (1 - i), endpoint1[1] * i + endpoint2[1] * (1 - i) };
        if (player.isOnGround()) {
            player.setVelocity(player.getVelocity().add(new Vector(endpoint[0], 0, endpoint[1])));
        }
    }

    private int initLean(Player player) {
        leanLevel.putIfAbsent(player, 0);
        return leanLevel.get(player);
    }

    private void resetLean(Player player) {
        leanLevel.put(player, 0);
    }

    private void addLeanLevel(Player player, int amount) {
        int level = initLean(player);
        leanLevel.put(player, Math.max(0, level + amount));
        leanLevel.get(player);
    }

    @SuppressWarnings("ConstantConditions")
    private void applyLeanLevel(Player player) {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 + leanLevel.get(player));
        for (AttributeModifier modifier : player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getModifiers()) {
            if (modifier.getUniqueId().equals(MODUUID)) {
                player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).removeModifier(modifier);
            }
        }
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).addModifier(new AttributeModifier(MODUUID, "lean", leanLevel.get(player) / 4f, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        player.getAttribute(Attribute.GENERIC_LUCK).setBaseValue(1 + leanLevel.get(player) / 2f);
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1f + 0.005f * leanLevel.get(player));
        player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(1.5f * leanLevel.get(player));
    }

    public void drinkLean(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 60, 1));
        float level = initLean(player);
        if (Math.random() < level / 100) {
            addLeanLevel(player, -1);
            player.sendMessage(ChatColor.RED + "You doubled down on lean.");
        } else if (Math.random() > level / 10) {
            addLeanLevel(player, 1);
            player.sendMessage(ChatColor.GREEN + "You feel a bit leanier.");
        } else {
            player.sendMessage(ChatColor.GRAY + "You're too tolerant to lean. Try again later.");
            return;
        }
        player.sendMessage(ChatColor.DARK_PURPLE + "Your lean level is now " + leanLevel.get(player) + ".");
        applyLeanLevel(player);
        player.setHealth(player.getHealth() * 0.3);
    }

}
