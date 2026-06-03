package com.bflgroup.bflshop.ui.ageingstocktaking.manual;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.db.DBConnection;
import com.bflgroup.bflshop.ui.ageingstocktaking.model.AgeingStockTakingReports;
import com.bflgroup.bflshop.ui.ageingstocktaking.model.AgeingStockTakingReportsForDelete;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AgeingStockTakingControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private AgeingStockTakingGlobal objAgeingStockTakingGlobal = AgeingStockTakingGlobal.getInstance();

    private boolean b_Result;
    private ResultSet rs;

    public AgeingStockTakingControl() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingStockTakingControl : Local Connection error");
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingStockTakingControl : Fetch Time error");
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

    public boolean getStockTakeDate() {
        try {
            objAgeingStockTakingGlobal.setDtFrom("");
            objAgeingStockTakingGlobal.setDtTo("");
            rs = dbConnection.getResultSet("select dtFrom=convert(varchar,getdate()-1,103),dtTo=convert(varchar,getdate(),103)", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingStockTakingGlobal.setDtFrom(rs.getString("dtFrom"));
                objAgeingStockTakingGlobal.setDtTo(rs.getString("dtTo"));
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("getStockTakeDate:" + e);
            return false;
        }
    }

    public boolean loadZone(boolean all) {
        List<String> arr;
        if (!checkConnection()) {
            objGlobal.setErrorMessage("exportToMainServer: Connection error");
            return false;
        }
        try {
            arr = new ArrayList<String>();
            if(all)
                rs = dbConnection.getResultSet("select zoneid=zone from stocktakingzone order by zone", objGlobal.getConnection());
            else
                rs = dbConnection.getResultSet("select distinct * from(select zoneid from stocktaking union all select zone from stocktakingzone where userassign<>'') x order by 1", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("zoneid"));
            }
            objAgeingStockTakingGlobal.setZoneList(arr);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("AgeingStockTakingControl.loadZone : " + e);
            return false;
        }
    }
    public boolean validateZoneUsed(String zoneId) {
        List<String> arr;
        if (!checkConnection()) {
            objGlobal.setErrorMessage("zoneLock: Connection error");
            return false;
        }
        try {
            String userAs = "";
            rs = dbConnection.getResultSet("select * from stocktakingzone where zone='" + zoneId + "'", objGlobal.getConnection());
            if (rs.next()) {
                userAs = rs.getString("UserAssign");
                if (!userAs.isEmpty()) {
                    if (!userAs.equals(objGlobal.getUserName())) {
                        objGlobal.setErrorMessage("User (" + userAs + ") already assigned to this zone");
                        return false;
                    }
                }
                if (!dbConnection.insertUpdate("update stocktakingzone set userassign='" + objGlobal.getUserName() + "' where zone='" + zoneId + "'", objGlobal.getConnection())) {
                    return false;
                }
            } else {
                objGlobal.setErrorMessage("Invalid Zone");
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("AgeingStockTakingControl.loadZone : " + e);
            return false;
        }
    }
    public ArrayList<AgeingStockTakingReports> loadAgingStockTakingRpt(String ord, String dtFrom, String dtTo) {
        ArrayList<AgeingStockTakingReports> listAgeingStockTakingReports = new ArrayList<AgeingStockTakingReports>();
        objAgeingStockTakingGlobal.setTotalScan(0);
        objAgeingStockTakingGlobal.setTotalMan(0);
        double totScan = 0, totMan = 0;
        if (!checkConnection()) {
            objGlobal.setErrorMessage("exportToMainServer: Connection error");
            return null;
        }
        try {
            String ords = "";
            if (ord.equals("User")) ords = " order by username";
            if (ord.equals("Zone")) ords = " order by zone";
            if (ord.equals("Quantity")) ords = " order by scanqty";
            /*if(!dbConnection.insertUpdate("drop table if exists #stkrpt", objGlobal.getConnection())){
                return null;
            }*/
            if (!dbConnection.insertUpdate("create table #stkrpt(zone varchar(20),username varchar(20),scanqty int,manqty int)", objGlobal.getConnection())) {
                return null;
            }
            if (!dbConnection.insertUpdate("insert into #stkrpt select username,zoneid,sum(quantity),0 from stocktaking where trndate between '" + dtFrom + "' and '" + dtTo + "' group by username,zoneid", objGlobal.getConnection())) {
                return null;
            }
            if (!dbConnection.insertUpdate("insert into #stkrpt select username,Zones,0,manualqty from StockTakeVerify", objGlobal.getConnection())) {
                return null;
            }
            rs = dbConnection.getResultSet("select zone,username,scanqty=sum(scanqty),manqty=sum(manqty),diff=sum(scanqty-manqty) from #stkrpt group by zone,username" + ords, objGlobal.getConnection());
            while (rs.next()) {
                listAgeingStockTakingReports.add(new AgeingStockTakingReports(rs.getString("zone"), rs.getString("username"), rs.getInt("scanqty"),
                        rs.getInt("manqty"), rs.getInt("diff")));
                totScan = totScan + rs.getInt("scanqty");
                totMan = totMan + rs.getInt("manqty");
            }
            objAgeingStockTakingGlobal.setTotalScan(totScan);
            objAgeingStockTakingGlobal.setTotalMan(totMan);
            if (!dbConnection.insertUpdate("drop table #stkrpt", objGlobal.getConnection())) {
                return null;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:loadAgingStockTakingRpt:" + ex);
            return null;
        }
        return listAgeingStockTakingReports;
    }

    public ArrayList<AgeingStockTakingReportsForDelete> loadAgingStockTakingForDelete(String zone, String itemcode) {
        ArrayList<AgeingStockTakingReportsForDelete> listAgeingStockTakingReportsItemSearch = new ArrayList<AgeingStockTakingReportsForDelete>();
        double totScan = 0;
        if (!checkConnection()) {
            objGlobal.setErrorMessage("exportToMainServer: Connection error");
            return null;
        }
        objAgeingStockTakingGlobal.setTotalScanDelQty(0);
        try {
            String srch = "";
            if (!itemcode.isEmpty()) srch = " and itemcode='" + itemcode + "' ";
            rs = dbConnection.getResultSet("select itemcode,ScanBarcode,srid,quantity from stocktaking where zoneid='" + zone + "' " + srch + " order by srid", objGlobal.getConnection());
            while (rs.next()) {
                listAgeingStockTakingReportsItemSearch.add(new AgeingStockTakingReportsForDelete(rs.getString("itemcode"), rs.getString("ScanBarcode"), rs.getString("srid")));
                totScan = totScan + rs.getInt("quantity");
            }
            objAgeingStockTakingGlobal.setTotalScanDelQty(totScan);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:loadAgingStockTakingForDelete:" + ex);
            return null;
        }
        return listAgeingStockTakingReportsItemSearch;
    }

    public boolean validateServerScan(String scan) {
        List<String> arr;
        if (!checkConnection()) {
            objGlobal.setErrorMessage("validateServerScan: Connection error");
            return false;
        }
        try {
            objAgeingStockTakingGlobal.setBarcode("");
            objAgeingStockTakingGlobal.setRfid("");
            rs = dbConnection.getResultSet("select itemcode from stocktaking where rfid='" + scan + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("RFID-Duplicate scan, itemcode: "+rs.getString("itemcode"));
                return false;
            }
            rs = dbConnection.getResultSet("select rfid,barcode from rfpair where rfid='" + scan + "'", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingStockTakingGlobal.setBarcode(rs.getString("barcode"));
                objAgeingStockTakingGlobal.setRfid(rs.getString("rfid"));
                return true;
            }
            rs = dbConnection.getResultSet("select itemcode from itemmaster where itemcode='" + scan + "'", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingStockTakingGlobal.setBarcode(scan);
                return true;
            } else {
                objGlobal.setErrorMessage("invalid RFID / Bracode");
                return false;
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage("AgeingStockTakingControl.validateServerScan : " + e.toString());
            return false;
        }
    }
}
