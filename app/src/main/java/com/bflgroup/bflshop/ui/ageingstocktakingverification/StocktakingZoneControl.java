package com.bflgroup.bflshop.ui.ageingstocktakingverification;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StocktakingZoneControl {

    DBConnection dbConnection = new DBConnection();
    Global objGlobal = Global.getInstance();

    private StockTakingZoneGlobal objStockTakingZoneGlobal = StockTakingZoneGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    boolean result;

    public StocktakingZoneControl() {
        result = dbConnection.connectDb();
        if (result == false) {
            objGlobal.setErrorMessage("StockTakingControl.validateItem : Connection error");
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (b_Result == false) {
            objGlobal.setErrorMessage("StockTakingControl : Fetch Time error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("StockTakingControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean loadZone() {
        List<String> arr;
        if (!checkConnection()) {
            objGlobal.setErrorMessage("exportToMainServer: Connection error");
            return false;
        }
        try {
            arr = new ArrayList<String>();
            rs = dbConnection.getResultSet("select distinct zone=zoneid from stocktaking order by zoneid", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("zone"));
            }
            objStockTakingZoneGlobal.setZoneList(arr);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("StocktakingZoneControl.loadZone : " + e.toString());
            return false;
        }
    }

    public boolean loadQuantity(String zone, String Datefrom, String DateTo) {
        int Quantity = 0;
        String Username = "";
        rs = dbConnection.getResultSet("select  username,qty = sum(quantity) from stocktaking where zoneid = '"+zone+"' and trndate between '"+Datefrom+"' and '"+DateTo+"' group by username", objGlobal.getConnection());
        try {
            while (rs.next()) {
                Quantity = rs.getInt("qty");
                Username = rs.getString("Username");
            }
            objStockTakingZoneGlobal.setQuantity(Quantity);
            objStockTakingZoneGlobal.setUsername(Username);
            return true;
        } catch (SQLException e) {
            objGlobal.setErrorMessage("StocktakingZoneControl.loadQuantity : " + e.toString());
            return false;
        }
    }

    public boolean saveRecord (String zone,String user, int ScanQty, int ManualQty, Context context) {
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            objGlobal.setErrorMessage("ShopReturnControl.InsertAdd : Getserverdatetime");
            return false;
        }
        int diffQty = ScanQty - ManualQty;
        rs = dbConnection.getResultSet("select  * from StockTakeVerify where zones = '" + zone + "' and userName = '" + user + "' and (trndate = '" + objGlobal.getServerDate() + "' or trnDate = convert(varchar,getdate()-1,103))", objGlobal.getConnection());
        try {
            if (rs.next()) {
                String query2 = "Update StockTakeVerify set ScanQty = " + ScanQty + ", ManualQty = " + ManualQty + ", trnDate = '" + objGlobal.getServerDate() + "',TrnTime = '" + objGlobal.getServerTime() + "', diff = " + diffQty + "  where zones = '" + zone + "' and userName = '" + user + "'";

                if (!dbConnection.insertUpdate(query2, objGlobal.getConnection())) {
                    okMessage("Alert", objGlobal.getErrorMessage(), context);
                    return false;
                }
            } else {
                String query1 = "insert into StockTakeVerify(UserName,Zones,TrnDate,TrnTime,ScanQty,ManualQty,Diff) values('" + user + "','" + zone + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "'," + ScanQty + ", " + ManualQty + "," + diffQty + ")";
                if (!dbConnection.insertUpdate(query1, objGlobal.getConnection())) {
                    okMessage("Alert", objGlobal.getErrorMessage(), context);
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getStockTakeDate() {
        try {
            objStockTakingZoneGlobal.setDtFrom("");
            objStockTakingZoneGlobal.setDtTo("");
            rs = dbConnection.getResultSet("select dtFrom=convert(varchar,getdate()-1,103),dtTo=convert(varchar,getdate(),103)", objGlobal.getConnection());
            if (rs.next()) {
                objStockTakingZoneGlobal.setDtFrom(rs.getString("dtFrom"));
                objStockTakingZoneGlobal.setDtTo(rs.getString("dtTo"));
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("getStockTakeDate:" + e);
            return false;
        }
    }

    private void okMessage(String title, String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }
}
