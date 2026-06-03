package com.bflgroup.bflshop.ui.salesinvoice;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SalesInvoicePayments {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private SalesInvoiceGlobal objSalesInvoiceGlobal = SalesInvoiceGlobal.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    public SalesInvoicePayments() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("SalesInvoicePayments : Local Connection error");
        }
        b_Result = dbConnection.connectCloudDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("SalesInvoicePayments : Cloud Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("SalesInvoicePayments.checkConnection : Connection error");
                return false;
            }
        }
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectCloudDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("SalesInvoicePayments.connectCloudDb : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean loadPaymentMode() {
        List<String> arr;
        try {
            arr = new ArrayList<String>();
            if(objPosGlobal.getCoffeeInvoice().equals("Y")) {
                arr.add("Card");
                //arr.add("Guest Purchase");
            } else {
                arr.add("Cash");
                arr.add("Card");
                arr.add("Total Discount");
                arr.add("E-Wallet");
                arr.add("Credit Note");
                arr.add("Gift Voucher");
                arr.add("Mall Voucher");
                arr.add("Staff Purchase");
            }
            objSalesInvoiceGlobal.setListPayments(arr);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoicePayments.loadPaymentMode : " + e);
            return false;
        }
    }

    public boolean addPaymentModes(String paymentMode, String refNo, float amount) {
        int rowno = 0;
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select rowno=isnull(max(rowno),0)+1 from tmpSinvoicePayments where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (rs.next()) {
                rowno = rs.getInt("rowno");
            }
            b_Result = dbConnection.insertUpdate("insert into tmpSinvoicePayments (DeviceId,UserId,PaymentType,ReferenceNo,Amount,RowNo) values ('" + objGlobal.getDeviceName() + "'," +
                    "" + objGlobal.getUserId() + ",'" + paymentMode + "','" + refNo + "'," + amount + "," + rowno + ")", objGlobal.getConnection());
            if (!b_Result) {
                return false;
            }
            b_Result = validateMainPaymentType();
            if (!b_Result) {
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoicePayments.validatePaymentModes : " + e);
            return false;
        }
    }

    public boolean deletePayments(int rowno) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (rowno == 0) {
                if (!dbConnection.insertUpdate("delete from tmpSinvoicePayments where Deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    return false;
                }
            } else {
                if (!dbConnection.insertUpdate("delete from tmpSinvoicePayments where Deviceid='" + objGlobal.getDeviceName() + "' and rowno=" + rowno, objGlobal.getConnection())) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SalesInvoiceControl:clearTable:" + ex);
            return false;
        }
    }

    private boolean validatePaymentCreditNote(String refno, float amount) {
        if(refno.isEmpty()){
            objGlobal.setErrorMessage("Please enter credit note number");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from bfldata.dbo.CrnoteHeadAll where CrNoteNo='" + refno + "' and CountryCode='" + objGlobal.getCountryCode() + "' and usedAmt=0", objGlobal.getCloudCon());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Credit Note or already used, :" + refno);
                return false;
            } else {
                if(amount>rs.getFloat("TotAmt")){
                    objGlobal.setErrorMessage("Enterred Amount (" + amount + ") is greater than voucher amount (" + rs.getFloat("amount") + ")");
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoicePayments.validatePaymentCreditNote : " + e);
            return false;
        }
    }

    private boolean validatePaymentGiftVoucher(String refno, float amount) {
        if(refno.isEmpty()){
            objGlobal.setErrorMessage("Please enter gift voucher number");
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select * from bfldata.dbo.VoucherIssuedAll where CountryCode='" + objGlobal.getCountryCode() + "' and voucherno='" + refno + "' and usedamt=0", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Gift Voucher or voucher is already used, :" + refno);
                return false;
            } else {
                if(amount>rs.getFloat("amount")){
                    objGlobal.setErrorMessage("Enterred Amount (" + amount + ") is greater than voucher amount (" + rs.getFloat("amount") + ")");
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoicePayments.validatePaymentGiftVoucher : " + e);
            return false;
        }
    }

    public boolean validateMainPaymentType() {
        String empcode="";
        try {
            objSalesInvoiceGlobal.setCashAmt(0);
            objSalesInvoiceGlobal.setCardAmt(0);
            objSalesInvoiceGlobal.setCreditNoteAmt(0);
            objSalesInvoiceGlobal.setWalletAmt(0);
            objSalesInvoiceGlobal.setGiftVoucherAmt(0);
            objSalesInvoiceGlobal.setMallVoucherAmt(0);
            objSalesInvoiceGlobal.setStaffPurchaseAmt(0);
            objSalesInvoiceGlobal.setTotalDiscountAmt(0);
            objSalesInvoiceGlobal.setStaffPurchaseEmpCode("");
            objSalesInvoiceGlobal.setStaffPurchaseEmpName("");
            objSalesInvoiceGlobal.setGuestPurchaseEmpCode("");
            objSalesInvoiceGlobal.setGuestPurchaseEmpName("");
            rs = dbConnection.getResultSet("select PaymentType,amount=sum(amount) from tmpSinvoicePayments where deviceid='" + objGlobal.getDeviceName() + "' group by PaymentType", objGlobal.getConnection());
            while (rs.next()) {
                if (rs.getString("PaymentType").equals("Cash")) {
                    objSalesInvoiceGlobal.setCashAmt(rs.getFloat("amount"));
                }
                if (rs.getString("PaymentType").equals("Card")) {
                    objSalesInvoiceGlobal.setCardAmt(rs.getFloat("amount"));
                }
                if (rs.getString("PaymentType").equals("E-Wallet")) {
                    objSalesInvoiceGlobal.setWalletAmt(rs.getFloat("amount"));
                }
                if (rs.getString("PaymentType").equals("Credit Note")) {
                    objSalesInvoiceGlobal.setCreditNoteAmt(rs.getFloat("amount"));
                }
                if (rs.getString("PaymentType").equals("Gift Voucher")) {
                    objSalesInvoiceGlobal.setGiftVoucherAmt(rs.getFloat("amount"));
                }
                if (rs.getString("PaymentType").equals("Mall Voucher")) {
                    objSalesInvoiceGlobal.setMallVoucherAmt(rs.getFloat("amount"));
                }
                if (rs.getString("PaymentType").equals("Staff Purchase")) {
                    objSalesInvoiceGlobal.setStaffPurchaseAmt(rs.getFloat("amount"));
                }
                if (rs.getString("PaymentType").equals("Guest Purchase")) {
                    objSalesInvoiceGlobal.setGuestPurchaseAmt(rs.getFloat("amount"));
                }
                if (rs.getString("PaymentType").equals("Total Discount")) {
                    objSalesInvoiceGlobal.setTotalDiscountAmt(rs.getFloat("amount"));
                }
            }
            objSalesInvoiceGlobal.setTotalDiscountAmt(objSalesInvoiceGlobal.getTotalDiscountAmt() + objSalesInvoiceGlobal.getGuestPurchaseAmt());
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoicePayments.validateMainPaymentType : " + e);
            return false;
        }
    }

    public ArrayList<SalesInvoicePaymentItems> loadAllScanPayments() {
        int slno = 0;
        ArrayList<SalesInvoicePaymentItems> listScanPayments = new ArrayList<SalesInvoicePaymentItems>();
        try {
            listScanPayments.clear();
            rs = dbConnection.getResultSet("select * from tmpSinvoicePayments where deviceid='" + objGlobal.getDeviceName() + "' order by rowno desc", objGlobal.getConnection());
            while (rs.next()) {
                slno++;
                listScanPayments.add(new SalesInvoicePaymentItems(slno, rs.getInt("rowno"), rs.getString("PaymentType"),
                        rs.getString("ReferenceNo"), rs.getFloat("Amount")));
            }
            return listScanPayments;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SalesInvoicePayments:loadAllScanPayments:" + ex);
            return null;
        }
    }

    public boolean loadInvoicePaymentAmount() {
        try {
            rs = dbConnection.getResultSet("select amount=sum(amount) from tmpSinvoicePayments where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (rs.next()) {
                objSalesInvoiceGlobal.setTotalPaymentAmt(rs.getFloat("amount"));
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SalesInvoicePayments:loadInvoicePaymentAmount:" + ex);
            return false;
        }
    }
}
