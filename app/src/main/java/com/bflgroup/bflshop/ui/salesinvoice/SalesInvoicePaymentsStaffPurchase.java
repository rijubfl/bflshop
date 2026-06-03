package com.bflgroup.bflshop.ui.salesinvoice;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;

public class SalesInvoicePaymentsStaffPurchase {
    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private SalesInvoiceGlobal objSalesInvoiceGlobal = SalesInvoiceGlobal.getInstance();
    private boolean b_Result;
    private ResultSet rs;

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("SalesInvoicePaymentsStaffPurchase.connectDb : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean validateEmployeeStaffPurchase(String empcode) {
        if(empcode.isEmpty()){
            objGlobal.setErrorMessage("Please enter Employee code");
            return false;
        }
        if (!checkConnection()) {
            return false;
        }
        objSalesInvoiceGlobal.setStaffPurchaseEmpName("");
        objSalesInvoiceGlobal.setStaffPurchaseEmpPurchaseAmt(0);
        try {
            if(objGlobal.getOfficeType().equals("HO")){
                rs = dbConnection.getResultSet("select EmpCode,EmpName from payroll.dbo.Employee where Inactive='N' and EmpCode='" + empcode + "' and branchcode in('001','031','011','010')", objGlobal.getConnection());
            } else {
                rs = dbConnection.getResultSet("select EmpCode,EmpName from attendance.dbo.Employee where empcode='" + empcode + "'", objGlobal.getConnection());
            }
            if (!rs.next()) {
                objGlobal.setErrorMessage("SalesInvoicePaymentsStaffPurchase:validateEmployeeStaffPurchase: Invalid Employee");
                return false;
            } else {
                objSalesInvoiceGlobal.setStaffPurchaseEmpName(rs.getString("empname"));
            }
            rs = dbConnection.getResultSet("select StaffAmt=isnull(sum(Netamount),0) from SalesHeader where month(InvoiceDate)=month(getdate()) and " +
                    "YEAR(InvoiceDate)=YEAR(getdate()) and BeamCardNo ='" + empcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objSalesInvoiceGlobal.setStaffPurchaseEmpPurchaseAmt(rs.getFloat("StaffAmt"));
            }
            objSalesInvoiceGlobal.setStaffPurchaseEmpPurchaseLimit(objPosGlobal.getStaffCeiling());
            rs = dbConnection.getResultSet("select * from StaffPurchaseMonLimit where EmpCode='" + empcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objSalesInvoiceGlobal.setStaffPurchaseEmpPurchaseLimit(rs.getFloat("StaffAmt"));
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SalesInvoicePaymentsStaffPurchase:validateEmployeeStaffPurchase:" + ex.toString());
            return false;
        }
    }

    public boolean validateEmployeeGuestPurchase(String empcode) {
        if(empcode.isEmpty()){
            objGlobal.setErrorMessage("Please enter Employee code");
            return false;
        }
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select EmpCode,EmpName from payroll.dbo.Employee where Inactive='N' and EmpCode='" + empcode + "' and branchcode in('001','031','011','010')", objGlobal.getConnection());
            if (rs.next()) {
                objSalesInvoiceGlobal.setGuestPurchaseEmpName(rs.getString("empname"));
            } else {
                objGlobal.setErrorMessage("SalesInvoiceControl:validateEmployeeGuestPurchase: Invalid Employee");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SalesInvoiceControl:validateEmployeeGuestPurchase:" + ex.toString());
            return false;
        }
    }

    public boolean validateStaffCeilingAmt(String empcode,float netAmt) {
        float stfAmt = 0;
        try {
            rs = dbConnection.getResultSet("select StaffAmt=isnull(sum(Netamount),0) from SalesHeader where month(InvoiceDate)=month(getdate()) and " +
                    "YEAR(InvoiceDate)=YEAR(getdate()) and BeamCardNo ='" + empcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                stfAmt = rs.getFloat("StaffAmt");
            }
            if ((stfAmt + netAmt) > objPosGlobal.getStaffCeiling()) {
                objGlobal.setErrorMessage("Staff purchase allowed maximum " + objPosGlobal.getStaffCeiling() + " AED only");
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoiceControl.validateStaffCeilingAmt : " + e.toString());
            return false;
        }
    }

}
