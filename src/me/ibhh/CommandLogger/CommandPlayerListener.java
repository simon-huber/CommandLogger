package me.ibhh.CommandLogger;

import java.io.File;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;
import me.ibhh.CommandLogger.Commands.CommandConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CommandPlayerListener implements Listener {

    private final CommandLogger plugin;
    private CommandConfigHandler commandConfig;

    public CommandPlayerListener(CommandLogger plugin) {
        this.plugin = plugin;
        commandConfig = new CommandConfigHandler();
        commandConfig.loadFromFile(plugin.getDataFolder() + File.separator + "config" );
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public CommandConfigHandler getCommandConfig() {
        return commandConfig;
    }

    public static boolean isChest(Block block) {
        return block.getState() instanceof Chest;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void join(PlayerJoinEvent event) {
        if(plugin.toggle){
            return;
        }
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
        if(plugin.toggle){
            return;
        }
        Player p = event.getPlayer();
        String Playername = p.getName();
        String Command = event.getMessage();
        Iterator<String> it = commandConfig.getCommands();
        while(it.hasNext()){
            if(Command.startsWith(it.next())){
                if(plugin.getConfig().getBoolean("debug")){
                    plugin.Logger("Command is hidden", "");
                }
                return;
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
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

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
