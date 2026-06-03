package com.bflgroup.bflshop.ui.ginverification;

import android.text.TextUtils;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;
import com.bflgroup.bflshop.ui.grntransfer.grnnew.GrnTransferNewGlobal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
//import java.util.Calendar;

public class GinVerificationControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private GrnTransferNewGlobal objGrnTransferNewGlobal = GrnTransferNewGlobal.getInstance();
    private GinVerificationGlobal objGinVerificationGlobal = GinVerificationGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;
    private boolean firstGrn;

    public GinVerificationControl() {
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("GinVerificationControl.connectDb : Cloud Connection error");
        }
        objGlobal.setCloudDbName("BFLDATA");
        b_Result = dbConnection.connectCloudDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("ShopReturnsControl : Cloud Connection error 1.0");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (!dbConnection.checkConnectionClosed()) {
            b_Result = dbConnection.connectDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("GinVerificationControl.connectDb : Connection error");
                return false;
            }
        }
        if (!dbConnection.checkCloudConnectionClosed()) {
            objGlobal.setCloudDbName("BFLDATA");
            b_Result = dbConnection.connectCloudDb();
            if (!b_Result) {
                objGlobal.setErrorMessage("ShopReturnsControl.checkConnection : Cloud Connection error 1.1");
                return false;
            }
        }
        return true;
    }

    public boolean validateGin(String ginNo) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(ginNo)) {
            objGlobal.setErrorMessage("Gin Number is empty");
            return false;
        }
        try {
            /*rs = dbConnection.getResultSet("select * from GinVerify where GinNo=" + ginNo, objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("GIN Verification already done");
                return false;
            }*/
            if (!dbConnection.insertUpdate("delete from tmpGinVerify where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            rs = dbConnection.getResultSet("select * from GoodsIssue where ActualShop='" + objPosGlobal.getShopName() + "' and sn=" + ginNo, objGlobal.getCloudCon());
            while (rs.next()) {
                if (!dbConnection.insertUpdate("insert into tmpGinVerify(DeviceId,GinNo,ShopName,PalletNo,TrfNo,ToteId,Verified,OldVerify,skipsku) values ('" + objGlobal.getDeviceName() + "'," +
                        "'" + rs.getString("sn") + "','" + rs.getString("shopname") + "','" + rs.getString("palletno") + "'," +
                        "'" + rs.getString("trfno") + "','" + rs.getString("ToteId") + "','N','N','')", objGlobal.getConnection())) {
                    return false;
                }
            }
            if (!dbConnection.insertUpdate("update tmpGinVerify set toteid=b.StoreIssue from tmpGinVerify a,TransferHeader b where a.trfno=b.TrfNo and " +
                    "deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("update tmpGinVerify set OldVerify='Y',Verified='Y' from tmpGinVerify a,GinVerify b where " +
                    "deviceid='" + objGlobal.getDeviceName() + "' and a.trfno=b.trfno", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:validateGin:" + ex);
            return false;
        }
    }

    public boolean saveGinVerification(String skipSkuGrn, String ginno) {
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorMessage("saveGinVerification:001:");
                return false;
            }
            objGlobal.getConnection().setAutoCommit(false);
            if (!dbConnection.insertUpdate("insert into GinVerify(GinNo,Trndate,TrfNo,ToteId,PalletNo,VerifyTime,UserId,skipsku) select GinNo,'" + objGlobal.getServerDate() + "',TrfNo,ToteId,PalletNo," +
                    "VerifyTime," + objGlobal.getUserId() + ",skipsku from tmpGinVerify where deviceid='" + objGlobal.getDeviceName() + "' and Verified='Y' and OldVerify='N'", objGlobal.getConnection())) {
                objGlobal.getConnection().rollback();
                return false;
            }

            b_Result = getLatestGrn(objGlobal.getServerDate());
            if (!b_Result) {
                return false;
            }
            if (firstGrn) {
                b_Result = dbConnection.insertUpdate("insert into GRNHeader values('" + objGrnTransferNewGlobal.getLatestGrnNo() + "','" + objGlobal.getServerDate() + "'," +
                        "'GIN No: " + ginno + "'," + objGlobal.getUserId() + ")", objGlobal.getConnection());
                if (!b_Result) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select trfno from tmpGinVerify where deviceid='" + objGlobal.getDeviceName() + "' and skipsku='Y' and Verified='Y' and OldVerify='N'", objGlobal.getConnection());
            while (rs.next()) {
                b_Result = getLatestGrnRf(objGlobal.getServerDate());
                if (!b_Result) {
                    return false;
                }
                b_Result = dbConnection.insertUpdate("insert into GRNdetail select '" + objGrnTransferNewGlobal.getLatestGrnNo() + "',trfno,'" + objGlobal.getServerDate() + "'," +
                        "sum(Quantity),'',sum(Quantity),0 from TransferDetail where TrfNo='" + rs.getString("trfno") + "' group by trfno", objGlobal.getConnection());
                if (!b_Result) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
                b_Result = dbConnection.insertUpdate("insert into GRNHeaderRF select '" + objGrnTransferNewGlobal.getLatestGrnNoRf() + "','" + objGlobal.getServerDate() + "'," +
                        "" + objGlobal.getUserId() + ",'" + rs.getString("trfno") + "'", objGlobal.getConnection());
                if (!b_Result) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
                b_Result = dbConnection.insertUpdate("insert into GRNdetailRF select '" + objGrnTransferNewGlobal.getLatestGrnNoRf() + "','" + ginno + "',TrfNo,Itemcode,'','',quantity,quantity,0 " +
                        "from TransferDetail where TrfNo='" + rs.getString("trfno") + "'", objGlobal.getConnection());
                if (!b_Result) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("BinBatchInControl:saveBatchIn:ex:" + ex);
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("BinBatchInControl:saveBatchIn:e:" + e);
                return false;
            }
            return false;
        }
    }

    private boolean getLatestGrn(String formattedDate) {
        String grfNo = "";
        int autoSn = 0;
        try {
            String query = "", prefix = "1";
            String yr = formattedDate.substring(formattedDate.lastIndexOf("/") + 1);
            firstGrn = true;
            prefix = objPosGlobal.getShopLetter() + yr.substring(2, 4);
            rs = dbConnection.getResultSet("select entryno from grnheader where entrydate='" + formattedDate + "'", objGlobal.getConnection());
            if (rs.next()) {
                grfNo = rs.getString("entryno").toString();
                firstGrn = false;
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

    private boolean getLatestGrnRf(String formattedDate) {
        String grfNo = "";
        int autoSn = 0;
        ResultSet rsDet;
        try {
            String query = "", prefix = "";
            String yr = formattedDate.substring(formattedDate.lastIndexOf("/") + 1);
            prefix = objPosGlobal.getShopLetter() + yr.substring(2, 4);
            if (objPosGlobal.getShopLetter().length() == 2) {
                query = "select en=isnull(max(right(entryno, len(entryno)-4)),0)+1 from grnheaderrf where entryno like '" + prefix + "%'";
            } else {
                query = "select en=isnull(max(right(entryno, len(entryno)-3)),0)+1 from grnheaderrf where entryno like '" + prefix + "%'";
            }
            rsDet = dbConnection.getResultSet(query, objGlobal.getConnection());
            if (rsDet.next()) {
                autoSn = Integer.parseInt(rsDet.getString("en").toString());
            }
            grfNo = prefix + String.format("%05d", autoSn);
            objGrnTransferNewGlobal.setLatestGrnNoRf(grfNo);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:getLatestGrnRf:" + ex.getMessage());
            return false;
        }
    }

    public boolean clearTable() {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (!dbConnection.insertUpdate("delete from tmpGinVerify where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("BinBatchInControl:boxValid:" + ex);
            return false;
        }
        return true;
    }

    public boolean validateTrfno(String trfToteId, String ginNo) {
        String trfno = "";
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(trfToteId)) {
            objGlobal.setErrorMessage("Transfer / Tote is empty");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from tmpGinVerify where deviceid='" + objGlobal.getDeviceName() + "' and (trfno='" + trfToteId + "' or toteid='" + trfToteId + "')", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Transfer/Tote Id is not found in GIN");
                return false;
            } else {
                trfno = rs.getString("trfno");
            }
            rs = dbConnection.getResultSet("select dt=convert(varchar,VerifyTime,103),tm=convert(varchar,VerifyTime,8) from tmpGinVerify where " +
                    "deviceid='" + objGlobal.getDeviceName() + "' and (OldVerify='Y' or Verified='Y')  and (trfno='" + trfToteId + "' or toteid='" + trfToteId + "')", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Transfer/Tote is already verified on " + rs.getString("dt") + " " + rs.getString("tm"));
                return false;
            }
            rs = dbConnection.getResultSet("select * from transferheader where trfno='" + trfno + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Data not updated (TransferHeader : " + trfno + "), please contact support team");
                return false;
            }
            rs = dbConnection.getResultSet("select * from transferdetail where trfno='" + trfno + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Data not updated (TransferDetail : " + trfno + "), please contact support team");
                return false;
            }
            rs = dbConnection.getResultSet("select * from grnheaderrf where trfno='" + trfno + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Tranfer already found in GRN (1), " + trfno);
                return false;
            }
            rs = dbConnection.getResultSet("select * from GRNDetail where trfno='" + trfno + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Tranfer already found in GRN (2), " + trfno);
                return false;
            }
            String skipsku = objPosGlobal.getSkipScanSkuGrn();
            if(skipsku.equals("Y")) {
                rs = dbConnection.getResultSet("select top 1 trfno from TransferDetail a,ItemMaster b,USAPriority c,SkuGrnDivision d where a.TrfNo='" + trfno + "' and a.ItemCode=b.ItemCode and " +
                        "c.DivisionY=d.Division and b.GroupCode=c.groupCode", objGlobal.getConnection());
                if (rs.next()) {
                    skipsku = "N";
                }
            }
            if (!dbConnection.insertUpdate("update tmpGinVerify set VerifyTime=getdate(),Verified='Y',skipsku='" + skipsku + "' where deviceid='" + objGlobal.getDeviceName() + "' and " +
                    "(trfno='" + trfToteId + "' or toteid='" + trfToteId + "') and ginno=" + ginNo, objGlobal.getConnection())) {
                return false;
            }
            objGinVerificationGlobal.setSkipsku(skipsku);
            objGinVerificationGlobal.setTrfno(trfno);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:validateGin:" + ex);
            return false;
        }
    }

    ArrayList<GinVerificationTicket> loadGinVerifyDetails() {
        ArrayList<GinVerificationTicket> listGinVerificationTicket = new ArrayList<GinVerificationTicket>();
        int tCnt = 0, yCnt = 0;
        try {
            listGinVerificationTicket.clear();
            rs = dbConnection.getResultSet("select ginNo,shopName,palletNo=isnull(palletNo,''),trfNo=isnull(trfNo,''),toteId=isnull(toteId,''),verified=isnull(verified,'')," +
                    "skipsku=isnull(skipsku,'') from tmpGinVerify where deviceid='" + objGlobal.getDeviceName() + "' and OldVerify='N' order by VerifyTime desc,trfNo", objGlobal.getConnection());
            while (rs.next()) {
                listGinVerificationTicket.add(new GinVerificationTicket(rs.getString("ginNo"), rs.getString("shopName"),
                        rs.getString("palletNo"), rs.getString("trfNo"), rs.getString("toteId"),
                        rs.getString("verified"), rs.getString("skipsku")));
                tCnt++;
                if (rs.getString("verified").toString().equals("Y")) yCnt++;
            }
            objGinVerificationGlobal.setScanCount(String.valueOf(yCnt) + "(" + String.valueOf(tCnt) + ")");
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:loadGinDetails:" + ex);
            return null;
        }
        return listGinVerificationTicket;
    }
}