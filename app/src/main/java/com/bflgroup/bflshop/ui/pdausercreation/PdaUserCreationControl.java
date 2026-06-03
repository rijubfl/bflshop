package com.bflgroup.bflshop.ui.pdausercreation;

import android.text.TextUtils;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PdaUserCreationControl {

    DBConnection dbConnection = new DBConnection();
    Global objGlobal = Global.getInstance();
    boolean b_Result;
    private ResultSet rs;

    public PdaUserCreationControl() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingStockTakingControl : Local Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("AgeingStockTakingControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean saveUsers(String username, String password) {
        int userid = 0;
        boolean userExists=false;
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(username)) {
            objGlobal.setErrorMessage("Please enter username");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            objGlobal.setErrorMessage("Please enter password");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select userid from fabsmain.dbo.[user] where username='" + username + "'", objGlobal.getConnection());
            if (rs.next()) {
                userid = rs.getInt("userid");
                userExists=true;
            } else {
                rs = dbConnection.getResultSet("select userid=isnull(max(userid),0)+1 from fabsmain.dbo.[user]", objGlobal.getConnection());
                if (rs.next()) {
                    userid = rs.getInt("userid");
                } else {
                    objGlobal.setErrorMessage("User already exists");
                    return false;
                }
            }
            if (userid == 0) {
                objGlobal.setErrorMessage("Userid is blank");
                return false;
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("delete from fabsmain.dbo.pdausers where (username='" + username + "' or userid=" + userid + ")", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if (!dbConnection.insertUpdate("insert into fabsmain.dbo.pdausers (Userid,username,pass,Dbname,ServerIP) values (" + userid + ",'" + username + "','" + password + "'," +
                    "'" + objGlobal.getDbName() + "','" + objGlobal.getServerIP() + "')", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if(!userExists) {
                if (!dbConnection.insertUpdate("insert into fabsmain.dbo.[user] (UserId,UserName,Pwd,RecStartingNo,RepCode,Country) values (" + userid + ",'" + username + "','xxForPdaxx'," +
                        "'" + username + "','01','" + objGlobal.getCountryCode() + "')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
                if (!dbConnection.insertUpdate("insert into fabsmain.dbo.userdef values(" + userid + ",'" + objGlobal.getDbName() + "')", objGlobal.getConnection())) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("BinBatchInControl:saveBatchIn:ex:" + ex.toString());
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("BinBatchInControl:saveBatchIn:e:" + e.toString());
                return false;
            }
            return false;
        }
    }
}
