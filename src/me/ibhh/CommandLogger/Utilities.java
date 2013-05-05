/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.CommandLogger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;

/**
 * Thanks to ryanclancy000 for the source :)
 * 
 * @author ryanclancy000
 */
public class Utilities {

	private final CommandLogger plugin;
	//
	public static final ChatColor red = ChatColor.RED;
	public static final ChatColor gray = ChatColor.GRAY;
	public static final ChatColor white = ChatColor.WHITE;
	public static final ChatColor green = ChatColor.GREEN;
	public static final ChatColor yellow = ChatColor.YELLOW;
	public static final String pre = gray + "[" + green + "PlugMan" + gray
			+ "] ";
	private static final String tooMany = red + "Too many arguments!";
	private static final String specifyPlugin = red + "Must specify a plugin!";
	private static final String pluginNotFound = red + "Plugin not found!";

	public Utilities(CommandLogger plugin) {
		this.plugin = plugin;
	}

	private Plugin getPlugin(String plugin) {
		for (Plugin pl : Bukkit.getServer().getPluginManager().getPlugins()) {
			if (pl.getDescription().getName().equalsIgnoreCase(plugin)) {
				return pl;
			}
		}
		return null;
	}

	private String consolidateArgs(String[] args) {
		String pl = args[1];
		if (args.length > 2) {
			for (int i = 2; i < args.length; i++) {
				pl = pl + " " + args[i];
			}
		}
		return pl;
	}

	// List Command
	public void listPlugins(CommandSender sender, String[] args) {

		if (args.length > 1) {
			sender.sendMessage(pre + red + tooMany);
			return;
		}

		StringBuilder list = new StringBuilder();
		List<String> pluginList = new ArrayList<String>();

		for (Plugin pl : Bukkit.getServer().getPluginManager().getPlugins()) {
			String plName = "";
			plName += pl.isEnabled() ? green : red;
			plName += pl.getDescription().getName();
			pluginList.add(plName);
		}
		Collections.sort(pluginList, String.CASE_INSENSITIVE_ORDER);
		for (String plName : pluginList) {
			if (list.length() > 0) {
				list.append(white + ", ");
			}
			list.append(plName);
		}
		sender.sendMessage(pre + gray + "Plugins: " + list);

	}

	// vList Command
	public void vlistPlugins(CommandSender sender, String[] args) {

		if (args.length > 1) {
			sender.sendMessage(pre + red + tooMany);
			return;
		}

		StringBuilder list = new StringBuilder();
		List<String> pluginList = new ArrayList<String>();

		for (Plugin pl : Bukkit.getServer().getPluginManager().getPlugins()) {
			String plName = "";
			plName += pl.isEnabled() ? green : red;
			plName += pl.getDescription().getFullName();
			pluginList.add(plName);
		}
		Collections.sort(pluginList, String.CASE_INSENSITIVE_ORDER);
		for (String plName : pluginList) {
			if (list.length() > 0) {
				list.append(white + ", ");
			}
			list.append(plName);
		}
		sender.sendMessage(pre + gray + "Plugins: " + list);

	}

	// Info Command
	public void pluginInfo(CommandSender sender, String[] args) {

		if (args.length == 1) {
			sender.sendMessage(pre + red + specifyPlugin);
			return;
		}

		Plugin targetPlugin = getPlugin(consolidateArgs(args));

		if (targetPlugin == null) {
			sender.sendMessage(pre + red + pluginNotFound);
			return;
		}
		sender.sendMessage(pre + gray + "Plugin Info: " + green
				+ targetPlugin.getName());
		sender.sendMessage(green + "Version: " + gray
				+ targetPlugin.getDescription().getVersion());
		sender.sendMessage(green + "Authors: " + gray
				+ targetPlugin.getDescription().getAuthors());
		sender.sendMessage(green
				+ "Status: "
				+ (targetPlugin.isEnabled() ? green + "Enabled" : red
						+ "Disabled"));
	}

