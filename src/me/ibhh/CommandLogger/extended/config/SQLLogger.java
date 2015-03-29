package me.ibhh.CommandLogger.extended.config;

/**
 *
 */
import me.ibhh.CommandLogger.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

import me.ibhh.CommandLogger.Tools.LogElement;
import me.ibhh.CommandLogger.Tools.TooManyElementsException;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * @author ibhh
 */
public class SQLLogger
{

	private Connection cn;
	private CommandLogger plugin;

	public SQLLogger(CommandLogger AuctTrade)
	{
		plugin = AuctTrade;
		cn = createConnection();
		PrepareDB();
	}

	public boolean deleteDB()
	{
		Statement st;
		boolean temp = false;
		try
		{
			st = cn.createStatement();
			st.executeUpdate("drop table if exists CommandLoggerExtended;");
			temp = true;
			cn.commit();
			st.close();
		}
		catch(SQLException e)
		{
			plugin.Logger("[CommandLogger]: Error while creating tables! - " + e.getMessage(), "Error");
			SQLErrorHandler(e);
		}
		return temp;
	}

	public boolean deleteOldEntries(long time)
	{
		Statement st;
		boolean temp = false;
		try
		{
			st = cn.createStatement();
			st.executeUpdate("DELETE FROM CommandLoggerExtended WHERE date < " + time + " AND servername='" + Bukkit.getServerName() + "';");
			temp = true;
			cn.commit();
			st.close();
		}
		catch(SQLException e)
		{
			plugin.Logger("[CommandLogger]: Error while deleting old entries! - " + e.getMessage(), "Error");
			SQLErrorHandler(e);
		}
		return temp;
	}

	public void PrepareDB()
	{
		if(plugin.getConfig().getBoolean("mysql"))
		{
			Statement st;
			try
			{
				st = cn.createStatement();
				st.executeUpdate("CREATE TABLE IF NOT EXISTS CommandLoggerExtended (ID INTEGER PRIMARY KEY AUTO_INCREMENT, date LONG, uuid VARCHAR(100), servername VARCHAR(30), world VARCHAR(30), X INT, Y INT, Z INT, message VARCHAR(300))");
				cn.commit();
				st.close();
			}
			catch(SQLException e)
			{
				plugin.Logger("[CommandLogger]: Error while creating tables! - " + e.getMessage(), "Error");
				SQLErrorHandler(e);
			}
		}
		else
		{
			Statement st;
			try
			{
				st = cn.createStatement();
				st.executeUpdate("CREATE TABLE IF NOT EXISTS CommandLoggerExtended ('ID' INTEGER PRIMARY KEY AUTOINCREMENT, 'date' LONG, 'uuid' VARCHAR, 'servername' VARCHAR, 'world' VARCHAR, 'X' INTEGER, 'Y' INTEGER, 'Z' INTEGER, 'message' VARCHAR)");
				cn.commit();
				st.close();
			}
			catch(SQLException e)
			{
				plugin.Logger("[CommandLogger]: Error while creating tables! - " + e.getMessage(), "Error");
				SQLErrorHandler(e);
			}
		}
	}

	public LogElement[] getLookupByUUID(UUID uuid, long time, int maxcount) throws SQLException, TooManyElementsException
	{
		Statement st = null;
		String sql;
		ResultSet result;
		try
		{
			st = cn.createStatement();
		}
		catch(SQLException e)
		{
			SQLErrorHandler(e);
		}
		sql = "SELECT * FROM CommandLoggerExtended" + " WHERE date > " + (new java.util.Date().getTime() - time) + " AND uuid = '" + uuid + "' AND servername='" + Bukkit.getServerName() + "' LIMIT " + maxcount + ";";
		result = st.executeQuery(sql);
		ArrayList<LogElement> ResultArrayList = new ArrayList<LogElement>();
		while(result.next() == true)
		{
			ResultArrayList.add(new LogElement(UUID.fromString(result.getString("uuid")), result.getString("servername"), result.getString("world"), new Location(Bukkit.getWorld(result.getString("world")), result.getInt("X"), result.getInt("Y"), result.getInt("Z")), result.getString("message"), result.getLong("date")));
		}
		st.close();
		result.close();
		LogElement[] Result = new LogElement[ResultArrayList.size()];
		Result = ResultArrayList.toArray(Result);
		return Result;
	}

