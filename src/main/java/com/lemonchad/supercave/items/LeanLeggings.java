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

public class LeanLeggings extends ItemStack {
    public LeanLeggings() {
        super(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();

        meta.setColor(Color.fromRGB(140, 50, 190));
        meta.setDisplayName("§5Lean Leggings");
        meta.setLore(Arrays.asList(
                "§9\"It's hard to recognize the devil when his",
                "§9hand is on your shoulder.\"",
                "§7Unparalleled mobility is revealed to you.",
                "",
                "§6Lean Leggings P8Z"
        ));

        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", 8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 5.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
        meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", .2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.movementSpeed", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));

        meta.setUnbreakable(true);

        NamespacedKey key = new NamespacedKey(Supercave.INSTANCE, "lean_leggings");
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

        setItemMeta(meta);
    }
}
