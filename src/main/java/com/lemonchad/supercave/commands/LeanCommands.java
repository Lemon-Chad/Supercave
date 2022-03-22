package com.lemonchad.supercave.commands;

import com.lemonchad.supercave.items.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.lemonchad.supercave.events.LeanBoss.summonLeanZombie;

public class LeanCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        switch (command.getName()) {
            case "givelean": {
                Inventory inventory = player.getInventory();
                inventory.addItem(SupercaveItems.LEAN);
                break;
            }
            case "giveleanarmor": {
                Inventory inventory = player.getInventory();
                inventory.addItem(SupercaveItems.LEAN_HELMET, SupercaveItems.LEAN_CHESTPLATE, SupercaveItems.LEAN_LEGGINGS, SupercaveItems.LEAN_BOOTS);
                break;
            }
            case "summonleanzombie": {
                summonLeanZombie(player.getLocation());
                break;
            }
            default:
                return false;
        }

        return true;
    }
}
