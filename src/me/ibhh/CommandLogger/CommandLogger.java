package me.ibhh.CommandLogger;

import me.ibhh.CommandLogger.Tools.Tools;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import me.ibhh.CommandLogger.Tools.IncorrectDatumException;
import me.ibhh.CommandLogger.Tools.LogElement;
import me.ibhh.CommandLogger.Tools.LookupRadiusTooBigException;
import me.ibhh.CommandLogger.Tools.NotActivtedException;
import me.ibhh.CommandLogger.Tools.TooManyElementsException;
import me.ibhh.CommandLogger.extended.config.ExtendedLoggerConfig;
import me.ibhh.CommandLogger.extended.config.SQLLogger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandLogger extends JavaPlugin {

    public float Version = 0;
    public float newversion = 0;
    public static boolean updateaviable = false;
    public static PluginManager pm;
    private CommandPlayerListener playerListener;
    public Utilities plugman;
    public PermissionsChecker permissionsChecker;
    public SQLConnectionHandler SQL;
    public ChatColor Prefix, Text;
    public Metrics metrics;
    public PlayerManager playerManager;
    public Update upd;
    public boolean toggle = true;
    private ExtendedLoggerConfig extendedConfig;
    private SQLLogger sqllogger;
    private HashMap<Player, String> Config = new HashMap<Player, String>();
    private HashMap<Player, String> Set = new HashMap<Player, String>();

    public void install() {
        if (getConfig().getBoolean("installondownload")) {
            Logger("Found Update! Installing now because of 'installondownload = true', please wait!", "Warning");
            playerManager.BroadcastMsg("CommandLogger.update", "Found Update! Installing now because of 'installondownload = true', please wait!");
        }
        if (getConfig().getBoolean("internet")) {
            try {
                String path = "plugins" + File.separator;
                if (upd.download(path)) {
                    Logger("Downloaded new Version!", "Warning");
                } else {
                    Logger(" Cant download new Version!", "Warning");
                }
            } catch (Exception e) {
                Logger("Error on dowloading new Version!", "Error");
                e.printStackTrace();
            }
            playerManager.BroadcastMsg("CommandLogger.update", "Sucessfully downloaded plugin!");
        }
        try {
            plugman.unloadPlugin("CommandLogger");
        } catch (NoSuchFieldException ex) {
            Logger("Error on installing! Please check the log!", "Error");
            playerManager.BroadcastMsg("CommandLogger.update", "Error on installing! Please check the log!");
            java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger("Error on installing! Please check the log!", "Error");
            playerManager.BroadcastMsg("CommandLogger.update", "Error on installing! Please check the log!");
            java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            plugman.loadPlugin("CommandLogger");
        } catch (InvalidPluginException ex) {
            Logger("Error on loading after installing! Please check the log!", "Error");
            playerManager.BroadcastMsg("CommandLogger.update", "Error on loading after installing! Please check the log!");
            java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidDescriptionException ex) {
            Logger("Error on loading after installing! Please check the log!", "Error");
            playerManager.BroadcastMsg("CommandLogger.update", "Error on loading after installing! Please check the log!");
            java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        Logger("Installing finished!", "");
        playerManager.BroadcastMsg("CommandLogger.update", "Installing finished!");
    }

    public ExtendedLoggerConfig getExtendedConfig() {
        if (extendedConfig == null) {
            extendedConfig = new ExtendedLoggerConfig(this);
        }
        return extendedConfig;
    }

    public SQLLogger getSqllogger() {
        if (this.getConfig().getBoolean("liteSQLdata")) {
            if (sqllogger == null) {
                sqllogger = new SQLLogger(this);
            }
        } else {
            try {
                throw new NotActivtedException("Please turn \"liteSQLdata\" int he config.yml file to true");
            } catch (NotActivtedException ex) {
                java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sqllogger;
    }

    /**
     * Compares Version to newVersion
     *
     * @param url from newVersion file + currentVersion
     */
    public void UpdateAvailable(final float currVersion) {
        if (getConfig().getBoolean("internet")) {
            try {
                if (upd.checkUpdate() > currVersion) {
                    updateaviable = true;
                }
            } catch (Exception e) {
                Logger("Error checking for new version! Message: " + e.getMessage(), "Error");
                Logger("May the mainserver is down!", "Error");
            }
        }
    }

    /**
     * Delete an download new version of CommandLogger in the Update folder.
     *
     * @param url
     * @param path
     * @param name
     * @param type
     * @return true if successfully downloaded CommandLogger
     */
    public boolean autoUpdate(final String path) {
        if (getConfig().getBoolean("internet")) {
            try {
                upd.download(path);
            } catch (Exception e) {
                Logger("Error on doing blacklist update! Message: " + e.getMessage(), "Error");
                Logger("may the mainserver is down!", "Error");
            }
        }
        return true;
    }

    /**
     * On disable checks if new version aviable and downloads if activatet
     */
    public void forceUpdate() {
        if (getConfig().getBoolean("internet")) {
            try {
                if (updateaviable) {
                    Logger("New version: " + newversion + " found!", "Warning");
                    Logger("******************************************", "Warning");
                    Logger("*********** Please update!!!! ************", "Warning");
                    Logger("* http://dev.bukkit.org/server-mods/playercommandlogger *", "Warning");
                    Logger("******************************************", "Warning");
                    if (getConfig().getBoolean("autodownload") || getConfig().getBoolean("installondownload")) {
                        if (getConfig().getBoolean("autodownload")) {
                            try {
                                String path = "plugins" + File.separator + "CommandLogger" + File.separator;
                                if (upd.download(path)) {
                                    Logger("Downloaded new Version!", "Warning");
                                } else {
                                    Logger(" Cant download new Version!", "Warning");
                                }
                            } catch (Exception e) {
                                Logger("Error on dowloading new Version!", "Error");
                                e.printStackTrace();
                            }
                        }
                        if (getConfig().getBoolean("installondownload")) {
                            try {
                                String path = "plugins" + File.separator;
                                if (upd.download(path)) {
                                    Logger("Downloaded new Version!", "Warning");
                                    Logger("CommandLogger will be updated on the next restart!", "Warning");
                                } else {
                                    Logger(" Cant download new Version!", "Warning");
                                }
                            } catch (Exception e) {
                                Logger("Error on donwloading new Version!", "Error");
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Logger("Please type [CommandLogger download] to download manual! ", "Warning");
                    }
                }
            } catch (Exception e) {
                Logger("Error on doing update check or update! Message: " + e.getMessage(), "Error");
                Logger("may the mainserver is down!", "Error");
            }
        }

    }

    @Override
    public void onDisable() {
        toggle = true;
        if (this.getConfig().getBoolean("liteSQLdata")) {
            getSqllogger().CloseCon();
        }
        playerListener.getCommandConfig().saveToFile(this.getDataFolder() + File.separator + "config");
        if (getConfig().getBoolean("internet")) {
            forceUpdate();
        }
        if (getConfig().getBoolean("enableingameandsql")) {
            SQL.CloseCon();
        }
        try {
            metrics.disable();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("[CommandLogger] disabled!");
    }

    /**
     * Creates and Updates the config.yml of CommandLogger
     *
     * @return true if created, false if failed
     */
    public boolean UpdateConfig() {
        try {
            getConfig().options().copyDefaults(true);
            saveConfig();
            reloadConfig();
            System.out.println("[CommandLogger] Config file found!");
            if (getConfig().getBoolean("debug")) {
                Logger("Ingame enabled: " + getConfig().getBoolean("enableingameandsql"), "");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Will be executed on enabling
     */
    @Override
    public void onEnable() {
        UpdateConfig();
        try {
            aktuelleVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Prefix = ChatColor.getByChar(getConfig().getString("PrefixColor"));
        Text = ChatColor.getByChar(getConfig().getString("TextColor"));
        upd = new Update(this);
        playerListener = new CommandPlayerListener(this);
        //Init the extended config and load
        getExtendedConfig();
        if (this.getConfig().getBoolean("liteSQLdata")) {
            getSqllogger();
        }
        permissionsChecker = new PermissionsChecker(this, "main");
        plugman = new Utilities(this);
        playerManager = new PlayerManager(this);
        if (getConfig().getBoolean("enableingameandsql")) {
            SQL = new SQLConnectionHandler(this);
            SQL.createConnection();
            SQL.PrepareDB();
            Logger("Prepairing DB!", "");
        }
        System.out.println("[CommandLogger] Version: " + this.Version + " successfully enabled!");
        if (getConfig().getBoolean("internet")) {
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!getConfig().getBoolean("internet")) {
                            return;
                        }
                        Logger("Searching update for CommandLogger!", "Debug");
                        newversion = upd.checkUpdate();
                        if (newversion == -1) {
                            newversion = aktuelleVersion();
                        }
                        Logger("installed CommandLogger version: " + Version + ", latest version: " + newversion, "Debug");
                        if (newversion > Version) {
                            Logger("New version: " + newversion + " found!", "Warning");
                            Logger("******************************************", "Warning");
                            Logger("*********** Please update!!!! ************", "Warning");
                            Logger("* http://dev.bukkit.org/server-mods/playercommandlogger *", "Warning");
                            Logger("******************************************", "Warning");
                            updateaviable = true;
                            if (getConfig().getBoolean("installondownload")) {
                                install();
                            }
                        } else {
                            Logger("No update found!", "Debug");
                        }
                    } catch (Exception e) {
                        Logger("Error on doing update check! Message: " + e.getMessage(), "Error");
                        Logger("may the mainserver is down!", "Error");
                    }
                }
            }, 30, 50000L);
        }
        startStatistics();
        this.getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                toggle = false;
                if (getConfig().getBoolean("liteSQLdata")) {
                    try {
                        getSqllogger().deleteOldEntries(Tools.time(getConfig().getString("CleanUPAfter")));
                    } catch (IncorrectDatumException ex) {
                        java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                        Logger("Please change the config.yml file! There is an error!", "Error");
                    }
                }
            }
        }, 20);
    }

    /**
     * Intern logger to send player messages and log it into file
     *
     * @param msg
     * @param TYPE
     */
    public void Logger(String msg, String TYPE) {
        if (TYPE.equalsIgnoreCase("Warning") || TYPE.equalsIgnoreCase("Error")) {
            System.err.println("[CommandLogger] " + TYPE + ": " + msg);
        } else if (TYPE.equalsIgnoreCase("Debug")) {
            if (getConfig().getBoolean("debug")) {
                System.out.println("[CommandLogger] " + "Debug: " + msg);
            }
        } else {
            System.out.println("[CommandLogger] " + msg);
        }
    }

    /**
     * Intern logger to send player messages and log it into file
     *
     * @param p
     * @param msg
     * @param TYPE
     */
    public void PlayerLogger(Player p, String msg, String TYPE) {
        if (TYPE.equalsIgnoreCase("Error")) {
            if (getConfig().getBoolean("UsePrefix")) {
                p.sendMessage(Prefix + "[CommandLogger] " + ChatColor.RED + "Error: " + Text + msg);
            } else {
                p.sendMessage(ChatColor.RED + "Error: " + Text + msg);
            }
        } else {
            if (getConfig().getBoolean("UsePrefix")) {
                p.sendMessage(Prefix + "[CommandLogger] " + Text + msg);
            } else {
                p.sendMessage(Text + msg);
            }
        }
    }

    /**
     * Sends statistics to http://metrics.griefcraft.com
     */
    private void startStatistics() {
        try {
            metrics = new Metrics(this);
            metrics.enable();
            metrics.start();
        } catch (Exception ex) {
            System.out.println(" [CommandLogger] There was an error while submitting statistics.");
        }
    }

    /**
     * Reads the installed version
     *
     * @return version
     */
    public float aktuelleVersion() {
        try {
            Version = Float.parseFloat(getDescription().getVersion());
        } catch (Exception e) {
            System.out.println("[CommandLogger]Could not parse version in float");
        }
        return Version;
    }

    public void onReload() {
        onEnable();
    }

    protected static boolean isConsole(CommandSender sender) {
        return !(sender instanceof Player);
    }

    /**
     * Commandexecutor of CommandLogger
     *
     * @param sender
     * @param cmd
     * @param label
     * @param args
     * @return false, if invalid command
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((isConsole(sender)) && (cmd.getName().equalsIgnoreCase("CommandLogger")) && (args.length == 1) && (args[0].equals("download"))) {
            try {
                String path = "plugins" + File.separator + "CommandLogger" + File.separator;
                if (upd.download(path)) {
                    Logger("Downloaded new Version!", "Warning");
                } else {
                    Logger(" Cant download new Version!", "Warning");
                }
            } catch (Exception e) {
                Logger("Error on dowloading new Version!", "Error");
                e.printStackTrace();
            }
        }
        if (cmd.getName().equalsIgnoreCase("CommandLogger") || cmd.getName().equalsIgnoreCase("CL")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("internet")) {
                        if (permissionsChecker.checkpermissions(player, "CommandLogger.db")) {
                            getConfig().set("internet", !getConfig().getBoolean("enableingameandsql"));
                            PlayerLogger(player, "enableingameandsql: " + getConfig().getBoolean("enableingameandsql"), "");
                            saveConfig();
                            reloadConfig();
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("reload")) {
                        if (permissionsChecker.checkpermissions(player, "CommandLogger.reload")) {
                            try {
                                PlayerLogger(player, "Please wait: Reloading this plugin!", "Warning");
                                plugman.unloadPlugin("CommandLogger");
                                plugman.loadPlugin("CommandLogger");
                                PlayerLogger(player, "Reloaded!", "");
                            } catch (InvalidPluginException ex) {
                                java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                PlayerLogger(player, "Error on reloading, please check the log!", "Error");
                            } catch (InvalidDescriptionException ex) {
                                java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                PlayerLogger(player, "Error on reloading, please check the log!", "Error");
                            } catch (NoSuchFieldException ex) {
                                java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                PlayerLogger(player, "Error on reloading, please check the log!", "Error");
                            } catch (IllegalAccessException ex) {
                                java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                PlayerLogger(player, "Error on reloading, please check the log!", "Error");
                            }
                        }
                    }
                    if (args[0].equalsIgnoreCase("update")) {
                        if (permissionsChecker.checkpermissions(player, "CommandLogger.update")) {
                            install();
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("deleteplayer")) {
                        if (getConfig().getBoolean("enableingameandsql")) {
                            if (permissionsChecker.checkpermissions(player, "CommandLogger.deleteplayer")) {
                                if (SQL.deletePlayer(args[1])) {
                                    if (SQL.deletePlayersDB(args[1])) {
                                        PlayerLogger(player, "Sucessfully deleted Player: " + args[1] + "from the table!", "");
                                    }
                                } else {
                                    PlayerLogger(player, "Error on deleting Player: " + args[1] + "from the table!", "Error");
                                }
                                return true;
                            }
                        } else {
                            PlayerLogger(player, "You dont use a db!", "Error");
                            return true;
                        }
                    }
                    if (args[0].equalsIgnoreCase("spy")) {
                        if (getConfig().getBoolean("enableingameandsql")) {
                            if (getConfig().getBoolean("debug")) {
                                Logger("Ingame enabled!", "");
                            }
                            if (permissionsChecker.checkpermissions(player, "CommandLogger.spy")) {
                                final String[] args1 = args;
                                final Player player1 = player;
                                this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (SQL.isindb(player1.getName())) {
                                                int temp = SQL.getToggle(player1.getName());
                                                if (temp != -1) {
                                                    if (temp == 1) {
                                                        SQL.UpdateXP(player1.getName(), 0);
                                                        temp = 0;
                                                    } else if (temp == 0) {
                                                        SQL.UpdateXP(player1.getName(), 1);
                                                        SQL.PrepareDBPlayersDB(player1.getName());
                                                        temp = 1;
                                                    } else {
                                                        temp = -2;
                                                    }
                                                }
                                                PlayerLogger(player1, "Sucessfully toggled spy to " + temp + " !", "");
                                            } else {
                                                SQL.InsertAuction(player1.getName(), 1);
                                                PlayerLogger(player1, "Sucessfully toggled spy to true!", "");
                                            }
                                        } catch (SQLException ex) {
                                            java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }, 3);
                                return true;
                            }
                        } else {
                            if (getConfig().getBoolean("debug")) {
                                Logger("Ingame not enabled!", "");
                            }
                            PlayerLogger(player, "You dont use a db!", "Error");
                            return true;
                        }
                    } else if (args[0].equalsIgnoreCase("deletemyconfig")) {
                        if (getConfig().getBoolean("enableingameandsql")) {
                            if (permissionsChecker.checkpermissions(player, "CommandLogger.deletemyconfig")) {
                                final Player player1 = player;
                                this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (SQL.isindb(player1.getName())) {
                                                if (SQL.deletePlayer(player1.getName())) {
                                                    PlayerLogger(player1, "Sucessfully deleted your toggle!", "");
                                                } else {
                                                    PlayerLogger(player1, "Error on deleting toggle!", "Error");
                                                }
                                            }
                                            if (SQL.deletePlayersDB(player1.getName())) {
                                                PlayerLogger(player1, "Sucessfully deleted your db!", "");
                                            } else {
                                                PlayerLogger(player1, "Error on deleting your db!", "Error");
                                            }
                                        } catch (SQLException ex) {
                                            java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }, 3);
                                return true;
                            }
                        } else {
                            PlayerLogger(player, "You dont use a db!", "Error");
                            return true;
                        }
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("show")) {
                        if (getConfig().getBoolean("enableingameandsql")) {
                            if (permissionsChecker.checkpermissions(player, "CommandLogger.show")) {
                                if (args.length == 2) {
                                    final String[] args1 = args;
                                    final Player player1 = player;
                                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String[] temp = SQL.getViewof(args1[1]);
                                                PlayerLogger(player1, "Player " + args1[1] + " watches:", "");
                                                for (String tempstr : temp) {
                                                    PlayerLogger(player1, tempstr, "");
                                                }
                                            } catch (SQLException ex) {
                                                java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }, 3);
                                }
                                return true;

                            }
                        } else {
                            PlayerLogger(player, "You dont use a db!", "Error");
                            return true;
                        }
                    } else if (args[0].equalsIgnoreCase("config")) {
                        if (permissionsChecker.checkpermissions(player, "CommandLogger.config")) {
                            if (args[1].equalsIgnoreCase("confirm")) {
                                if (Config.containsKey(player)) {
                                    String temp = getConfig().getString(Config.get(player));
                                    Logger("Temp: " + temp, "Debug");
                                    boolean isboolean = false;
                                    if (temp.equalsIgnoreCase("true") || temp.equalsIgnoreCase("false")) {
                                        isboolean = true;
                                        Logger("Config is boolean!", "Debug");
                                    }
                                    boolean istTrue = false;
                                    if (isboolean) {
                                        if (Set.get(player).equalsIgnoreCase("true")) {
                                            istTrue = true;
                                            Logger("Config is true!", "Debug");
                                        }
                                    }
                                    if (!isboolean) {
                                        getConfig().set(Config.get(player), Set.get(player));
                                    } else {
                                        getConfig().set(Config.get(player), istTrue);
                                        Logger("Set boolean", "Debug");
                                    }
                                    saveConfig();
                                    reloadConfig();
                                    PlayerLogger(player, "You set  " + Config.get(player) + " from " + temp + " to " + getConfig().getString(Config.get(player)) + " !", "Warning");
                                    Set.remove(player);
                                    Config.remove(player);
                                } else {
                                    PlayerLogger(player, "Please enter a command first!", "Error");
                                }
                                return true;
                            } else if (args[1].equalsIgnoreCase("cancel")) {
                                if (Config.containsKey(player)) {
                                    PlayerLogger(player, "Command canceled!", "Warning");
                                    Set.remove(player);
                                    Config.remove(player);
                                } else {
                                    PlayerLogger(player, "Please enter a command first!", "Error");
                                }
                                return true;
                            }
                        }
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("lookup")) {
                        if (permissionsChecker.checkpermissions(player, "CommandLogger.lookup")) {
                            if (!this.getConfig().getBoolean("liteSQLdata")) {
                                try {
                                    throw new NotActivtedException("Please turn \"liteSQLdata\" int he config.yml file to true");
                                } catch (NotActivtedException ex) {
                                    PlayerLogger(player, ex.getMessage(), "Error");
                                    java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                return true;
                            }
                            if (Tools.isInteger(args[2])) {
                                final Location playerloc = player.getLocation();
                                final int radius = Integer.parseInt(args[2]);
                                if (radius > this.getConfig().getInt("maxLookupRadius")) {
                                    try {
                                        throw new LookupRadiusTooBigException("maxLookupRadius: " + this.getConfig().getInt("maxLookupRadius"));
                                    } catch (LookupRadiusTooBigException ex) {
                                        PlayerLogger(player, ex.getMessage(), "Error");
                                    }
                                }
                                final Player p = player;
                                final String[] argsAsync = args;
                                this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                                    @Override
                                    public void run() {
                                        long time = System.nanoTime();
                                        try {
                                            try {
                                                LogElement[] selection;
                                                try {
                                                    selection = getSqllogger().getLookup(playerloc.getWorld().getName(), Tools.time(argsAsync[1]), getConfig().getInt("maxElementsPerQuery"));
                                                } catch (TooManyElementsException ex) {
                                                    PlayerLogger(p, ex.getMessage(), "Error");
                                                    PlayerLogger(p, "Command executed in " + ((System.nanoTime() - time) / 1000000) + " ms!", "");
                                                    return;
                                                }
                                                LogElement[] newselection = new LogElement[selection.length];
                                                int i = 0;
                                                for (LogElement log : selection) {
                                                    if (log != null) {
                                                        if (playerloc.distance(log.getLocation()) <= radius) {
                                                            newselection[i] = log;
                                                            i++;
                                                        }
                                                    }
                                                }

                                                if (newselection.length > getConfig().getInt("maxElementsPerQuery")) {
                                                    try {
                                                        throw new TooManyElementsException("Too many elements found: " + newselection.length);
                                                    } catch (TooManyElementsException ex) {
                                                        PlayerLogger(p, ex.getMessage(), "Error");
                                                    }
                                                }

                                                for (LogElement log : newselection) {
                                                    if (log != null) {
                                                        PlayerLogger(p, new Date(log.getDate()).toString()
                                                                + " Player: " + log.getName()
                                                                + " Loc: " + log.getLocation().getBlockX() + " " + log.getLocation().getBlockY() + " " + log.getLocation().getBlockZ() + " "
                                                                + log.getMessage(), "");
                                                    }
                                                }
                                            } catch (IncorrectDatumException ex) {
                                                PlayerLogger(p, "You have entered a incorrect time: " + ex.getMessage(), "Error");
                                            }
                                        } catch (SQLException ex) {
                                            PlayerLogger(p, ex.getMessage(), "Error");
                                            java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        PlayerLogger(p, "Command executed in " + ((System.nanoTime() - time) / 1000000) + " ms!", "");
                                    }
                                });
                            } else {
                                PlayerLogger(player, "Please enter the radius as an integer value!", "Error");
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("lookuptofile")) {
                        if (permissionsChecker.checkpermissions(player, "CommandLogger.lookuptofile")) {
                            if (!this.getConfig().getBoolean("liteSQLdata")) {
                                try {
                                    throw new NotActivtedException("Please turn \"liteSQLdata\" int he config.yml file to true");
                                } catch (NotActivtedException ex) {
                                    PlayerLogger(player, ex.getMessage(), "Error");
                                    java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                return true;
                            }
                            if (Tools.isInteger(args[2])) {
                                final Location playerloc = player.getLocation();
                                final int radius = Integer.parseInt(args[2]);
                                final Player p = player;
                                final String[] argsAsync = args;
                                this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                                    @Override
                                    public void run() {
                                        long time = System.nanoTime();
                                        try {
                                            try {
                                                LogElement[] selection;
                                                try {
                                                    selection = getSqllogger().getLookup(playerloc.getWorld().getName(), Tools.time(argsAsync[1]), 0);
                                                } catch (TooManyElementsException ex) {
                                                    PlayerLogger(p, ex.getMessage(), "Error");
                                                    PlayerLogger(p, "Command executed in " + ((System.nanoTime() - time) / 1000000) + " ms!", "");
                                                    return;
                                                }
                                                LogElement[] newselection = new LogElement[selection.length];
                                                int i = 0;
                                                for (LogElement log : selection) {
                                                    if (log != null) {
                                                        if (playerloc.distance(log.getLocation()) <= radius) {
                                                            newselection[i] = log;
                                                            i++;
                                                        }
                                                    }
                                                }
                                                writeLogsSyncToFile(newselection);
                                            } catch (IncorrectDatumException ex) {
                                                PlayerLogger(p, "You have entered a incorrect time: " + ex.getMessage(), "Error");
                                            }
                                        } catch (SQLException ex) {
                                            PlayerLogger(p, ex.getMessage(), "Error");
                                            java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        PlayerLogger(p, "Command executed in " + ((System.nanoTime() - time) / 1000000) + " ms!", "");
                                    }
                                });
                            } else {
                                PlayerLogger(player, "Please enter the radius as an integer value!", "Error");
                            }
                        }
                    }
                    if (args[0].equalsIgnoreCase("lookupPlayer")) {
                        if (permissionsChecker.checkpermissions(player, "CommandLogger.lookupPlayer")) {
                            if (!this.getConfig().getBoolean("liteSQLdata")) {
                                try {
                                    throw new NotActivtedException("Please turn \"liteSQLdata\" int he config.yml file to true");
                                } catch (NotActivtedException ex) {
                                    PlayerLogger(player, ex.getMessage(), "Error");
                                    java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                return true;
                            }
                            final Player p = player;
                            final String[] argsAsync = args;
                            this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                                @Override
                                public void run() {
                                    long time = System.nanoTime();
                                    try {
                                        try {
                                            LogElement[] selection;
                                            try {
                                                selection = getSqllogger().getLookupByName(p.getName(), Tools.time(argsAsync[1]), getConfig().getInt("maxElementsPerQuery"));
                                            } catch (TooManyElementsException ex) {
                                                PlayerLogger(p, ex.getMessage(), "Error");
                                                PlayerLogger(p, "Command executed in " + ((System.nanoTime() - time) / 1000000) + " ms!", "");
                                                return;
                                            }

                                            if (selection.length > getConfig().getInt("maxElementsPerQuery")) {
                                                try {
                                                    throw new TooManyElementsException("Too many elements found: " + selection.length);
                                                } catch (TooManyElementsException ex) {
                                                    PlayerLogger(p, ex.getMessage(), "Error");
                                                }
                                            }
                                            PlayerLogger(p, "Player: " + argsAsync[2], "");
                                            for (LogElement log : selection) {
                                                if (log != null) {
                                                    PlayerLogger(p, new Date(log.getDate()).toString()
                                                            + " Loc: " + log.getLocation().getBlockX() + " " + log.getLocation().getBlockY() + " " + log.getLocation().getBlockZ() + " "
                                                            + log.getMessage(), "");
                                                }
                                            }
                                        } catch (IncorrectDatumException ex) {
                                            PlayerLogger(p, "You have entered a incorrect time: " + ex.getMessage(), "Error");
                                        }
                                    } catch (SQLException ex) {
                                        PlayerLogger(p, ex.getMessage(), "Error");
                                        java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    PlayerLogger(p, "Command executed in " + ((System.nanoTime() - time) / 1000000) + " ms!", "");
                                }
                            });
                        }
                    } else if (args[0].equalsIgnoreCase("lookupPlayertofile")) {
                        if (permissionsChecker.checkpermissions(player, "CommandLogger.lookupPlayertofile")) {
                            if (!this.getConfig().getBoolean("liteSQLdata")) {
                                try {
                                    throw new NotActivtedException("Please turn \"liteSQLdata\" int he config.yml file to true");
                                } catch (NotActivtedException ex) {
                                    PlayerLogger(player, ex.getMessage(), "Error");
                                    java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                return true;
                            }
                            final Player p = player;
                            final String[] argsAsync = args;
                            this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                                @Override
                                public void run() {
                                    long time = System.nanoTime();
                                    try {
                                        try {
                                            LogElement[] selection;
                                            try {
                                                selection = getSqllogger().getLookupByName(p.getName(), Tools.time(argsAsync[1]), 0);
                                            } catch (TooManyElementsException ex) {
                                                PlayerLogger(p, ex.getMessage(), "Error");
                                                PlayerLogger(p, "Command executed in " + ((System.nanoTime() - time) / 1000000) + " ms!", "");
                                                return;
                                            }
                                            writeLogsSyncToFile(selection);
                                        } catch (IncorrectDatumException ex) {
                                            PlayerLogger(p, "You have entered a incorrect time: " + ex.getMessage(), "Error");
                                        }
                                    } catch (SQLException ex) {
                                        PlayerLogger(p, ex.getMessage(), "Error");
                                        java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    PlayerLogger(p, "Command executed in " + ((System.nanoTime() - time) / 1000000) + " ms!", "");
                                }
                            });
                        }
                    } else if (args[0].equalsIgnoreCase("edit")) {
                        if (getConfig().getBoolean("enableingameandsql")) {
                            if (permissionsChecker.checkpermissions(player, "CommandLogger.spy")) {
                                final String[] args1 = args;
                                final Player player1 = player;
                                this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (Tools.isInteger(args1[2])) {
                                                SQL.PrepareDBPlayersDB(player1.getName());
                                                if (SQL.isinplayersdb(player1.getName(), args1[1])) {
                                                    SQL.UpdatePlayersDB(player1.getName(), args1[1], Integer.parseInt(args1[2]));
                                                    PlayerLogger(player1, "Set the toggle from " + args1[1] + " to " + args1[2], "");
                                                } else {
                                                    SQL.InsertintoPlayersDB(player1.getName(), args1[1], Integer.parseInt(args1[2]));
                                                    PlayerLogger(player1, "Set the toggle from " + args1[1] + " to " + args1[2], "");
                                                }
                                            } else {
                                                PlayerLogger(player1, "Enter 0 for deny or 1 for allow commandmessages from " + args1[1], "");
                                            }

                                        } catch (SQLException ex) {
                                            java.util.logging.Logger.getLogger(CommandLogger.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }, 3);
                                return true;
                            }
                        } else {
                            PlayerLogger(player, "You dont use a db!", "Error");
                            return true;
                        }
                    }
                }
                if (args.length >= 3) {
                    if (args[0].equalsIgnoreCase("config")) {
                        if (permissionsChecker.checkpermissions(player, "CommandLogger.config")) {
                            if (!Config.containsKey(player)) {
                                Config.put(player, args[1]);
                                String Configtext = args[2];
                                for (int i = 3; i < args.length; i++) {
                                    Configtext = Configtext.concat(args[i]);
                                }
                                Set.put(player, Configtext);
                                PlayerLogger(player, "Do you want to edit " + args[1] + " from " + getConfig().getString(args[1]) + " to " + Configtext + " ?", "Warning");
                                PlayerLogger(player, "Please confirm within 15 sec!", "Warning");
                                PlayerLogger(player, "Please confirm with \"/cl config confirm\" !", "Warning");
                                PlayerLogger(player, "Please cancel with \"/cl config cancel\" !", "Warning");
                                final Player player1 = player;
                                getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Config.containsKey(player1)) {
                                            Config.remove(player1);
                                            Set.remove(player1);
                                            PlayerLogger(player1, "You havent confirmed within 15 sec!", "Warning");
                                        }
                                    }
                                }, 300);
                                return true;
                            } else {
                                PlayerLogger(player, "Please confirm or cancel your last command first!", "Error");
                                return true;
                            }
                        }
                    }
                }
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("hide")) {
                        if (permissionsChecker.checkpermissions(player, "CommandLogger.setCommandHidden")) {
                            String commandtohide = "";
                            for (int i = 1; i < args.length; i++) {
                                commandtohide = commandtohide.concat(args[i] + " ");
                            }
                            if (playerListener.getCommandConfig().addCommand(commandtohide)) {
                                PlayerLogger(player, "Command \"" + commandtohide + "\" added.", "");
                            } else {
                                PlayerLogger(player, "Command already hidden!", "Error");
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        if (permissionsChecker.checkpermissions(player, "CommandLogger.setCommandHidden")) {
                            String commandtohide = "";
                            for (int i = 1; i < args.length; i++) {
                                commandtohide = commandtohide.concat(args[i] + " ");
                            }
                            if (playerListener.getCommandConfig().removeCommand(commandtohide)) {
                                PlayerLogger(player, "Command \"" + commandtohide + "\" removed.", "");
                            } else {
                                PlayerLogger(player, "Command was not found!", "Error");
                            }
                        }
                    }
                }
            } else {
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("hide")) {
                        String commandtohide = "";
                        for (int i = 1; i < args.length; i++) {
                            commandtohide = commandtohide.concat(args[i] + " ");
                        }
                        if (playerListener.getCommandConfig().addCommand(commandtohide)) {
                            Logger("Command \"" + commandtohide + "\" added.", "");
                        } else {
                            Logger("Command already hidden!", "Error");
                        }
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        String commandtohide = "";
                        for (int i = 1; i < args.length; i++) {
                            commandtohide = commandtohide.concat(args[i] + " ");
                        }
                        if (playerListener.getCommandConfig().removeCommand(commandtohide)) {
                            Logger("Command \"" + commandtohide + "\" removed.", "");
                        } else {
                            Logger("Command was not found!", "Error");
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Writes log into file
     */
    public void writeLog(final LogElement log) {
        this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                if (getConfig().getBoolean("liteSQLdata")) {
                    getSqllogger().Insert(log);
                }
                String Stream = now.toString();
                String path = getDataFolder().toString() + "/";
                File file1 = new File(path + "allfile" + ".txt");
                File file = new File(path + log.getName() + ".txt");
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
                    out.println("[" + Stream + "] in world: "
                            + log.getWorld()
                            + " X: " + (int) log.getLocation().getBlockX()
                            + " Y: " + (int) log.getLocation().getY()
                            + " Z: " + (int) log.getLocation().getZ()
                            + " " + log.getMessage());
                    //Close the output stream
                    out.close();
                } catch (Exception e) {//Catch exception if any
                    System.out.println("Error: " + e.getMessage());
                }
                if (getConfig().getBoolean("allfile")) {
                    try {
                        // Create file
                        FileWriter fstream1 = new FileWriter(file1, true);
                        PrintWriter out1 = new PrintWriter(fstream1);
                        out1.println("[" + Stream + "] "
                                + log.getName()
                                + " in world: " + log.getWorld()
                                + " X: " + (int) log.getLocation().getBlockX()
                                + " Y: " + (int) log.getLocation().getY()
                                + " Z: " + (int) log.getLocation().getZ()
                                + " " + log.getMessage());
                        //Close the output stream
                        out1.close();
                    } catch (Exception e) {//Catch exception if any
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            }
        });
    }

    public void writeLogsSyncToFile(final LogElement[] logelements) {
        String path = getDataFolder().toString() + File.separator + "saves";
        File pathfile = new File(path);
        pathfile.mkdirs();
        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        File file = new File(path + File.separator + ft.format(now) + ".txt");
        // Create file
        try {
            FileWriter fstream1 = new FileWriter(file, true);
            PrintWriter out1 = new PrintWriter(fstream1);
            for (LogElement log : logelements) {
                out1.println("[" + new Date(log.getDate()) + "] "
                        + log.getName()
                        + " in world: " + log.getWorld()
                        + " X: " + log.getLocation().getBlockX()
                        + " Y: " + log.getLocation().getBlockY()
                        + " Z: " + log.getLocation().getBlockZ()
                        + " " + log.getMessage());
            }
            //Close the output stream
            out1.close();
        } catch (Exception e) {//Catch exception if any
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Writes log into file
     *
     * @param Playername
     * @param Command
     * @param world
     * @param X
     * @param Y
     * @param Z
     */
    public void writeLogCommand(final LogElement log) {
        this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                if (getConfig().getBoolean("liteSQLdata")) {
                    getSqllogger().Insert(log);
                }
                String Stream = now.toString();
                String path = getDataFolder().toString() + "/";
                File file1 = new File(path + "allfile" + ".txt");
                File file = new File(path + log.getName() + ".txt");
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
                    out.println(
                            "[" + Stream + "] in world: "
                            + log.getWorld()
                            + " X: " + log.getLocation().getBlockX()
                            + " Y: " + log.getLocation().getBlockY()
                            + " Z: " + log.getLocation().getBlockZ()
                            + " Command: " + log.getMessage());
                    //Close the output stream
                    out.close();
                } catch (Exception e) {//Catch exception if any
                    System.out.println("Error: " + e.getMessage());
                }
                if (getConfig().getBoolean("allfile")) {
                    try {
                        // Create file
                        FileWriter fstream1 = new FileWriter(file1, true);
                        PrintWriter out1 = new PrintWriter(fstream1);
                        out1.println("[" + Stream + "] " + log.getName()
                                + " in world: " + log.getWorld()
                                + " X: " + log.getLocation().getBlockX()
                                + " Y: " + log.getLocation().getBlockY()
                                + " Z: " + log.getLocation().getBlockZ()
                                + " Command: " + log.getMessage());
                        //Close the output stream
                        out1.close();
                    } catch (Exception e) {//Catch exception if any
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            }
        });
    }

    public void dumpintofile(String input) {
        Date now = new Date();
        String Stream = now.toString();
        String path = getDataFolder().toString() + File.separator;
        File file = new File(path + Stream + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        try {
            // Create file
            FileWriter fstream = new FileWriter(file, true);
            PrintWriter out = new PrintWriter(fstream);
            out.println(input);
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.out.println("Error: " + e.getMessage());
        }
    }
}