	// Status Command
	public void pluginStatus(CommandSender sender, String[] args) {

		if (args.length == 1) {
			sender.sendMessage(pre + red + specifyPlugin);
			return;
		}

		Plugin targetPlugin = getPlugin(consolidateArgs(args));

		if (targetPlugin == null) {
			sender.sendMessage(pre + red + pluginNotFound);
			return;
		}

		if (targetPlugin.isEnabled()) {
			sender.sendMessage(pre + green + targetPlugin.getName()
					+ " is enabled!");
		} else {
			sender.sendMessage(pre + green + targetPlugin.getName()
					+ " is disabled!");
		}

	}

	// Usage Command
	@SuppressWarnings("rawtypes")
	public void usageCommand(CommandSender sender, String[] args) {

		if (args.length == 1) {
			sender.sendMessage(pre + red + specifyPlugin);
			return;
		}

		Plugin targetPlugin = getPlugin(consolidateArgs(args));

		if (targetPlugin == null) {
			sender.sendMessage(pre + red + pluginNotFound);
			return;
		}

		ArrayList<String> out = new ArrayList<String>();
		ArrayList<String> parsedCommands = new ArrayList<String>();
		Map commands = targetPlugin.getDescription().getCommands();

		if (commands != null) {
			Iterator commandsIt = commands.entrySet().iterator();
			while (commandsIt.hasNext()) {
				Map.Entry thisEntry = (Map.Entry) commandsIt.next();
				if (thisEntry != null) {
					parsedCommands.add((String) thisEntry.getKey());
				}
			}
		}

		if (!parsedCommands.isEmpty()) {

			StringBuilder commandsOut = new StringBuilder();
			commandsOut.append(pre).append(gray + "Command List: ");

			for (int i = 0; i < parsedCommands.size(); i++) {

				String thisCommand = parsedCommands.get(i);

				if (commandsOut.length() + thisCommand.length() > 55) {
					sender.sendMessage(commandsOut.toString());
					commandsOut = new StringBuilder();
				}

				if (parsedCommands.size() > 0) {
					commandsOut.append(green + "\"").append(thisCommand)
							.append("\" ");
				} else {
					commandsOut.append(green + "\"").append(thisCommand)
							.append("\"");
				}

			}

			out.add(commandsOut.toString());

		} else {
			out.add(pre + red + "Plugin has no registered commands!");
		}

		for (String s : out) {
			sender.sendMessage(s);
		}

	}

	// Test Command
	public void testPerms(CommandSender sender, String[] args) {

		if (args.length == 1) {
			sender.sendMessage(pre + red
					+ "Must specify permission and player!");
			return;
		}

		if (args.length == 2) {
			if (sender.hasPermission(args[1])) {
				sender.sendMessage(pre + green + "You have permission for '"
						+ args[1] + "'.");
			} else {
				sender.sendMessage(pre + red
						+ "You do not have permission for '" + args[1] + "'.");
			}
		}

		if (args.length == 3) {
			Player target = Bukkit.getPlayer(args[2]);
			if (target == null) {
				sender.sendMessage(pre + red + "Player not found!");
			} else {
				if (target.hasPermission(args[1])) {
					sender.sendMessage(pre + green + target.getName()
							+ " has permission for " + args[1]);
				} else {
					sender.sendMessage(pre + red + target.getName()
							+ " does not have permission for " + args[1]);
				}
			}
		}

	}

	// Load Command
	public void loadPlugin(CommandSender sender, String args) {

		Plugin targetPlugin = getPlugin(args);
		File pluginFile = new File(new File("plugins"), args + ".jar");

		if (targetPlugin != null) {
			if (targetPlugin.isEnabled()) {
				sender.sendMessage(red + "Plugin already loaded and enabled!");
				return;
			}
			sender.sendMessage(red + "Plugin already loaded, but is disabled!");
			return;
		}

		if (pluginFile.isFile()) {
			try {
				plugin.getServer().getPluginManager().loadPlugin(pluginFile);
				sender.sendMessage(green + args + " loaded!");
			} catch (UnknownDependencyException e) {
				sender.sendMessage(red
						+ "File exists, but is missing a dependency!");
			} catch (InvalidPluginException e) {
				sender.sendMessage(red
						+ "File exists, but isn't a plugin file!");
			} catch (InvalidDescriptionException e) {
				sender.sendMessage(red
						+ "Plugin exists, but has an invalid description!");
			}
		} else {
			sender.sendMessage(red + "File doesn't exist!");
		}

	}

