package com.reefmc.reeffaction.listener;

import com.reefmc.reeffaction.ReefFaction;
import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class PlayerListener implements Listener {
    private ReefFaction plugin;

    public PlayerListener(ReefFaction instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDamageScoreboard(EntityDamageEvent event) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(plugin.sb);
            p.setHealth(p.getHealth());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Player) {
                Player d = (Player) event.getDamager();

                if (!plugin.combatTag.containsKey(d.getName())) {
                    d.sendMessage("§8[§3ReefFaction§8] §cYou have been tagged! You will §ldie §r§cif you disconnect within 15 seconds.");
                    BarAPI.setMessage(d, "§c§lYou have been tagged! If you quit, you will die.");
                }

                Calendar cal = GregorianCalendar.getInstance();
                cal.add(Calendar.SECOND, 15);
                Long checkTime = cal.getTimeInMillis();

                plugin.combatTag.put(d.getName(), checkTime);
                plugin.sb.getTeam("Tagged").addPlayer(Bukkit.getOfflinePlayer(d.getUniqueId()));

                Player p = (Player) event.getEntity();

                if (!plugin.combatTag.containsKey(p.getName())) {
                    BarAPI.setMessage(p, "§c§lYou have been tagged! If you quit, you will die.");
                    p.sendMessage("§8[§3ReefFaction§8] §cYou have been tagged! You will §ldie §r§cif you disconnect within 15 seconds.");
                }

                cal = GregorianCalendar.getInstance();
                cal.add(Calendar.SECOND, 15);
                checkTime = cal.getTimeInMillis();

                plugin.combatTag.put(p.getName(), checkTime);
                plugin.sb.getTeam("Tagged").addPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player p = event.getPlayer();

        if (plugin.combatTag.containsKey(p.getName())) {
            p.sendMessage("§8[§3ReefFaction§8] §cYou cannot teleport while you are tagged!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player p = event.getPlayer();

        if (event.getMessage().startsWith("/") && plugin.combatTag.containsKey(p.getName())) {
            p.sendMessage("§8[§3ReefFaction§8] §cYou cannot run commands while you are tagged!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDeath(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        if (plugin.combatTag.containsKey(p.getName())) {
            Bukkit.broadcastMessage("§8[§3ReefFaction§8] §6" + event.getPlayer().getName() + " combat logged and was killed!");
            event.getPlayer().setHealth(0D);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().setScoreboard(plugin.sb);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPickup(PlayerPickupItemEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onXp(PlayerExpChangeEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            event.setAmount(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getGameMode() == GameMode.CREATIVE) {
            event.getDrops().clear();
        }

        event.getEntity().getLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());
    }
}
