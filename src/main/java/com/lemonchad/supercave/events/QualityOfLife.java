package com.lemonchad.supercave.events;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class QualityOfLife implements Listener {


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack mainhand = event.getPlayer().getInventory().getItemInMainHand();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null &&
                clickedBlock.getType().equals(Material.SUGAR_CANE) &&
                mainhand.getType().equals(Material.BONE_MEAL) &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            // Get top of sugar cane
            Block nextBlock = clickedBlock.getLocation().clone().add(0, 1, 0).getBlock();
            while (nextBlock.getType().equals(Material.SUGAR_CANE)) {
                clickedBlock = nextBlock;
                nextBlock = clickedBlock.getLocation().clone().add(0, 1, 0).getBlock();
            }
            // If there is no growing space, do nothing
            if (!nextBlock.getType().equals(Material.AIR)) {
                return;
            }

            // Consume bonemeal
            mainhand.setAmount(mainhand.getAmount() - 1);

            Ageable ageable = (Ageable) clickedBlock.getBlockData();
            // Get threshold for random number based on age
            int age = ageable.getAge();
            double threshold = 1 - age / 15f;

            // If random number is greater than threshold, grow
            if (Math.random() > threshold) {
                nextBlock.setType(Material.SUGAR_CANE);
            } else {
                // Otherwise, add age
                ageable.setAge(age + 1);
                clickedBlock.setBlockData(ageable);
            }

            // Spawn particles
            clickedBlock.getWorld().spawnParticle(Particle.COMPOSTER, clickedBlock.getLocation(), (int) (Math.random() * 5 + 5), 0.5, 0.5, 0.5, 0);
        }
    }

    @EventHandler
    public void onParticleProjectileHit(ParticleProjectileHitEvent event) {
        Entity hitEntity = event.getHitEntity();
        if (hitEntity instanceof Player) {
            Player player = (Player) hitEntity;
            if (player.isBlocking()) {
                event.setCancelled(true);
                event.getProjectile().kill();
                player.getLocation().getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
            }
        }
    }
}
