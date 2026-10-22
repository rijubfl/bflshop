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

    // Save is not globally serialized across PDAs.
    public synchronized boolean saveGinVerification(String skipSkuGrn, String ginno) {
        boolean transactionStarted = false;
        try {
            if (!checkConnection() || !dbConnection.getServerDateTime(objGlobal.getConnection())) {
                objGlobal.setErrorMessage("GIN save: database connection/date unavailable");
                return false;
            }
            objGlobal.getConnection().setAutoCommit(false);
            transactionStarted = true;

            // Read pending transfers once. DISTINCT avoids processing repeated temp rows.
            ArrayList<String> pending = new ArrayList<>();
            try (java.sql.PreparedStatement query = objGlobal.getConnection().prepareStatement(
                    "SELECT DISTINCT TrfNo FROM tmpGinVerify " +
                            "WHERE DeviceId=? AND GinNo=? AND Verified='Y' AND OldVerify='N' AND TrfNo IS NOT NULL")) {
                query.setString(1, objGlobal.getDeviceName());
                query.setString(2, ginno);
                try (ResultSet rows = query.executeQuery()) {
                    while (rows.next()) pending.add(rows.getString(1));
                }
            }

            // No pending work: idempotent success.
            if (pending.isEmpty()) {
                objGlobal.getConnection().commit();
                return true;
            }

            // Do not allocate a GRN number unless at least one new SKU-skip GRN is needed.
            boolean grnNumberReady = false;
            for (String trfno : pending) {
                boolean alreadyVerified;
                boolean alreadyGrn;
                boolean skipSku;
                try (java.sql.PreparedStatement q = objGlobal.getConnection().prepareStatement(
                        "SELECT CASE WHEN EXISTS(SELECT 1 FROM GinVerify WHERE TrfNo=?) THEN 1 ELSE 0 END, " +
                                "CASE WHEN EXISTS(SELECT 1 FROM GRNDetail WHERE TrfNo=?) OR " +
                                "EXISTS(SELECT 1 FROM GRNHeaderRF WHERE TrfNo=?) THEN 1 ELSE 0 END, " +
                                "CASE WHEN EXISTS(SELECT 1 FROM tmpGinVerify WHERE DeviceId=? AND GinNo=? " +
                                "AND TrfNo=? AND Verified='Y' AND OldVerify='N' AND skipsku='Y') THEN 1 ELSE 0 END")) {
                    q.setString(1, trfno);
                    q.setString(2, trfno);
                    q.setString(3, trfno);
                    q.setString(4, objGlobal.getDeviceName());
                    q.setString(5, ginno);
                    q.setString(6, trfno);
                    try (ResultSet row = q.executeQuery()) {
                        if (!row.next()) throw new SQLException("Transfer status not returned: " + trfno);
                        alreadyVerified = row.getInt(1) == 1;
                        alreadyGrn = row.getInt(2) == 1;
                        skipSku = row.getInt(3) == 1;
                    }
                }

                // An already-received transfer must never be received a second time.
                // If verification is missing, fail instead of silently creating inconsistent data.
                if (alreadyGrn && !alreadyVerified) {
                    throw new SQLException("Transfer already exists in GRN but not GinVerify: " + trfno);
                }
                if (!alreadyVerified) {
                    try (java.sql.PreparedStatement insert = objGlobal.getConnection().prepareStatement(
                            "INSERT INTO GinVerify(GinNo,Trndate,TrfNo,ToteId,PalletNo,VerifyTime,UserId,skipsku) " +
                                    "SELECT TOP 1 GinNo,?,TrfNo,ToteId,PalletNo,VerifyTime,?,skipsku " +
                                    "FROM tmpGinVerify WHERE DeviceId=? AND GinNo=? AND TrfNo=? " +
                                    "AND Verified='Y' AND OldVerify='N' ORDER BY VerifyTime DESC")) {
                        insert.setString(1, objGlobal.getServerDate());
                        insert.setInt(2, Integer.parseInt(String.valueOf(objGlobal.getUserId())));
                        insert.setString(3, objGlobal.getDeviceName());
                        insert.setString(4, ginno);
                        insert.setString(5, trfno);
                        if (insert.executeUpdate() != 1) throw new SQLException("Verification insert failed: " + trfno);
                    }
                }

                if (skipSku && !alreadyGrn) {
                    if (!grnNumberReady) {
                        if (!getLatestGrn(objGlobal.getServerDate()))
                            throw new SQLException(objGlobal.getErrorMessage());
                        grnNumberReady = true;
                    }
                    if (!getLatestGrnRf(objGlobal.getServerDate()))
                        throw new SQLException(objGlobal.getErrorMessage());

                    if (firstGrn) {
                        try (java.sql.PreparedStatement header = objGlobal.getConnection().prepareStatement(
                                "INSERT INTO GRNHeader(EntryNo,EntryDate,Remarks,UserId) VALUES(?,?,?,?)")) {
                            header.setString(1, objGrnTransferNewGlobal.getLatestGrnNo());
                            header.setString(2, objGlobal.getServerDate());
                            header.setString(3, "GIN No: " + ginno);
                            header.setInt(4, Integer.parseInt(String.valueOf(objGlobal.getUserId())));
                            if (header.executeUpdate() != 1) throw new SQLException("GRN header insert failed");
                        }
                        firstGrn = false;
                    }
                    try (java.sql.PreparedStatement detail = objGlobal.getConnection().prepareStatement(
                            "INSERT INTO GRNDetail(EntryNo,TrfNo,TrfDate,Qty,Remarks,ScanQty,Diff) " +
                                    "SELECT ?,TrfNo,?,SUM(Quantity),'',SUM(Quantity),0 " +
                                    "FROM TransferDetail WHERE TrfNo=? GROUP BY TrfNo")) {
                        detail.setString(1, objGrnTransferNewGlobal.getLatestGrnNo());
                        detail.setString(2, objGlobal.getServerDate());
                        detail.setString(3, trfno);
                        if (detail.executeUpdate() != 1)
                            throw new SQLException("TransferDetail missing for " + trfno);
                    }
                    try (java.sql.PreparedStatement rfHeader = objGlobal.getConnection().prepareStatement(
                            "INSERT INTO GRNHeaderRF(EntryNo,EntryDate,UserId,TrfNo) VALUES(?,?,?,?)")) {
                        rfHeader.setString(1, objGrnTransferNewGlobal.getLatestGrnNoRf());
                        rfHeader.setString(2, objGlobal.getServerDate());
                        rfHeader.setInt(3, Integer.parseInt(String.valueOf(objGlobal.getUserId())));
                        rfHeader.setString(4, trfno);
                        if (rfHeader.executeUpdate() != 1) throw new SQLException("RF header insert failed");
                    }
                    try (java.sql.PreparedStatement rfDetail = objGlobal.getConnection().prepareStatement(
                            "INSERT INTO GRNDetailRF(EntryNo,GINNo,TrfNo,Itemcode,RfId,ScanMode,TrfQty,ScanQty,Diff,SerializedCode) " +
                                    "SELECT ?,?,TrfNo,Itemcode,EPC,'',Quantity,Quantity,0,SerializedCode " +
                                    "FROM TransferDetail WHERE TrfNo=?")) {
                        rfDetail.setString(1, objGrnTransferNewGlobal.getLatestGrnNoRf());
                        rfDetail.setString(2, ginno);
                        rfDetail.setString(3, trfno);
                        if (rfDetail.executeUpdate() < 1) throw new SQLException("RF detail missing: " + trfno);
                    }
                }
            }
            try (java.sql.PreparedStatement mark = objGlobal.getConnection().prepareStatement(
                    "UPDATE tmpGinVerify SET OldVerify='Y' WHERE DeviceId=? AND GinNo=? " +
                            "AND Verified='Y' AND OldVerify='N'")) {
                mark.setString(1, objGlobal.getDeviceName());
                mark.setString(2, ginno);
                mark.executeUpdate();
            }
            objGlobal.getConnection().commit();
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GinVerificationControl:saveGinVerification: " + ex);
            if (transactionStarted) {
                try { objGlobal.getConnection().rollback(); }
                catch (SQLException rollbackEx) {
                    objGlobal.setErrorMessage(objGlobal.getErrorMessage() + "; rollback: " + rollbackEx);
                }
            }
            return false;
        } finally {
            if (transactionStarted) {
                try { objGlobal.getConnection().setAutoCommit(true); }
                catch (SQLException ex) { objGlobal.setErrorMessage("Restore autoCommit: " + ex); }
            }
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
                objGlobal.setErrorMessage("Transfer already found in GRN (1), " + trfno);
                return false;
            }
            rs = dbConnection.getResultSet("select * from GRNDetail where trfno='" + trfno + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Transfer already found in GRN (2), " + trfno);
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