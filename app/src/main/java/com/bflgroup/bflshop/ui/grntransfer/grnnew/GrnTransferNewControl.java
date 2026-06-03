package com.bflgroup.bflshop.ui.grntransfer.grnnew;

import android.text.TextUtils;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class GrnTransferNewControl {

    private DBConnection dbConnection = new DBConnection();

    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();

    private GrnTransferNewGlobal objGrnTransferNewGlobal = GrnTransferNewGlobal.getInstance();

    private boolean b_Result;
    private boolean firstGrn;
    private ResultSet rs;

    public GrnTransferNewControl() {
        checkConnection();
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("GrnTransferNewControl : Local Connection error");
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (!b_Result) {
            objGlobal.setErrorMessage("GrnTransferNewControl : Fetch Time error");
        }
        if (!objPosGlobal.getCloudMode().equals("Y")) {
            objGlobal.setCloudDbName("BFLDATA");
            b_Result = dbConnection.connectCloudDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("GrnShopTransferControl : Cloud Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validateTransferNumber(boolean view,boolean save, String scan, String ginno) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(ginno)) {
            objGlobal.setErrorMessage("Please enter GIN Number");
            return false;
        }
        if (TextUtils.isEmpty(scan)) {
            objGlobal.setErrorMessage("Please enter Transfer Number");
            return false;
        }
        try {
            if(objPosGlobal.getCloudMode().equals("Y")) {
                rs = dbConnection.getResultSet("select top 1 * from BFLDATA.dbo.GoodsIssue where sn='" + ginno + "' and ActualShop='" + objPosGlobal.getShopName() + "'", objGlobal.getCloudCon());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Invalid GIN Number, " + ginno);
                    return false;
                }
            }
            else {
                rs = dbConnection.getResultSet("select top 1 * from GoodsIssue where GINNo='" + ginno + "' and ShopName='" + objPosGlobal.getShopName() + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Invalid GIN Number, " + ginno);
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select top 1 TrfNo,TrfDate=convert(varchar,TrfDate,103),StoreIssue from transferheader where (trfno='" + scan + "' or StoreIssue='" + scan + "') order by trfdate desc", objGlobal.getConnection());
            if (rs.next()) {
                objGrnTransferNewGlobal.setTrfno(rs.getString("trfno"));
                objGrnTransferNewGlobal.setTrfdate(rs.getString("trfdate"));
                objGrnTransferNewGlobal.setToteid(rs.getString("storeissue"));
                objGrnTransferNewGlobal.setFromshop("HO");
            } else {
                objGlobal.setErrorMessage("Invalid Transfer Number/Toteid, " + scan);
                return false;
            }
            if(objPosGlobal.getCloudMode().equals("Y")) {
                rs = dbConnection.getResultSet("select top 1 * from BFLDATA.dbo.GoodsIssue where sn='" + ginno + "' and trfno='" + objGrnTransferNewGlobal.getTrfno() + "'", objGlobal.getCloudCon());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Transfer(" + objGrnTransferNewGlobal.getTrfno() + ") is not found in GIN(" + ginno + ")");
                    return false;
                }
            } else {
                rs = dbConnection.getResultSet("select top 1 * from GoodsIssue where GINNo='" + ginno + "' and trfno='" + objGrnTransferNewGlobal.getTrfno() + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Transfer(" + objGrnTransferNewGlobal.getTrfno() + ") is not found in GIN(" + ginno + ")");
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select * from grnheaderrf where trfno='" + objGrnTransferNewGlobal.getTrfno() + "'", objGlobal.getConnection());
            if (rs.next()) {
                if (!view) {
                    objGlobal.setErrorMessage("Tranfer already found in GRN, " + objGrnTransferNewGlobal.getTrfno());
                    return false;
                }
            } else {
                if (view) {
                    objGlobal.setErrorMessage("Tranfer not found in GRN, " + objGrnTransferNewGlobal.getTrfno());
                    return false;
                }
            }
            if (!save) {
                b_Result = dbConnection.insertUpdate("delete from tmpGrnScanItems where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                if (!b_Result) {
                    return false;
                }
                b_Result = dbConnection.insertUpdate("insert into tmpGrnScanItems(DeviceId,Itemcode,Description,Price,TrfQty,ScanQty,ScanPrice,SysStock) " +
                        "select '" + objGlobal.getDeviceName() + "',itemcode,(select description from itemmaster where ItemCode=a.itemcode),(select top 1 salesrate from SalesPrice " +
                        "where CostCode='" + objPosGlobal.getCostCode() + "' and ItemCode=a.itemcode order by trndate desc),Quantity,0,0,(select sum(quantity) from locstock where " +
                        "CostCode='" + objPosGlobal.getCostCode() + "' and ItemCode=a.itemcode) from TransferDetail a where TrfNo='" + objGrnTransferNewGlobal.getTrfno() + "'", objGlobal.getConnection());
                if (!b_Result) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:validateTransferNumber:" + ex.getMessage());
            return false;
        }
    }
    private boolean getLatestGrn(String formattedDate) {
        String grfNo = "";
        int autoSn = 0;
        try {
            String query = "", prefix = "1";
            String yr = formattedDate.substring(formattedDate.lastIndexOf("/") + 1);
            firstGrn=true;
            prefix = objPosGlobal.getShopLetter() + yr.substring(2, 4);
            rs = dbConnection.getResultSet("select entryno from grnheader where entrydate='" + formattedDate + "'", objGlobal.getConnection());
            if (rs.next()) {
                grfNo = rs.getString("entryno").toString();
                firstGrn=false;
            } else {
                if (objPosGlobal.getShopLetter().length() == 2) {
                    query = "select en=isnull(max(right(entryno, len(entryno)-4)),0)+1 from grnheader where entryno like '" + prefix + "%'";
                } else {
                    query = "select en=isnull(max(right(entryno, len(entryno)-3)),0)+1 from grnheader where entryno like '" + prefix + "%'";
                }
                rs = dbConnection.getResultSet(query, objGlobal.getConnection());
                if (rs.next()) {
                    autoSn = Integer.parseInt(rs.getString("en").toString());
                }
                grfNo = prefix + String.format("%05d", autoSn);
            }
            objGrnTransferNewGlobal.setLatestGrnNo(grfNo);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:getLatestGrn:" + ex.getMessage());
            return false;
        }
    }

    private boolean getLatestGrnRf() {
        String grfNo = "";
        int autoSn = 0;
        try {
            String query = "", prefix = "";
            String yr = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            prefix = objPosGlobal.getShopLetter() + yr.substring(2, 4);
            if (objPosGlobal.getShopLetter().length() == 2) {
                query = "select en=isnull(max(right(entryno, len(entryno)-4)),0)+1 from grnheaderrf where entryno like '" + prefix + "%'";
            } else {
                query = "select en=isnull(max(right(entryno, len(entryno)-3)),0)+1 from grnheaderrf where entryno like '" + prefix + "%'";
            }
            rs = dbConnection.getResultSet(query, objGlobal.getConnection());
            if (rs.next()) {
                autoSn = Integer.parseInt(rs.getString("en").toString());
            }
            grfNo = prefix + String.format("%05d", autoSn);
            objGrnTransferNewGlobal.setLatestGrnNoRf(grfNo);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:getLatestGrnRf:" + ex.getMessage());
            return false;
        }
    }

    public boolean validateRfid(String rfid,String trfno) {
        try {
            objGrnTransferNewGlobal.setScanBarcode("");
            rs = dbConnection.getResultSet("select barcode from tmpGrnScanItemsRfid where RFID='" + rfid + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("RFID already scan, (" + rfid + ", barcode:" + rs.getString("barcode") + ")");
                return false;
            }
            rs = dbConnection.getResultSet("select barcode from RFPair where RFID='" + rfid + "' and TrfNo='" + trfno + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGrnTransferNewGlobal.setScanBarcode(rs.getString("barcode"));
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:validateRfid:" + ex.getMessage());
            return false;
        }
    }

    public boolean validateScanItem(String itemcode,int qty, String rfid, float scanprice) {
        boolean itemfound = false;
        String description = "";
        float price = 0;
        int trfQty=0,scanQty=0,diffQty=0;
        int sysstock=0;
        objGrnTransferNewGlobal.setScanDescription("");
        objGrnTransferNewGlobal.setScanSysStock(0);
        objGrnTransferNewGlobal.setScanSysPrice(0);
        try {
            rs = dbConnection.getResultSet("select Itemcode,description,Price,ScanPrice,SysStock from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                description = rs.getString("description");
                price = rs.getFloat("Price");
                sysstock= rs.getInt("SysStock");
                itemfound = true;
            }
            if (!itemfound) {
                rs = dbConnection.getResultSet("select description,sprice=(select salesrate from SalesPrice where CostCode='" + objPosGlobal.getCostCode() + "' and ItemCode=a.itemcode)," +
                        "stock=(select quantity from locstock where itemcode=a.itemcode and costcode='" + objPosGlobal.getCostCode() + "') from ItemMaster a where ItemCode='" + itemcode + "'", objGlobal.getConnection());
                if (rs.next()) {
                    description = rs.getString("description");
                    price = rs.getFloat("sprice");
                    sysstock=rs.getInt("stock");
                }
                if(objPosGlobal.getGrnItemVal().equals("Y")) {
                    if(description.isEmpty()){
                        objGlobal.setErrorMessage("Can't proceed, Invalid item or description is blank");
                        return false;
                    }
                }
                if(objPosGlobal.getGrnSpVal().equals("Y")) {
                    if(price==0){
                        objGlobal.setErrorMessage("Can't proceed, Sales Price is 0");
                        return false;
                    }
                }
                b_Result = dbConnection.insertUpdate("insert into tmpGrnScanItems(DeviceId,Itemcode,Description,Price,TrfQty,ScanQty,ScanPrice,SysStock) values ('" + objGlobal.getDeviceName() + "'," +
                        "'" + itemcode + "','" + description + "'," + price + ",0,0," + scanprice + "," + sysstock + ")", objGlobal.getConnection());
                if (!b_Result) {
                    return false;
                }
            }
            b_Result = dbConnection.insertUpdate("update tmpGrnScanItems set ScanPrice=" + scanprice + ",ScanQty=ScanQty+" + qty + ",ScanDt=convert(varchar,getdate(),103),ScanTm=convert(varchar,getdate(),8) " +
                    "where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
            if(!rfid.isEmpty()) {
                b_Result = dbConnection.insertUpdate("insert into tmpGrnScanItemsRfid values('" + objGlobal.getDeviceName() + "','" + rfid + "','" + itemcode + "')", objGlobal.getConnection());
                if (!b_Result) {
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select TrfQty,ScanQty,DiffQty=ScanQty-TrfQty from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                trfQty = rs.getInt("TrfQty");
                scanQty = rs.getInt("ScanQty");
                diffQty = rs.getInt("DiffQty");
            }
            objGrnTransferNewGlobal.setScanDescription(description);
            objGrnTransferNewGlobal.setScanSysStock(sysstock);
            objGrnTransferNewGlobal.setScanSysPrice(price);
            objGrnTransferNewGlobal.setItemscanqty(scanQty);
            objGrnTransferNewGlobal.setItemtrfqty(trfQty);
            objGrnTransferNewGlobal.setItemdiffqty(diffQty);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:validateScanItem:" + ex.getMessage());
            return false;
        }
    }

    public boolean editItem(String itemcode,int qty) {
        try {
            b_Result = dbConnection.insertUpdate("update tmpGrnScanItems set ScanQty=" + qty + ",ScanDt=convert(varchar,getdate(),103),ScanTm=convert(varchar,getdate(),8) " +
                    "where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:editItem:" + ex.getMessage());
            return false;
        }
    }

    public boolean viewExistItem(String itemcode) {
        String description = "";
        float price = 0;
        int trfQty=0,scanQty=0,diffQty=0;
        objGrnTransferNewGlobal.setScanDescription("");
        //objGrnTransferNewGlobal.setScanPrice(0);
        try {
            rs = dbConnection.getResultSet("select Itemcode,description,Price,TrfQty,ScanQty,DiffQty=ScanQty-TrfQty from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and Itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                description = rs.getString("description");
                price = rs.getFloat("Price");
                trfQty = rs.getInt("TrfQty");
                scanQty = rs.getInt("ScanQty");
                diffQty = rs.getInt("DiffQty");
            }
            objGrnTransferNewGlobal.setScanDescription(description);
            //objGrnTransferNewGlobal.setScanPrice(price);
            objGrnTransferNewGlobal.setItemscanqty(scanQty);
            objGrnTransferNewGlobal.setItemtrfqty(trfQty);
            objGrnTransferNewGlobal.setItemdiffqty(diffQty);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:validateScanItem:" + ex.getMessage());
            return false;
        }
    }
    public boolean deleteAll() {
        try {
            b_Result = dbConnection.insertUpdate("delete from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
            b_Result = dbConnection.insertUpdate("delete from tmpGrnScanItemsRfid where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:delete:" + ex.getMessage());
            return false;
        }
    }

    public boolean findExcessAndMissing() {
        int excess = 0, missing = 0;
        try {
            rs = dbConnection.getResultSet("select excess=sum(ScanQty-TrfQty) from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and TrfQty-ScanQty<0", objGlobal.getConnection());
            if (rs.next()) {
                excess = rs.getInt("excess");
            }
            rs = dbConnection.getResultSet("select missing=abs(sum(ScanQty-TrfQty)) from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and TrfQty-ScanQty>0", objGlobal.getConnection());
            if (rs.next()) {
                missing = rs.getInt("missing");
            }
            objGrnTransferNewGlobal.setTotalExcess(excess);
            objGrnTransferNewGlobal.setTotalMissing(missing);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:findExcessAndMissing:" + ex.getMessage());
            return false;
        }
    }

    public String validateManagerVerify(String password) {
        String retVal="";
        if(password.isEmpty()){
            objGlobal.setErrorMessage("Please enter password");
            return retVal;
        }
        try {
            rs = dbConnection.getResultSet("select mgrname from managercode where pwd='" + password + "' and isnull(mgrname,'')<>''", objGlobal.getConnection());
            if (rs.next()) {
                retVal = rs.getString("mgrname");
            } else {
                objGlobal.setErrorMessage("Invalid password");
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:validateTransferNumber:" + ex);
        }
        return retVal;
    }

    public ArrayList<GrnTransferNewTrfScanItems> loadGrnTrfScanItems() {
        ArrayList<GrnTransferNewTrfScanItems> listGrnTransferNewTrfScanItems = new ArrayList<GrnTransferNewTrfScanItems>();
        int scanqty = 0, trfqty = 0, diffqty = 0;
        objGrnTransferNewGlobal.setDiffqty(0);
        objGrnTransferNewGlobal.setTrfqty(0);
        objGrnTransferNewGlobal.setScanqty(0);
        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferNewControl:loadGrnTrfScanItems: connection error");
            return null;
        }
        try {
            rs = dbConnection.getResultSet("select itemcode,scanqty,trfqty,diffqty=scanqty-trfqty from tmpGrnScanItems where deviceid='" + objGlobal.getDeviceName() + "' order by ScanDt desc,ScanTm desc", objGlobal.getConnection());
            while (rs.next()) {
                listGrnTransferNewTrfScanItems.add(new GrnTransferNewTrfScanItems(rs.getString("itemcode"), rs.getInt("scanqty"), rs.getInt("trfqty"), rs.getInt("diffqty")));
                scanqty = scanqty + rs.getInt("scanqty");
                trfqty = trfqty + rs.getInt("trfqty");
            }
            diffqty = scanqty - trfqty;
            objGrnTransferNewGlobal.setDiffqty(diffqty);
            objGrnTransferNewGlobal.setTrfqty(trfqty);
            objGrnTransferNewGlobal.setScanqty(scanqty);
            return listGrnTransferNewTrfScanItems;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:loadGrnTrfScanItems:" + ex.getMessage());
            return null;
        }
    }

    public boolean grnSave(String trfNo, String trfDate,String ginno) {
        if(!validateTransferNumber(false,true,trfNo,ginno)) {
            return false;
        }
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        try{
            rs = dbConnection.getResultSet("select top 1 TrfNo,TrfDate=convert(varchar,TrfDate,103),StoreIssue from transferheader where (trfno='" + trfNo + "' or StoreIssue='" + trfNo + "') order by trfdate desc", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Transfer Number/Toteid, " + trfNo);
                return false;
            }
            rs = dbConnection.getResultSet("select * from grnheaderrf where trfno='" + trfNo + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Tranfer already found in GRN (1), " + trfNo);
                return false;
            }
            rs = dbConnection.getResultSet("select * from GRNDetail where trfno='" + trfNo + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Tranfer already found in GRN (2), " + trfNo);
                return false;
            }
            rs = dbConnection.getResultSet("select * from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "' and ScanQty>0", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("No item is scanned for save");
                return false;
            }
        } catch (Exception e){
            objGlobal.setErrorMessage("GrnTransferNewControl:grnSave:try(1):" + e.getMessage());
            return false;
        }
        try {
            b_Result = getLatestGrnRf();
            if(!b_Result){
                return false;
            }
            b_Result = getLatestGrn(objGlobal.getServerDate());
            if(!b_Result){
                return false;
            }
            objGlobal.getConnection().setAutoCommit(false);
            b_Result = dbConnection.insertUpdate("insert into grnheaderrf values('" + objGrnTransferNewGlobal.getLatestGrnNoRf() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getUserName() + "'," +
                    "'" + trfNo + "')", objGlobal.getConnection());
            if (!b_Result) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            b_Result = dbConnection.insertUpdate("insert into grndetailrf select '" + objGrnTransferNewGlobal.getLatestGrnNoRf() + "','" + ginno + "','" + trfNo + "',itemcode,itemcode,'',TrfQty,ScanQty,(ScanQty-TrfQty) from tmpGrnScanItems where " +
                    "deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!b_Result) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            if (firstGrn) {
                b_Result = dbConnection.insertUpdate("insert into grnheader values('" + objGrnTransferNewGlobal.getLatestGrnNo() + "','" + objGlobal.getServerDate() + "',''," + objGlobal.getUserId() + ")", objGlobal.getConnection());
                if (!b_Result) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
            }
            b_Result = dbConnection.insertUpdate("insert into grndetail select '" + objGrnTransferNewGlobal.getLatestGrnNo() + "','" + trfNo + "','" + trfDate + "',sum(TrfQty),'',sum(ScanQty),sum(ScanQty-TrfQty) from tmpGrnScanItems where " +
                    "deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!b_Result) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception e) {
            try {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
            } catch (SQLException sqlException) {
                objGlobal.setErrorMessage("GrnTransferNewControl:grnSave:try(2):" + e.getMessage());
                return false;
            }
            objGlobal.setErrorMessage("GrnTransferNewControl:grnSave:try(3):" + e.getMessage());
            return false;
        }
    }
}