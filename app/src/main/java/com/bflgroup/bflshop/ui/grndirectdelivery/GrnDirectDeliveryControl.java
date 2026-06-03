package com.bflgroup.bflshop.ui.grndirectdelivery;

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

public class GrnDirectDeliveryControl {

    DBConnection dbConnection = new DBConnection();
    Global objGlobal = Global.getInstance();
    PosGlobal objPosGlobal = PosGlobal.getInstance();

    boolean result;
    boolean firstGrn;
    String grnRfEnGlb;
    String trfNo;

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

    public GrnDirectDeliveryControl() {
        objGlobal.setCloudDbName("BFLDATA");
        result = dbConnection.connectCloudDb();
        if (!result) {
            objGlobal.setErrorMessage("GrnDirectDeliveryControl.GrnDirectDeliveryControl : Cloud Connection error");
        }
    }

    public boolean checkConnection() {
        if (dbConnection.checkConnectionClosed() == false) {
            result = dbConnection.connectCloudDb();
            if (!result) {
                objGlobal.setErrorMessage("GrnDirectDeliveryControl.checkConnection : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validateDirectDeliveryNumber(String trfNo, Boolean view) {
        if (!checkConnection()) {
            return false;
        }
        if (TextUtils.isEmpty(trfNo)) {
            objGlobal.setErrorMessage("Please enter PO number");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select top 1 * from DirectDelivery where PONo='" + trfNo + "' and shopname='" + objPosGlobal.getShopName() + "'", objGlobal.getCloudCon());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid DirectDelivery Number");
                return false;
            }
            rs = dbConnection.getResultSet("select * from DirectDeliveryHeader where PONo='" + trfNo + "' and shop='" + objPosGlobal.getShopName() + "'", objGlobal.getCloudCon());
            if (rs.next()) {
                if (!view) {
                    objGlobal.setErrorMessage("Tranfer already found in GRN");
                    return false;
                }
            } else {
                if (view) {
                    objGlobal.setErrorMessage("Tranfer not found in GRN");
                    return false;
                }
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnDirectDeliveryControl:validateDirectDeliveryNumber:" + ex.toString());
            return false;
        }
        return true;
    }

    String getLatestGrnRf() {
        int autoSn = 0;
        try {
            rs = dbConnection.getResultSet("select en=isnull(max(cast(right(EntryNo,4) as int)),0)+1 from DirectDeliveryHeader where EntryNo like '" + objPosGlobal.getShopLetter() + "%'", objGlobal.getCloudCon());
            if (rs.next()) {
                autoSn = Integer.parseInt(rs.getString("en").toString());
            } else {
                autoSn=1;
            }
            return objPosGlobal.getShopLetter() + String.format("%05d", autoSn);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnDirectDeliveryControl:getLatestGrnRf:" + ex.toString());
            return "";
        }
    }

    public boolean saveGrn(String trfNo, ArrayList<GrnDirectDeliveryScanItemsAll> listGrnDirectDeliveryScanItemsAll) {
        result = validateDirectDeliveryNumber(trfNo, false);
        if (result == false) {
            return false;
        }
        firstGrn = true;
        if (!dbConnection.getServerDateTime(objGlobal.getCloudCon())) {
            return false;
        }
        String grnRfEn = getLatestGrnRf();
        if (grnRfEn == "") {
            return false;
        }
        String itemCode = "";
        int scan = 0, trf = 0, diff = 0;
        result = dbConnection.insertUpdate("delete from tmpDiffDetails where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getCloudCon());
        if (!result) {
            return false;
        }
        if (listGrnDirectDeliveryScanItemsAll.isEmpty()) {
            objGlobal.setErrorMessage("GrnDirectDeliveryControl.saveGrn : No records");
            return false;
        }
        for (int i = 0; i < listGrnDirectDeliveryScanItemsAll.size(); i++) {
            itemCode = listGrnDirectDeliveryScanItemsAll.get(i).itemCode;
            scan = listGrnDirectDeliveryScanItemsAll.get(i).scanQty;
            trf = listGrnDirectDeliveryScanItemsAll.get(i).trfQty;
            diff = listGrnDirectDeliveryScanItemsAll.get(i).diffQty;
            //itemCode = itemCode.replaceAll("[^a-zA-Z0-9]", " ").trim();
            result = dbConnection.insertUpdate("insert into tmpDiffDetails values('" + objGlobal.getDeviceName() + "','" + itemCode + "'," + trf + "," + scan + "," + diff + ")", objGlobal.getCloudCon());
            if (!result) {
                return false;
            }
        }
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getCloudCon())) {
                return false;
            }
            objGlobal.getCloudCon().setAutoCommit(false);
            result = dbConnection.insertUpdate("insert into DirectDeliveryHeader values('" + grnRfEn + "','" + trfNo + "','" + objPosGlobal.getShopName() + "','" + objPosGlobal.getShopName() + "','" + objGlobal.getServerDate() + "','" + objGlobal.getServerTime() + "'," + objGlobal.getUserId() + ")", objGlobal.getCloudCon());
            if (!result) {
                objGlobal.getCloudCon().rollback();
                return false;
            }
            result = dbConnection.insertUpdate("insert into DirectDeliveryDetail select '" + grnRfEn + "',itemcode,sum(TrfQty),sum(ScanQty),sum(DiffQty) from tmpDiffDetails where DeviceId='" + objGlobal.getDeviceName() + "' group by itemcode", objGlobal.getCloudCon());
            if (!result) {
                objGlobal.getCloudCon().rollback();
                return false;
            }
            objGlobal.getCloudCon().commit();
            setGrnRfEnGlb(grnRfEn);
            setTrfNo(trfNo);
            return true;
        } catch (Exception e) {
            try {
                objGlobal.getCloudCon().rollback();
            } catch (SQLException sqlException) {
                return false;
            }
            return false;
        }
    }
}
