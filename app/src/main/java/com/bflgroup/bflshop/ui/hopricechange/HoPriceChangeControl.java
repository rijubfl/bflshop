package com.bflgroup.bflshop.ui.hopricechange;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;
import com.bflgroup.bflshop.ui.ageingslashing.AgeingSlashingGlobal;
import com.bflgroup.bflshop.ui.ageingslashing.model.AgeingSlashingItemsScan;
import com.bflgroup.bflshop.ui.ageingslashing.model.AgeingSlashingScanDetailsGlobal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class HoPriceChangeControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private AgeingSlashingScanDetailsGlobal objAgeingSlashingScanDetailsGlobal = AgeingSlashingScanDetailsGlobal.getInstance();
    private AgeingSlashingGlobal objAgeingSlashingGlobal = AgeingSlashingGlobal.getInstance();

    DecimalFormat formatterNoDecimal = new DecimalFormat("#########");

    private boolean b_Result;
    private ResultSet rs;

    public HoPriceChangeControl() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingSlashingControl : Local Connection error");
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingSlashingControl : Fetch Time error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("AgeingSlashingControl.checkConnection : Connection error");
                return false;
            }
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingSlashingControl : Fetch Time error");
        }
        return true;
    }

    public boolean getExportQty(String batchNo, String itemcode) {
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select qty=count(*) from ageingitemslognew where batchno='" + batchNo + "' and itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingSlashingScanDetailsGlobal.setSlashQty(rs.getInt("qty"));
            }
            rs = dbConnection.getResultSet("select qty=count(*) from scanageinglog where batchno='" + batchNo + "' and itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingSlashingScanDetailsGlobal.setScanQty(rs.getInt("qty"));
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
        return true;
    }

    public boolean getAgeingItems(String batchno, String itemcode) {
        String trnmode="",trndate="";
        int datediff=0;
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select *,SavePerc=(round(100-((NewPrice/WasPrice)*100),2)) from AgeingItems where SlashingType='HOPRICING' and batchno='" + batchno + "' and itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingSlashingScanDetailsGlobal.setItemName(rs.getString("ItemName"));
                objAgeingSlashingScanDetailsGlobal.setDepartment(rs.getString("Department"));
                objAgeingSlashingScanDetailsGlobal.setDivision(rs.getString("Division"));
                objAgeingSlashingScanDetailsGlobal.setGroupCode(rs.getString("GroupCode"));
                objAgeingSlashingScanDetailsGlobal.setGroupName(rs.getString("GroupName"));
                objAgeingSlashingScanDetailsGlobal.setCurrPrice(rs.getFloat("CurrPrice"));
                objAgeingSlashingScanDetailsGlobal.setNewPrice(rs.getFloat("NewPrice"));
                objAgeingSlashingScanDetailsGlobal.setSavePercentage(rs.getFloat("SavePerc"));
                objAgeingSlashingScanDetailsGlobal.setLabelType(rs.getString("LabelType"));
                objAgeingSlashingScanDetailsGlobal.setSlashingType(rs.getString("SlashingType"));
                objAgeingSlashingScanDetailsGlobal.setWasPrice(rs.getFloat("WasPrice"));
                objAgeingSlashingScanDetailsGlobal.setEligibleQty(rs.getInt("EligibleQty"));
            } else {
                if (objPosGlobal.getHoPricingSP().equals("Y")) {
                    rs = dbConnection.getResultSet("select ItemName=Description,GroupCode,LabelType='YELLOW',SlashingType='HOPRICING' from ItemMaster where itemcode='" + itemcode + "'", objGlobal.getConnection());
                    if (rs.next()) {
                        objAgeingSlashingScanDetailsGlobal.setItemName(rs.getString("ItemName"));
                        objAgeingSlashingScanDetailsGlobal.setGroupCode(rs.getString("GroupCode"));
                        objAgeingSlashingScanDetailsGlobal.setLabelType(rs.getString("LabelType"));
                        objAgeingSlashingScanDetailsGlobal.setSlashingType(rs.getString("SlashingType"));
                    } else {
                        objGlobal.setErrorMessage("Item (" + itemcode + ") is not eligible for printing");
                        return false;
                    }
                    rs = dbConnection.getResultSet("select CurrPrice=SalesRate,NewPrice=SalesRate,SavePerc=0,WasPrice=SalesRate,dtdiff=datediff(day,trndate,getdate()),TrMode,trndate=convert(varchar,getdate(),103) from " +
                            "SalesPrice where itemcode='" + itemcode + "' and costcode='" + objPosGlobal.getCostCode() + "'", objGlobal.getConnection());
                    if (rs.next()) {
                        objAgeingSlashingScanDetailsGlobal.setCurrPrice(rs.getFloat("CurrPrice"));
                        objAgeingSlashingScanDetailsGlobal.setNewPrice(rs.getFloat("NewPrice"));
                        objAgeingSlashingScanDetailsGlobal.setSavePercentage(rs.getFloat("SavePerc"));
                        objAgeingSlashingScanDetailsGlobal.setWasPrice(rs.getFloat("WasPrice"));
                        trndate = rs.getString("trndate");
                        trnmode = rs.getString("TrMode");
                        datediff = rs.getInt("dtdiff");
                    }
                    if (!trnmode.equals(objPosGlobal.getHoPricingSPLetter())) {
                        objGlobal.setErrorMessage("(" + trnmode + ") This transaction mode is not allowed for printing");
                        return false;
                    }
                    if (datediff > objPosGlobal.getHoPricingSPDays()) {
                        objGlobal.setErrorMessage("Printing is permitted only within " + objPosGlobal.getHoPricingSPDays() + " days prior to the price change date (" + trndate + ")");
                        return false;
                    }
                    objAgeingSlashingScanDetailsGlobal.setEligibleQty(1);
                    rs = dbConnection.getResultSet("select EligibleQty=QUANTITY from LocStock where itemcode='" + itemcode + "' and costcode='" + objPosGlobal.getCostCode() + "'", objGlobal.getConnection());
                    if (rs.next()) {
                        objAgeingSlashingScanDetailsGlobal.setEligibleQty(rs.getInt("EligibleQty"));
                    }
                    rs = dbConnection.getResultSet("select Department,Division=DivisionY,GroupName=Grpname from USAPriority where groupCode='426'", objGlobal.getConnection());
                    if (rs.next()) {
                        objAgeingSlashingScanDetailsGlobal.setDepartment(rs.getString("Department"));
                        objAgeingSlashingScanDetailsGlobal.setDivision(rs.getString("Division"));
                        objAgeingSlashingScanDetailsGlobal.setGroupName(rs.getString("GroupName"));
                    }
                } else {
                    objGlobal.setErrorMessage("Item (" + itemcode + ") is not eligible for printing");
                    return false;
                }
            }
            rs = dbConnection.getResultSet(" select * from salesprice where itemcode='" + itemcode + "' and costcode='" + objPosGlobal.getCostCode() + "' and " +
                    "salesrate=" + objAgeingSlashingScanDetailsGlobal.getNewPrice(), objGlobal.getConnection());
            if (!rs.next()) {
                rs = dbConnection.getResultSet(" select * from oldsalesprice where itemcode='" + itemcode + "' and costcode='" + objPosGlobal.getCostCode() + "' and " +
                        "salesrate=" + objAgeingSlashingScanDetailsGlobal.getNewPrice(), objGlobal.getConnection());
                if (!rs.next()) {
                    objGlobal.setErrorMessage("Sales price(" + objAgeingSlashingScanDetailsGlobal.getNewPrice() + ") is not valid, item:" + itemcode + ", Sales Price:" + objAgeingSlashingScanDetailsGlobal.getNewPrice());
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
    }

    public boolean getAgeingPairBarcodeFromRfid(String scan) {
        if (!checkConnection()) {
            return false;
        }
        try {
            objAgeingSlashingScanDetailsGlobal.setRfidScanBarcode("");
            rs = dbConnection.getResultSet("select top 1 barcode from rfpair where rfid='" + scan + "' order by entrydate desc", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingSlashingScanDetailsGlobal.setRfidScanBarcode(rs.getString("barcode"));
            } else {
                objAgeingSlashingScanDetailsGlobal.setRfidScanBarcode(scan);
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
        return true;
    }
    public boolean getLatestBatchNo() {
        if (!checkConnection()) {
            return false;
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (!b_Result) {
            objGlobal.setErrorMessage("AgeingSlashingControl:getLatestBatchNo : Fetch Time error");
            return false;
        }
        try {
            objAgeingSlashingGlobal.setBatchno("");
            rs = dbConnection.getResultSet("select top 1 BatchNO from AgeingItems where SlashingType='HOPRICING' order by TrnDateTime desc", objGlobal.getConnection());
            //rs = dbConnection.getResultSet("SELECT BatchNO=CONVERT(VARCHAR, GETDATE(), 112)", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingSlashingGlobal.setBatchno(rs.getString("BatchNO"));
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
        return true;
    }

    public boolean saveScanLog(String batchNo, String scanCode, String itemcode, String remarks) {
        if (!checkConnection()) {
            return false;
        }
        try {
            b_Result = dbConnection.insertUpdate("insert into scanAgeingLog (SrId,BatchNo,ScanBarCode,ScanDate,ScanTime,Remarks,username,DeviceId,Itemcode) " +
                    "values (replace(replace(replace(replace(convert(varchar,getdate(),121),'-',''),' ',''),':',''),'.',''),'" + batchNo + "','" + scanCode + "',convert(varchar,getdate(),103),convert(varchar,getdate(),8),'" +
                    remarks + "','" + objGlobal.getUserName() + "','" + objGlobal.getDeviceName() + "','" + itemcode + "')", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("DbManagerAgeing:saveScanLog : " + ex);
            return false;
        }
        return true;
    }

    public boolean validateTrfNo(String trfNo) {
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select TrfNo,TrfDate=convert(varchar,TrfDate,103),diff=DATEDIFF(DAY,TrfDate,getdate()) from RFIDTransfer where trfno='" + trfNo + "'", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingSlashingScanDetailsGlobal.setTrfNo(trfNo);
                objAgeingSlashingScanDetailsGlobal.setTrfDate("'"+rs.getString("TrfDate")+"'");
                objAgeingSlashingScanDetailsGlobal.setDateDiff(rs.getInt("diff"));
            } else {
                rs = dbConnection.getResultSet("select TrfNo='T'+sn,TrfDate=convert(varchar,PriceDate,103),diff=DATEDIFF(DAY,PriceDate,getdate()) from TrfSalesPriceAgeing " +
                        "where sn='" + trfNo.replace("T","") + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objAgeingSlashingScanDetailsGlobal.setTrfNo(trfNo);
                    objAgeingSlashingScanDetailsGlobal.setTrfDate("'"+rs.getString("TrfDate")+"'");
                    objAgeingSlashingScanDetailsGlobal.setDateDiff(rs.getInt("diff"));
                }
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
        return true;
    }

    public boolean findLastSlno() {
        objAgeingSlashingScanDetailsGlobal.setNewTrfNo("");
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select slno=isnull(max(cast(replace(sn,'PD','') as int)),0)+1 from trfsalespriceAgeing where SN like 'PD%'", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingSlashingScanDetailsGlobal.setNewTrfNo("TPD" + formatterNoDecimal.format(rs.getInt("slno")));
            } else {
                objAgeingSlashingScanDetailsGlobal.setNewTrfNo("TPD1");
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.toString());
            return false;
        }
        return true;
    }
    ArrayList<AgeingSlashingItemsScan> loadLastScanItems() {
        if (!checkConnection()) {
            return null;
        }
        ArrayList<AgeingSlashingItemsScan> listAgeingSlashingItemsScan = new ArrayList<AgeingSlashingItemsScan>();
        try {
            listAgeingSlashingItemsScan.clear();
            rs = dbConnection.getResultSet("select top 50 NewBarcode,CurrPrice,NewPrice,LabelType=case when LabelType='YELLOW' then 'Y' else 'R' end,Export from AgeingItemsLogNew where " +
                    "deviceid='" + objGlobal.getDeviceName() + "' and SlashingType='HOPRICING' order by ScanDate desc,ScanTime desc", objGlobal.getConnection());
            while (rs.next()) {
                listAgeingSlashingItemsScan.add(new AgeingSlashingItemsScan(rs.getString("NewBarcode"), rs.getFloat("CurrPrice"),
                        rs.getFloat("NewPrice"), rs.getString("LabelType"), rs.getString("Export")));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingSlashingControl:loadLastScanItems:" + ex.toString());
            return null;
        }
        return listAgeingSlashingItemsScan;
    }

    public boolean loadScannedCountTotal(String batchno) {
        if (!checkConnection()) {
            return false;
        }
        try {
            objAgeingSlashingGlobal.setTotalScan(0);
            rs = dbConnection.getResultSet("select total=count(*) from AgeingItemsLogNew where BatchNO='" + batchno + "'", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingSlashingGlobal.setTotalScan(rs.getDouble("total"));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingSlashingControl:loadScannedCountTotal:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean loadScannedCountExportTotal(String batchno) {
        if (!checkConnection()) {
            return false;
        }
        try {
            objAgeingSlashingGlobal.setTotalScanExport(0);
            rs = dbConnection.getResultSet("select total=count(*) from AgeingItemsLogNew where deviceid='" + objGlobal.getDeviceName() + "' and BatchNO='" + batchno + "'", objGlobal.getConnection());
            if (rs.next()) {
                objAgeingSlashingGlobal.setTotalScanExport(rs.getDouble("total"));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingSlashingControl:loadScannedCountExportTotal:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean exportToMainServer(String BatchNO,String itemcode,String pType) {
        if (!checkConnection()) {
            objGlobal.setErrorMessage("AgeingSlashingControl:exportToMainServer : Connection error");
            return false;
        }
        try {
            objGlobal.getConnection().setAutoCommit(false);
            b_Result = dbConnection.insertUpdate("insert into AgeingItemsLogNew(BatchNO,Srid,Itemcode,ItemName,Department,Division,GroupCode,GroupName,CurrPrice,NewPrice,LabelType,SlashingType,WasPrice,TrfNo,ScanDate," +
                    "ScanTime,NewBarcode,NewTrfNo,Export,DeviceId,UserId) values ('" + BatchNO + "',replace(replace(replace(replace(convert(varchar,getdate(),121),'-',''),' ',''),':',''),'.',''),'" + itemcode + "'," +
                    "'" + objAgeingSlashingScanDetailsGlobal.getItemName() + "','" + objAgeingSlashingScanDetailsGlobal.getDepartment() + "','" + objAgeingSlashingScanDetailsGlobal.getDivision() + "'," +
                    "'" + objAgeingSlashingScanDetailsGlobal.getGroupCode() + "','" + objAgeingSlashingScanDetailsGlobal.getGroupName() + "'," + objAgeingSlashingScanDetailsGlobal.getCurrPrice() + "," +
                    "" + objAgeingSlashingScanDetailsGlobal.getNewPrice() + ",'" + objAgeingSlashingScanDetailsGlobal.getLabelType() + "','HOPRICING'," +
                    "" + objAgeingSlashingScanDetailsGlobal.getWasPrice() + ",'" + objAgeingSlashingScanDetailsGlobal.getTrfNo() + "',convert(varchar,getdate(),103),convert(varchar,getdate(),8)," +
                    "'" + objAgeingSlashingScanDetailsGlobal.getNewBarcode() + "','" + objAgeingSlashingScanDetailsGlobal.getNewTrfNo() + "','Y','" + objGlobal.getDeviceName() + "'," +
                    "" + objGlobal.getUserId() + ")", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = saveScanLog(BatchNO,objAgeingSlashingScanDetailsGlobal.getOldBarcode(),itemcode,pType);
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                return false;
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("AgeingSlashingControl:exportToMainServer:ex2:" + ex);
                objGlobal.getConnection().rollback();
            } catch (SQLException e) {
                objGlobal.setErrorMessage("AgeingSlashingControl:exportToMainServer:ex3:" + e);
                return false;
            }
            return false;
        }
    }

    public boolean validateForReprint(String batchno,String itemcode) {
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from AgeingItemsLogNew where BatchNO='" + batchno + "' and itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("AgeingSlashingControl:validateForReprint:Print log not found");
                return false;
            }
             } catch (Exception ex) {
            objGlobal.setErrorMessage("AgeingSlashingControl:validateForReprint:" + ex.toString());
            return false;
        }
        return true;
    }
}
