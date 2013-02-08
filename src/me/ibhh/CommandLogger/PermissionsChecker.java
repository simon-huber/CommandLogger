package me.ibhh.CommandLogger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsChecker {

    private CommandLogger plugin;
    public int PermPlugin = 0;

    public PermissionsChecker(CommandLogger pl, String von) {
        this.plugin = pl;
        final String von2 = von;

        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    plugin.Logger("checking PermissionsPlugin!", "Debug");
                    searchpermplugin();
                } catch (Exception e) {
                }
            }
        }, 0);
    }

    public void searchpermplugin() {
        if(plugin.getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {
            PermPlugin = 2;
            plugin.Logger("Permissions: Hooked into PermissionsEX!", "Debug");
            return;
        }
        if(plugin.getServer().getPluginManager().isPluginEnabled("GroupManager")) {
            PermPlugin = 3;
            plugin.Logger("Permissions: Hooked into GroupManager!", "Debug");
            return;
        }
        if (plugin.getServer().getPluginManager().isPluginEnabled("bPermissions")) {
            PermPlugin = 4;
            plugin.Logger("Permissions: Hooked into bPermissions!", "Debug");
            return;
        }
        PermPlugin = 1;
    }

    public boolean checkpermissionssilent(Player player, String action) {
        if (plugin.toggle) {
            return false;
        }
        try {
            if (player.isOp()) {
                return true;
            }
            if (PermPlugin == 1) {
                try {
                    return player.hasPermission(action) || player.hasPermission(action.toLowerCase());
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (PermPlugin == 2) {
                if (!Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) {
                    return false;
                }
                try {
                    PermissionUser user = PermissionsEx.getUser(player);;
                    if (user.has(action)) {
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    try {
                        PermissionManager permissions = PermissionsEx.getPermissionManager();
                        if (permissions.has(player, action)) {
                            return true;
                        }
                        return false;
                    } catch (Exception e1) {
                        e.printStackTrace();
                        plugin.Logger("Error on checking Permission with PermissionsEx!", "Error");
                        plugin.Logger("May the /reload command caused this issue!", "Error");
                        plugin.Logger("May your permissions.yml is wrong, please check it!", "Error");
                        plugin.Logger("------------", "Error");
                        plugin.Logger("If you mean this is an error, use /" + plugin.getName() + " report myissueisthis", "Error");
                        plugin.Logger("------------", "Error");
                        return false;
                    }
                }
            } else if (PermPlugin == 3) {
                if (!Bukkit.getPluginManager().isPluginEnabled("GroupManager")) {
                    return false;
                }
                try {
                    return player.hasPermission(action) || player.hasPermission(action.toLowerCase());
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (PermPlugin == 4) {
                if (!Bukkit.getPluginManager().isPluginEnabled("bPermissions")) {
                    return false;
                }
                try {
                    if (player.hasPermission(action) || player.hasPermission(action.toLowerCase())) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                System.out.println("PermissionsEx plugin are not found.");
                return false;
            }
        } catch (Exception e) {
            plugin.Logger("Error on checking permissions!", "Error");
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkpermissions(Player player, String action) {
        if (plugin.toggle) {
            return false;
        }
        try {
            if (player.isOp()) {
                return true;
            }
            if (PermPlugin == 1) {
                try {
                    if (player.hasPermission(action) || player.hasPermission(action.toLowerCase())) {
                        return true;
                    } else {
                        plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                        return false;
                    }
                } catch (Exception e) {
                    plugin.Logger("Error on checking permissions with BukkitPermissions!", "Error");
                    plugin.PlayerLogger(player, "Error on checking permissions with BukkitPermissions!", "Error");
                    e.printStackTrace();
                    return false;
                }
            } else if (PermPlugin == 2) {
                if (!Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) {
                    plugin.PlayerLogger(player, "PermissionsEX is not enabled!", "Error");
                    return false;
                }
                try {
                    PermissionUser user = PermissionsEx.getUser(player);
                    if (user.has(action)) {
                        return true;
                    } else {
                        plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                        return false;
                    }
                } catch (Exception e) {
                    if (plugin.getConfig().getBoolean("debug")) {
                        e.printStackTrace();
                    }
                    try {
                        PermissionManager permissions = PermissionsEx.getPermissionManager();

                        if (permissions.has(player, action)) {
                            return true;
                        } else {
                            plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                            return false;
                        }
                    } catch (Exception e1) {
                        plugin.PlayerLogger(player, "Error on checking Permission with PermissionsEx! Please inform an Admin!", "Error");
                        plugin.Logger("Error on checking Permission with PermissionsEx!", "Error");
                        plugin.Logger("May the /reload command caused this issue!", "Error");
                        plugin.Logger("May your permissions.yml is wrong, please check it!", "Error");
                        plugin.Logger("------------", "Error");
                        plugin.Logger("If you mean this is an error, use /" + plugin.getName() + " report myissueisthis", "Error");
                        plugin.Logger("------------", "Error");
                        e.printStackTrace();
                        return false;
                    }
                }
            } else if (PermPlugin == 3) {
                if (!Bukkit.getPluginManager().isPluginEnabled("GroupManager")) {
                    plugin.PlayerLogger(player, "GroupManager is not enabled!", "Error");
                    return false;
                }
                try {
                    if (player.hasPermission(action) || player.hasPermission(action.toLowerCase())) {
                        return true;
                    } else {
                        plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                        return false;
                    }
                } catch (Exception e) {
                    plugin.Logger("Error on checking permissions with GroupManager!", "Error");
                    plugin.PlayerLogger(player, "Error on checking permissions with GroupManager!", "Error");
                    e.printStackTrace();
                    return false;
                }
            } else if (PermPlugin == 4) {
                if (!Bukkit.getPluginManager().isPluginEnabled("bPermissions")) {
                    plugin.PlayerLogger(player, "bPermissions is not enabled!", "Error");
                    return false;
                }
                try {
                    if (player.hasPermission(action) || player.hasPermission(action.toLowerCase())) {
                        return true;
                    } else {
                        plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                        return false;
                    }
                } catch (Exception e) {
                    plugin.Logger("Error on checking permissions with bPermissions!", "Error");
                    plugin.PlayerLogger(player, "Error on checking permissions with bPermissions!", "Error");
                    e.printStackTrace();
                    return false;
                }
            } else {
                plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
                System.out.println("PermissionsEx plugin are not found.");
                return false;
            }
        } catch (Exception e) {
            plugin.Logger("Error on checking permissions!", "Error");
            plugin.PlayerLogger(player, "Error on checking permissions!", "Error");
            e.printStackTrace();
            return false;
        }
    }
}