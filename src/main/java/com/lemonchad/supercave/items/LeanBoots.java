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

public class LeanBoots extends ItemStack {
    public LeanBoots() {
        super(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();

        meta.setColor(Color.fromRGB(140, 50, 190));
        meta.setDisplayName("§5Lean Grieves");
        meta.setLore(Arrays.asList(
                "§9\"Eyes killer cold and black and bare.\"",
                "§7Fire is no feat for your feet.",
                "",
                "§6Lean Grieves MX9"
        ));

        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", 5.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "generic.armorToughness", 5.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
        meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "generic.knockbackResistance", .2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));

        meta.setUnbreakable(true);

        NamespacedKey key = new NamespacedKey(Supercave.INSTANCE, "lean_boots");
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

        setItemMeta(meta);
    }
}