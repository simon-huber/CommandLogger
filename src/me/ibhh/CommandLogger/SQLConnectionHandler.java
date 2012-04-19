package me.ibhh.CommandLogger;

/**
 *
 */
import java.io.File;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Simon
 *
 */
public class SQLConnectionHandler {

    private Connection cn;
    private CommandLogger auTrade;

    public SQLConnectionHandler(CommandLogger AuctTrade) {
        auTrade = AuctTrade;
        cn = null;
    }

    public boolean deleteDB() {
        Statement st;
        boolean temp = false;
        try {
            String sql = "drop table if exists CommandLogger;";
            st = cn.createStatement();
            st.executeUpdate("drop table if exists CommandLogger;");
            temp = true;
            cn.commit();
            st.close();
        } catch (SQLException e) {
            System.out.println("[CommandLogger]: Error while creating tables! - " + e.getMessage());
            SQLErrorHandler(e);
        }
        return temp;
    }

    public boolean deletePlayer(String player) {
        Statement st;
        boolean temp = false;
        try {
            st = cn.createStatement();
            st.executeUpdate("DELETE FROM CommandLogger WHERE 'name'='" + player + "';");
            temp = true;
            cn.commit();
            st.close();
        } catch (SQLException e) {
            System.out.println("[CommandLogger]: Error while deleting player! - " + e.getMessage());
            SQLErrorHandler(e);
        }
        return temp;
    }

    public boolean deletePlayersDB(String player) {
        Statement st;
        boolean temp = false;
        try {
            st = cn.createStatement();
            st.executeUpdate("drop table if exists " + player + ";");
            temp = true;
            cn.commit();
            st.close();
        } catch (SQLException e) {
            System.out.println("[CommandLogger]: Error while creating tables! - " + e.getMessage());
            SQLErrorHandler(e);
        }
        return temp;
    }

