package com.bflgroup.bflshop.db;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.text.TextUtils;

import com.bflgroup.bflshop.comm.Global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    Global global = Global.getInstance();
    String UserName, Password;

    @SuppressLint("NewApi")
    public boolean connectDbMain() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String connectionString = "";
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionString = "jdbc:jtds:sqlserver://" + global.getServerIP() + ";databaseName=FABSMAIN;user=BFLOGIN;password=#lFl@Trf%;";
            global.setConnection(DriverManager.getConnection(connectionString));
            return true;
        } catch (SQLException se) {
            global.setErrorMessage("DBConnection.connectDb : error here 1 : " + se);
            return false;
        } catch (ClassNotFoundException e) {
            global.setErrorMessage("DBConnection.connectDb : error here 2 : " + e);
            return false;
        } catch (Exception e) {
            global.setErrorMessage("DBConnection.connectDb : error here 3 : " + e);
            return false;
        }
    }

    @SuppressLint("NewApi")
    public boolean connectDb() {
        UserName = global.getSqUserName();
        Password = global.getSqPassword();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String connectionString = "";
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionString = "jdbc:jtds:sqlserver://" + global.getServerIP() + ";databaseName=" + global.getDbName() + ";user=" +
                    UserName + ";password=" + Password + ";";
            global.setConnection(DriverManager.getConnection(connectionString));
            return true;
        } catch (SQLException se) {
            global.setErrorMessage("DBConnection.connectDb : error here 1 : " + se);
            return false;
        } catch (ClassNotFoundException e) {
            global.setErrorMessage("DBConnection.connectDb : error here 2 : " + e);
            return false;
        } catch (Exception e) {
            global.setErrorMessage("DBConnection.connectDb : error here 3 : " + e);
            return false;
        }
    }

    @SuppressLint("NewApi")
    public boolean connectCloudDb() {
        UserName = "BFL";
        Password = "LFBmct1971";
        String serverIp = "bfl-db-prod-im.cy4jmc1yawh1.ap-south-1.rds.amazonaws.com";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String connectionString = "";
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionString = "jdbc:jtds:sqlserver://" + serverIp + ";databaseName=" + global.getCloudDbName() + ";user=" +
                    UserName + ";password=" + Password + ";";
            global.setCloudCon(DriverManager.getConnection(connectionString));
            return true;
        } catch (SQLException se) {
            global.setErrorMessage("DBConnection.connectDb : error here 1 : " + se);
            return false;
        } catch (ClassNotFoundException e) {
            global.setErrorMessage("DBConnection.connectDb : error here 2 : " + e);
            return false;
        } catch (Exception e) {
            global.setErrorMessage("DBConnection.connectDb : error here 3 : " + e);
            return false;
        }
    }

    public boolean insertUpdate(String qry, Connection con) {
        boolean result = false;
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(qry);
            result = true;
        } catch (Exception ex) {
            global.setErrorMessage("DBConnection.insertUpdate : " + ex + ", :" + qry);
            return false;
        }
        return result;
    }

    public boolean checkConnectionClosed() {
        try {
            if (global.getConnection().isClosed()) return false;
            if (global.getConnection() == null) return false;
            return true;
        } catch (Exception e) {
            global.setErrorMessage("DBConnection.checkConnectionClosed : " + e);
            return false;
        }
    }

    public boolean checkCloudConnectionClosed() {
        try {
            if (global.getCloudCon().isClosed()) return false;
            if (global.getCloudCon() == null) return false;
            return true;
        } catch (Exception e) {
            global.setErrorMessage("DBConnection.checkCloudConnectionClosed : " + e);
            return false;
        }
    }

    public ResultSet getResultSet(String query, Connection con) {
        Statement stmt;
        ResultSet rs;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);
            return rs;
        } catch (Exception e) {
            global.setErrorMessage("DBConnection.getResultSet : " + e);
            return null;
        }
    }

    public boolean getServerDateTime(Connection con) {
        Statement stmt;
        ResultSet rs;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select dt=convert(varchar,getdate(),103),tm=convert(varchar,getdate(),8)");
            if (rs.next()) {
                global.setServerDate(rs.getString("dt"));
                global.setServerTime(rs.getString("tm"));
                return true;
            } else {
                global.setErrorMessage("getServerDateTime : Date not found");
                return false;
            }
        } catch (Exception e) {
            global.setErrorMessage("getServerDateTime : Try : " + e);
            return false;
        }
    }
}
