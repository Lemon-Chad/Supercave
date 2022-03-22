package com.lemonchad.supercave.items;

import com.lemonchad.supercave.Supercave;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.UUID;

public class LeanChestplate extends ItemStack {
    public LeanChestplate() {
        super(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();

        meta.setColor(Color.fromRGB(140, 50, 190));
        meta.setDisplayName("§5Lean Breastplate");
        meta.setLore(Arrays.asList(
                "§9\"You've gone pale.\"",
                "§7Projectiles have no effect on you.",
                "",
                "§6Lean Breastplate XO3"
        ));

        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", 10.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 5.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", .2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));

        meta.setUnbreakable(true);

        NamespacedKey key = new NamespacedKey(Supercave.INSTANCE, "lean_chestplate");
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

        setItemMeta(meta);
    }
}
