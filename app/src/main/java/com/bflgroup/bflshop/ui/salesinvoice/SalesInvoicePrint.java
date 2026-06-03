package com.bflgroup.bflshop.ui.salesinvoice;

import com.bflgroup.bflshop.comm.Company;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.comm.PosPrintTerms;
import com.bflgroup.bflshop.db.DBConnection;
import com.bflgroup.bflshop.printclass.BluetoothUtil;
import com.bflgroup.bflshop.printclass.SunmiPrintHelper;

import java.sql.ResultSet;
import java.util.LinkedList;

public class SalesInvoicePrint {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PosPrintTerms objPosPrintTerms = PosPrintTerms.getInstance();
    private PosGlobal objPosGlobal = new PosGlobal();
    private SalesInvoiceGlobal objSalesInvoiceGlobal = SalesInvoiceGlobal.getInstance();
    private SalesInvoicePaymentsStaffPurchase objSalesInvoicePaymentsStaffPurchase = new SalesInvoicePaymentsStaffPurchase();
    private SalesInvoicePayments objSalesInvoicePayments = new SalesInvoicePayments();
    private Company objCompany = new Company();
    private boolean b_Result;
    private String s_Result;
    private ResultSet rs;

    LinkedList<TableItem> datalist;

    public SalesInvoicePrint() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("SalesInvoicePrint.SalesInvoicePrint : Local Connection error");
        }
        b_Result = dbConnection.connectCloudDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("SalesInvoicePrint.SalesInvoicePrint : Cloud Connection error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("SalesInvoicePrint.checkConnection : Connection error");
                return false;
            }
        }
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectCloudDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("SalesInvoicePrint.connectCloudDb : Connection error");
                return false;
            }
        }
        return true;
    }

    public boolean printMainInvoice(String invoiceNo) {
        if (!BluetoothUtil.isBlueToothPrinter) {
            b_Result = printMainInvoiceNoBluTooth(invoiceNo);
            return b_Result;
        } else {
            //printByBluTooth(content);
            return false;
        }
    }

    public boolean validateInvoiceForPrint(String invoiceNo, String empname, String password) {
        String appEmpName="";
        if(invoiceNo.isEmpty()){
            objGlobal.setErrorMessage("Invalid Enter Invoice Number");
            return false;
        }
        if(empname.isEmpty() || password.isEmpty()){
            objGlobal.setErrorMessage("Please enter manager code and password");
            return false;
        }
        b_Result = checkConnection();
        if (!b_Result) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select invoiceno,invoicedate from salesheader where invoiceno='" + invoiceNo + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid Invoice Number");
                return false;
            }
            /*rs = dbConnection.getResultSet("select * from managercode where (code='" + empname + "' or mgrname='" + empname + "') and pwd='" + password + "'", objGlobal.getConnection());
            if (!rs.next()) {
                objGlobal.setErrorMessage("Invalid username or password");
                return false;
            } else {
                appEmpName = rs.getString("mgrname");
            }*/
            b_Result = dbConnection.insertUpdate("insert into InvoiceReprintPOS values('" + invoiceNo + "',getdate(),'" + objGlobal.getUserName() + "','" + appEmpName + "','" + empname + "','" + password + "')", objGlobal.getConnection());
            if (b_Result == false) {
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoicePrint.validateInvoiceForPrint : " + e.toString());
            return false;
        }
    }

    public boolean printMainInvoiceNoBluTooth(String invoiceNo) {
        String invoiceDate = "", staffPurchaseEmpCode = "", voucherNo = "", guestPurchaseEmpCode = "";
        String creditNoteNo="",creditNoteNo1="",creditNoteNo2="",creditNoteNo3="",creditNoteNo4="";
        float creditNoteAmt=0,creditNoteAmt1=0,creditNoteAmt2=0,creditNoteAmt3=0,creditNoteAmt4=0;
        String grossAmtH = "Gross", discAmtH = "Discount", beforeVatH = "Before VAT", vatAmtH = "VAT", netAmtH = "Net", cashAmtH = "Cash", cardAmtH = "Card", changeAmtH = "Change";
        float grossAmtF = 0, discAmtF = 0, beforeVatF = 0, vatAmtF = 0, netAmtF = 0, cashAmtF = 0, cardAmtF = 0, changeAmtF = 0, voucherAmt = 0;
        String lineStr30Bold = "-------------------------";
        String lineStr24Bold = "--------------------------------";
        String lineStr18Bold = "------------------------------------------";
        String font = "";
        String extraSpace = "    ";
        int fontSize = 24, lineSize = 18;
        boolean fontBold = true, underLine = false;
        int slno = 1;
        TableItem ti = new TableItem();
        try {
            b_Result = checkConnection();
            if (!b_Result) {
                return false;
            }
            rs = dbConnection.getResultSet("select invoiceno,dt=convert(varchar,invoicedate,103)+' '+time1,GrossAmount,TotalDiscount,Netamount,VatAmt=(select sum(vatamt) from " +
                    "InvoicevatItems where invoiceno=a.invoiceno),CashAmt,CreditAmt,DONo,BeamCardNo,VoucherNo,VoucherAmt,CreditNoteNo,CreditNoteNo1,CreditNoteNo2,CreditNoteNo3," +
                    "CreditNoteNo4,CreditNoteAmt,CreditNoteAmt1,CreditNoteAmt2,CreditNoteAmt3,CreditNoteAmt4 from salesheader a where invoiceno='" + invoiceNo + "'", objGlobal.getConnection());
            if (rs.next()) {
                invoiceDate = rs.getString("dt");
                grossAmtF = rs.getFloat("GrossAmount");
                discAmtF = rs.getFloat("TotalDiscount");
                beforeVatF = rs.getFloat("GrossAmount") - rs.getFloat("TotalDiscount");
                vatAmtF = rs.getFloat("VatAmt");
                netAmtF = rs.getFloat("Netamount");
                cardAmtF = rs.getFloat("CreditAmt");
                cashAmtF = rs.getFloat("CashAmt");
                changeAmtF = rs.getFloat("DONo");
                staffPurchaseEmpCode = rs.getString("BeamCardNo");
                voucherNo = rs.getString("VoucherNo");
                voucherAmt = rs.getFloat("VoucherAmt");
                creditNoteNo= rs.getString("CreditNoteNo");
                creditNoteNo1= rs.getString("CreditNoteNo1");
                creditNoteNo2= rs.getString("CreditNoteNo2");
                creditNoteNo3= rs.getString("CreditNoteNo3");
                creditNoteNo4= rs.getString("CreditNoteNo4");
                creditNoteAmt= rs.getFloat("CreditNoteAmt");
                creditNoteAmt1= rs.getFloat("CreditNoteAmt1");
                creditNoteAmt2= rs.getFloat("CreditNoteAmt2");
                creditNoteAmt3= rs.getFloat("CreditNoteAmt3");
                creditNoteAmt4= rs.getFloat("CreditNoteAmt4");
            } else {
                objGlobal.setErrorMessage("SalesInvoicePrint.printMainInvoiceNoBluTooth : Invalid Invoice " + invoiceNo);
                return false;
            }

            rs = dbConnection.getResultSet("select PayType,RefNo,Amount=sum(Amount) from SalesInvoicePayments where invoiceno='" + invoiceNo + "' group by PayType,RefNo", objGlobal.getConnection());
            while (rs.next()) {
                if(rs.getString("PayType").equals("Guest Purchase")) {
                    guestPurchaseEmpCode = rs.getString("RefNo");
                }
                if(rs.getString("PayType").equals("Staff Purchase")) { }
            }

            if (!staffPurchaseEmpCode.isEmpty()) {
                b_Result = objSalesInvoicePaymentsStaffPurchase.validateEmployeeStaffPurchase(staffPurchaseEmpCode);
                if (!b_Result) {
                    return false;
                }
            }
            if (!guestPurchaseEmpCode.isEmpty()) {
                b_Result = objSalesInvoicePaymentsStaffPurchase.validateEmployeeGuestPurchase(guestPurchaseEmpCode);
                if (!b_Result) {
                    return false;
                }
            }

            fontSize = 30;
            fontBold = true;
            underLine = false;
            SunmiPrintHelper.getInstance().setAlign(1);

            SunmiPrintHelper.getInstance().printText(objCompany.getTaxHead() + "\n", fontSize, fontBold, underLine, font);
            SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getArabicTaxInv() + "\n", fontSize, fontBold, underLine, font);

            if(objPosGlobal.getCoffeeInvoice().equals("Y")) {
                SunmiPrintHelper.getInstance().printText("BFL CAFE L.L.C.\n", fontSize, fontBold, underLine, font);
            } else {
                SunmiPrintHelper.getInstance().printText(objCompany.getCompanyName() + "\n", fontSize, fontBold, underLine, font);
                SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getArabicCompName() + "\n", fontSize, fontBold, underLine, font);
            }
            SunmiPrintHelper.getInstance().printText(objCompany.getAddress() + "\n", fontSize, fontBold, underLine, font);
            SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getArabicAddress() + "\n", fontSize, fontBold, underLine, font);

            fontSize = 24;
            fontBold = true;
            underLine = false;
            SunmiPrintHelper.getInstance().printText(objCompany.getTelHead() + " / " + objPosPrintTerms.getArabicTelNo() + " : " + "\n", fontSize, fontBold, underLine, font);
            SunmiPrintHelper.getInstance().printText(objCompany.getTelNo() + "\n", fontSize, fontBold, underLine, font);
            SunmiPrintHelper.getInstance().printText(lineStr18Bold + "\n", lineSize, fontBold, underLine, font);

            SunmiPrintHelper.getInstance().setAlign(1);
            SunmiPrintHelper.getInstance().printText(objCompany.getTaxAddressHead() + " / " + objPosPrintTerms.getArabicTaxAddress() + " : \n", fontSize, fontBold, underLine, font);
            SunmiPrintHelper.getInstance().printText(objCompany.getTaxAddress() + "\n", fontSize, fontBold, underLine, font);
            SunmiPrintHelper.getInstance().printText(objCompany.getTaxAddress1() + "\n", fontSize, fontBold, underLine, font);

            SunmiPrintHelper.getInstance().printText(objCompany.getTRNNoHead() + " / " + objPosPrintTerms.getArabicTRN() + " : " + "\n", fontSize, fontBold, underLine, font);
            SunmiPrintHelper.getInstance().printText(objCompany.getTRNNo() + "\n", fontSize, fontBold, underLine, font);

            SunmiPrintHelper.getInstance().printText(lineStr18Bold + "\n", lineSize, fontBold, underLine, font);

            SunmiPrintHelper.getInstance().setAlign(1);
            SunmiPrintHelper.getInstance().printText("User" + " / " + objPosPrintTerms.getArabicUser() + " : " + "\n", fontSize, fontBold, underLine, font);
            SunmiPrintHelper.getInstance().printText(objGlobal.getUserName() + "\n", fontSize, fontBold, underLine, font);

            SunmiPrintHelper.getInstance().printText("Inv. No." + " / " + objPosPrintTerms.getArabicInvNo() + "\n", fontSize, fontBold, underLine, font);
            SunmiPrintHelper.getInstance().printText(invoiceNo + "\n", fontSize, fontBold, underLine, font);

            SunmiPrintHelper.getInstance().printText("Inv. Date" + " / " + objPosPrintTerms.getArabicInvDate() + "\n", fontSize, fontBold, underLine, font);
            SunmiPrintHelper.getInstance().printText(invoiceDate + "\n", fontSize, fontBold, underLine, font);

            SunmiPrintHelper.getInstance().setAlign(0);
            SunmiPrintHelper.getInstance().printText(lineStr18Bold + "\n", lineSize, fontBold, underLine, font);

            if (!objSalesInvoiceGlobal.getStaffPurchaseEmpCode().isEmpty()) {
                fontBold = true;
                underLine = true;
                SunmiPrintHelper.getInstance().setAlign(1);
                SunmiPrintHelper.getInstance().printText("STAFF PURCHASE\n", fontSize, fontBold, underLine, font);
                underLine = false;
                SunmiPrintHelper.getInstance().printText(staffPurchaseEmpCode + " - " + objSalesInvoiceGlobal.getStaffPurchaseEmpName() + "\n", fontSize, fontBold, underLine, font);
                SunmiPrintHelper.getInstance().printText(lineStr18Bold + "\n", lineSize, fontBold, underLine, font);
            }
            if (!objSalesInvoiceGlobal.getGuestPurchaseEmpCode().isEmpty()) {
                fontBold = true;
                underLine = true;
                SunmiPrintHelper.getInstance().setAlign(1);
                SunmiPrintHelper.getInstance().printText("GUEST PURCHASE\n", fontSize, fontBold, underLine, font);
                underLine = false;
                SunmiPrintHelper.getInstance().printText(guestPurchaseEmpCode + " - " + objSalesInvoiceGlobal.getGuestPurchaseEmpName() + "\n", fontSize, fontBold, underLine, font);
                SunmiPrintHelper.getInstance().printText(lineStr18Bold + "\n", lineSize, fontBold, underLine, font);
            }

            SunmiPrintHelper.getInstance().setAlign(0);
            datalist = new LinkedList<>();
            ti = new TableItem();
            ti.text = new String[]{"Item", "Amount"};
            ti.width = new int[]{1, 1};
            ti.align = new int[]{0, 2};
            datalist.add(ti);
            ti = new TableItem();
            ti.text = new String[]{objPosPrintTerms.getArabicItem(), objPosPrintTerms.getArabicAmount()};
            ti.width = new int[]{1, 1};
            ti.align = new int[]{0, 2};
            datalist.add(ti);
            for (TableItem tableItem : datalist) {
                SunmiPrintHelper.getInstance().printTable(tableItem.getText(), tableItem.getWidth(), tableItem.getAlign());
            }
            SunmiPrintHelper.getInstance().printText(lineStr18Bold + "\n", lineSize, fontBold, underLine, font);

            datalist = new LinkedList<>();
            rs = dbConnection.getResultSet("select itemcode,ItemDescription=left(ItemDescription,35),Amount=((Quantity*rate)-discount),ArabicName=isnull((Select top 1 left(ArabicName,35) From " +
                    "ArabicItems Where ItemCode=a.ItemCode and isnull(ArabicName,'')<>'' order by trndate desc),'') from salesdetail a where invoiceno='" + invoiceNo + "' order by rowno", objGlobal.getConnection());
            while (rs.next()) {
                ti = new TableItem();
                ti.text = new String[]{rs.getString("itemcode"), String.format("%.2f", rs.getFloat("Amount"))};
                ti.width = new int[]{3, 1};
                ti.align = new int[]{0, 2};
                datalist.add(ti);

                ti = new TableItem();
                ti.text = new String[]{rs.getString("ItemDescription")};
                ti.width = new int[]{1};
                ti.align = new int[]{0};
                datalist.add(ti);

                if (!rs.getString("ArabicName").isEmpty()) {
                    ti = new TableItem();
                    ti.text = new String[]{rs.getString("ArabicName")};
                    ti.width = new int[]{1};
                    ti.align = new int[]{0};
                    datalist.add(ti);
                }
                slno++;
            }
            for (TableItem tableItem : datalist) {
                SunmiPrintHelper.getInstance().printTable(tableItem.getText(), tableItem.getWidth(), tableItem.getAlign());
            }
            SunmiPrintHelper.getInstance().printText(lineStr18Bold + "\n", lineSize, fontBold, underLine, font);

            datalist = new LinkedList<>();

            //grossAmtF
            ti = new TableItem();
            ti.text = new String[]{grossAmtH + "/" + objPosPrintTerms.getArabicGross(), ":", String.format("%.2f", grossAmtF)};
            ti.width = new int[]{3, 1, 1};
            ti.align = new int[]{0, 1, 2};
            datalist.add(ti);

            //discAmtF
            ti = new TableItem();
            ti.text = new String[]{discAmtH + "/" + objPosPrintTerms.getArabicDiscount(), ":", String.format("%.2f", discAmtF)};
            ti.width = new int[]{3, 1, 1};
            ti.align = new int[]{0, 1, 2};
            datalist.add(ti);

            //beforeVatF
            ti = new TableItem();
            ti.text = new String[]{beforeVatH + "/" + objPosPrintTerms.getArabicBefVat(), ":", String.format("%.2f", beforeVatF)};
            ti.width = new int[]{3, 1, 1};
            ti.align = new int[]{0, 1, 2};
            datalist.add(ti);

            //vatAmtF
            ti = new TableItem();
            ti.text = new String[]{vatAmtH + "/" + objPosPrintTerms.getArabicVat(), ":", String.format("%.2f", vatAmtF)};
            ti.width = new int[]{3, 1, 1};
            ti.align = new int[]{0, 1, 2};
            datalist.add(ti);

            //netAmtF
            ti = new TableItem();
            ti.text = new String[]{netAmtH + "/" + objPosPrintTerms.getArabicNet(), ":", String.format("%.2f", netAmtF)};
            ti.width = new int[]{3, 1, 1};
            ti.align = new int[]{0, 1, 2};
            datalist.add(ti);

            if (cardAmtF>0) {
                ti = new TableItem();
                ti.text = new String[]{cardAmtH + "/" + objPosPrintTerms.getArabicCard(), ":", String.format("%.2f", cardAmtF)};
                ti.width = new int[]{3, 1, 1};
                ti.align = new int[]{0, 1, 2};
                datalist.add(ti);
            }

            if (cashAmtF>0) {
                ti = new TableItem();
                ti.text = new String[]{cashAmtH + "/" + objPosPrintTerms.getArabicCash(), ":", String.format("%.2f", cashAmtF)};
                ti.width = new int[]{3, 1, 1};
                ti.align = new int[]{0, 1, 2};
                datalist.add(ti);
            }

            ti = new TableItem();
            ti.text = new String[]{changeAmtH + "/" + objPosPrintTerms.getArabicCash(), ":", String.format("%.2f", changeAmtF)};
            ti.width = new int[]{3, 1, 1};
            ti.align = new int[]{0, 1, 2};
            datalist.add(ti);

            for (TableItem tableItem : datalist) {
                SunmiPrintHelper.getInstance().printTable(tableItem.getText(), tableItem.getWidth(), tableItem.getAlign());
            }
            SunmiPrintHelper.getInstance().printText(lineStr18Bold + "\n", lineSize, fontBold, underLine, font);
            if(!creditNoteNo.isEmpty() || !creditNoteNo1.isEmpty() || !creditNoteNo2.isEmpty() || !creditNoteNo3.isEmpty() || !creditNoteNo4.isEmpty()) {
                datalist = new LinkedList<>();
                SunmiPrintHelper.getInstance().setAlign(1);
                SunmiPrintHelper.getInstance().printText("CREDIT NOTES" + "\n", fontSize, fontBold, underLine, font);
                SunmiPrintHelper.getInstance().setAlign(0);
                if(!creditNoteNo.isEmpty()) {
                    ti = new TableItem();
                    ti.text = new String[]{creditNoteNo, " : ", String.format("%.2f", creditNoteAmt)};
                    ti.width = new int[]{2, 1, 1};
                    ti.align = new int[]{0, 1, 2};
                    datalist.add(ti);
                }
                if(!creditNoteNo1.isEmpty()) {
                    ti = new TableItem();
                    ti.text = new String[]{creditNoteNo1, " : ", String.format("%.2f", creditNoteAmt1)};
                    ti.width = new int[]{2, 1, 1};
                    ti.align = new int[]{0, 1, 2};
                    datalist.add(ti);
                }
                if(!creditNoteNo2.isEmpty()) {
                    ti = new TableItem();
                    ti.text = new String[]{creditNoteNo2, " : ", String.format("%.2f", creditNoteAmt2)};
                    ti.width = new int[]{2, 1, 1};
                    ti.align = new int[]{0, 1, 2};
                    datalist.add(ti);
                }
                if(!creditNoteNo3.isEmpty()) {
                    ti = new TableItem();
                    ti.text = new String[]{creditNoteNo3, " : ", String.format("%.2f", creditNoteAmt3)};
                    ti.width = new int[]{2, 1, 1};
                    ti.align = new int[]{0, 1, 2};
                    datalist.add(ti);
                }
                if(!creditNoteNo4.isEmpty()) {
                    ti = new TableItem();
                    ti.text = new String[]{creditNoteNo4, " : ", String.format("%.2f", creditNoteAmt4)};
                    ti.width = new int[]{2, 1, 1};
                    ti.align = new int[]{0, 1, 2};
                    datalist.add(ti);
                }
                for (TableItem tableItem : datalist) {
                    SunmiPrintHelper.getInstance().printTable(tableItem.getText(), tableItem.getWidth(), tableItem.getAlign());
                }
                SunmiPrintHelper.getInstance().printText(lineStr18Bold + "\n", lineSize, fontBold, underLine, font);
            }
            if(!voucherNo.isEmpty()) {
                datalist = new LinkedList<>();
                SunmiPrintHelper.getInstance().setAlign(1);
                SunmiPrintHelper.getInstance().printText("VOUCHERS" + "\n", fontSize, fontBold, underLine, font);
                SunmiPrintHelper.getInstance().setAlign(0);
                ti = new TableItem();
                ti.text = new String[]{voucherNo, " : ", String.format("%.2f", voucherAmt)};
                ti.width = new int[]{2, 1, 1};
                ti.align = new int[]{0, 1, 2};
                datalist.add(ti);
                for (TableItem tableItem : datalist) {
                    SunmiPrintHelper.getInstance().printTable(tableItem.getText(), tableItem.getWidth(), tableItem.getAlign());
                }
                SunmiPrintHelper.getInstance().printText(lineStr18Bold + "\n", lineSize, fontBold, underLine, font);
            }
            if(objPosGlobal.getCoffeeInvoice().equals("Y")) {
                fontSize = 20;
                fontBold = true;
                underLine = false;
                SunmiPrintHelper.getInstance().setAlign(1);
                SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgThankYou() + "\n", fontSize, fontBold, underLine, font);
                SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgSeeUsAgain() + "\n", fontSize, fontBold, underLine, font);
                SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgSuggesions() + "\n", fontSize, fontBold, underLine, font);
            } else {
                if (objPosGlobal.getPrintEnglishTersm().equals("Y")) {
                    fontSize = 20;
                    fontBold = true;
                    underLine = false;
                    if (!objPosPrintTerms.getgUserMsg1().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg1() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg2().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg2() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg3().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg3() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg4().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg4() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg5().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg5() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg6().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg6() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg7().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg7() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg8().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg8() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg9().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg9() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg10().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg10() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg11().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg11() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg12().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg12() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg13().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg13() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg14().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg14() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsg15().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsg15() + "\n", fontSize, fontBold, underLine, font);
                    SunmiPrintHelper.getInstance().printLine(1);
                }
                if (objPosGlobal.getPrintArabicTersm().equals("Y")) {
                    fontSize = 20;
                    fontBold = true;
                    underLine = false;
                    SunmiPrintHelper.getInstance().setAlign(2);
                    if (!objPosPrintTerms.getgUserMsgArabic1().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic1() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsgArabic2().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic2() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsgArabic3().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic3() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsgArabic4().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic4() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsgArabic5().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic5() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsgArabic6().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic6() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsgArabic7().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic7() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsgArabic8().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic8() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsgArabic9().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic9() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsgArabic10().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic10() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsgArabic11().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic11() + "\n", fontSize, fontBold, underLine, font);
                    if (!objPosPrintTerms.getgUserMsgArabic12().isEmpty())
                        SunmiPrintHelper.getInstance().printText(objPosPrintTerms.getgUserMsgArabic12() + "\n", fontSize, fontBold, underLine, font);
                }
            }
            SunmiPrintHelper.getInstance().setAlign(1);
            SunmiPrintHelper.getInstance().printText("\n", 24, true, false, "");
            SunmiPrintHelper.getInstance().printBarCode(invoiceNo, 8, 70, 2, 2);
            SunmiPrintHelper.getInstance().printText("\n", 24, true, false, "");
            SunmiPrintHelper.getInstance().printQr(invoiceNo, 8, 3);
            SunmiPrintHelper.getInstance().printText("\n", 24, true, false, "");
            SunmiPrintHelper.getInstance().printLine(3);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoicePrint.printMainInvoiceNoBluTooth : " + e.toString());
            return false;
        }
    }

    private class TableItem {
        private String[] text;
        private int[] width;
        private int[] align;
        public TableItem() {
            text = new String[]{"test",":","0"};
            width = new int[]{3,1,1};
            align = new int[]{0,1,2};
        }
        public String[] getText() {
            return text;
        }

        public void setText(String[] text) {
            this.text = text;
        }

        public int[] getWidth() {
            return width;
        }

        public void setWidth(int[] width) {
            this.width = width;
        }

        public int[] getAlign() {
            return align;
        }

        public void setAlign(int[] align) {
            this.align = align;
        }
    }

}
