package me.ibhh.CommandLogger.extended.config;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ibhh.CommandLogger.CommandLogger;

/**
 * Class to create and Handle the config of the extended logger option
 *
 * @author ibhh
 */
public class ExtendedLoggerConfig {

    //Saves a reference to the plugin
    private final CommandLogger plugin;
    //Saves a reference to the config
    private YamlConfiguration config;

    public ExtendedLoggerConfig(final CommandLogger plugin) {
        //sets the reference to a private object
        this.plugin = plugin;
        //Create config if not exist
        createConfig();
    }

    /**
     * Returns the current configuration
     *
     * @return YamlConfiguration
     */
    public YamlConfiguration getConfig() {
        return config;
    }

    /**
     * Creates the config and addes defaults
     */
    private void createConfig() {
        File folder = new File(plugin.getDataFolder() + File.separator);
        folder.mkdirs();
        File configl = new File(plugin.getDataFolder() + File.separator + "config" + File.separator + "extendedLoggerConfig.yml");
        if (!configl.exists()) {
            try {
                configl.createNewFile();
            } catch (IOException ex) {
                plugin.Logger("Cannot create new config file!", "Error");
            }
        }
        config = YamlConfiguration.loadConfiguration(configl);
        config.addDefault("enableExtendedLogger", false);
        config.addDefault("PlayerDropItemEvent", false);
        config.addDefault("PlayerPickupItemEvent", false);
        config.addDefault("PlayerDeathEvent", true);
        config.addDefault("PlayerGameModeChangeEvent", true);
        config.addDefault("PlayerKickEvent", true);
        config.addDefault("PlayerLevelChangeEvent", true);
        config.addDefault("PlayerQuitEvent", true);
        config.addDefault("PlayerJoinEvent", true);
        config.addDefault("AsyncPlayerChatEvent", true);
        
        try {
            //Copy predefined values
            config.options().copyDefaults(true);
            //Save changes
            config.save(configl);
        } catch (IOException ex) {
            ex.printStackTrace();
            plugin.Logger("Couldnt save config!", "Error");
        }
        try {
            config = YamlConfiguration.loadConfiguration(configl);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.Logger("Couldnt load config!", "Error");
            plugin.getConfig().set("language", "en");
            plugin.saveConfig();
            plugin.onDisable();
            return;
        }
        plugin.Logger("extendedConfig loaded", "Debug");
    }
}
