package me.ibhh.CommandLogger;

import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPlayerListener implements Listener {

    private final CommandLogger plugin;
    double doubeline;

    public CommandPlayerListener(CommandLogger plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        String Playername = p.getName();
        String Command = event.getMessage();
        if ((plugin.getConfig().getBoolean("all")) || (plugin.getConfig().getBoolean(Playername))) {
            String world = event.getPlayer().getLocation().getWorld().getName();
            int X = event.getPlayer().getLocation().getBlockX();
            int Y = event.getPlayer().getLocation().getBlockY();
            int Z = event.getPlayer().getLocation().getBlockZ();
            plugin.writeLog(Playername, Command, world, X, Y, Z);
            if (plugin.getConfig().getBoolean("showcommandsonconsole")) {
                System.out.println("[CommandLogger] Player: " + Playername + " Command: " + Command);
            }
            sendMessagetoop("Player: " + Playername + " Command: " + Command, Playername);
        }
    }

    public void sendMessagetoop(final String msg, final String playername) {
        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (plugin.permissionsChecker.checkpermissionssilent(player, "CommandLogger.spy")) {
                        try {
                            if (plugin.SQL.isindb(player.getName())) {
                                int temp = plugin.SQL.getToggle(player.getName());
                                if (temp == 1) {
                                    if (plugin.SQL.isinplayersdb(player.getName(), playername)) {
                                        if (plugin.SQL.getToggleDB(player.getName(), playername) == 1) {
                                            plugin.PlayerLogger(player, msg, "");
                                        }
                                    } else {
                                        if (plugin.SQL.getToggleDB(player.getName(), "all") == 1) {
                                            plugin.PlayerLogger(player, msg, "");
                                        }
                                    }
                                }
                            } else {
                                plugin.SQL.InsertAuction(player.getName(), 0);
                                plugin.SQL.PrepareDBPlayersDB(player.getName());
                                int temp = plugin.SQL.getToggle(player.getName());
                                if (temp == 1) {
                                    plugin.PlayerLogger(player, msg, "");
                                }
                            }
                        } catch (SQLException ex) {
                            java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }, 0);
    }
}
