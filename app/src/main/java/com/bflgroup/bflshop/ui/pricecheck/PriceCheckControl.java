package com.bflgroup.bflshop.ui.pricecheck;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;
import java.sql.Statement;

public class PriceCheckControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private PriceCheckScanDetail objPriceCheckScanDetail = PriceCheckScanDetail.getInstance();

    private boolean b_Result;

    Statement stmt;
    boolean result;
    private ResultSet rs;

    public PriceCheckControl() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("PriceCheckControl : Local Connection error");
        }
        objGlobal.setCloudDbName("BFLDATA");
        result = dbConnection.connectCloudDb();
        if (result == false) {
            objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer : Cloud Connection error 1.0");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("PriceCheckControl.checkConnection : Connection error");
                return false;
            }
        }
        if (dbConnection.checkCloudConnectionClosed()==false) {
            objGlobal.setCloudDbName("BFLDATA");
            result = dbConnection.connectCloudDb();
            if (result == false) {
                objGlobal.setErrorMessage("PriceCheckControl.checkConnection : Cloud Connection error 1.1");
                return false;
            }
        }
        return true;
    }

    public boolean getItemDetails(String scan) {
        if (!checkConnection()) {
            return false;
        }
        try {
            String itemcode = "", groupcode = "", message="";
            if (scan.contains("/")) {
                String[] scanAr = scan.split("/");
                itemcode = scanAr[0];
            } else {
                itemcode = scan;
            }
            rs = dbConnection.getResultSet("select itemcode from rfpair where rfid='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                itemcode = rs.getString("itemcode");
            }
            rs = dbConnection.getResultSet("select itemcode,description,ItemType,groupcode,GrpName=isnull((select description from itemgroup where groupcode=a.groupcode),'')," +
                    "SalesPrice=isnull((select Top 1 Salesrate from Salesprice where Itemcode=a.Itemcode and costcode='" + objPosGlobal.getCostCode() + "'),0)," +
                    "stock=(select quantity from locstock where itemcode=a.itemcode and costcode='" + objPosGlobal.getCostCode() + "' and loccode='" + objPosGlobal.getLocCode() + "') from itemmaster a where itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objPriceCheckScanDetail.setItemcode(rs.getString("itemcode"));
                objPriceCheckScanDetail.setDescription(rs.getString("description"));
                objPriceCheckScanDetail.setGroup(rs.getString("GrpName"));
                objPriceCheckScanDetail.setStock(rs.getInt("stock"));
                objPriceCheckScanDetail.setPrice(rs.getFloat("SalesPrice"));
                groupcode = rs.getString("groupcode");
                objPriceCheckScanDetail.setDepartment("");
                objPriceCheckScanDetail.setDivision("");
                objPriceCheckScanDetail.setBrand("");
                objPriceCheckScanDetail.setiClass("");
                objPriceCheckScanDetail.setSubclass("");
                rs = dbConnection.getResultSet("select subclass=isnull(subclass,''),class='',Brand=isnull(Brand,''),season='' from itemmh where itemcode='" + objPriceCheckScanDetail.getItemcode() + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objPriceCheckScanDetail.setBrand(rs.getString("Brand"));
                    objPriceCheckScanDetail.setiClass(rs.getString("class"));
                    objPriceCheckScanDetail.setSubclass(rs.getString("subclass"));
                    objPriceCheckScanDetail.setSeason(rs.getString("season"));
                }
                rs = dbConnection.getResultSet("select department,DivisionY from usapriority where groupcode='" + groupcode + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objPriceCheckScanDetail.setDepartment(rs.getString("department"));
                    objPriceCheckScanDetail.setDivision(rs.getString("DivisionY"));
                }
                objPriceCheckScanDetail.setMessage("");
                if(message.isEmpty()){
                    rs = dbConnection.getResultSet("select Caption,Country,Shop from bfldata.dbo.ItemsPullOut where itemcode='" + itemcode + "' and " +
                            "country='" + objGlobal.getCountryCode() + "'", objGlobal.getCloudCon());
                    if(rs.next()) message=rs.getString("Caption");
                }
                if(message.isEmpty()){
                    rs = dbConnection.getResultSet("select Caption,Country,Shop from bfldata.dbo.ItemsPullOut where groupcode='" + groupcode + "' and " +
                            "country='" + objGlobal.getCountryCode() + "'", objGlobal.getCloudCon());
                    if(rs.next()) message=rs.getString("Caption");
                }
                if(message.isEmpty()){
                    rs = dbConnection.getResultSet("select Caption,Country,Shop from bfldata.dbo.ItemsPullOut where itemcode='" + itemcode + "' and " +
                            "Shop='" + objPosGlobal.getShopName() + "'", objGlobal.getCloudCon());
                    if(rs.next()) message=rs.getString("Caption");
                }
                if(message.isEmpty()){
                    rs = dbConnection.getResultSet("select Caption,Country,Shop from bfldata.dbo.ItemsPullOut where groupcode='" + groupcode + "' and " +
                            "Shop='" + objPosGlobal.getShopName() + "'", objGlobal.getCloudCon());
                    if(rs.next()) message=rs.getString("Caption");
                }
                objPriceCheckScanDetail.setMessage(message);
            } else {
                objGlobal.setErrorMessage("Invalid itemcode / Barcode / RFID");
                return false;
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
        return true;
    }
}
