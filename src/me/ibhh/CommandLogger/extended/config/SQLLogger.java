package me.ibhh.CommandLogger.extended.config;

/**
 *
 */
import me.ibhh.CommandLogger.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import me.ibhh.CommandLogger.Tools.LogElement;
import me.ibhh.CommandLogger.Tools.TooManyElementsException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author ibhh
 *
 */
public class SQLLogger {

    private Connection cn;
    private CommandLogger plugin;

    public SQLLogger(CommandLogger AuctTrade) {
        plugin = AuctTrade;
        cn = createConnection();
        PrepareDB();
    }

    public boolean deleteDB() {
        Statement st;
        boolean temp = false;
        try {
            st = cn.createStatement();
            st.executeUpdate("drop table if exists CommandLoggerExtended;");
            temp = true;
            cn.commit();
            st.close();
        } catch (SQLException e) {
            plugin.Logger("[CommandLogger]: Error while creating tables! - " + e.getMessage(), "Error");
            SQLErrorHandler(e);
        }
        return temp;
    }

    public boolean deleteOldEntries(long time) {
        Statement st;
        boolean temp = false;
        try {
            st = cn.createStatement();
            st.executeUpdate("DELETE FROM CommandLoggerExtended WHERE date < " + time + ";");
            temp = true;
            cn.commit();
            st.close();
        } catch (SQLException e) {
            plugin.Logger("[CommandLogger]: Error while deleting old entries! - " + e.getMessage(), "Error");
            SQLErrorHandler(e);
        }
        return temp;
    }

    public void PrepareDB() {
        Statement st;
        try {
            st = cn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS CommandLoggerExtended ('date' LONG PRIMARY KEY NOT NULL, 'name' VARCHAR, 'world' VARCHAR, 'X' INTEGER, 'Y' INTEGER, 'Z' INTEGER, 'message' VARCHAR)");
            cn.commit();
            st.close();
        } catch (SQLException e) {
            plugin.Logger("[CommandLogger]: Error while creating tables! - " + e.getMessage(), "Error");
            SQLErrorHandler(e);
        }
    }
    
    public LogElement[] getLookupByName(String name, long time, int maxcount) throws SQLException, TooManyElementsException {
        Statement st = null;
        String sql;
        ResultSet result;
        try {
            st = cn.createStatement();
        } catch (SQLException e) {
            SQLErrorHandler(e);
        }
        sql = "SELECT * FROM CommandLoggerExtended"
                + " WHERE date > " + (new java.util.Date().getTime() - time) + " AND name = '" + name+ "';";
        result = st.executeQuery(sql);
        ArrayList<LogElement> ResultArrayList = new ArrayList<LogElement>();
        while (result.next() == true) {
            ResultArrayList.add(new LogElement(
                    result.getString("name"),
                    result.getString("world"),
                    new Location(
                    Bukkit.getWorld(result.getString("world")), result.getInt("X"), result.getInt("Y"), result.getInt("Z")),
                    result.getString("message"),
                    result.getLong("date")));
        }
        st.close();
        result.close();
        LogElement[] Result = new LogElement[ResultArrayList.size()];
        Result = ResultArrayList.toArray(Result);
        return Result;
    }

    public LogElement[] getLookup(String world, long time, int maxcount) throws SQLException, TooManyElementsException {
        Statement st = null;
        String sql;
        ResultSet result;
        try {
            st = cn.createStatement();
        } catch (SQLException e) {
            SQLErrorHandler(e);
        }
        sql = "SELECT * FROM CommandLoggerExtended"
                + " WHERE date > " + (new java.util.Date().getTime() - time) + " AND world = '" + world + "';";
        result = st.executeQuery(sql);
        ArrayList<LogElement> ResultArrayList = new ArrayList<LogElement>();
        while (result.next() == true) {
            ResultArrayList.add(new LogElement(
                    result.getString("name"),
                    result.getString("world"),
                    new Location(
                    Bukkit.getWorld(result.getString("world")), result.getInt("X"), result.getInt("Y"), result.getInt("Z")),
                    result.getString("message"),
                    result.getLong("date")));
        }
        st.close();
        result.close();
        LogElement[] Result = new LogElement[ResultArrayList.size()];
        Result = ResultArrayList.toArray(Result);
        return Result;
    }

    public boolean Insert(LogElement log) {
        try {
            long time = System.nanoTime();
            plugin.Logger("Starting insert ...", "Debug");
            PreparedStatement ps = cn.prepareStatement("INSERT INTO CommandLoggerExtended ('date', 'name', 'world', 'X', 'Y', 'Z', 'message') VALUES (?,?,?,?,?,?,?);");
            ps.setLong(1, new java.util.Date().getTime());
            ps.setString(2, log.getName());
            ps.setString(3, log.getWorld());
            ps.setInt(4, log.getLocation().getBlockX());
            ps.setInt(5, log.getLocation().getBlockY());
            ps.setInt(6, log.getLocation().getBlockZ());
            ps.setString(7, log.getMessage());
            ps.execute();
            cn.commit();
            ps.close();
            plugin.Logger("executed in " + ((System.nanoTime() - time) / 1000000), "Debug");
        } catch (SQLException e) {
            System.out.println("[CommandLogger] Error while inserting into DB! - " + e.getMessage());
            SQLErrorHandler(e);
            return false;
        }
        return true;
    }

    public Connection createConnection() {
        try {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException cs) {
                plugin.Logger(cs.getMessage(), "Error");
            }
            cn = DriverManager.getConnection("jdbc:sqlite:plugins" + File.separator + "CommandLogger" + File.separator + "CommandLoggerData.sqlite");
            cn.setAutoCommit(false);
            return cn;
        } catch (SQLException e) {
            SQLErrorHandler(e);
        }
        return null;
    }

    private void SQLErrorHandler(SQLException ex) {
        do {
            try {
                plugin.Logger("Exception Message: " + ex.getMessage(), "Error");
                plugin.Logger("DBMS Code: " + ex.getErrorCode(), "Error");
                ex.printStackTrace();
            } catch (Exception ne) {
                plugin.Logger(ne.getMessage(), "Error");
            }
        } while ((ex = ex.getNextException()) != null);
    }

    public boolean CloseCon() {
        try {
            cn.close();
            return true;
        } catch (SQLException e) {
            plugin.Logger("[CommandLogger] Failed to close connection to DB!", "Error");
            SQLErrorHandler(e);
            return false;
        }
    }
}
