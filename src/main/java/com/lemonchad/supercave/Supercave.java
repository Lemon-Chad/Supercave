package com.lemonchad.supercave;

import com.lemonchad.supercave.commands.LeanCommands;
import com.lemonchad.supercave.events.*;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Guardian;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public final class Supercave extends JavaPlugin {
    public static Supercave INSTANCE;
    public static final Set<BossBar> bars = new HashSet<>();

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        INSTANCE = this;

        // Events
        Bukkit.getPluginManager().registerEvents(new BedTroll(), this);
        Bukkit.getPluginManager().registerEvents(new LeanEffects(), this);
        Bukkit.getPluginManager().registerEvents(new LeanBoss(), this);
        Bukkit.getPluginManager().registerEvents(new QualityOfLife(), this);

        // Commands
        LeanCommands commands = new LeanCommands();
        getCommand("givelean").setExecutor(commands);
        getCommand("giveleanarmor").setExecutor(commands);
        getCommand("summonleanzombie").setExecutor(commands);

        // Lean Mobs
        LeanMobGenerator.register(Guardian.class, e -> e.setVelocity(e.getVelocity().setY(3)));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        bars.forEach(this::disableBossBar);
    }

    public void disableBossBar(BossBar bar) {
        bars.remove(bar);
        bar.removeAll();
        bar.setVisible(false);
    }
}