	// Unload Command
	@SuppressWarnings("unchecked")
	public void unloadPlugin(CommandSender sender, String args)
			throws NoSuchFieldException, IllegalAccessException {

		if (getPlugin(args) == null) {
			sender.sendMessage(red + pluginNotFound);
			return;
		}

		PluginManager pm = Bukkit.getServer().getPluginManager();
		SimplePluginManager spm = (SimplePluginManager) pm;
		SimpleCommandMap cmdMap = null;
		List<Plugin> plugins = null;
		Map<String, Plugin> names = null;
		Map<String, Command> commands = null;
		Map<Event, SortedSet<RegisteredListener>> listeners = null;
		boolean reloadlisteners = true;

		if (spm != null) {
			Field pluginsField = spm.getClass().getDeclaredField("plugins");
			pluginsField.setAccessible(true);
			plugins = (List<Plugin>) pluginsField.get(spm);

			Field lookupNamesField = spm.getClass().getDeclaredField(
					"lookupNames");
			lookupNamesField.setAccessible(true);
			names = (Map<String, Plugin>) lookupNamesField.get(spm);

			try {
				Field listenersField = spm.getClass().getDeclaredField(
						"listeners");
				listenersField.setAccessible(true);
				listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField
						.get(spm);
			} catch (Exception e) {
				reloadlisteners = false;
			}

			Field commandMapField = spm.getClass().getDeclaredField(
					"commandMap");
			commandMapField.setAccessible(true);
			cmdMap = (SimpleCommandMap) commandMapField.get(spm);

			Field knownCommandsField = cmdMap.getClass().getDeclaredField(
					"knownCommands");
			knownCommandsField.setAccessible(true);
			commands = (Map<String, Command>) knownCommandsField.get(cmdMap);
		}

		for (Plugin p : Bukkit.getServer().getPluginManager().getPlugins()) {
			if (p.getDescription().getName().equalsIgnoreCase(args)) {
				pm.disablePlugin(p);
				sender.sendMessage(green + p.getName()
						+ " has been unloaded and disabled!");
				if (plugins != null && plugins.contains(p)) {
					plugins.remove(p);
				}

				if (names != null && names.containsKey(args)) {
					names.remove(args);
				}

				if (listeners != null && reloadlisteners) {
					for (SortedSet<RegisteredListener> set : listeners.values()) {
						for (Iterator<RegisteredListener> it = set.iterator(); it
								.hasNext();) {
							RegisteredListener value = it.next();

							if (value.getPlugin() == p) {
								it.remove();
							}
						}
					}
				}

				if (cmdMap != null) {
					for (Iterator<Map.Entry<String, Command>> it = commands
							.entrySet().iterator(); it.hasNext();) {
						Map.Entry<String, Command> entry = it.next();
						if (entry.getValue() instanceof PluginCommand) {
							PluginCommand c = (PluginCommand) entry.getValue();
							if (c.getPlugin() == p) {
								c.unregister(cmdMap);
								it.remove();
							}
						}
					}
				}
			}
		}
	}

