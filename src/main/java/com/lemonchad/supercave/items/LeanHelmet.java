package com.lemonchad.supercave.items;

import com.lemonchad.supercave.Supercave;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.UUID;

public class LeanHelmet extends ItemStack {
    public LeanHelmet() {
        super(Material.SKELETON_SKULL);
        ItemMeta meta = getItemMeta();

        meta.setDisplayName("§5Lean Crown");
        meta.setLore(Arrays.asList(
                "§9\"I am become death, destroyer of worlds.\"",
                "§7Your crown of thorns infuses you with smite.",
                "",
                "§6Leviathan Skull JP3"
        ));

        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", 4.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 5.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));
        meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", .2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD));

        meta.setUnbreakable(true);

        NamespacedKey key = new NamespacedKey(Supercave.INSTANCE, "lean_helmet");
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

        setItemMeta(meta);
    }
}
