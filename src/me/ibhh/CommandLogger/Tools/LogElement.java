package me.ibhh.CommandLogger.Tools;

import java.util.Date;
import java.util.UUID;

import org.bukkit.Location;

/**
 * @author ibhh
 */
public class LogElement
{

	private UUID uuid;
	private String servername;
	private String world;
	private Location location;
	private String message;
	private long date;

	public LogElement(UUID uuid, String servername, String world, String message, Location location)
	{
		this.uuid = uuid;
		this.servername = servername;
		this.world = world;
		this.location = location;
		this.message = message;
		this.date = new Date().getTime();
	}

	public LogElement(UUID uuid, String servername, String world, Location location, String message, long date)
	{
		this.uuid = uuid;
		this.servername = servername;
		this.world = world;
		this.location = location;
		this.message = message;
		this.date = date;
	}

	public Location getLocation()
	{
		return location;
	}

	public String getMessage()
	{
		return message;
	}

	public String getServername()
	{
		return servername;
	}

	public UUID getUuid()
	{
		return uuid;
	}

	public String getWorld()
	{
		return world;
	}

	public long getDate()
	{
		return date;
	}

}
