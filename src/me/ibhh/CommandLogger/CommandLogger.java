package me.ibhh.CommandLogger;

import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandLogger extends JavaPlugin {

    public float Version = 0.0F;
    public static PluginManager pm;

    @Override
    public void onDisable() {
        System.out.println("[AnimalShop] disabled!");
    }

    public boolean UpdateConfig() {
        try {
            getConfig().options().copyDefaults(true);
            saveConfig();
            reloadConfig();
            System.out.println("[AnimalShop] Config file found!");

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

        System.out.println("[AnimalShop] Version: " + this.Version
                + " successfully enabled!");

        String URL = "http://ibhh.de:80/aktuelleversionAnimalShop.html";
        if (Update.UpdateAvailable(URL, this.Version)) {
            System.out.println("[AnimalShop] New version: "
                    + Update.getNewVersion(URL) + " found!");
            System.out.println("[AnimalShop] ******************************************");
            System.out.println("[AnimalShop] *********** Please update!!!! ************");
            System.out.println("[AnimalShop] * http://ibhh.de/AnimalShop.jar *");
            System.out.println("[AnimalShop] ******************************************");
            if (getConfig().getBoolean("autodownload")) {
                try {
                    String path = getDataFolder().toString() + "/Update/";
                    Update.autoUpdate("http://ibhh.de/AnimalShop.jar", path,
                            "AnimalShop.jar");
                } catch (Exception e) {
                    System.out.println("[AnimalShop] Error on checking permissions with PermissionsEx!");

                    e.printStackTrace();
                    return;
                }
            } else {
                System.out.println("[AnimalShop] Please type [AnimalShop download] to download manual! ");
            }
        }
    }

    public float aktuelleVersion() {
        try {
            this.Version = Float.parseFloat(getDescription().getVersion());
        } catch (Exception e) {
            System.out.println("[AnimalShop]Could not parse version in float");
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
        if ((sender instanceof Player)) {
            File file = new File(getDataFolder().toString() + sender.getName());

            if (file.exists()) {
            } else {
            }
        } else if ((isConsole(sender))
                && (cmd.getName().equalsIgnoreCase("AnimalShop"))
                && (args.length == 1)
                && (args[0].equals("download"))) {
            String path = getDataFolder().toString()
                    + "/Update/";
            Update.autoUpdate(
                    "http://ibhh.de/AnimalShop.jar", path,
                    "AnimalShop.jar");
        }
        return false;
    }
}