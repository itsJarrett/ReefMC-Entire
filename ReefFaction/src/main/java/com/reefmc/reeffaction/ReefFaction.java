package com.reefmc.reeffaction;

import com.reefmc.reeffaction.listener.PlayerListener;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ReefFaction extends JavaPlugin {

    public Scoreboard sb;
    public HashMap<String, Long> combatTag = new HashMap<>();
    private Objective ob;

    public void onEnable() {
        sb = Bukkit.getScoreboardManager().getNewScoreboard();
        ob = sb.registerNewObjective("showhealth", "health");
        ob.setDisplaySlot(DisplaySlot.BELOW_NAME);
        ob.setDisplayName("§c❤");

        sb.registerNewTeam("Tagged");
        Team tagged = sb.getTeam("Tagged");
        tagged.setPrefix("§c");
        tagged.setSuffix("§r");

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                try {
                    Calendar cal = GregorianCalendar.getInstance();
                    Long checkTime = cal.getTimeInMillis();

                    HashMap<String, Long> combatList = combatTag;
                    for (String player : combatTag.keySet()) {
                        Long lastDamage = combatTag.get(player);
                        if (Bukkit.getPlayer(player) != null) {
                            if (lastDamage < checkTime) {
                                combatList.remove(player);
                                Bukkit.getPlayer(player).sendMessage("§8[§3ReefFaction§8] §aYou have been untagged and can disconnect.");
                                sb.getTeam("Tagged").removePlayer(Bukkit.getOfflinePlayer(player));
                                BarAPI.setMessage(Bukkit.getPlayer(player), "§aYou have been untagged and can disconnect safely.", 5);
                            }
                        }
                    }

                    combatTag = combatList;
                } catch (Exception ex) {

                }
            }
        }, 5l, 5l);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(sb);
            p.setHealth(p.getHealth());
        }
    }

    public boolean isWithinRegion(Player player, String region) {
        return isWithinRegion(player.getLocation(), region);
    }

    public boolean isWithinRegion(Block block, String region) {
        return isWithinRegion(block.getLocation(), region);
    }

    public boolean isWithinRegion(Location loc, String region) {
        WorldGuardPlugin guard = getWorldGuard();
        RegionManager manager = guard.getRegionManager(loc.getWorld());
        ApplicableRegionSet set = manager.getApplicableRegions(loc);
        for (ProtectedRegion each : set) {
            if (each.getId().equalsIgnoreCase(region)) {
                return true;
            }
        }

        return false;
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }
}
