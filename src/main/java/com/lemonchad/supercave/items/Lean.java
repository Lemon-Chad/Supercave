package com.lemonchad.supercave.items;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Arrays;

public class Lean extends ItemStack {
    public Lean() {
        super(Material.POTION);
        PotionMeta meta = (PotionMeta) getItemMeta();
        meta.setColor(Color.fromRGB(140, 50, 190));
        meta.setDisplayName("§5Lean");
        meta.setLore(Arrays.asList(
                "§9Double Down",
                "§7Increases your lean level by 1"
        ));
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        setItemMeta(meta);
    }
}
