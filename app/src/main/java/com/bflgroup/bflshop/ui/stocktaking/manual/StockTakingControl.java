package com.bflgroup.bflshop.ui.stocktaking.manual;

import android.util.Log;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockTakingControl {

    DBConnection dbConnection = new DBConnection();
    Global objGlobal = Global.getInstance();
    StockTakingGlobal objStockTakingGlobal = StockTakingGlobal.getInstance();

    boolean result;
    ResultSet rs;

    public StockTakingControl() {
        result = dbConnection.connectDb();
        if (result == false) {
            objGlobal.setErrorMessage("StockTakingControl.validateItem : Connection error");
        }
    }




    public boolean saveScan(String scan, String itemcode) {
        try {
            result = dbConnection.insertUpdate("insert into stocktaking(Trndate,Time1,username,itemcode,Quantity,ZoneID,UserId,Device,ScanBarcode,SrId) " +
                    "values (convert(varchar,getdate(),103),convert(varchar,getdate(),8),'" + objGlobal.getUserName() + "','" + itemcode + "',1,''," +
                    "" + objGlobal.getUserId() + ",'" + objGlobal.getDeviceName() + "','" + scan + "'," +
                    "(replace(replace(replace(replace(convert(varchar,getdate(),121),'-',''),' ',''),':',''),'.','')))", objGlobal.getConnection());
            if (result == false) {
                return false;
            }
            getTotalCount();
            getTotalCountByUser();
            getTotalCountByItem(itemcode);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("StockTakingControl.saveScan : " + e.toString());
            return false;
        }
    }

    public Boolean validateItem(String itemcode) {
        if (dbConnection.checkConnectionClosed()) {
            result = dbConnection.connectDb();
            if (result == false) {
                objGlobal.setErrorMessage("StockTakingControl.validateItem : Connection error");
                return false;
            }
        }
        try {
            rs = dbConnection.getResultSet("select description from itemmaster where itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objStockTakingGlobal.setDescription(rs.getString("description").toString());
            } else {
                objGlobal.setErrorMessage("Invalid itemcode, "+itemcode);
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:validateItem:" + ex.toString());
            return false;
        }
    }

    private Boolean getTotalCount() {
        try {
            rs = dbConnection.getResultSet("select tot=isnull(sum(quantity),0) from stocktaking", objGlobal.getConnection());
            if (rs.next()) {
                objStockTakingGlobal.setTotalCount(rs.getString("tot").toString());
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:getTotalCount:" + ex.toString());
            return false;
        }
    }

    private Boolean getTotalCountByUser() {
        try {
            rs = dbConnection.getResultSet("select tot=isnull(sum(quantity),0) from stocktaking where " +
                    "userid=" + objGlobal.getUserId(), objGlobal.getConnection());
            if (rs.next()) {
                objStockTakingGlobal.setTotalCountByUser(rs.getString("tot").toString());
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:getTotalCountByUser:" + ex.toString());
            return false;
        }
    }

    private Boolean getTotalCountByItem(String itemcode) {
        try {
            rs = dbConnection.getResultSet("select tot=isnull(sum(quantity),0) from stocktaking where " +
                    "itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objStockTakingGlobal.setTotalCountByItem(rs.getString("tot").toString());
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:getTotalCountByItem:" + ex.toString());
            return false;
        }
    }

    public ArrayList<StockTakingItem> loadStockTakingItem() {
        ArrayList<StockTakingItem> listStockTakingItem = new ArrayList<StockTakingItem>();
        try {
            getTotalCount();
            getTotalCountByUser();
            rs = dbConnection.getResultSet("select top 50 itemcode,dt=convert(varchar,trndate,103),tm=convert(varchar,time1,8) from " +
                    "stocktaking where userid="+objGlobal.getUserId()+" order by trndate desc,time1 desc", objGlobal.getConnection());
            while (rs.next()) {
                listStockTakingItem.add(new StockTakingItem(rs.getString("itemcode").toString(),
                        rs.getString("dt").toString(), rs.getString("tm").toString()));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:loadTransferItemsAll:" + ex.toString());
            return listStockTakingItem;
        }
        return listStockTakingItem;
    }

}
