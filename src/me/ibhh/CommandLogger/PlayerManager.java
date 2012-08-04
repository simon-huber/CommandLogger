/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.CommandLogger;

import org.bukkit.entity.Player;

/**
 *
 * @author Simon
 */
public class PlayerManager {

    private CommandLogger plugin;

    public PlayerManager(CommandLogger pl) {
        plugin = pl;
    }

    public int BroadcastMsg(String Permission, String msg) {
        int BroadcastedPlayers = 0;
        if (plugin.toggle) {
            return 0;
        }
        try {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (plugin.permissionsChecker != null) {
                    if (plugin.permissionsChecker.checkpermissionssilent(player, Permission)) {
                        if (plugin.getConfig().getBoolean("UsePrefix")) {
                            player.sendMessage(plugin.Prefix + "[CommandLogger] " + plugin.Text + msg);
                        } else {
                            player.sendMessage(plugin.Text + msg);
                        }
                        BroadcastedPlayers++;
                    }
                }
            }
        } catch (Exception e) {
            plugin.Logger("Error on broadcasting message.", "Error");
        }
        return BroadcastedPlayers;
    }
}
