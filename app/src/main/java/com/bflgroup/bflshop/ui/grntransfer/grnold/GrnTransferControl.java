package com.bflgroup.bflshop.ui.grntransfer.grnold;

import android.text.TextUtils;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class GrnTransferControl {

    DBConnection dbConnection = new DBConnection();
    Global objGlobal = Global.getInstance();
    PosGlobal objPosGlobal = PosGlobal.getInstance();

    boolean result;
    boolean firstGrn;
    String query;
    String trfDate;
    String grnRfEnGlb;
    String missingBarcodeEntryNo;
    String trfNo;

    Statement stmt;
    ResultSet rs;

    public String getGrnRfEnGlb() {
        return grnRfEnGlb;
    }
    public void setGrnRfEnGlb(String grnRfEnGlb) {
        this.grnRfEnGlb = grnRfEnGlb;
    }
    public String getTrfNo() {
        return trfNo;
    }
    public void setTrfNo(String trfNo) {
        this.trfNo = trfNo;
    }
    public String getMissingBarcodeEntryNo() { return missingBarcodeEntryNo; }
    public void setMissingBarcodeEntryNo(String missingBarcodeEntryNo) { this.missingBarcodeEntryNo = missingBarcodeEntryNo; }

    public GrnTransferControl() {
        result = dbConnection.connectDb();
        if (result == false) {
            objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer : Local Connection error 1.0");
        }
        objGlobal.setCloudDbName("BFLDATA");
        result = dbConnection.connectCloudDb();
        if (result == false) {
            objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer : Cloud Connection error 1.0");
        }
    }

    public boolean validateShopTransfer(String trfNo,Boolean view) {
        if (TextUtils.isEmpty(trfNo)) {
            objGlobal.setErrorMessage("Please enter entry number from target shop");
            return false;
        }
        if (dbConnection.checkConnectionClosed()==false) {
            result = dbConnection.connectDb();
            if (result == false) {
                objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer : Local Connection error 1.1");
                return false;
            }
        }
        if (dbConnection.checkCloudConnectionClosed()==false) {
            objGlobal.setCloudDbName("BFLDATA");
            result = dbConnection.connectCloudDb();
            if (result == false) {
                objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer : Cloud Connection error 1.1");
                return false;
            }
        }
        try {
            String[] arrOfStr = trfNo.split("/", 0);
            String shopLetter = arrOfStr[0].replace("RTN", "");
            rs = dbConnection.getResultSet("select DataName,ShopName from bfldata.dbo.datasettings where ShopLetter='" + shopLetter + "'", objGlobal.getCloudCon());
            if (rs.next()) {
                objGlobal.setCloudDbName(rs.getString("DataName").toString());
            } else {
                objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer1 : Shop not found in datasettings, " + shopLetter);
                return false;
            }
            result = dbConnection.connectCloudDb();
            if (result == false) {
                objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer2 : Connection error");
                return false;
            }
            rs = dbConnection.getResultSet("select *,RecUserId=isnull(RecUserId,0) from storeheader where entryno='" + trfNo + "' and " +
                    "shopname='" + objPosGlobal.getShopName() + "'", objGlobal.getCloudCon());
            if (rs.next()) {
                if(String.valueOf(rs.getInt("RecUserId")).equals("0")) {
                    if(view) {
                        objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer3 : Entry number not yet save, " + trfNo);
                        return false;
                    }
                } else{
                    if(!view) {
                        objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer3 : Entry number already save, " + trfNo);
                        return false;
                    }
                }
            } else {
                objGlobal.setErrorMessage("GrnTransferControl.validateShopTransfer3 : Invalid Entry number, " + trfNo);
                return false;
            }
            objGlobal.setToteTrfNo(trfNo);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:validateShopTransfer4 :" + ex.toString());
            return false;
        }
    }

    public boolean validateTransferNumber(String trfNo,Boolean view) {
        if (dbConnection.checkConnectionClosed()==false) {
            result = dbConnection.connectDb();
            if (result == false) {
                objGlobal.setErrorMessage("GrnTransferControl.validateTransferNumber : Connection error");
                return false;
            }
        }
        if (TextUtils.isEmpty(trfNo)) {
            objGlobal.setErrorMessage("Please enter transfer number");
            return false;
        }
        try {
            query = "select top 1 * from transferheader where (trfno='" + trfNo + "' or StoreIssue='" + trfNo + "') order by trfdate desc";
            stmt = objGlobal.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                objGlobal.setToteTrfNo(rs.getString("trfno"));
                java.util.Date date = rs.getDate("trfdate");
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                trfDate = df.format(date);
            } else {
                objGlobal.setErrorMessage("Invalid Transfer Number, " + trfNo);
                return false;
            }
            query = "select * from grnheaderrf where trfno='" + trfNo + "'";
            stmt = objGlobal.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                if(!view) {
                    objGlobal.setErrorMessage("Tranfer already found in GRN, " + trfNo);
                    return false;
                }
            } else {
                if(view) {
                    objGlobal.setErrorMessage("Tranfer not found in GRN, "+ trfNo);
                    return false;
                }
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:validateTransferNumber:" + ex.toString());
            return false;
        }
        return true;
    }

    String getLatestGrn(String formattedDate) {
        String grfNo = "";
        int autoSn = 0;
        try {
            String query = "", prefix = "1";
            String yr = "";
            rs = dbConnection.getResultSet("select yr=right(year(getdate()),2)",objGlobal.getConnection());
            if(rs.next()) yr=rs.getString("yr");
            prefix = objPosGlobal.getShopLetter() + yr.substring(2, 4);
            query = "select entryno from grnheader where entrydate='" + formattedDate + "'";
            stmt = objGlobal.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                grfNo = rs.getString("entryno").toString();
                firstGrn = false;
            } else {
                if (objPosGlobal.getShopLetter().length() == 2) {
                    query = "select en=isnull(max(right(entryno, len(entryno)-4)),0)+1 from grnheader where entryno like '" + prefix + "%'";
                } else {
                    query = "select en=isnull(max(right(entryno, len(entryno)-3)),0)+1 from grnheader where entryno like '" + prefix + "%'";
                }
                stmt = objGlobal.getConnection().createStatement();
                rs = stmt.executeQuery(query);
                if (rs.next()) {
                    autoSn = Integer.parseInt(rs.getString("en").toString());
                }
                grfNo = prefix + String.format("%05d", autoSn);
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:getLatestGrn:" + ex.toString());
            return "";
        }
        return grfNo;
    }

    String getLatestGrnRf() {
        String grfNo = "";
        int autoSn = 0;
        try {
            String query = "", prefix = "";
            String yr = "";
            rs = dbConnection.getResultSet("select yr=right(year(getdate()),2)", objGlobal.getConnection());
            if (rs.next()) yr = rs.getString("yr");
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
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:getLatestGrnRf:" + ex);
            return "";
        }
        return grfNo;
    }

    public boolean validItemcode(String itemcode) {
        try {
            query = "select description from itemmaster where itemcode='" + itemcode + "'";
            stmt = objGlobal.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferControl:validateTransferNumber:" + ex.toString());
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
            query = "select mgrname from managercode where pwd='" + password + "' and isnull(mgrname,'')<>''";
            stmt = objGlobal.getConnection().createStatement();
            rs = stmt.executeQuery(query);
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

    private boolean saveMissingBarcodeEntry(String trfNo) {
        try {
            rs = dbConnection.getResultSet("select a.*,b.SalesPrice,b.RecQty from itemmaster a,StoreDetail b where a.ItemCode=b.ItemCode and b.EntryNo='" + trfNo + "' and b.RecQty>0", objGlobal.getCloudCon());
            while (rs.next()) {
                result = dbConnection.insertUpdate("insert into MissingBarcodeDetail(EntryNo,Itemcode,Itemname,quantity,Price,rfid) values ('" + trfNo + "','" + rs.getString("itemcode") + "'," +
                        "'" + rs.getString("description") + "'," + rs.getInt("RecQty") + "," + rs.getFloat("SalesPrice") + ",'')", objGlobal.getConnection());
                if (result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            result = dbConnection.insertUpdate("insert into MissingBarcodeHeader (EntryNo,Trndate,Time1,Remarks,PreparedBy,ShopManager,UserId,Costcode,Loccode,ReceivedBy,ReceivedDate) values " +
                    "('" + trfNo + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "','PDA-Received-" + trfNo + "','" + objGlobal.getUserName() + "','" + objGlobal.getUserName() + "'," +
                    "" + objGlobal.getUserId() + ",'" + objPosGlobal.getCostCode() + "','" + objPosGlobal.getLocCode() + "','" + objGlobal.getUserName() + "','" + objGlobal.getServerDate() + "')", objGlobal.getConnection());
            if (result == false) {
                return false;
            }
            setMissingBarcodeEntryNo(trfNo);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
    }

    public boolean saveShopTransfer(String trfNo, ArrayList<GrnTransferScanItemsAll> listGrnTransferScanItemsAll) {
        result = validateShopTransfer(trfNo,false);
        if (result == false) {
            return false;
        }
        String itemCode = "";
        int scan = 0, trf = 0, diff = 0;
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        if (listGrnTransferScanItemsAll.isEmpty()) {
            objGlobal.setErrorMessage("GrnTransferControl.saveShopTransfer : No records");
            return false;
        }
        result = dbConnection.insertUpdate("delete from tmpDiffDetails where TrfNo='" + trfNo + "'", objGlobal.getCloudCon());
        if (result == false) {
            return false;
        }
        for (int i = 0; i < listGrnTransferScanItemsAll.size(); i++) {
            itemCode = listGrnTransferScanItemsAll.get(i).itemCode;
            scan = listGrnTransferScanItemsAll.get(i).scanQty;
            trf = listGrnTransferScanItemsAll.get(i).trfQty;
            diff = listGrnTransferScanItemsAll.get(i).diffQty;
            result = dbConnection.insertUpdate("insert into tmpDiffDetails values('" + itemCode + "'," + scan + "," + trf + "," + diff + "," +
                    "" + objGlobal.getUserId() + ",'" + trfNo + "')", objGlobal.getCloudCon());
            if (result == false) {
                return false;
            }
        }
        try {
            //String missingBarcodeEntryNo = getLatestMissingBarcodeEntryNo();
            objGlobal.getCloudCon().setAutoCommit(false);
            objGlobal.getConnection().setAutoCommit(false);
            rs = dbConnection.getResultSet("select a.*,b.SalesPrice from itemmaster a,StoreDetail b where a.ItemCode=b.ItemCode and b.EntryNo='" + trfNo + "'", objGlobal.getCloudCon());
            while (rs.next()) {
                result = dbConnection.insertUpdate("delete from ItemMaster where ItemCode='" + rs.getString("ItemCode") + "'", objGlobal.getConnection());
                if (result == false) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
                result = dbConnection.insertUpdate("insert into ItemMaster(ItemCode,Description,ShortName,UnitCode,GroupCode,CatCode,OpeningDate,ToPrint,batch) " +
                        "values ('" + rs.getString("ItemCode") + "','" + rs.getString("Description") + "','" + rs.getString("ShortName") + "','" + rs.getString("UnitCode") + "'," +
                        "'" + rs.getString("GroupCode") + "','" + rs.getString("CatCode") + "','" + objGlobal.getServerDate() + "','" + rs.getString("ToPrint") + "',0)", objGlobal.getConnection());
                if (result == false) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
                result = dbConnection.insertUpdate("insert into oldsalesprice select costcode,itemcode,salesrate,retailrate from salesprice where " +
                        "itemcode='" + rs.getString("itemcode") + "' and costcode='" + objPosGlobal.getCostCode() + "'", objGlobal.getConnection());
                if (result == false) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
                result = dbConnection.insertUpdate("delete from salesprice where itemcode='" + rs.getString("itemcode") + "' and " +
                        "costcode='" + objPosGlobal.getCostCode() + "'", objGlobal.getConnection());
                if (result == false) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
                result = dbConnection.insertUpdate("insert into salesprice values('" + objPosGlobal.getCostCode() + "','" + rs.getString("itemcode") + "'," +
                        "" + rs.getFloat("SalesPrice") + "," + rs.getFloat("SalesPrice") + ",'N','" + objGlobal.getServerDate() + "')", objGlobal.getConnection());
                if (result == false) {
                    objGlobal.getCloudCon().rollback();
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            result = dbConnection.insertUpdate("insert into StoreDetail select '" + trfNo + "',itemcode,0,0,0 from tmpDiffDetails where " +
                    "TrfNo='" + trfNo + "' and itemcode not in(select itemcode from StoreDetail where entryno='" + trfNo + "') group by itemcode", objGlobal.getCloudCon());
            if (result == false) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            result = dbConnection.insertUpdate("update StoreDetail set RecQty=a.scan from tmpDiffDetails a,StoreDetail b where a.TrfNo='" + trfNo + "' and b.EntryNo='" + trfNo + "' and a.itemcode=b.itemcode", objGlobal.getCloudCon());
            if (result == false) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            result = dbConnection.insertUpdate("update StoreHeader set RecUserId=" + objGlobal.getUserId() + ",RecDateTime=getdate() where " +
                    "EntryNo='" + trfNo + "'", objGlobal.getCloudCon());
            if (result == false) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            result = dbConnection.insertUpdate("insert into bfldata.dbo.ShopToShopTransfer(ShopName,EntryNo,Trndate,TrnTime,TargetShop,TrfIssueNo,TrfRecNo,Category) select ShopFrom,EntryNo," +
                    "convert(varchar,getdate(),103),convert(varchar,getdate(),8),ShopName,'','',TrfNo1 from StoreHeader where EntryNo='" + trfNo + "'", objGlobal.getCloudCon());
            if (result == false) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            result = saveMissingBarcodeEntry(trfNo);
            if (result == false) {
                objGlobal.getCloudCon().rollback();
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getCloudCon().commit();
            objGlobal.getConnection().commit();
            objGlobal.getCloudCon().setAutoCommit(true);
            objGlobal.getConnection().setAutoCommit(true);
            setTrfNo(trfNo);
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

    public boolean saveGrn(String trfNo, ArrayList<GrnTransferScanItemsAll> listGrnTransferScanItemsAll) {
        result = validateTransferNumber(trfNo,false);
        if (result == false) {
            return false;
        }
        firstGrn = true;
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        String grnRfEn = getLatestGrnRf();
        if (grnRfEn == "") {
            return false;
        }
        String grnEn = getLatestGrn(objGlobal.getServerDate());
        if (grnEn == "") {
            return false;
        }
        String itemCode = "";
        int scan = 0, trf = 0, diff = 0;
        result = dbConnection.insertUpdate("delete from tmpDiffDetails where userid=" + objGlobal.getUserId(), objGlobal.getConnection());
        if (result == false) {
            return false;
        }
        if (listGrnTransferScanItemsAll.isEmpty()) {
            objGlobal.setErrorMessage("GrnTransferControl.saveGrn : No records");
            return false;
        }
        for (int i = 0; i < listGrnTransferScanItemsAll.size(); i++) {
            itemCode = listGrnTransferScanItemsAll.get(i).itemCode;
            scan = listGrnTransferScanItemsAll.get(i).scanQty;
            trf = listGrnTransferScanItemsAll.get(i).trfQty;
            diff = listGrnTransferScanItemsAll.get(i).diffQty;
            itemCode = itemCode.replaceAll("[^a-zA-Z0-9]", " ").trim();
            result = dbConnection.insertUpdate("insert into tmpDiffDetails values('" + itemCode + "'," + scan + "," + trf + "," + diff + "," +
                    "" + objGlobal.getUserId() + ",'" + trfNo + "')", objGlobal.getConnection());
            if (result == false) {
                return false;
            }
        }
        try {
            objGlobal.getConnection().setAutoCommit(false);
            result = dbConnection.insertUpdate("insert into grnheaderrf values('" + grnRfEn + "','" + objGlobal.getServerDate() + "','" + objGlobal.getUserName() + "','" + trfNo + "')", objGlobal.getConnection());
            if (result == false) {
                objGlobal.getConnection().rollback();
                return false;
            }
            result = dbConnection.insertUpdate("insert into grndetailrf select '" + grnRfEn + "',0,'" + trfNo + "',itemcode,itemcode,'',trf,scan,diff from tmpDiffDetails where trfno='" + trfNo + "' and userid=" + objGlobal.getUserId(), objGlobal.getConnection());
            if (result == false) {
                objGlobal.getConnection().rollback();
                return false;
            }
            if (firstGrn == true) {
                result = dbConnection.insertUpdate("insert into grnheader values('" + grnEn + "','" + objGlobal.getServerDate() + "',''," + objGlobal.getUserId() + ")", objGlobal.getConnection());
                if (result == false) {
                    objGlobal.getConnection().rollback();
                    return false;
                }
            }
            result = dbConnection.insertUpdate("insert into grndetail select '" + grnEn + "','" + trfNo + "','" + trfDate + "',sum(trf),'',sum(scan),sum(diff) from tmpDiffDetails where trfno='" + trfNo + "' and userid=" + objGlobal.getUserId(), objGlobal.getConnection());
            if (result == false) {
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            setGrnRfEnGlb(grnRfEn);
            setTrfNo(trfNo);
            return true;
        } catch (Exception e) {
            try {
                objGlobal.getConnection().rollback();
            } catch (SQLException sqlException) {
                return false;
            }
            return false;
        }
    }
}
