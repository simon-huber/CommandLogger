package me.ibhh.CommandLogger;

import org.bukkit.entity.Player;

public class PermissionsChecker {

    private CommandLogger plugin;

    public PermissionsChecker(CommandLogger pl, String von) {
        this.plugin = pl;
    }

    public boolean checkpermissionssilent(Player player, String action) {
        if (plugin.toggle) {
            return false;
        }
        if (player.isOp()) {
            return true;
        }
        try {
            if (player.hasPermission(action) || player.hasPermission(action.toLowerCase())) {
                return true;
            } else {
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
        if (player.isOp()) {
            return true;
        }
        try {
            if (player.hasPermission(action) || player.hasPermission(action.toLowerCase())) {
                return true;
            } else {
                plugin.PlayerLogger(player, player.getName() + " " + plugin.getConfig().getString("permissions.error." + plugin.getConfig().getString("language")) + " (" + action + ")", "Error");
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