    public void PrepareDBPlayersDB(String player) {
        Statement st;
        try {
            st = cn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS " + player + " ('name' VARCHAR PRIMARY KEY NOT NULL, 'toggle' INTEGER)");
            cn.commit();
            st.close();
        } catch (SQLException e) {
            System.out.println("[CommandLogger]: Error while creating tables! - " + e.getMessage());
            SQLErrorHandler(e);
        }
        try {
            if (!isinplayersdb(player, "all")) {
                try {
                    PreparedStatement ps = cn.prepareStatement("INSERT INTO " + player + " ('name', 'toggle') VALUES (?,?);");
                    ps.setString(1, "all");
                    ps.setInt(2, 0);
                    ps.execute();
                    cn.commit();
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("[CommandLogger] Error while inserting XP into DB! - " + e.getMessage());
                    SQLErrorHandler(e);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean UpdatePlayersDB(String player, String name, int on) {
        try {
            PreparedStatement ps = cn.prepareStatement("UPDATE " + player + " SET toggle='" + on + "' WHERE name='" + name + "';");
            ps.executeUpdate();
            cn.commit();
            ps.close();
        } catch (SQLException e) {
            System.out.println("[CommandLogger] Error while inserting XP into DB! - " + e.getMessage());
            SQLErrorHandler(e);
            return false;
        }
        return true;
    }

    public boolean InsertintoPlayersDB(String player, String name, int xp) {
        try {
            PreparedStatement ps = cn.prepareStatement("INSERT INTO " + player + " ('name', 'toggle') VALUES (?,?);");
            ps.setString(1, name);
            ps.setInt(2, xp);
            ps.execute();
            cn.commit();
            ps.close();
        } catch (SQLException e) {
            System.out.println("[CommandLogger] Error while inserting XP into DB! - " + e.getMessage());
            SQLErrorHandler(e);
            return false;
        }
        return true;
    }

    public void PrepareDB() {
        Statement st;
        try {
            st = cn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS CommandLogger ('name' VARCHAR PRIMARY KEY NOT NULL, 'toggle' INTEGER)");
            cn.commit();
            st.close();
        } catch (SQLException e) {
            System.out.println("[CommandLogger]: Error while creating tables! - " + e.getMessage());
            SQLErrorHandler(e);
        }
        //Implemented for SQL support
//        try {
//            st = cn.createStatement();
//            st.executeUpdate("CREATE TABLE IF NOT EXISTS data ('ID' INTEGER PRIMARY KEY NOT NULL AUTOINCREMENT, 'playername' VARCHAR, 'command' VARCHAR, 'world' VARCHAR, 'x' INTEGER, 'y' INTEGER, 'z' INTEGER, 'time' INTEGER)");
//            cn.commit();
//            st.close();
//        } catch (SQLException e) {
//            System.out.println("[CommandLogger]: Error while creating tables! - " + e.getMessage());
//            SQLErrorHandler(e);
//        }
    }

    public boolean InsertAuction(String name, int xp) {
        try {
            PreparedStatement ps = cn.prepareStatement("INSERT INTO CommandLogger ('name', 'toggle') VALUES (?,?);");
            ps.setString(1, name);
            ps.setInt(2, xp);
            ps.execute();
            cn.commit();
            ps.close();
        } catch (SQLException e) {
            System.out.println("[CommandLogger] Error while inserting XP into DB! - " + e.getMessage());
            SQLErrorHandler(e);
            return false;
        }
        return true;
    }

    public boolean UpdateXP(String name, int on) {
        try {
            PreparedStatement ps = cn.prepareStatement("UPDATE CommandLogger SET toggle='" + on + "' WHERE name='" + name + "';");
            ps.executeUpdate();
            cn.commit();
            ps.close();
        } catch (SQLException e) {
            System.out.println("[CommandLogger] Error while inserting XP into DB! - " + e.getMessage());
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
                ErrorLogger(cs.getMessage());
            }
            cn = DriverManager.getConnection("jdbc:sqlite:plugins" + File.separator + "CommandLogger" + File.separator + "CommandLogger.sqlite");
            cn.setAutoCommit(false);
            return cn;
        } catch (SQLException e) {
            SQLErrorHandler(e);
        }
        return null;
    }

    private void ErrorLogger(String Error) {
        System.err.println("[CommandLogger] Error:" + Error);
    }

    private void SQLErrorHandler(SQLException ex) {
        do {
            try {
                ErrorLogger("Exception Message: " + ex.getMessage());
                ErrorLogger("DBMS Code: " + ex.getErrorCode());
                ex.printStackTrace();
            } catch (Exception ne) {
                ErrorLogger(ne.getMessage());
            }
        } while ((ex = ex.getNextException()) != null);
    }

    public boolean CloseCon() {
        try {
            cn.close();
            return true;
        } catch (SQLException e) {
            System.out.println("[CommandLogger] Failed to close connection to DB!");
            SQLErrorHandler(e);
            return false;
        }
    }

    public boolean isinplayersdb(String player, String name) throws SQLException {
        boolean a = false;
        Statement st = null;
        String sql;
        ResultSet result;
        try {
            st = cn.createStatement();
        } catch (SQLException e) {
            SQLErrorHandler(e);
        }
        sql = "SELECT COUNT(name) FROM " + player + " WHERE name='" + name + "';";
        try {
            result = st.executeQuery(sql);
        } catch (SQLException e1) {
            SQLErrorHandler(e1);
            return false;
        }
        try {
            result.next();
            int b = result.getInt(1);
            if (b > 0) {
                a = true;
            }
            result.close();
            st.close();
        } catch (SQLException e2) {
            SQLErrorHandler(e2);
        }
        return a;
    }

    public boolean isindb(String name) throws SQLException {
        boolean a = false;
        Statement st = null;
        String sql;
        ResultSet result;
        try {
            st = cn.createStatement();
        } catch (SQLException e) {
            SQLErrorHandler(e);
        }
        sql = "SELECT COUNT(name) FROM CommandLogger WHERE name='" + name + "';";
        try {
            result = st.executeQuery(sql);
        } catch (SQLException e1) {
            SQLErrorHandler(e1);
            return false;
        }
        try {
            result.next();
            int b = result.getInt(1);
            if (b > 0) {
                a = true;
            }
            result.close();
            st.close();
        } catch (SQLException e2) {
            SQLErrorHandler(e2);
        }
        return a;
    }

    public String[] getViewof(String player) throws SQLException {
        Statement st = null;
        String sql;
        ResultSet result;
        int anzahl = 1;
        Statement st1 = null;
        String sql1;
        ResultSet result1;
        try {
            st1 = cn.createStatement();
        } catch (SQLException e) {
            SQLErrorHandler(e);
        }
        sql1 = "SELECT COUNT(name) FROM " + player + ";";
        result1 = st1.executeQuery(sql1);
        try {
            while (result1.next() == true) {
                int b = result1.getInt(1);
                anzahl = b;
            }
            result1.close();
            st1.close();
        } catch (SQLException e2) {
            SQLErrorHandler(e2);
        }
        try {
            st = cn.createStatement();
        } catch (SQLException e) {
            SQLErrorHandler(e);
        }
        sql = "SELECT * FROM " + player + ";";
        result = st.executeQuery(sql);
        String[] Result = new String[anzahl];
        try {
            int i = 0;
            while (result.next() == true) {
                int on;
                on = result.getInt("toggle");
                String onString;
                onString = result.getString("name");
                Result[i] = onString + " mode: " + on;
                i++;
            }
            st.close();
            result.close();
        } catch (SQLException e2) {
            SQLErrorHandler(e2);
        }
        return Result;
    }

    public int getToggleDB(String player, String name) throws SQLException {
        Statement st = null;
        String sql;
        ResultSet result;
        try {
            st = cn.createStatement();
        } catch (SQLException e) {
            SQLErrorHandler(e);
        }
        sql = "SELECT toggle FROM " + player + " WHERE name='" + name + "';";
        result = st.executeQuery(sql);
        int on = -1;
        try {
            while (result.next() == true) {
                on = result.getInt("toggle");
            }
            st.close();
            result.close();
        } catch (SQLException e2) {
            SQLErrorHandler(e2);
        }
        return on;
    }

    public int getToggle(String name) throws SQLException {
        Statement st = null;
        String sql;
        ResultSet result;
        try {
            st = cn.createStatement();
        } catch (SQLException e) {
            SQLErrorHandler(e);
        }
        sql = "SELECT toggle FROM CommandLogger WHERE name='" + name + "';";
        result = st.executeQuery(sql);
        int on = -1;
        try {
            while (result.next() == true) {
                on = result.getInt("toggle");
            }
            st.close();
            result.close();
        } catch (SQLException e2) {
            SQLErrorHandler(e2);
        }
        return on;
    }
}
