package com.bflgroup.bflshop.ui.grntransfer.grnnew;

import android.text.TextUtils;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GrnShopTransferControl {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private GrnTransferNewGlobal objGrnTransferNewGlobal = GrnTransferNewGlobal.getInstance();
    private boolean b_Result;
    private ResultSet rs;
    public GrnShopTransferControl() {
        checkConnection();
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("GrnShopTransferControl : Local Connection error");
            return false;
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (!b_Result) {
            objGlobal.setErrorMessage("GrnShopTransferControl : Fetch Time error");
            return false;
        }
        objGlobal.setCloudDbName("BFLDATA");
        b_Result = dbConnection.connectCloudDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("GrnShopTransferControl : Cloud Connection error");
            return false;
        }
        return true;
    }

    public boolean validateShopTransfer(Boolean view,boolean save, String entryno) {
        if (TextUtils.isEmpty(entryno)) {
            objGlobal.setErrorMessage("Please enter entry number from target shop");
            return false;
        }
        if (!checkConnection()) {
            return false;
        }
        try {
            String[] arrOfStr = entryno.split("/", 0);
            String shopLetter = arrOfStr[0].replace("RTN", "");
            rs = dbConnection.getResultSet("select DataName,ShopName from bfldata.dbo.datasettings where ShopLetter='" + shopLetter + "'", objGlobal.getCloudCon());
            if (rs.next()) {
                objGlobal.setCloudDbName(rs.getString("DataName").toString());
            } else {
                objGlobal.setErrorMessage("GrnShopTransferControl.validateShopTransfer1 : Shop not found in datasettings, " + shopLetter);
                return false;
            }
            b_Result = dbConnection.connectCloudDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("GrnShopTransferControl.validateShopTransfer2 : Connection error");
                return false;
            }
            rs = dbConnection.getResultSet("select entryno,TrfDate=convert(varchar,EntryDate,103),RecUserId=isnull(RecUserId,0),ShopFrom from storeheader where entryno='" + entryno + "' and " +
                    "shopname='" + objPosGlobal.getShopName() + "'", objGlobal.getCloudCon());
            if (rs.next()) {
                objGrnTransferNewGlobal.setTrfno(rs.getString("entryno"));
                objGrnTransferNewGlobal.setTrfdate(rs.getString("trfdate"));
                objGrnTransferNewGlobal.setToteid("");
                objGrnTransferNewGlobal.setFromshop(rs.getString("ShopFrom"));
                if (String.valueOf(rs.getInt("RecUserId")).equals("0")) {
                    if (view) {
                        objGlobal.setErrorMessage("GrnShopTransferControl.validateShopTransfer3 : Entry number not yet save, " + entryno);
                        return false;
                    }
                } else {
                    if (!view) {
                        objGlobal.setErrorMessage("GrnShopTransferControl.validateShopTransfer3 : Entry number already save, " + entryno);
                        return false;
                    }
                }
            } else {
                objGlobal.setErrorMessage("GrnShopTransferControl.validateShopTransfer3 : Invalid Entry number, " + entryno);
                return false;
            }
            if (!save) {
                b_Result = dbConnection.insertUpdate("delete from tmpGrnScanItems where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                if (!b_Result) {
                    return false;
                }
                rs = dbConnection.getResultSet("select itemcode,Description=(select description from itemmaster where ItemCode=a.itemcode),Price=SalesPrice,TrfQty=Quantity,ScanQty=0 from storedetail a where entryno='" + entryno + "'", objGlobal.getCloudCon());
                while (rs.next()) {
                    b_Result = dbConnection.insertUpdate("insert into tmpGrnScanItems(DeviceId,Itemcode,Description,Price,TrfQty,ScanQty) values ('" + objGlobal.getDeviceName() + "','" + rs.getString("Itemcode") + "'," +
                            "'" + rs.getString("Description") + "'," + rs.getString("Price") + "," + rs.getString("TrfQty") + "," + rs.getString("ScanQty") + ")", objGlobal.getConnection());
                    if (!b_Result) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnShopTransferControl:validateShopTransfer4 :" + ex.getMessage());
            return false;
        }
    }

    public boolean saveShopTransfer(String entryno) {
        if(!validateShopTransfer(false,true,entryno)){
            return false;
        }
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        b_Result = dbConnection.insertUpdate("delete from bfldata.dbo.tmpDiffDetailsNew where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getCloudCon());
        if (!b_Result) {
            return false;
        }

        try {
            rs = dbConnection.getResultSet("select itemcode,TrfQty,ScanQty,DiffQty=ScanQty-TrfQty from tmpGrnScanItems where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            while (rs.next()) {
                b_Result = dbConnection.insertUpdate("insert into bfldata.dbo.tmpDiffDetailsNew(deviceid,Itemcode,scan,trf,diff,EntryNo) values ('" + objGlobal.getDeviceName() + "'," +
                        "'" + rs.getString("itemcode") + "'," + rs.getInt("ScanQty") + "," + rs.getInt("TrfQty") + "," + rs.getInt("DiffQty") + "," +
                        "'" + entryno + "')", objGlobal.getCloudCon());
                if (!b_Result) {
                    return false;
                }
            }
            objGlobal.getCloudCon().setAutoCommit(false);
            objGlobal.getConnection().setAutoCommit(false);
            rs = dbConnection.getResultSet("select a.*,b.SalesPrice from itemmaster a,StoreDetail b where a.ItemCode=b.ItemCode and b.EntryNo='" + entryno + "'", objGlobal.getCloudCon());
            while (rs.next()) {
                b_Result = dbConnection.insertUpdate("delete from ItemMaster where ItemCode='" + rs.getString("ItemCode") + "'", objGlobal.getConnection());
                if (!b_Result) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
                b_Result = dbConnection.insertUpdate("insert into ItemMaster(ItemCode,Description,ShortName,UnitCode,GroupCode,CatCode,OpeningDate,ToPrint,batch) values ('" + rs.getString("ItemCode") + "'," +
                        "'" + rs.getString("Description") + "','" + rs.getString("ShortName") + "','" + rs.getString("UnitCode") + "'," +
                        "'" + rs.getString("GroupCode") + "','" + rs.getString("CatCode") + "','" + objGlobal.getServerDate() + "','" + rs.getString("ToPrint") + "',0)", objGlobal.getConnection());
                if (!b_Result) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
                b_Result = dbConnection.insertUpdate("insert into oldsalesprice select costcode,itemcode,salesrate,retailrate from salesprice where " +
                        "itemcode='" + rs.getString("itemcode") + "' and costcode='" + objPosGlobal.getCostCode() + "'", objGlobal.getConnection());
                if (!b_Result) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
                b_Result = dbConnection.insertUpdate("delete from salesprice where itemcode='" + rs.getString("itemcode") + "' and " +
                        "costcode='" + objPosGlobal.getCostCode() + "'", objGlobal.getConnection());
                if (!b_Result) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
                b_Result = dbConnection.insertUpdate("insert into salesprice values('" + objPosGlobal.getCostCode() + "','" + rs.getString("itemcode") + "'," +
                        "" + rs.getFloat("SalesPrice") + "," + rs.getFloat("SalesPrice") + ",'N','" + objGlobal.getServerDate() + "')", objGlobal.getConnection());
                if (!b_Result) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            b_Result = dbConnection.insertUpdate("insert into StoreDetail select '" + entryno + "',itemcode,0,0,0 from bfldata.dbo.tmpDiffDetailsNew where " +
                    "EntryNo='" + entryno + "' and itemcode not in(select itemcode from StoreDetail where entryno='" + entryno + "') group by itemcode", objGlobal.getCloudCon());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = dbConnection.insertUpdate("update StoreDetail set RecQty=a.scan from bfldata.dbo.tmpDiffDetailsNew a,StoreDetail b where a.DeviceId='" + objGlobal.getDeviceName() + "' and a.EntryNo=b.EntryNo " +
                    "and b.EntryNo='" + entryno + "' and a.itemcode=b.itemcode", objGlobal.getCloudCon());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = dbConnection.insertUpdate("update StoreHeader set RecUserId=" + objGlobal.getUserId() + ",RecDateTime=getdate() where EntryNo='" + entryno + "'", objGlobal.getCloudCon());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = dbConnection.insertUpdate("insert into bfldata.dbo.ShopToShopTransfer(ShopName,EntryNo,Trndate,TrnTime,TargetShop,TrfIssueNo,TrfRecNo,Category) select ShopFrom,EntryNo," +
                    "convert(varchar,getdate(),103),convert(varchar,getdate(),8),ShopName,'','',TrfNo1 from StoreHeader where EntryNo='" + entryno + "'", objGlobal.getCloudCon());
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = saveMissingBarcodeEntry(entryno);
            if (!b_Result) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getCloudCon().commit();
            objGlobal.getConnection().commit();
            objGlobal.getCloudCon().setAutoCommit(true);
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception e) {
            try {
                objGlobal.setErrorMessage(e.toString());
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
            } catch (SQLException sqlException) {
                objGlobal.setErrorMessage(sqlException.toString());
                return false;
            }
            return false;
        }
    }

    private boolean saveMissingBarcodeEntry(String trfNo) {
        try {
            rs = dbConnection.getResultSet("select a.*,b.SalesPrice,b.RecQty from itemmaster a,StoreDetail b where a.ItemCode=b.ItemCode and b.EntryNo='" + trfNo + "' and b.RecQty>0", objGlobal.getCloudCon());
            while (rs.next()) {
                b_Result  = dbConnection.insertUpdate("insert into MissingBarcodeDetail(EntryNo,Itemcode,Itemname,quantity,Price,rfid) values ('" + trfNo + "','" + rs.getString("itemcode") + "'," +
                        "'" + rs.getString("description") + "'," + rs.getInt("RecQty") + "," + rs.getFloat("SalesPrice") + ",'')", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            b_Result = dbConnection.insertUpdate("insert into MissingBarcodeHeader (EntryNo,Trndate,Time1,Remarks,PreparedBy,ShopManager,UserId,Costcode,Loccode,ReceivedBy,ReceivedDate) values " +
                    "('" + trfNo + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','PDA-Received-" + trfNo + "','" + objGlobal.getUserName() + "','" + objGlobal.getUserName() + "'," +
                    "" + objGlobal.getUserId() + ",'" + objPosGlobal.getCostCode() + "','" + objPosGlobal.getLocCode() + "','" + objGlobal.getUserName() + "','" + objGlobal.getServerDate() + "')", objGlobal.getConnection());
            if (b_Result == false) {
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
    }


}
