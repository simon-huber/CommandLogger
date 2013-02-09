package me.ibhh.CommandLogger.Tools;

import java.util.Date;
import org.bukkit.Location;

/**
 *
 * @author ibhh
 */
public class LogElement {
    
    private String name;
    private String world;
    private Location location;
    private String message;
    private long date;

    public LogElement(String name, String world, String message, Location location) {
        this.name = name;
        this.world = world;
        this.location = location;
        this.message = message;
        this.date = new Date().getTime();
    }

    public LogElement(String name, String world, Location location, String message, long date) {
        this.name = name;
        this.world = world;
        this.location = location;
        this.message = message;
        this.date = date;
    }
    
    

    public Location getLocation() {
        return location;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public long getDate() {
        return date;
    }
    
}
