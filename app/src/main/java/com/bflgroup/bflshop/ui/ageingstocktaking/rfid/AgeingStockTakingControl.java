package com.bflgroup.bflshop.ui.ageingstocktaking.rfid;

import android.util.Log;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AgeingStockTakingControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private AgeingStockTakingGlobal objAgeingStockTakingGlobal = AgeingStockTakingGlobal.getInstance();

    private boolean b_Result;
    private ResultSet rs;

    public AgeingStockTakingControl() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingStockTakingControl : Local Connection error");
        }
        b_Result = dbConnection.connectCloudDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingStockTakingControl : Cloud Connection error 1.0");
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
        objGlobal.setCloudDbName("BFLDATA");
        b_Result = dbConnection.connectCloudDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingStockTakingControl : Cloud Connection error 1.0");
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

    public boolean InsertScanned(boolean scan) {
       // List<String> arr;
        if (!checkConnection()) {
            objGlobal.setErrorMessage("exportToMainServer: Connection error");
            return false;
        }
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        try {

            if (!dbConnection.insertUpdate("insert into stocktaking(Trndate,Time1,username,itemcode,Quantity,ZoneID,UserId,Device,ScanBarcode,SrId,Result,rfid) " +
                    "select '" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime()+ "','" + objGlobal.getUserName() + "',itemcode," +
                    "count(itemcode),''," + objGlobal.getUserId() + ",devicename,barcode," +
                    "(replace(replace(replace(replace(convert(varchar,getdate(),121),'-',''),' ',''),':',''),'.','')),'',rfid from tmpStocktakeRfidDetail where itemcode <> '' group by itemcode,rfid,devicename,barcode ", objGlobal.getConnection())){

             return false;
        }
            else scan = true;
            if (!dbConnection.insertUpdate("insert into StocktakeScannedRfidLog ( devicename,rfid,Remarks,itemcode,username,barcode,RfidMaster,Division,Department,trndate,shopname) select *,getdate(),'"+objPosGlobal.getShopName()+"' from tmpStocktakeRfidDetail where devicename = '"+objGlobal.getDeviceName()+"'", objGlobal.getConnection())) {
                return false;
            }else scan = true;

            if (!dbConnection.insertUpdate("delete from  tmpStocktakeRfidDetail where devicename = '"+objGlobal.getDeviceName()+"'", objGlobal.getConnection())) {
                return false;
            }else scan = true;

        } catch (Exception e) {
            objGlobal.setErrorMessage("AgeingStockTakingControl.loadZone : " + e);
            return false;
        }
        return scan;
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

    public ArrayList<RfidMissingDepartment> loadMissingDepartment(String Division,String Category, String Subcategory){
        String[] subCat = Subcategory.split(", ");
        List<String> wordList = Arrays.asList(subCat);
        String level2 = "";
        for(String cat : subCat){
            level2 = "'" + cat + "' ,";
        }
        int index = level2.lastIndexOf(",");
        level2 = level2.substring(0, index);

        if (!checkConnection()) {
            objGlobal.setErrorMessage("Stocktake:loadRfidMissing: connection error");
            return null;
        }
        ArrayList<RfidMissingDepartment> listRfidMissingItems = new ArrayList<RfidMissingDepartment>();
        try {

            if(subCat[0].equals("ALL") || Subcategory.equals("")) {
                dbConnection.insertUpdate("Drop table  if exists  #abs",objGlobal.getConnection());
                dbConnection.insertUpdate("select a.Division, a.Department, SysQty = sum(LocStockQty), ScanQty = (select count(itemcode) from tmpStocktakeRfidDetail where a.Division = division and Department = a.Department and itemcode <> '' and b.itemcode = itemcode) into #abs from LevelWiseDetails a, StockTakeResults b where CategoryLevel1 = '"+Category+"' and a.division = b.Division and b.Department = a.Department  and isRfid = 'Y' group by a.Division,a.Department, b.itemcode" ,objGlobal.getConnection());
                rs = dbConnection.getResultSet("select Department,SysQty = sum(SysQty),ScanQty = sum(ScanQty), Diff = sum(SysQty) - sum(ScanQty)  from #abs where division = '"+Division+"' group by Department  having sum(SysQty) - sum(ScanQty) <> 0 and sum(SysQty)>0",objGlobal.getConnection());
            }else{

                dbConnection.insertUpdate("Drop table if exists #abs",objGlobal.getConnection());
                dbConnection.insertUpdate("select a.Division, a.Department, SysQty = sum(LocStockQty), ScanQty = (select count(itemcode) from tmpStocktakeRfidDetail where a.Division = division and Department = a.Department and itemcode <> '' and b.itemcode = itemcode) into #abs from LevelWiseDetails a, StockTakeResults b where CategoryLevel2 in ("+level2+") and a.division = b.Division and b.Department = a.Department  and isRfid = 'Y' group by a.Division,a.Department, b.itemcode" ,objGlobal.getConnection());
                rs = dbConnection.getResultSet("select Department,SysQty = sum(SysQty),ScanQty = sum(ScanQty),Diff =  sum(SysQty) - sum(ScanQty)  from #abs where division = '"+Division+"' group by Department  having sum(SysQty) - sum(ScanQty) <> 0 and sum(SysQty)>0",objGlobal.getConnection());

            }

            while (rs.next()) {
                listRfidMissingItems.add(new RfidMissingDepartment(rs.getString("Department"), rs.getInt("SysQty"), rs.getInt("ScanQty"), rs.getInt("Diff")));
            }
            return listRfidMissingItems;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:loadRfidExcess:" + ex.getMessage());
            return null;
        }

    }
    public ArrayList<RfidMissingitemcodes> loaditemImage(String itemcode){

        if (!checkConnection()) {
            objGlobal.setErrorMessage("Stocktake:loadRfidMissing: connection error");
            return null;
        }
        ArrayList<RfidMissingitemcodes> listRfidMissingItems = new ArrayList<RfidMissingitemcodes>();
        try {
            rs = dbConnection.getResultSet("select a.itemcode,Description,Division,Department,GroupName,imagelink from  StockTakeResults a LEFT JOIN  itemPics b on a.itemcode = b.itemcode where a.itemcode = '"+itemcode+"' ", objGlobal.getConnection());
            while (rs.next()) {
                listRfidMissingItems.add(new RfidMissingitemcodes(rs.getString("itemcode"),rs.getString("Description"),rs.getString("Division"),rs.getString("Department"),rs.getString("Groupname"), rs.getString("imagelink")));
            }
            return listRfidMissingItems;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:loadRfidExcess:" + ex.getMessage());
            return null;
        }

    }

    public ArrayList<RfidMissingDivision> loadRfidMissingDivision(String Category, String Subcategory){


        String[] subCat = Subcategory.split(", ");
        List<String> wordList = Arrays.asList(subCat);
        String level2 = "";
        for(String cat : subCat){
            level2 = "'" + cat + "' ,";
        }
        int index = level2.lastIndexOf(",");
        level2 = level2.substring(0, index);

        if (!checkConnection()) {
            objGlobal.setErrorMessage("Stocktake:loadRfidMissing: connection error");
            return null;
        }
        ArrayList<RfidMissingDivision> listRfidMissingItems = new ArrayList<RfidMissingDivision>();
        try {

            if(subCat[0].equals("ALL") || Subcategory.equals("")) {

                dbConnection.insertUpdate("Drop table  if exists  #abs",objGlobal.getConnection());
                dbConnection.insertUpdate("select a.Division, a.Department, SysQty = sum(LocStockQty), ScanQty = (select count(itemcode) from tmpStocktakeRfidDetail where a.Division = division and Department = a.Department and itemcode <> '' and b.itemcode = itemcode) into #abs from LevelWiseDetails a, StockTakeResults b where CategoryLevel1 = '"+Category+"' and a.division = b.Division and b.Department = a.Department  and isRfid = 'Y' group by a.Division,a.Department, b.itemcode" ,objGlobal.getConnection());
                rs = dbConnection.getResultSet("select Division,SysQty = sum(SysQty),ScanQty = sum(ScanQty), Diff = sum(SysQty) - sum(ScanQty)  from #abs group by Division  having sum(SysQty) - sum(ScanQty) <> 0 and sum(SysQty)>0",objGlobal.getConnection());

            }else{

                dbConnection.insertUpdate("Drop table if exists #abs",objGlobal.getConnection());
                dbConnection.insertUpdate("select a.Division, a.Department, SysQty = sum(LocStockQty), ScanQty = (select count(itemcode) from tmpStocktakeRfidDetail where a.Division = division and Department = a.Department and itemcode <> '' and b.itemcode = itemcode) into #abs from LevelWiseDetails a, StockTakeResults b where CategoryLevel2 in ("+level2+") and a.division = b.Division and b.Department = a.Department  and isRfid = 'Y' group by a.Division,a.Department, b.itemcode" ,objGlobal.getConnection());
                rs = dbConnection.getResultSet("select Division,SysQty = sum(SysQty),ScanQty = sum(ScanQty),Diff =  sum(SysQty) - sum(ScanQty)  from #abs group by Division  having sum(SysQty) - sum(ScanQty) <> 0 and sum(SysQty)>0",objGlobal.getConnection());

            }
            while (rs.next()) {
                listRfidMissingItems.add(new RfidMissingDivision(rs.getString("Division"), rs.getInt("SysQty"), rs.getInt("ScanQty"), rs.getInt("Diff")));
            }
            return listRfidMissingItems;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:loadRfidExcess:" + ex.getMessage());
            return null;
        }

    }

    public ArrayList<RfidMissingItems> loadRfidMissing(String Department, String Category, String Subcategory){
        String[] subCat = Subcategory.split(", ");
        List<String> wordList = Arrays.asList(subCat);
        String level2 = "";
        for(String cat : subCat){
            level2 = "'" + cat + "' ,";
        }
        int index = level2.lastIndexOf(",");
        level2 = level2.substring(0, index);

        if (!checkConnection()) {
            objGlobal.setErrorMessage("Stocktake:loadRfidMissing: connection error");
            return null;
        }
        ArrayList<RfidMissingItems> listRfidMissingItems = new ArrayList<>();
        try {
            if(subCat[0].equals("ALL") || Subcategory.equals("")) {

                dbConnection.insertUpdate("Drop table  if exists  #abs",objGlobal.getConnection());
                dbConnection.insertUpdate("select a.Division, a.Department, itemcode,groupname, Description, SysQty = sum(LocStockQty), ScanQty = (select count(itemcode) from tmpStocktakeRfidDetail where a.Division = division and Department = a.Department and itemcode <> '' and b.itemcode = itemcode) into #abs from LevelWiseDetails a, StockTakeResults b where CategoryLevel1 = '"+Category+"' and a.division = b.Division and b.Department = a.Department  and isRfid = 'Y' group by a.Division,a.Department,b.itemcode,groupname, Description" ,objGlobal.getConnection());
                rs = dbConnection.getResultSet("select itemcode,groupname, Description,Department,SysQty = sum(SysQty),ScanQty = sum(ScanQty), Diff = sum(SysQty) - sum(ScanQty)  from #abs where department = '"+Department+"' group by Department,itemcode,groupname, Description  having sum(SysQty) - sum(ScanQty) <> 0 and sum(SysQty)>0",objGlobal.getConnection());

            }else{

                dbConnection.insertUpdate("Drop table if exists #abs",objGlobal.getConnection());
                dbConnection.insertUpdate("select a.Division, a.Department,itemcode,groupname, Description, SysQty = sum(LocStockQty), ScanQty = (select count(itemcode) from tmpStocktakeRfidDetail where a.Division = division and Department = a.Department and itemcode <> '' and b.itemcode = itemcode) into #abs from LevelWiseDetails a, StockTakeResults b where CategoryLevel2 in ("+level2+") and a.division = b.Division and b.Department = a.Department  and isRfid = 'Y' group by a.Division,a.Department, b.itemcode,groupname, Description" ,objGlobal.getConnection());
                rs = dbConnection.getResultSet("select itemcode,groupname, Description,Department,SysQty = sum(SysQty),ScanQty = sum(ScanQty),Diff =  sum(SysQty) - sum(ScanQty)  from #abs where department = '"+Department+"' group by Department,itemcode,groupname, Description  having sum(SysQty) - sum(ScanQty) <> 0 and sum(SysQty)>0",objGlobal.getConnection());


            }

   while (rs.next()) {
                listRfidMissingItems.add(new RfidMissingItems(rs.getString("itemcode"), rs.getString("Description"),rs.getString("Groupname"),rs.getInt("SysQty"), rs.getInt("ScanQty"), rs.getInt("Diff")));
            }
            return listRfidMissingItems;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:loadRfidExcess:" + ex.getMessage());
            return null;
        }

    }

    public ArrayList<String> loadCategory()
    {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            arrayList.add("-- Select Category --");
            String Query = "select distinct CategoryLevel1 from LevelWiseDetails";
            ResultSet rs1 = dbConnection.getResultSet(Query, objGlobal.getConnection());
            while (rs1.next()) {
                arrayList.add(rs1.getString("CategoryLevel1"));
            }
        } catch (Exception e) {
            Log.e("Alert",e.toString());
        }
        return arrayList;
    }
    public ArrayList<String> loadSubCategory(String Category)
    {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            arrayList.add("ALL");
            String Query = "select distinct CategoryLevel2 from LevelWiseDetails where CategoryLevel1 = '"+Category+"'";
            ResultSet rs1 = dbConnection.getResultSet(Query, objGlobal.getConnection());
            while (rs1.next()) {
                arrayList.add(rs1.getString("CategoryLevel2"));
            }
        } catch (Exception e) {
            Log.e("Alert",e.toString());
        }
        return arrayList;
    }

    //Shows System/Scan/Missing/Excess QTY
    public Double loadRfIdDetails(String Category, String Subcategory) {

        if (!checkConnection()) {
            objGlobal.setErrorMessage("exportToMainServer: Connection error");
            return null;
        }
        String[] subCat = Subcategory.split(", ");
        List<String> wordList = Arrays.asList(subCat);
        String level2 = "";
        for(String cat : subCat){
            level2 = "'" + cat + "' ,";
        }
        int index = level2.lastIndexOf(",");
        level2 = level2.substring(0, index);

        Double scannedQty = 0.0;
        Double RfidSysQty = 0.0;
        Double TotalSysQty = 0.0;
        Double missing = 0.0;
        Double excess = 0.0;
        objAgeingStockTakingGlobal.setTotalScanQty(0.0);
        objAgeingStockTakingGlobal.setRFIDsysQty(0.0);
        objAgeingStockTakingGlobal.setDg1TotalQty(0);
        objAgeingStockTakingGlobal.settotalsys(0.0);
        objAgeingStockTakingGlobal.settotaldiffqty(0.0);
        objAgeingStockTakingGlobal.settotalexcessqty(0.0);
        double totScan = 0, totMan = 0;
        if (!checkConnection()) {
            objGlobal.setErrorMessage("exportToMainServer: Connection error");
            return null;
        }
        try {

            rs = dbConnection.getResultSet("select Qty = count(*) from tmpStocktakeRfidDetail  where itemcode <> ''" , objGlobal.getConnection());
            if (rs.next()) {
                scannedQty = rs.getDouble("Qty");
                objAgeingStockTakingGlobal.setTotalScanQty(rs.getDouble("Qty"));
            }

            rs = dbConnection.getResultSet("select * from bfldata..ShopSystemStockRequest where shopname='"+  objPosGlobal.getShopName() +"' and CAST(CompletedDt AS DATE) = CAST(GETDATE() AS DATE) and status = 'Updated'", objGlobal.getCloudCon());
            if (rs.next()) {

                rs = dbConnection.getResultSet("select qty = sum(LocStockQty) from StockTakeResults where isnull(isRfid,'') = 'Y'" , objGlobal.getConnection());
                if (rs.next()) {

                    objAgeingStockTakingGlobal.setRFIDsysQty(rs.getDouble("qty"));
                }

                rs = dbConnection.getResultSet("select Qty = sum(LocStockQty) from StockTakeResults" , objGlobal.getConnection());
                if (rs.next()) {
                    // setrfpaircount = rs.getDouble("Qty");
                    objAgeingStockTakingGlobal.settotalsys(rs.getDouble("Qty"));
                }
                if(subCat[0].equals("ALL") || Subcategory.equals("")) {
                    rs = dbConnection.getResultSet("select Qty = sum(LocStockQty) from StockTakeResults a, LevelWiseDetails b where a.Division = b.Division and a.Department = b.Department and CategoryLevel1 = '" + Category + "' and ISNULL(isRfid,'') = 'Y'", objGlobal.getConnection());
                    if (rs.next()) {
                        objAgeingStockTakingGlobal.setDg1TotalQty(rs.getInt("Qty"));
                    }

                    if (!dbConnection.insertUpdate("drop table if exists #temp1", objGlobal.getConnection())) {
                        return null;
                    }

                    if (!dbConnection.insertUpdate("select b.itemcode, scanQty = count(a.itemcode),LocStockQty into #temp1  from StockTakeResults a, tmpStocktakeRfidDetail b,LevelWiseDetails c where a.itemcode = b.itemcode and a.Division = c.Division and a.Department = c.Department and c.CategoryLevel1 = '" + Category + "'  and  isnull(isrfid,'') = 'Y' and LocStockQty>0 group by b.itemcode,LocStockQty ", objGlobal.getConnection())) {
                        return null;
                    }
                    rs = dbConnection.getResultSet("select Missing = sum(case when (scanQty>LocStockQty) then LocStockQty Else scanQty End) from #temp1", objGlobal.getConnection());
                    if (rs.next()) {
                        missing = rs.getDouble("Missing");
                        objAgeingStockTakingGlobal.settotaldiffqty(objAgeingStockTakingGlobal.getDg1TotalQty() - missing);
                    }


                    if (!dbConnection.insertUpdate("drop table if exists #temp", objGlobal.getConnection())) {
                        return null;
                    }
                    if (!dbConnection.insertUpdate("select itemcode,ScanQty= count(itemcode),sysstock = isnull((select locstockQty from StockTakeResults where itemcode = a.itemcode),0) into #temp from tmpStocktakeRfidDetail a, LevelWiseDetails c where a.Division = c.Division and a.Department = c.Department and c.CategoryLevel1 = '" + Category + "' and itemcode <> '' group by itemcode", objGlobal.getConnection())) {
                        return null;
                    }
                    rs = dbConnection.getResultSet("select Extra = sum(case when (ScanQty>sysstock) and (sysstock>0) then (ScanQty -sysstock) when (ScanQty<=sysstock) then 0 Else ScanQty End) from #temp", objGlobal.getConnection());
                    if (rs.next()) {
                        excess = rs.getDouble("Extra");
                        objAgeingStockTakingGlobal.settotalexcessqty(excess);
                    }
                }else {

                        rs = dbConnection.getResultSet("select Qty = sum(LocStockQty) from StockTakeResults a, LevelWiseDetails b where a.Division = b.Division and a.Department = b.Department and CategoryLevel2 in ("+ level2 +") and ISNULL(isRfid,'') = 'Y'", objGlobal.getConnection());
                        if (rs.next()) {
                            // setrfpaircount = rs.getDouble("Qty");
                            objAgeingStockTakingGlobal.setDg1TotalQty(rs.getInt("Qty"));
                        }

                        if (!dbConnection.insertUpdate("drop table if exists #temp1", objGlobal.getConnection())) {
                            return null;
                        }

                        if (!dbConnection.insertUpdate("select b.itemcode, scanQty = count(a.itemcode),LocStockQty into #temp1  from StockTakeResults a, tmpStocktakeRfidDetail b,LevelWiseDetails c where a.itemcode = b.itemcode and a.Division = c.Division and a.Department = c.Department and c.CategoryLevel2 in ("+ level2 +")  and  isnull(isrfid,'') = 'Y' and LocStockQty>0 group by b.itemcode,LocStockQty ", objGlobal.getConnection())) {
                            return null;
                        }
                        rs = dbConnection.getResultSet("select Missing = sum(case when (scanQty>LocStockQty) then LocStockQty Else scanQty End) from #temp1", objGlobal.getConnection());
                        if (rs.next()) {
                            missing = rs.getDouble("Missing");
                            objAgeingStockTakingGlobal.settotaldiffqty(objAgeingStockTakingGlobal.getDg1TotalQty() - missing);
                        }


                        if (!dbConnection.insertUpdate("drop table if exists #temp", objGlobal.getConnection())) {
                            return null;
                        }
                        if (!dbConnection.insertUpdate("select itemcode,ScanQty= count(itemcode),sysstock = isnull((select locstockQty from StockTakeResults where itemcode = a.itemcode),0) into #temp from tmpStocktakeRfidDetail a, LevelWiseDetails c where a.Division = c.Division and a.Department = c.Department and c.CategoryLevel2 in ("+ level2 +") and itemcode <> '' group by itemcode", objGlobal.getConnection())) {
                            return null;
                        }
                        rs = dbConnection.getResultSet("select Extra = sum(case when (ScanQty>sysstock) and (sysstock>0) then (ScanQty -sysstock) when (ScanQty<=sysstock) then 0 Else ScanQty End) from #temp", objGlobal.getConnection());
                        if (rs.next()) {
                            excess = rs.getDouble("Extra");
                            objAgeingStockTakingGlobal.settotalexcessqty(excess);
                        }
                    }
            }

            //UserCategory(Category);

        } catch (Exception ex) {
            objGlobal.setErrorMessage("StockTakingControl:loadAgingStockTakingRpt:" + ex);
            return null;
        }
        return scannedQty;
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

    public ArrayList<String> UserCategory(String Category, String Subcategory) {
        ArrayList<String> scanRfids = new ArrayList<String>();

        String[] subCat = Subcategory.split(", ");
        List<String> wordList = Arrays.asList(subCat);

        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:calcScanTotal: connection error");
            return null;
        }
        objAgeingStockTakingGlobal.setRfidScanQty(0);

        try {

            ResultSet myRs = null;

            if(subCat[0].equals("ALL") || Subcategory.equals("")){
                PreparedStatement statement = objGlobal.getConnection().prepareStatement("select * from tmpStocktakeRfidDetail a, LevelWiseDetails b where a.department = b.Department and CategoryLevel1 ='"+Category+"' and isnull (barcode, '') <>''");
                ResultSet rs = null;

                    rs = statement.executeQuery();
                    while (rs.next()) {
                        scanRfids.add(rs.getString("rfid"));

                }
            }else {
                PreparedStatement statement = objGlobal.getConnection().prepareStatement("select * from tmpStocktakeRfidDetail a, LevelWiseDetails b where a.department = b.Department and CategoryLevel2 in (?) and isnull (barcode, '') <>''");
                ResultSet rs = null;
                for (String cat : subCat) {
                    statement.setString(1, cat);
                    rs = statement.executeQuery();
                    while (rs.next()) {
                        scanRfids.add(rs.getString("rfid"));
                    }
                }
            }

            objAgeingStockTakingGlobal.setRfidScanQty(scanRfids.size());

           // myRs = statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return scanRfids;
    }
    public boolean saveScannedRfid(List<String> epcTidUser) {
        String scanRfids = "";
        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:calcScanTotal: connection error");
            return false;
        }
        try {

            String SQL_INSERT = "insert into tmpStocktakeRfidDetail (devicename,rfid,Remarks,itemcode,username) values( '" + objGlobal.getDeviceName() + "',?,'','','" + objGlobal.getUserName() + "') ";
            PreparedStatement statement = objGlobal.getConnection().prepareStatement(SQL_INSERT);
            int i = 0;
            for (String epc : epcTidUser) {
                statement.setString(1, epc);
                statement.addBatch();
                i++;
                if (i % 3000 == 0 || i == epcTidUser.size()) {
                    //myRs = statement.executeQuery();
                    statement.executeBatch();
                    statement.clearBatch();
                }

            }
            String DELETE = "with CTE as (select rfid,ROW_NUMBER() over(partition by rfid order by rfid) as duplicate from tmpStocktakeRfidDetail) Delete from CTE WHERE duplicate > 1";

            statement = objGlobal.getConnection().prepareStatement(DELETE);
            int j = 0;
            statement.addBatch();
            statement.executeUpdate();
            statement.clearBatch();
                // Display function to show the Resultset


                // if (!b_Result) return false;
                b_Result = dbConnection.insertUpdate("update tmpStocktakeRfidDetail set itemcode = b.itemcode, barcode = b.Barcode from tmpStocktakeRfidDetail a, RFPair b where a.rfid = b.RFID", objGlobal.getConnection());
                if (!b_Result) return false;
                b_Result = dbConnection.insertUpdate("update tmpStocktakeRfidDetail set rfidMaster = 'Y' from tmpStocktakeRfidDetail a, RFIDMaster b where a.rfid = b.RFID and a.RFID not in (select rfid from RFPair)", objGlobal.getConnection());
                if (!b_Result) return false;
                b_Result = dbConnection.insertUpdate("update tmpStocktakeRfidDetail set  division = c.Divisiony from tmpStocktakeRfidDetail a, ItemMaster b, USAPriority c where  b.GroupCode = c.groupCode and a.itemcode = b.ItemCode",objGlobal.getConnection());
                if (!b_Result) return false;
                b_Result = dbConnection.insertUpdate("update tmpStocktakeRfidDetail set  department = c.department  from tmpStocktakeRfidDetail a, ItemMaster b, USAPriority c where  b.GroupCode = c.groupCode and a.itemcode = b.ItemCode",objGlobal.getConnection());
                if (!b_Result) return false;
                return b_Result;

        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:" + ex.getMessage());
            return false;
        }
    }

        public ArrayList<String> ScannedDetails() {
        ArrayList<String> scanRfids = new ArrayList<String>();
        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:calcScanTotal: connection error");
            return null;
        }
        try {
            String Search = "select * from tmpStocktakeRfidDetail ";
            PreparedStatement statement = objGlobal.getConnection().prepareStatement(Search);
            ResultSet myRs;
            myRs = statement.executeQuery();
             while (myRs.next()) {
                scanRfids.add(myRs.getString("rfid"));
            }

        }catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:" + ex.getMessage());
            return null;
        }
        return scanRfids;

        }

}
