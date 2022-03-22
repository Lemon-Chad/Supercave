package com.lemonchad.supercave.events;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class QualityOfLife implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.SPAWNER) && event.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
            ItemStack item = new ItemStack(Material.SPAWNER);

            CreatureSpawner oldSpawner = (CreatureSpawner) block.getState();
            BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
            CreatureSpawner newSpawner = (CreatureSpawner) meta.getBlockState();
            newSpawner.setSpawnedType(oldSpawner.getSpawnedType());

            block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
            event.getBlock().setType(Material.AIR);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack mainhand = event.getPlayer().getInventory().getItemInMainHand();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null &&
                clickedBlock.getType().equals(Material.SUGAR_CANE) &&
                mainhand.getType().equals(Material.BONE_MEAL)) {
            mainhand.setAmount(mainhand.getAmount() - 1);
            if (mainhand.getAmount() == 0)
                event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            Ageable cane = (Ageable) clickedBlock.getBlockData();
            cane.setAge(cane.getMaximumAge());
            clickedBlock.setBlockData(cane);
            clickedBlock.getWorld().spawnParticle(Particle.COMPOSTER, clickedBlock.getLocation(), 5, 0.5, 0.5, 0.5);
            event.setCancelled(true);
        }
    }
}
