package me.ibhh.CommandLogger;

import java.io.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        if (plugin.getConfig().getBoolean("all") || plugin.getConfig().getBoolean(Playername)) {
            Date now = new Date();
            String Stream = now.toString();
            String path = plugin.getDataFolder().toString() + "/";
            File file = new File(path + Playername + ".txt");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
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
        }
    }
}