	public LogElement[] getLookup(String world, long time, int maxcount) throws SQLException, TooManyElementsException
	{
		Statement st = null;
		String sql;
		ResultSet result;
		try
		{
			st = cn.createStatement();
		}
		catch(SQLException e)
		{
			SQLErrorHandler(e);
		}
		sql = "SELECT * FROM CommandLoggerExtended" + " WHERE date > " + (new java.util.Date().getTime() - time) + " AND world = '" + world + "' AND servername='" + Bukkit.getServerName() + "' LIMIT " + maxcount + ";";
		result = st.executeQuery(sql);
		ArrayList<LogElement> ResultArrayList = new ArrayList<LogElement>();
		while(result.next() == true)
		{
			ResultArrayList.add(new LogElement(UUID.fromString(result.getString("uuid")), result.getString("servername"), result.getString("world"), new Location(Bukkit.getWorld(result.getString("world")), result.getInt("X"), result.getInt("Y"), result.getInt("Z")), result.getString("message"), result.getLong("date")));
		}
		st.close();
		result.close();
		LogElement[] Result = new LogElement[ResultArrayList.size()];
		Result = ResultArrayList.toArray(Result);
		return Result;
	}

	public boolean Insert(final LogElement log)
	{
		try
		{
			long time = System.nanoTime();
			plugin.Logger("Starting insert ...", "Debug");
			PreparedStatement ps = cn.prepareStatement("INSERT INTO CommandLoggerExtended (date, uuid, servername, world, X, Y, Z, message) VALUES (?,?,?,?,?,?,?,?);");
			ps.setLong(1, new java.util.Date().getTime());
			ps.setString(2, log.getUuid().toString());
			ps.setString(3, log.getServername());
			ps.setString(4, log.getWorld());
			ps.setInt(5, log.getLocation().getBlockX());
			ps.setInt(6, log.getLocation().getBlockY());
			ps.setInt(7, log.getLocation().getBlockZ());
			ps.setString(8, log.getMessage());
			ps.execute();
			cn.commit();
			ps.close();
			plugin.Logger("executed in " + ((System.nanoTime() - time) / 1000000), "Debug");
		}
		catch(SQLException e)
		{
			System.out.println("[CommandLogger] Error while inserting into DB! - " + e.getMessage());
			SQLErrorHandler(e);
			return false;
		}
		return true;
	}

	public Connection createConnection()
	{
		if(plugin.getConfig().getBoolean("liteSQLdata"))
		{
			try
			{
				try
				{
					Class.forName("org.sqlite.JDBC");
				}
				catch(ClassNotFoundException cs)
				{
					plugin.Logger(cs.getMessage(), "Error");
				}
				cn = DriverManager.getConnection("jdbc:sqlite:plugins" + File.separator + "CommandLogger" + File.separator + "CommandLoggerSQLLoggerData.sqlite");
				cn.setAutoCommit(false);
				return cn;
			}
			catch(SQLException e)
			{
				SQLErrorHandler(e);
			}
		}
		else if(plugin.getConfig().getBoolean("mysql"))
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				cn = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("host") + "/" + plugin.getConfig().getString("database"), plugin.getConfig().getString("user"), plugin.getConfig().getString("password"));
				cn.setAutoCommit(false);
				return cn;
			}
			catch(SQLException e)
			{
				plugin.Logger("could not be enabled: Exception occured while trying to connect to DB", "Error");
				SQLErrorHandler(e);
				if(cn != null)
				{
					plugin.Logger("Old Connection still activated", "Error");
					try
					{
						cn.close();
						plugin.Logger("Old connection that was still activated has been successfully closed", "Error");
					}
					catch(SQLException e2)
					{
						plugin.Logger("Failed to close old connection that was still activated", "Error");
						SQLErrorHandler(e2);
					}
				}
				return null;
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	private void SQLErrorHandler(SQLException ex)
	{
		do
		{
			try
			{
				plugin.Logger("Exception Message: " + ex.getMessage(), "Error");
				plugin.Logger("DBMS Code: " + ex.getErrorCode(), "Error");
				ex.printStackTrace();
			}
			catch(Exception ne)
			{
				plugin.Logger(ne.getMessage(), "Error");
			}
		}
		while((ex = ex.getNextException()) != null);
	}

	public boolean CloseCon()
	{
		try
		{
			cn.close();
			return true;
		}
		catch(SQLException e)
		{
			plugin.Logger("[CommandLogger] Failed to close connection to DB!", "Error");
			SQLErrorHandler(e);
			return false;
		}
	}
}
