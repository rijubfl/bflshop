package com.bflgroup.bflshop.ui.salesinvoice;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;

public class SalesInvoicePaymentsGiftVoucher {
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

}