	// Reload Command
	public void reloadPlugin(CommandSender sender, String args) {
		if (args.equalsIgnoreCase("xpShop")) {
			plugin.PlayerLogger((Player) sender,
					"Please wait: Reloading this plugin!", "Warning");
		}
		if ("all".equalsIgnoreCase(args) || "*".equalsIgnoreCase(args)) {
			for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
				Bukkit.getPluginManager().disablePlugin(pl);
				try {
					unloadPlugin(sender, args);
				} catch (NoSuchFieldException ex) {
					Logger.getLogger(Utilities.class.getName()).log(
							Level.SEVERE, null, ex);
				} catch (IllegalAccessException ex) {
					Logger.getLogger(Utilities.class.getName()).log(
							Level.SEVERE, null, ex);
				}
				loadPlugin(sender, args);
				plugin.getServer().getPluginManager().enablePlugin(pl);
			}
			sender.sendMessage(pre + green + "All plugins reloaded!");
			return;
		}
		if (getPlugin(args) == null) {
			sender.sendMessage(pre + red + pluginNotFound);
			return;
		}
		Plugin targetPlugin = getPlugin(args);
		Bukkit.getPluginManager().disablePlugin(targetPlugin);
		try {
			unloadPlugin(sender, args);
		} catch (NoSuchFieldException ex) {
			Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null,
					ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null,
					ex);
		}
		loadPlugin(sender, args);
		plugin.getServer().getPluginManager().enablePlugin(targetPlugin);
		sender.sendMessage(green + targetPlugin.getName() + " Reloaded!");

	}

	// Enable Command
	public void enablePlugin(CommandSender sender, String[] args) {

		if (args.length == 1) {
			sender.sendMessage(pre + red + specifyPlugin);
			return;
		}

		if ("all".equalsIgnoreCase(args[1]) || "*".equalsIgnoreCase(args[1])) {
			for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
				Bukkit.getPluginManager().enablePlugin(pl);
			}
			sender.sendMessage(pre + green + "All plugins enabled!");
			return;
		}

		String pl = consolidateArgs(args);

		if (getPlugin(pl) == null) {
			sender.sendMessage(pre + red + pluginNotFound);
			return;
		}

		if (getPlugin(pl).isEnabled()) {
			sender.sendMessage(pre + red + "Plugin already enabled!");
			return;
		}

		Plugin targetPlugin = getPlugin(pl);
		Bukkit.getPluginManager().enablePlugin(targetPlugin);
		sender.sendMessage(pre + green + targetPlugin.getName() + " Enabled!");

	}

	// Disable Command
	public void disablePlugin(CommandSender sender, String[] args) {

		if (args.length == 1) {
			sender.sendMessage(pre + red + specifyPlugin);
			return;
		}

		if ("all".equalsIgnoreCase(args[1]) || "*".equalsIgnoreCase(args[1])) {
			for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
				Bukkit.getPluginManager().disablePlugin(pl);
			}
			sender.sendMessage(pre + red + "All plugins disabled!");
			return;
		}

		String pl = consolidateArgs(args);

		if (getPlugin(pl) == null) {
			sender.sendMessage(pre + red + pluginNotFound);
			return;
		}

		if (!getPlugin(pl).isEnabled()) {
			sender.sendMessage(pre + red + "Plugin already disabled!");
			return;
		}

		Plugin targetPlugin = getPlugin(pl);
		Bukkit.getPluginManager().disablePlugin(targetPlugin);
		sender.sendMessage(pre + red + targetPlugin.getName() + " Disabled!");

	}

	public void noPerms(CommandSender sender) {
		sender.sendMessage(red
				+ "You do not have permission for that command...");
	}

	/**
	 * Retrieves a precise and short error message returing only where something
	 * went wrong.
	 * 
	 * @param e
	 *            Throwable exception
	 * @return String Name of class where something went wrong.
	 */
	public static String getExceptionMessage(final Throwable e) {
		if (e.getCause() != null) {
			String msg = getExceptionMessage(e.getCause());
			if (!msg.equalsIgnoreCase(e.getClass().getName())) {
				return msg;
			}
		}

		if (e.getLocalizedMessage() != null) {
			return e.getLocalizedMessage();
		} else if (e.getMessage() != null) {
			return e.getMessage();
		} else if (e.getClass().getCanonicalName() != null) {
			return e.getClass().getCanonicalName();
		} else {
			return e.getClass().getName();
		}
	}

	/**
	 * Unloads a plugin by name.
	 * 
	 * @param pluginName
	 *            The name of the plugin to unload.
	 * @throws NoSuchFieldException
	 *             Unknown field for
	 *             plugins/lookupNames/commandMap/knownCommands.
	 * @throws IllegalAccessException
	 *             Unable to access plugin field(s).
	 */
	@SuppressWarnings("unchecked")
	public void unloadPlugin(final String pluginName)
			throws NoSuchFieldException, IllegalAccessException {
		PluginManager manager = plugin.getServer().getPluginManager();
		SimplePluginManager spm = (SimplePluginManager) manager;
		SimpleCommandMap commandMap = null;
		List<Plugin> plugins = null;
		Map<String, Plugin> lookupNames = null;
		Map<String, Command> knownCommands = null;
		Map<Event, SortedSet<RegisteredListener>> listeners = null;
		boolean reloadlisteners = true;

		if (spm != null) {
			Field pluginsField = spm.getClass().getDeclaredField("plugins");
			pluginsField.setAccessible(true);
			plugins = (List<Plugin>) pluginsField.get(spm);

			Field lookupNamesField = spm.getClass().getDeclaredField(
					"lookupNames");
			lookupNamesField.setAccessible(true);
			lookupNames = (Map<String, Plugin>) lookupNamesField.get(spm);

			try {
				Field listenersField = spm.getClass().getDeclaredField(
						"listeners");
				listenersField.setAccessible(true);
				listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField
						.get(spm);
			} catch (Exception e) {
				reloadlisteners = false;
			}

			Field commandMapField = spm.getClass().getDeclaredField(
					"commandMap");
			commandMapField.setAccessible(true);
			commandMap = (SimpleCommandMap) commandMapField.get(spm);

			Field knownCommandsField = commandMap.getClass().getDeclaredField(
					"knownCommands");
			knownCommandsField.setAccessible(true);
			knownCommands = (Map<String, Command>) knownCommandsField
					.get(commandMap);
		}

		for (Plugin pl : plugin.getServer().getPluginManager().getPlugins()) {
			if (pl.getDescription().getName().equalsIgnoreCase(pluginName)) {
				manager.disablePlugin(pl);
				if (plugins != null && plugins.contains(pl)) {
					plugins.remove(pl);
				}

				if (lookupNames != null && lookupNames.containsKey(pluginName)) {
					lookupNames.remove(pluginName);
				}

				if (listeners != null && reloadlisteners) {
					for (SortedSet<RegisteredListener> set : listeners.values()) {
						for (Iterator<RegisteredListener> it = set.iterator(); it
								.hasNext();) {
							RegisteredListener value = it.next();

							if (value.getPlugin() == pl) {
								it.remove();
							}
						}
					}
				}

				if (commandMap != null) {
					for (Iterator<Map.Entry<String, Command>> it = knownCommands
							.entrySet().iterator(); it.hasNext();) {
						Map.Entry<String, Command> entry = it.next();
						if (entry.getValue() instanceof PluginCommand) {
							PluginCommand c = (PluginCommand) entry.getValue();
							if (c.getPlugin() == pl) {
								c.unregister(commandMap);
								it.remove();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Loads a plugin by name.
	 * 
	 * @param pluginName
	 *            The name of the plugin to load.
	 * @throws InvalidPluginException
	 *             Not a plugin.
	 * @throws InvalidDescriptionException
	 *             Invalid description.
	 * @since 1.0.0
	 */
	public void loadPlugin(final String pluginName)
			throws InvalidPluginException, InvalidDescriptionException {
		PluginManager manager = plugin.getServer().getPluginManager();
		Plugin plugin = manager.loadPlugin(new File("plugins", pluginName
				+ ".jar"));

		if (plugin == null) {
			return;
		}

		manager.enablePlugin(plugin);
	}

}