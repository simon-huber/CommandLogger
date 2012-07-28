package me.ibhh.CommandLogger;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class CommandPlayerListener implements Listener {

    private final CommandLogger plugin;
    double doubeline;

    public CommandPlayerListener(CommandLogger plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static boolean isChest(Block block) {
        return block.getState() instanceof Chest;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void join(PlayerJoinEvent event) {
        if (plugin.permissionsChecker.checkpermissionssilent(event.getPlayer(), "CommandLogger.admin")) {
            if (plugin.updateaviable) {
                plugin.PlayerLogger(event.getPlayer(), "installed CommandLogger version: " + plugin.Version + ", latest version: " + plugin.newversion, "Warning");
                plugin.PlayerLogger(event.getPlayer(), "New CommandLogger update aviable: type \"/CommandLogger update\" to install!", "Warning");
                if (!plugin.getConfig().getBoolean("installondownload")) {
                    plugin.PlayerLogger(event.getPlayer(), "Please edit the config.yml if you wish that the plugin updates itself atomatically!", "Warning");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        String Playername = p.getName();
        String Command = event.getMessage();
        if (plugin.getConfig().getBoolean("regenfix")) {
            if (event.getMessage().toLowerCase().startsWith(("//regen"))) {
                if (plugin.getConfig().getBoolean("debug")) {
                    plugin.Logger("Player: " + event.getPlayer().getName() + " command: " + event.getMessage(), "");
                }
                Plugin lwcpl = (LWCPlugin) plugin.getServer().getPluginManager().getPlugin("LWC");
                LWC lwc = null;
                if (lwcpl != null) {
                    lwc = new LWC(null);
                }
                WorldEditPlugin worldEditPlugin = null;
                worldEditPlugin = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
                Selection sel = worldEditPlugin.getSelection(p);
                Location minblock = sel.getMinimumPoint();
                plugin.Logger("minblock == " + minblock, "Debug");
                Location maxblock = sel.getMaximumPoint();
                plugin.Logger("maxblock == " + maxblock, "Debug");
                plugin.Logger("minblock X: " + minblock.getBlockX() + " Y: " + minblock.getBlockY() + " Z: " + minblock.getBlockZ(), "Debug");
                plugin.Logger("maxblock X: " + maxblock.getBlockX() + " Y: " + maxblock.getBlockY() + " Z: " + maxblock.getBlockZ(), "Debug");
                int laenge = sel.getLength();
                plugin.Logger("laenge == " + laenge, "Debug");
                int hohe = sel.getHeight();
                plugin.Logger("hoehe == " + hohe, "Debug");
                int breite = sel.getWidth();
                plugin.Logger("breite == " + breite, "Debug");
                for (int i = 0; i < laenge; i++) {
                    for (int i2 = 0; i2 < breite; i2++) {
                        for (int i3 = 0; i3 < hohe; i3++) {
                            Block aktblock;
                            if (minblock.getZ() < maxblock.getBlockZ()) {
                                if (minblock.getBlockX() < maxblock.getBlockX()) {
                                    aktblock = sel.getWorld().getBlockAt(minblock.getBlockX() + i2, minblock.getBlockY() + i3, minblock.getBlockZ() + i);
                                } else {
                                    aktblock = sel.getWorld().getBlockAt(minblock.getBlockX() - i2, minblock.getBlockY() + i3, minblock.getBlockZ() + i);
                                }
                            } else {
                                if (minblock.getBlockX() < maxblock.getBlockX()) {
                                    aktblock = sel.getWorld().getBlockAt(minblock.getBlockX() + i2, minblock.getBlockY() + i3, minblock.getBlockZ() - i);
                                } else {
                                    aktblock = sel.getWorld().getBlockAt(minblock.getBlockX() - i2, minblock.getBlockY() + i3, minblock.getBlockZ() - i);
                                }
                            }
                            if (aktblock != null) {
                                plugin.Logger(aktblock.getLocation().getBlock() + "", "Debug");
                                if (isChest(aktblock)) {
                                    aktblock.breakNaturally();
                                    Entity[] ent = aktblock.getChunk().getEntities();
                                    for (Entity entity : ent) {
                                        if (entity.getType().equals(EntityType.PLAYER)) {
                                            Player pl = (Player) entity;
                                            pl.kickPlayer("Stopping client crash becaue of //regen");
                                        }
                                    }
//                                    Chest chestblock = (Chest) aktblock.getState();
//                                    Inventory chestinv = chestblock.getInventory();
//                                    chestinv.clear();
//                                    chestblock.update();
//                                    chestblock.setType(org.bukkit.Material.AIR);
//                                    chestblock.update();
//                                    try {
//                                        if (lwc != null) {
//                                            Protection prot = lwc.findProtection(aktblock);
//                                            if (prot != null) {
//                                                plugin.Logger("Protection found", "Debug");
//                                                prot.remove();
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                        plugin.Logger("Exception LWC: " + e.getMessage(), "Error");
//                                    }
                                    plugin.Logger("Chest cleared!", "Debug");
                                } else {
                                    plugin.Logger("Block != Chest", "Debug");
                                }
                            } else {
                                plugin.Logger("Block == null", "Debug");
                            }
                        }
                    }
                }
            }
        }
        if ((plugin.getConfig().getBoolean("all")) || (plugin.getConfig().getBoolean(Playername))) {
            String world = event.getPlayer().getLocation().getWorld().getName();
            int X = event.getPlayer().getLocation().getBlockX();
            int Y = event.getPlayer().getLocation().getBlockY();
            int Z = event.getPlayer().getLocation().getBlockZ();
            plugin.writeLog(Playername, Command, world, X, Y, Z);
            if (plugin.getConfig().getBoolean("showcommandsonconsole")) {
                System.out.println("[CommandLogger] Player: " + Playername + " Command: " + Command);
            }
            if (plugin.getConfig().getBoolean("enableingameandsql")) {
                sendMessagetoop("Player: " + Playername + " Command: " + Command, Playername);
            }
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
