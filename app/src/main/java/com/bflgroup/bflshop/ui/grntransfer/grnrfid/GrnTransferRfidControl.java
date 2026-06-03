package com.bflgroup.bflshop.ui.grntransfer.grnrfid;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GrnTransferRfidControl {
    private boolean b_Result;
    private ResultSet rs;
    private ResultSet rsDet;
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private GrnTransferRfidGlobal objGrnTransferRfidGlobal = GrnTransferRfidGlobal.getInstance();
    private boolean firstGrn;

    public GrnTransferRfidControl() {
        checkConnection();
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (!b_Result) {
            objGlobal.setErrorMessage("GrnTransferRfidControl : Local Connection error");
            return false;
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (!b_Result) {
            objGlobal.setErrorMessage("GrnTransferRfidControl : Fetch Time error");
            return false;
        }
        return true;
    }

    public boolean validateGinVerification(String ginno) {
        int trfqty = 0;
        if (ginno.isEmpty()) {
            objGlobal.setErrorMessage("Please enter GIN Number");
            return false;
        }
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select GinNo,dt=convert(varchar,getdate(),103) from GinVerify where GinNo=" + ginno, objGlobal.getConnection());
            if (rs.next()) {
                objGrnTransferRfidGlobal.setGindate(rs.getString("dt"));
            } else {
                objGlobal.setErrorMessage("GIN Number not yet verified");
                return false;
            }
            rs = dbConnection.getResultSet("select * from GRNHeaderRF where TrfNo in(select TrfNo from GoodsIssue where GINNo=" + ginno + ")", objGlobal.getConnection());
            if (rs.next()) {
                objGlobal.setErrorMessage("Some Transfers already done GRN");
                return false;
            }
            b_Result = dbConnection.insertUpdate("delete from tmpRfidTransferGrnDetail where devicename='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
            rs = dbConnection.getResultSet("select a.Trfno,b.Toteid,a.itemcode,a.quantity from TransferDetail a,GinVerify b where b.GINNo=" + ginno + " and a.TrfNo=b.TrfNo", objGlobal.getConnection());
            while (rs.next()) {
                trfqty = rs.getInt("quantity");
                for (int i = 1; i <= trfqty; i++) {
                    b_Result = dbConnection.insertUpdate("insert into tmpRfidTransferGrnDetail values ('" + objGlobal.getDeviceName() + "','" + rs.getString("trfno") + "'," +
                            "'" + rs.getString("toteid") + "','" + rs.getString("itemcode") + "','',1,0,0,0,0)", objGlobal.getConnection());
                    if (!b_Result) {
                        return false;
                    }
                }
            }
            rs = dbConnection.getResultSet("select TrfNo,Itemcode,RFID from RFPair where TrfNo in(select trfno from GinVerify where GinNo=" + ginno + ")", objGlobal.getConnection());
            while (rs.next()) {
                b_Result = dbConnection.insertUpdate("WITH CTE AS (SELECT TOP 1 * FROM tmpRfidTransferGrnDetail where rfid='' and devicename='" + objGlobal.getDeviceName() + "' and " +
                        "Trfno='" + rs.getString("TrfNo") + "' and itemcode='" + rs.getString("Itemcode") + "') UPDATE CTE SET Rfid='" + rs.getString("rfid") + "'", objGlobal.getConnection());
                if (!b_Result) {
                    return false;
                }
            }
            b_Result = dbConnection.insertUpdate("update tmpRfidTransferGrnDetail set TrfQtyRfid=TotTrfQty where devicename='" + objGlobal.getDeviceName() + "' and rfid<>''", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
            b_Result = dbConnection.insertUpdate("update tmpRfidTransferGrnDetail set TrfQtyNonRfid=TotTrfQty where devicename='" + objGlobal.getDeviceName() + "' and rfid=''", objGlobal.getConnection());
            return b_Result;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:validateShopTransfer :" + ex.getMessage());
            return false;
        }
    }

    public boolean findRfidScanTotal() {
        objGrnTransferRfidGlobal.setTotTotes(0);
        objGrnTransferRfidGlobal.setTotTrfQty(0);
        objGrnTransferRfidGlobal.setTrfQtyRfid(0);
        objGrnTransferRfidGlobal.setTrfQtyNonRfid(0);
        objGrnTransferRfidGlobal.setScanQtyRfid(0);
        objGrnTransferRfidGlobal.setScanQtyNonRfid(0);
        objGrnTransferRfidGlobal.setDiffQtyRfid(0);
        objGrnTransferRfidGlobal.setDiffQtyNonRfid(0);

        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:calcScanTotal: connection error");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select TotTotes=count(distinct trfno),TotTrfQty=sum(TotTrfQty),TrfQtyRfid=sum(TrfQtyRfid),TrfQtyNonRfid=sum(TrfQtyNonRfid),ScanQtyRfid=sum(ScanQtyRfid)," +
                    "ScanQtyNonRfid=sum(ScanQtyNonRfid),DiffQtyRfid=sum(TrfQtyRfid-ScanQtyRfid),DiffQtyNonRfid=sum(TrfQtyNonRfid-ScanQtyNonRfid) from tmpRfidTransferGrnDetail " +
                    "where devicename='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGrnTransferRfidGlobal.setTotTotes(rs.getInt("TotTotes"));
                objGrnTransferRfidGlobal.setTotTrfQty(rs.getInt("TotTrfQty"));
                objGrnTransferRfidGlobal.setTrfQtyRfid(rs.getInt("TrfQtyRfid"));
                objGrnTransferRfidGlobal.setTrfQtyNonRfid(rs.getInt("TrfQtyNonRfid"));
                objGrnTransferRfidGlobal.setScanQtyRfid(rs.getInt("ScanQtyRfid"));
                objGrnTransferRfidGlobal.setScanQtyNonRfid(rs.getInt("ScanQtyNonRfid"));
                objGrnTransferRfidGlobal.setDiffQtyRfid(rs.getInt("DiffQtyRfid"));
                objGrnTransferRfidGlobal.setDiffQtyNonRfid(rs.getInt("DiffQtyNonRfid"));
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:findRfidScanTotal :" + ex.getMessage());
            return false;
        }
    }

    public ArrayList<GrnTransferRfidScanItems> loadScanRfidDetails(boolean showDiff) {
        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:calcScanTotal: connection error");
            return null;
        }
        String having = "";
        if (showDiff)
            having = "having sum(TrfQtyRfid-ScanQtyRfid)<>0 or sum(TrfQtyNonRfid-ScanQtyNonRfid)<>0";
        ArrayList<GrnTransferRfidScanItems> listScanRfidDetails = new ArrayList<GrnTransferRfidScanItems>();
        try {
            rs = dbConnection.getResultSet("select rowNo=(ROW_NUMBER() OVER(ORDER BY trfno)),Trfno,Toteid,TotTrfQty=sum(TotTrfQty),TrfQtyRfid=sum(TrfQtyRfid),TrfQtyNonRfid=sum(TrfQtyNonRfid)," +
                    "ScanQtyRfid=sum(ScanQtyRfid),ScanQtyNonRfid=sum(ScanQtyNonRfid),DiffQtyRfid=sum(TrfQtyRfid-ScanQtyRfid),DiffQtyNonRfid=sum(TrfQtyNonRfid-ScanQtyNonRfid) from " +
                    "tmpRfidTransferGrnDetail where devicename='" + objGlobal.getDeviceName() + "' group by Trfno,Toteid " + having + " order by trfno", objGlobal.getConnection());
            while (rs.next()) {
                listScanRfidDetails.add(new GrnTransferRfidScanItems(rs.getInt("rowNo"), rs.getString("Trfno"), rs.getString("Toteid"),
                        rs.getInt("TotTrfQty"), rs.getInt("TrfQtyRfid"), rs.getInt("TrfQtyNonRfid"), rs.getInt("ScanQtyRfid"),
                        rs.getInt("ScanQtyNonRfid"), rs.getInt("DiffQtyRfid"), rs.getInt("DiffQtyNonRfid")));
            }
            return listScanRfidDetails;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:loadScanRfidDetails:" + ex.getMessage());
            return null;
        }
    }

    public ArrayList<GrnTransferRfidExcessItems> loadRfidExcess() {
        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:loadRfidExcess: connection error");
            return null;
        }
        ArrayList<GrnTransferRfidExcessItems> listGrnTransferRfidExcessItems = new ArrayList<GrnTransferRfidExcessItems>();
        try {
            rs = dbConnection.getResultSet("select rfid,remarks,trfno from tmpGrnScannedRfidDetail where devicename='" + objGlobal.getDeviceName() + "' and " +
                    "excess='Y' order by rfid", objGlobal.getConnection());
            while (rs.next()) {
                listGrnTransferRfidExcessItems.add(new GrnTransferRfidExcessItems(rs.getString("rfid"), rs.getString("remarks"), rs.getString("trfno")));
            }
            return listGrnTransferRfidExcessItems;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:loadRfidExcess:" + ex.getMessage());
            return null;
        }
    }

    public ArrayList<GrnTransferRfidScanDiffItems> loadScanRfidDetailsDiff(String trfno, boolean showDiff) {
        String having = "";
        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:calcScanTotal: connection error");
            return null;
        }
        objGrnTransferRfidGlobal.setTrfTotTrfQty(0);
        objGrnTransferRfidGlobal.setTrfTrfQtyRfid(0);
        objGrnTransferRfidGlobal.setTrfTrfQtyNonRfid(0);
        objGrnTransferRfidGlobal.setTrfScanQtyRfid(0);
        objGrnTransferRfidGlobal.setTrfScanQtyNonRfid(0);
        objGrnTransferRfidGlobal.setTrfDiffQtyRfid(0);
        objGrnTransferRfidGlobal.setTrfDiffQtyNonRfid(0);
        ArrayList<GrnTransferRfidScanDiffItems> listGrnTransferRfidScanDiffItems = new ArrayList<GrnTransferRfidScanDiffItems>();
        try {
            if (showDiff)
                having = "having sum(TrfQtyRfid-ScanQtyRfid)<>0 or sum(TrfQtyNonRfid-ScanQtyNonRfid)<>0";
            rs = dbConnection.getResultSet("select itemcode,TotTrfQty=sum(TotTrfQty),TrfQtyRfid=sum(TrfQtyRfid),TrfQtyNonRfid=sum(TrfQtyNonRfid),ScanQtyRfid=sum(ScanQtyRfid)," +
                    "ScanQtyNonRfid=sum(ScanQtyNonRfid),DiffQtyRfid=sum(TrfQtyRfid-ScanQtyRfid),DiffQtyNonRfid=sum(TrfQtyNonRfid-ScanQtyNonRfid) from tmpRfidTransferGrnDetail where " +
                    "trfno='" + trfno + "' and devicename='" + objGlobal.getDeviceName() + "' group by itemcode " + having + " order by itemcode", objGlobal.getConnection());
            while (rs.next()) {
                listGrnTransferRfidScanDiffItems.add(new GrnTransferRfidScanDiffItems(rs.getString("itemcode"), rs.getInt("trfQtyRfid"),
                        rs.getInt("trfQtyNonRfid"), rs.getInt("scanQtyRfid"), rs.getInt("scanQtyNonRfid"), rs.getInt("diffQtyRfid"),
                        rs.getInt("diffQtyNonRfid")));
            }
            rs = dbConnection.getResultSet("select TotTrfQty=sum(TotTrfQty),TrfQtyRfid=sum(TrfQtyRfid),TrfQtyNonRfid=sum(TrfQtyNonRfid),ScanQtyRfid=sum(ScanQtyRfid)," +
                    "ScanQtyNonRfid=sum(ScanQtyNonRfid),DiffQtyRfid=sum(TrfQtyRfid-ScanQtyRfid),DiffQtyNonRfid=sum(TrfQtyNonRfid-ScanQtyNonRfid) from tmpRfidTransferGrnDetail " +
                    "where trfno='" + trfno + "' and devicename='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (rs.next()) {
                objGrnTransferRfidGlobal.setTrfTotTrfQty(rs.getInt("TotTrfQty"));
                objGrnTransferRfidGlobal.setTrfTrfQtyRfid(rs.getInt("TrfQtyRfid"));
                objGrnTransferRfidGlobal.setTrfTrfQtyNonRfid(rs.getInt("TrfQtyNonRfid"));
                objGrnTransferRfidGlobal.setTrfScanQtyRfid(rs.getInt("ScanQtyRfid"));
                objGrnTransferRfidGlobal.setTrfScanQtyNonRfid(rs.getInt("ScanQtyNonRfid"));
                objGrnTransferRfidGlobal.setTrfDiffQtyRfid(rs.getInt("DiffQtyRfid"));
                objGrnTransferRfidGlobal.setTrfDiffQtyNonRfid(rs.getInt("DiffQtyNonRfid"));
            }
            return listGrnTransferRfidScanDiffItems;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:loadScanRfidDetailsDiff:" + ex.getMessage());
            return null;
        }
    }

    public boolean deleteAll() {
        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:calcScanTotal: connection error");
            return false;
        }
        try {
            b_Result = dbConnection.insertUpdate("delete from tmpGrnScannedRfidDetail where devicename='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!b_Result) return false;
            b_Result = dbConnection.insertUpdate("delete from tmpRfidTransferGrnDetail where devicename='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            return b_Result;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:delete:" + ex.getMessage());
            return false;
        }
    }

    public boolean saveScannedRfid(List<String> epcTidUser) {
        String scanRfids = "";
        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:calcScanTotal: connection error");
            return false;
        }
        try {
            b_Result = dbConnection.insertUpdate("delete from tmpGrnScannedRfidDetail where devicename='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (!b_Result) return false;
            for (int i = 0; i < epcTidUser.size() - 1; i++) {
                scanRfids = epcTidUser.get(i);
                rs = dbConnection.getResultSet("select * from tmpGrnScannedRfidDetail where devicename='" + objGlobal.getDeviceName() + "' and rfid='" + scanRfids + "'", objGlobal.getConnection());
                if (!rs.next()) {
                    b_Result = dbConnection.insertUpdate("insert into tmpGrnScannedRfidDetail values('" + objGlobal.getDeviceName() + "','" + scanRfids + "','Y','','')", objGlobal.getConnection());
                    if (!b_Result) return false;
                }
            }
            b_Result = dbConnection.insertUpdate("update tmpRfidTransferGrnDetail set ScanQtyRfid=TrfQtyRfid,ScanQtyNonRfid=0 where devicename='" + objGlobal.getDeviceName() + "' and " +
                    "rfid in(select rfid from tmpGrnScannedRfidDetail where devicename='" + objGlobal.getDeviceName() + "')", objGlobal.getConnection());
            if (!b_Result) return false;
            b_Result = dbConnection.insertUpdate("delete from tmpGrnScannedRfidDetail where devicename='" + objGlobal.getDeviceName() + "' and rfid in(select rfid from " +
                    "tmpRfidTransferGrnDetail where devicename='" + objGlobal.getDeviceName() + "')", objGlobal.getConnection());
            if (!b_Result) return false;
            b_Result = dbConnection.insertUpdate("delete from tmpGrnScannedRfidDetail where devicename='" + objGlobal.getDeviceName() + "' and rfid not in(select rfid from RFIDMaster)", objGlobal.getConnection());
            if (!b_Result) return false;
            b_Result = dbConnection.insertUpdate("delete from tmpGrnScannedRfidDetail where devicename='" + objGlobal.getDeviceName() + "' and rfid in(select rfid from rfpair)", objGlobal.getConnection());
            return b_Result;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:" + ex.getMessage());
            return false;
        }
    }

    public boolean validateScanItem(String trfno, String itemcode) {
        boolean itemfound = false;
        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:calcScanTotal: connection error");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from tmpRfidTransferGrnDetail where ScanQtyNonRfid=0 and devicename='" + objGlobal.getDeviceName() + "' and trfno='" + trfno + "' and " +
                    "itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) itemfound = true;
            if (!itemfound) {
                objGlobal.setErrorMessage("Item not found");
                return false;
            }
            b_Result = dbConnection.insertUpdate("WITH CTE AS (SELECT TOP 1 * FROM tmpRfidTransferGrnDetail where rfid='' and ScanQtyNonRfid=0 and " +
                    "devicename='" + objGlobal.getDeviceName() + "' and trfno='" + trfno + "' and itemcode='" + itemcode + "') UPDATE CTE SET ScanQtyNonRfid=1", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:validateScanItem:" + ex.getMessage());
            return false;
        }
    }

    public boolean validateVerifyTransfer(String trfno) {
        if (!checkConnection()) {
            objGlobal.setErrorMessage("GrnTransferRfidControl:validateVerifyTransfer: connection error");
            return false;
        }
        try {
            objGrnTransferRfidGlobal.setVerifyTrfNo("");
            rs = dbConnection.getResultSet("select Trfno from tmpRfidTransferGrnDetail where Trfno='" + trfno + "' or Toteid='" + trfno + "' and " +
                    "devicename='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (rs.next()){
                objGrnTransferRfidGlobal.setVerifyTrfNo(rs.getString("Trfno"));
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:validateScanItem:" + ex.getMessage());
            return false;
        }
    }

    public boolean grnSave(String ginno) {
        String trfNo = "";
        if (!checkConnection()) {
            return false;
        }
        if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
            return false;
        }
        try {
            b_Result = getLatestGrn(objGlobal.getServerDate());
            if (!b_Result) {
                return false;
            }
            if (firstGrn) {
                b_Result = dbConnection.insertUpdate("insert into grnheader values('" + objGrnTransferRfidGlobal.getLatestGrnNo() + "','" + objGlobal.getServerDate() + "'," +
                        "''," + objGlobal.getUserId() + ")", objGlobal.getConnection());
                if (!b_Result) {
                    return false;
                }
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage("GrnTransferNewControl:grnSave:try(1):" + e.getMessage());
            return false;
        }
        try {
            rsDet = dbConnection.getResultSet("select distinct Trfno,inGrnDet=isnull((select 'Y' from GRNDetail where Trfno=a.Trfno),'N'),inGrnRf=isnull((select 'Y' from " +
                    "GRNHeaderRF where Trfno=a.Trfno),'N') from tmpRfidTransferGrnDetail a where devicename='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            while (rsDet.next()) {
                trfNo = rsDet.getString("Trfno");
                try {
                    objGlobal.getConnection().setAutoCommit(false);
                    if (rsDet.getString("inGrnDet").equals("N")) {
                        b_Result = dbConnection.insertUpdate("insert into grndetail select '" + objGrnTransferRfidGlobal.getLatestGrnNo() + "',TrfNo,'" + objGlobal.getServerDate() + "'," +
                                "sum(TotTrfQty),'',sum(ScanQtyRfid+ScanQtyNonRfid),(sum(ScanQtyRfid+ScanQtyNonRfid)-sum(TotTrfQty)) from tmpRfidTransferGrnDetail " +
                                "where TrfNo='" + trfNo + "' and devicename='" + objGlobal.getDeviceName() + "' group by TrfNo", objGlobal.getConnection());
                        if (!b_Result) {
                            objGlobal.getConnection().rollback();
                            objGlobal.getConnection().setAutoCommit(true);
                            return false;
                        }
                    }
                    if (rsDet.getString("inGrnRf").equals("N")) {
                        b_Result = getLatestGrnRf(objGlobal.getServerDate());
                        if (!b_Result) {
                            return false;
                        }
                        b_Result = dbConnection.insertUpdate("insert into grnheaderrf select distinct '" + objGrnTransferRfidGlobal.getLatestGrnNoRf() + "','" + objGlobal.getServerDate() + "'," +
                                "'" + objGlobal.getUserName() + "',TrfNo from tmpRfidTransferGrnDetail where TrfNo='" + trfNo + "' and devicename='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                        if (!b_Result) {
                            objGlobal.getConnection().rollback();
                            objGlobal.getConnection().setAutoCommit(true);
                            return false;
                        }
                        b_Result = dbConnection.insertUpdate("insert into grndetailrf select '" + objGrnTransferRfidGlobal.getLatestGrnNoRf() + "','" + ginno + "',TrfNo,Itemcode,RfId,'',sum(TotTrfQty)," +
                                "sum(ScanQtyRfid+ScanQtyNonRfid),(sum(ScanQtyRfid+ScanQtyNonRfid)-sum(TotTrfQty)) from tmpRfidTransferGrnDetail where TrfNo='" + trfNo + "' and " +
                                "devicename='" + objGlobal.getDeviceName() + "' group by TrfNo,Itemcode,RfId", objGlobal.getConnection());
                        if (!b_Result) {
                            objGlobal.getConnection().rollback();
                            objGlobal.getConnection().setAutoCommit(true);
                            return false;
                        }
                    }
                    objGlobal.getConnection().commit();
                    objGlobal.getConnection().setAutoCommit(true);
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
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("GrnTransferNewControl:grnSave:try(3):" + e.getMessage());
            return false;
        }
    }

    private boolean getLatestGrnRf(String formattedDate) {
        String grfNo = "";
        int autoSn = 0;
        try {
            String query = "", prefix = "";
            String yr = formattedDate.substring(formattedDate.lastIndexOf("/") + 1);
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
            objGrnTransferRfidGlobal.setLatestGrnNoRf(grfNo);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:getLatestGrnRf:" + ex.getMessage());
            return false;
        }
    }

    private boolean getLatestGrn(String formattedDate) {
        String grfNo = "";
        int autoSn = 0;
        try {
            String query = "", prefix = "1";
            String yr =formattedDate.substring(formattedDate.lastIndexOf("/") + 1);
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
            objGrnTransferRfidGlobal.setLatestGrnNo(grfNo);
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferNewControl:getLatestGrn:" + ex.getMessage());
            return false;
        }
    }

}
