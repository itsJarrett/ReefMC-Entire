package com.reefmc.reefskywars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ReefSkyWars extends JavaPlugin implements Listener {

    Location spawn;

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        spawn = new Location(Bukkit.getWorld("world"), 0.5, 15, -0.5);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(spawn);
    }
}
