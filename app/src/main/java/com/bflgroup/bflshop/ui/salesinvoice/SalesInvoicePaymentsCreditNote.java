package com.bflgroup.bflshop.ui.salesinvoice;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;

public class SalesInvoicePaymentsCreditNote {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private SalesInvoiceGlobal objSalesInvoiceGlobal = SalesInvoiceGlobal.getInstance();
    private boolean b_Result;
    private ResultSet rs;

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectCloudDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("SalesInvoicePaymentsCreditNote.connectCloudDb : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validatePaymentCreditNote(String crnoteNo) {
        if(crnoteNo.isEmpty()){
            objGlobal.setErrorMessage("Please enter credit note number");
            return false;
        }
        if (!checkConnection()) {
            return false;
        }
        try {
            objSalesInvoiceGlobal.setCrnoteExpDate("");
            objSalesInvoiceGlobal.setCrnoteTotalAmt(0);
            rs = dbConnection.getResultSet("select SReturnNo,CrNoteNo,TotAmt,ReturnDate=convert(varchar,ReturnDate,103),expDate=convert(varchar,ReturnDate+60,103) from bfldata.dbo.CrnoteHeadAll where " +
                    "CrNoteNo='" + crnoteNo + "' and CountryCode='" + objGlobal.getCountryCode() + "' and usedAmt=0", objGlobal.getCloudCon());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Credit Note or already used, :" + crnoteNo);
                return false;
            } else {
                objSalesInvoiceGlobal.setCrnoteExpDate(rs.getString("expDate"));
                objSalesInvoiceGlobal.setCrnoteTotalAmt(rs.getFloat("TotAmt"));
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoicePayments.validatePaymentCreditNote : " + e.toString());
            return false;
        }
    }

    /*private boolean validatePaymentCreditNote(String crnoteNo,float totAmt, float useAmt) {

    }*/

}
