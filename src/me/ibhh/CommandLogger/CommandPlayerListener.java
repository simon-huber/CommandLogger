package me.ibhh.CommandLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

public class CommandPlayerListener extends PlayerListener {

    private final CommandLogger plugin;
    double doubeline;

    public CommandPlayerListener(CommandLogger plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        String Playername = p.getDisplayName();
        String Command = event.getMessage();
        if ((plugin.getConfig().getBoolean("all")) || (plugin.getConfig().getBoolean(Playername))) {
            writeLog(Playername, Command);
        }
    }

    public void writeLog(String Playername, String Command) {
        Date now = new Date();
        String Stream = now.toString();
        String path = plugin.getDataFolder().toString() + "/";
        File file1 = new File(path + "allfile" + ".txt");
        File file = new File(path + Playername + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        if (!file1.exists()) {
            try {
                file1.createNewFile();
            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        try {
            // Create file
            FileWriter fstream = new FileWriter(file, true);
            PrintWriter out = new PrintWriter(fstream);
            out.println("[" + Stream + "] " + Command);
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.out.println("Error: " + e.getMessage());
        }
        if (plugin.getConfig().getBoolean("allfile")) {
            try {
                // Create file
                FileWriter fstream1 = new FileWriter(file1, true);
                PrintWriter out1 = new PrintWriter(fstream1);
                out1.println("[" + Stream + "] " + Playername + " " + Command);
                //Close the output stream
                out1.close();
            } catch (Exception e) {//Catch exception if any
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}