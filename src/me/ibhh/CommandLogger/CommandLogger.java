package me.ibhh.CommandLogger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandLogger extends JavaPlugin {

    public float Version = 0.0F;
    public static PluginManager pm;
    private CommandPlayerListener playerListener;

    @Override
    public void onDisable() {
        System.out.println("[CommandLogger] disabled!");
    }

    public boolean UpdateConfig() {
        try {
            getConfig().options().copyDefaults(true);
            saveConfig();
            reloadConfig();
            System.out.println("[CommandLogger] Config file found!");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onEnable() {
        UpdateConfig();
        try {
            aktuelleVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.playerListener = new CommandPlayerListener(this);
        PluginManager pm1 = getServer().getPluginManager();
        pm1.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Normal, this);
        System.out.println("[CommandLogger] Version: " + this.Version
                + " successfully enabled!");

        String URL = "http://ibhh.de:80/aktuelleversionCommandLogger.html";
        if (Update.UpdateAvailable(URL, this.Version)) {
            System.out.println("[CommandLogger] New version: "
                    + Update.getNewVersion(URL) + " found!");
            System.out.println("[CommandLogger] ******************************************");
            System.out.println("[CommandLogger] *********** Please update!!!! ************");
            System.out.println("[CommandLogger] * http://ibhh.de/CommandLogger.jar *");
            System.out.println("[CommandLogger] ******************************************");
            if (getConfig().getBoolean("autodownload")) {
                try {
                    String path = getDataFolder().toString() + "/Update/";
                    Update.autoUpdate("http://ibhh.de/CommandLogger.jar", path,
                            "CommandLogger.jar");
                } catch (Exception e) {
                    System.out.println("[CommandLogger] Error on checking permissions with PermissionsEx!");

                    e.printStackTrace();
                }
            } else {
                System.out.println("[CommandLogger] Please type [CommandLogger download] to download manual! ");
            }
        }
    }

    public float aktuelleVersion() {
        try {
            this.Version = Float.parseFloat(getDescription().getVersion());
        } catch (Exception e) {
            System.out.println("[CommandLogger]Could not parse version in float");
        }
        return this.Version;
    }

    public void onReload() {
        onEnable();
    }

    protected static boolean isConsole(CommandSender sender) {
        return !(sender instanceof Player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((isConsole(sender))
                && (cmd.getName().equalsIgnoreCase("CommandLogger"))
                && (args.length == 1)
                && (args[0].equals("download"))) {
            String path = getDataFolder().toString()
                    + "/Update/";
            Update.autoUpdate(
                    "http://ibhh.de/CommandLogger.jar", path,
                    "CommandLogger.jar");
        }
        return false;
    }
}