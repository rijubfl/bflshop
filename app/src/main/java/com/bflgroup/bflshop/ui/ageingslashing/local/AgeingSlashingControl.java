package com.bflgroup.bflshop.ui.ageingslashing.local;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.db.DBConnection;
import com.bflgroup.bflshop.ui.ageingslashing.AgeingSlashingGlobal;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AgeingSlashingControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private AgeingSlashingGlobal objAgeingSlashingGlobal = AgeingSlashingGlobal.getInstance();

    private boolean b_Result;
    private ResultSet rs;

    public AgeingSlashingControl() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingSlashingControl : Local Connection error");
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingSlashingControl : Fetch Time error");
        }
        b_Result = savePdaSaveServer();
        if (b_Result == false) {
            objGlobal.setErrorMessage("AgeingSlashingControl : savePdaSaveServer error");
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
        if (dbConnection.checkCloudConnectionClosed() == false) {
            objGlobal.setCloudDbName("BFLDATA");
            b_Result = dbConnection.connectCloudDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("AgeingSlashingControl.checkConnection : Cloud Connection error 1.1");
                return false;
            }
        }
        return true;
    }

    public boolean savePdaSaveServer() {
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from bfldata.dbo.PdaMaping where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getCloudCon());
            if (!rs.next()) {
                b_Result = dbConnection.insertUpdate("insert into bfldata.dbo.PdaMaping values('" + objGlobal.getDeviceName() + "','',getdate(),0)", objGlobal.getCloudCon());
                if (b_Result == false) {
                    return false;
                }
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage("DBConnection.getResultSet : " + e.toString());
        }
        return true;
    }

    public boolean validatePdaRefNo() {
        objAgeingSlashingGlobal.setPdaPrefix("");
        objAgeingSlashingGlobal.setPdaPrefixSn(0);
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select PdaPrefix,lastSn=isnull(lastSn,0)+1 from bfldata.dbo.PdaMaping where PdaPrefix<>'' and DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getCloudCon());
            if (rs.next()) {
                objAgeingSlashingGlobal.setPdaPrefix(rs.getString("PdaPrefix"));
                objAgeingSlashingGlobal.setPdaPrefixSn(rs.getDouble("lastSn"));
            } else {
                objGlobal.setErrorMessage("AgeingControl.PdaPrefix not found");
                return false;
            }
        } catch (Exception e) {
            objGlobal.setErrorMessage("AgeingControl.validatePdaRefNo : " + e.toString());
            return false;
        }
        return true;
    }


}
