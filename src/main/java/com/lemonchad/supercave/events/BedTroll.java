package com.lemonchad.supercave.events;

import com.lemonchad.supercave.Supercave;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.HashSet;
import java.util.Set;

public class BedTroll implements Listener {
    public static final float CHANCE = 150;
    public static final Set<Player> TROLLED = new HashSet<>();

    private void troll(Block block) {
        block.getWorld().createExplosion(
                block.getLocation(),
                (float) (Math.floor(Math.random() * 20) + 10),
                true
        );
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        double chance = Math.random();
        if (chance < 1 / CHANCE) {
            event.getPlayer().sendMessage(ChatColor.RED + "skill issue.");
            TROLLED.add(event.getPlayer());
            Bukkit.getScheduler().scheduleSyncDelayedTask(Supercave.INSTANCE, () -> {
                troll(event.getBed());
                event.getPlayer().setHealth(0);
            }, (long) (Math.random() * 40 + 20));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (TROLLED.contains(event.getEntity())) {
            TROLLED.remove(event.getEntity());
            event.setDeathMessage("https://knowyourmeme.com/memes/trollface");
        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        if (TROLLED.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        Location[] locations = {
                block.getLocation().add(1, 0, 0),
                block.getLocation().add(-1, 0, 0),
                block.getLocation().add(0, 0, 1),
                block.getLocation().add(0, 0, -1)
        };
        for (Location location : locations) {
            if (location.getBlock().getBlockData() instanceof Bed) {
                troll(location.getBlock());
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Block block = event.getHitBlock();
        // If the projectile is on fire
        if (block != null && block.getBlockData() instanceof Bed && event.getEntity().getFireTicks() > 0) {
            troll(block);
        }
    }

}
