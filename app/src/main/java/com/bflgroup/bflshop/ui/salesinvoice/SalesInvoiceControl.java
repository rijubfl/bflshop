package com.bflgroup.bflshop.ui.salesinvoice;

import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.db.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesInvoiceControl {

    private DBConnection dbConnection = new DBConnection();
    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private SalesInvoiceGlobal objSalesInvoiceGlobal = SalesInvoiceGlobal.getInstance();
    private SalesInvoicePayments objSalesInvoicePayments = new SalesInvoicePayments();
    private SalesInvoicePaymentsStaffPurchase objSalesInvoicePaymentsStaffPurchase = new SalesInvoicePaymentsStaffPurchase();
    private boolean b_Result;
    private ResultSet rs;

    public SalesInvoiceControl() {
        objGlobal.setErrorMessage("");
        b_Result = dbConnection.connectDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("SalesInvoiceControl.SalesInvoiceControl : Local Connection error");
        }
        b_Result = dbConnection.connectCloudDb();
        if (b_Result == false) {
            objGlobal.setErrorMessage("SalesInvoiceControl.SalesInvoiceControl : Cloud Connection error");
        }
        b_Result = dbConnection.getServerDateTime(objGlobal.getConnection());
        if (b_Result == false) {
            objGlobal.setErrorMessage("SalesInvoiceControl.SalesInvoiceControl : Fetch Time error");
        }
    }

    public boolean checkConnection() {
        objGlobal.setErrorMessage("");
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("SalesInvoiceControl.checkConnection : Connection error");
                return false;
            }
        }
        if (dbConnection.checkConnectionClosed() == false) {
            b_Result = dbConnection.connectCloudDb();
            if (b_Result == false) {
                objGlobal.setErrorMessage("SalesInvoiceControl.connectCloudDb : Connection error");
                return false;
            }
        }
        return true;
    }

    List<String> loadCoffeeItemsSpinner(String type) {
        List<String> arr;
        arr = new ArrayList<String>();
        if (!checkConnection()) {
            return null;
        }
        try {
            arr.add("");
            rs = dbConnection.getResultSet("select descr=itemcode+' - '+description from itemmaster where catcode='400' and remarks='Coffee' order by description", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(rs.getString("descr"));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SalesInvoiceControl:loadCoffeeItemsSpinner:" + ex);
            return null;
        }
        return arr;
    }

    public boolean getItemDetailsBarcodeRfid(String scan, int quantity, float sp) {
        String rfid = "", barcode = "", itemcode = "", trfno = "", description = "", unitCode = "", groupCode = "", catCode = "";
        int rowno = 0;
        float salesPrice = 0, costRate = 0, discAmt = 0;
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select barcode from rfpair where rfid='" + scan + "'", objGlobal.getConnection());
            if (rs.next()) {
                barcode = rs.getString("barcode");
                rfid = scan;
            } else {
                barcode = scan;
            }
            if (barcode.contains("/")) {
                String[] parts = barcode.split("/", 3);
                itemcode = parts[0];
                salesPrice = Float.valueOf(parts[1]);
                trfno = parts[2];
            } else {
                itemcode = scan;
                salesPrice = sp;
            }
            if (!rfid.isEmpty()) {
                rs = dbConnection.getResultSet("select * from tmpSinvoiceItems where upper(rfid)='" + rfid + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setErrorMessage("RFID already scanned, rfid:" + rfid);
                    return false;
                }
            }
            rs = dbConnection.getResultSet("select ItemCode,Description,UnitCode,CostRate,GroupCode,CatCode from itemmaster where itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                description = rs.getString("Description");
                unitCode = rs.getString("UnitCode");
                groupCode = rs.getString("GroupCode");
                catCode = rs.getString("CatCode");
                costRate = rs.getFloat("CostRate");
            } else {
                objGlobal.setErrorMessage("Invalid itemcode, Scan:(" + scan + ", Itemcode:" + itemcode + ")");
                return false;
            }
            objSalesInvoiceGlobal.setLoadSalesPrice(false);
            if (salesPrice == 0) {
                b_Result = loadSalesPrice(itemcode);
                if (!b_Result) {
                    return false;
                }
                if (objSalesInvoiceGlobal.getListScanSalesPrice().size() == 1) {
                    salesPrice = Float.valueOf(objSalesInvoiceGlobal.getListScanSalesPrice().get(0));
                } else {
                    objSalesInvoiceGlobal.setLoadSalesPrice(true);
                    return false;
                }
            }
            b_Result = validateSalesPrice(itemcode, salesPrice);
            if (!b_Result) {
                return false;
            }
            b_Result = getItemIsSlashed(itemcode, salesPrice, trfno);
            if (!b_Result) {
                return false;
            }
            b_Result = getDiscountPercentage(itemcode, groupCode);
            if (!b_Result) {
                return false;
            }
            if (objPosGlobal.getSlashedItemDisc().equals("N")) {
                if (objSalesInvoiceGlobal.isItemSlashed()) {
                    objSalesInvoiceGlobal.setDiscountPerForItem(0);
                }
            }
            discAmt = ((salesPrice * objSalesInvoiceGlobal.getDiscountPerForItem()) / 100) * quantity;

            rs = dbConnection.getResultSet("select rowno=isnull(max(rowno),0)+1 from tmpSinvoiceItems where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (rs.next()) {
                rowno = rs.getInt("rowno");
            }
            if (!dbConnection.insertUpdate("insert into tmpSinvoiceItems(DeviceId,UserId,Scan,RfId,Barcode,Itemcode,Description,UnitCode,Quantity,SalesPrice,DiscPer,DiscAmt,Total,CostRate,TrfNo," +
                    "TaxPer,TaxCalcAmt,RowNo,DiscSplitAmt,VatAmt) values ('" + objGlobal.getDeviceName() + "'," + objGlobal.getUserId() + ",'" + scan + "','" + rfid + "','" + barcode + "','" + itemcode + "'," +
                    "'" + description + "','" + unitCode + "'," + quantity + "," + salesPrice + "," + objSalesInvoiceGlobal.getDiscountPerForItem() + "," + discAmt + ",0," +
                    "" + costRate + ",'" + trfno + "'," + objSalesInvoiceGlobal.getItemVatPercentage() + "," + objSalesInvoiceGlobal.getItemVatCalcAmount() + "," + rowno + ",0,0)", objGlobal.getConnection())) {
                return false;
            }
            //apply all special discounts
            if (objPosGlobal.getBuyGet().equals("Y")) {
                b_Result = setBuyGetDiscount();
                if (!b_Result) {
                    return false;
                }
            }
            if (!dbConnection.insertUpdate("update tmpSinvoiceItems set Total=round((Quantity*SalesPrice)-DiscAmt,2) where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoiceControl.getItemDetailsBarcodeRfid : " + e);
            return false;
        }
    }

    private boolean setBuyGetDiscount() {
        int totQty = 0, discQty = 0;
        try {
            if (!dbConnection.insertUpdate("create table #buyGetDisctems(rowno int,itemcode varchar(15),qty int,rate float,discqty int)", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("update tmpSinvoiceItems set DiscPer=0,DiscAmt=0 where deviceid='" + objGlobal.getDeviceName() + "' and itemcode in(select itemcode from BuyGetDiscItems)", objGlobal.getConnection())) {
                return false;
            }
            if (!dbConnection.insertUpdate("insert into #buyGetDisctems select RowNo,Itemcode,Quantity,SalesPrice,Quantity from tmpSinvoiceItems where deviceid='" + objGlobal.getDeviceName() + "' and " +
                    "itemcode in(select itemcode from BuyGetDiscItems)", objGlobal.getConnection())) {
                return false;
            }
            rs = dbConnection.getResultSet("select qty=sum(qty) from #buyGetDisctems", objGlobal.getConnection());
            if (rs.next()) {
                totQty = rs.getInt("qty");
            }
            discQty = totQty / objPosGlobal.getBuyGetBuyQty();
            while (discQty > 0) {
                rs = dbConnection.getResultSet("select top 1 * from #buyGetDisctems where discqty>0 order by rate,itemcode,rowno", objGlobal.getConnection());
                if (rs.next()) {
                    if (!dbConnection.insertUpdate("update tmpSinvoiceItems set DiscPer=" + objPosGlobal.getBuyGetDiscPer() + ",DiscAmt=DiscAmt+(SalesPrice*" + objPosGlobal.getBuyGetDiscPer() + "/100) " +
                            "where deviceid='" + objGlobal.getDeviceName() + "' and itemcode='" + rs.getString("itemcode") + "' and rowno=" + rs.getInt("rowno"), objGlobal.getConnection())) {
                        return false;
                    }
                    if (!dbConnection.insertUpdate("update #buyGetDisctems set discqty=discqty-1 where rowno=" + rs.getInt("rowno") + " and itemcode='" + rs.getString("itemcode") + "'", objGlobal.getConnection())) {
                        return false;
                    }
                }
                discQty--;
            }
            if (!dbConnection.insertUpdate("drop table #buyGetDisctems", objGlobal.getConnection())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoiceControl.setBuyGetDiscount : " + e);
            return false;
        }
    }

    private boolean getDiscountPercentage(String itemcode, String groupCode) {
        objSalesInvoiceGlobal.setDiscountPerForItem(0);
        try {
            if (objPosGlobal.getApplyItemDiscount().equals("Y")) {
                rs = dbConnection.getResultSet("select discount from itemdisc where itemcode='" + itemcode + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objSalesInvoiceGlobal.setDiscountPerForItem(rs.getFloat("discount"));
                    return true;
                }
            }
            if (objPosGlobal.getApplyGroupDiscount().equals("Y")) {
                rs = dbConnection.getResultSet("select discount from discountgroup where groupcode='" + groupCode + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objSalesInvoiceGlobal.setDiscountPerForItem(rs.getFloat("discount"));
                    return true;
                }
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoiceControl.checkDiscount : " + e);
            return false;
        }
    }

    public boolean deleteScanItems(String itemcode, int rowno) {
        if (!checkConnection()) {
            return false;
        }
        try {
            if (itemcode.isEmpty()) {
                if (!dbConnection.insertUpdate("delete from tmpSinvoiceItems where Deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection())) {
                    return false;
                }
            } else {
                if (!dbConnection.insertUpdate("delete from tmpSinvoiceItems where Deviceid='" + objGlobal.getDeviceName() + "' and itemcode='" + itemcode + "' and rowno=" + rowno, objGlobal.getConnection())) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SalesInvoiceControl:clearTable:" + ex);
            return false;
        }
    }

    private boolean getItemIsSlashed(String itemcode, float salesRate, String trfNo) {
        objSalesInvoiceGlobal.setItemSlashed(false);
        try {
            rs = dbConnection.getResultSet("select itemcode from ItemsSlashed where itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objSalesInvoiceGlobal.setItemSlashed(true);
                return true;
            }
            rs = dbConnection.getResultSet("select itemcode from PriceDetailAgeing where newprice=" + salesRate + " and itemcode='" + itemcode + "'", objGlobal.getConnection());
            if (rs.next()) {
                objSalesInvoiceGlobal.setItemSlashed(true);
                return true;
            }
            if (trfNo.startsWith("D") && objPosGlobal.getAsIsShop().equals("Y")) {
                rs = dbConnection.getResultSet("select itemcode from TrfSalesPrice where itemcode='" + itemcode + "'", objGlobal.getConnection());
                if (rs.next()) {
                    objSalesInvoiceGlobal.setItemSlashed(true);
                    return true;
                }
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoiceControl.getItemIsSlashed : " + e);
            return false;
        }
    }

    private boolean validateSalesPrice(String itemcode, float salesPrice) {
        try {
            rs = dbConnection.getResultSet("select salesrate from salesprice where salesrate=" + salesPrice + " and itemcode='" + itemcode + "' and costcode='" + objPosGlobal.getCostCode() + "'", objGlobal.getConnection());
            if (rs.next()) {
                return true;
            }
            rs = dbConnection.getResultSet("select salesrate from oldsalesprice where salesrate=" + salesPrice + " and itemcode='" + itemcode + "' and costcode='" + objPosGlobal.getCostCode() + "'", objGlobal.getConnection());
            if (rs.next()) {
                return true;
            }
            objGlobal.setErrorMessage("Sales price is not valid, item:" + itemcode + ", Sales Price:" + salesPrice);
            return false;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoiceControl.validateSalesPrice : " + e);
            return false;
        }
    }

    private boolean loadSalesPrice(String itemCode) {
        List<String> arr;
        try {
            arr = new ArrayList<String>();
            rs = dbConnection.getResultSet("select distinct salesrate from salesprice where itemcode='" + itemCode + "' and costcode='" + objPosGlobal.getCostCode() + "' union " +
                    "select distinct salesrate from oldsalesprice where itemcode='" + itemCode + "' and costcode='" + objPosGlobal.getCostCode() + "' order by salesrate", objGlobal.getConnection());
            while (rs.next()) {
                arr.add(String.format("%.2f", rs.getFloat("salesrate")));
            }
            objSalesInvoiceGlobal.setListScanSalesPrice(arr);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoiceControl.loadSalesPrice : " + e);
            return false;
        }
    }

    public ArrayList<SalesInvoiceScanItems> loadAllScanItems() {
        int slno = 0;
        ArrayList<SalesInvoiceScanItems> listScanItems = new ArrayList<SalesInvoiceScanItems>();
        try {
            listScanItems.clear();
            rs = dbConnection.getResultSet("select rowno,itemcode,description,quantity,salesprice,discamt,total from tmpSinvoiceItems where deviceid='" + objGlobal.getDeviceName() + "' order by rowno desc", objGlobal.getConnection());
            while (rs.next()) {
                slno++;
                listScanItems.add(new SalesInvoiceScanItems(slno, rs.getInt("rowno"), rs.getString("itemcode").toString(),
                        rs.getString("description").toString(), rs.getInt("quantity"), rs.getFloat("salesprice"),
                        rs.getFloat("discamt"), rs.getFloat("total")));
            }
            return listScanItems;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SalesInvoiceControl:loadAllScanItems:" + ex);
            return null;
        }
    }

    public boolean loadInvoiceAmount() {
        try {
            rs = dbConnection.getResultSet("select quantity=sum(quantity),discamt=sum(discamt),total=sum(total) from tmpSinvoiceItems where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (rs.next()) {
                objSalesInvoiceGlobal.setTotalQty(rs.getInt("quantity"));
                objSalesInvoiceGlobal.setTotalDisc(rs.getFloat("discamt"));
                objSalesInvoiceGlobal.setTotalAmt(rs.getFloat("total"));
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SalesInvoiceControl:loadAmount:" + ex);
            return false;
        }
    }

    public boolean validateMain(float paidAmount) {
        if (!checkConnection()) {
            return false;
        }
        try {
            b_Result = loadInvoiceAmount();
            if (!b_Result) {
                return false;
            }
            b_Result = objSalesInvoicePayments.loadInvoicePaymentAmount();
            if (!b_Result) {
                return false;
            }
            b_Result = objSalesInvoicePayments.validateMainPaymentType();
            if (!b_Result) {
                return false;
            }
            if (objSalesInvoiceGlobal.getCashAmt() > 0) {
                if (paidAmount <= 0) {
                    objGlobal.setErrorMessage("Please enter paid amount");
                    return false;
                }
                if (objSalesInvoiceGlobal.getCashAmt() > paidAmount) {
                    objGlobal.setErrorMessage("Paid amount should be greater than cash amount");
                    return false;
                }
            }
            if (objSalesInvoiceGlobal.getTotalAmt() == 0) {
                objGlobal.setErrorMessage("Please scan items");
                return false;
            }
            if (objSalesInvoiceGlobal.getTotalPaymentAmt() == 0) {
                objGlobal.setErrorMessage("Please enter payments");
                return false;
            }
            if (objSalesInvoiceGlobal.getTotalPaymentAmt() != objSalesInvoiceGlobal.getTotalAmt()) {
                objGlobal.setErrorMessage("Net amount and payment amount are not match,(Net:" + objSalesInvoiceGlobal.getTotalAmt() + ", " +
                        "Pay:" + objSalesInvoiceGlobal.getTotalPaymentAmt() + ")");
                return false;
            }
            return true;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SalesInvoiceControl:validateMain:" + ex);
            return false;
        }
    }

    public boolean saveInvoice(float paidAmount, float change) {
        String trnType = "", paymentTerms = "", lpoNo = "", details = "", entryMode = "A", customerName = "", add1 = "", expiryApprove = "", creditCard = "", coffVchNo = "", remarks = "";
        String cardName = "", cardNos = "", cusMobileNo = "", isCard = "N", isCrNote = "N", telPhno = "", areaCode = "", mLock = "N", expCntry = "", loyaltyCardno = "";
        String crNoteNo1 = "", crNoteNo2 = "", crNoteNo3 = "", crNoteNo4 = "", crNoteNo5 = "", voucherNo = "", bmCrdNo = "", cardAppCode = "", cardRecptNo = "", crCode = "", dbCode = "", custCode = "";
        float crNoteAmt1 = 0, crNoteAmt2 = 0, crNoteAmt3 = 0, crNoteAmt4 = 0, crNoteAmt5 = 0, voucherAmt = 0, bmAmt = 0, totalVouchAmt = 0;
        float totCreditNoteAmt = 0, giftWrapAmt = 0, returnAmount = 0, costOfSales = 0, loyaltyPointEarn = 0;
        float finalGrossAmt = 0, finalNetAmt = 0, finalDiscAmt = 0, vatAmount = 0, postAmt = 0;
        String dbCashAcCode = "126201", dbCardAcCode = "126205", dbStaffPAcCode = "124006", crVatAcCode = "221328", dbDiscAcCode = "320001", dbCrNoteAcCode = "", dbWalletAcCode = "", crAcCode = "";
        int rowno = 1;

        Date lpoDate = null;
        //bmCrdNo = SalesInvoicePaymentsStaffPurchase.getStaffPurchaseEmpCode();
        try {
            if (!dbConnection.getServerDateTime(objGlobal.getConnection())) {
                return false;
            }
            b_Result = getSalesInvoiceNumber();
            if (!b_Result) {
                return false;
            }
            trnType = "C";
            crCode = "310100";
            dbCode = "126201";
            custCode = "11111";
            crAcCode = "310100";
            remarks = "Both-Cash Customer";
            finalGrossAmt = objSalesInvoiceGlobal.getTotalAmt();
            finalNetAmt = objSalesInvoiceGlobal.getTotalAmt() - objSalesInvoiceGlobal.getTotalDiscountAmt();
            finalDiscAmt = objSalesInvoiceGlobal.getTotalDisc() + objSalesInvoiceGlobal.getTotalDiscountAmt();
            if (objSalesInvoiceGlobal.getCashAmt() > 0) {
                trnType = "C";
                crCode = "310100";
                dbCode = "126201";
                custCode = "11111";
                crAcCode = "310100";
                remarks = "Both-Cash Customer";
            }
            if (objSalesInvoiceGlobal.getCardAmt() > 0) {
                trnType = "R";
                crCode = "310200";
                dbCode = "126205";
                custCode = "222222";
                crAcCode = "310200";
                remarks = "Both-Card Customer";
            }
            if (objSalesInvoiceGlobal.getCashAmt() > 0 && objSalesInvoiceGlobal.getCardAmt() > 0) {
                trnType = "B";
                crCode = "310200";
                dbCode = "126205";
                custCode = "222222";
                crAcCode = "310200";
                remarks = "Both-Card/Cash Customer";
            }
            if (objSalesInvoiceGlobal.getStaffPurchaseAmt() > 0) {
                trnType = "C";
                crCode = "310100";
                dbCode = "126201";
                custCode = "11111";
                crAcCode = "310100";
                remarks = "Both-Cash Customer";
            }

            //calc vat
            if (objPosGlobal.getApplyVat().equals("Y")) {
                b_Result = dbConnection.insertUpdate("update tmpSinvoiceItems set DiscSplitAmt=round((((total)/(select sum(total) from tmpSinvoiceItems where " +
                        "deviceid='" + objGlobal.getDeviceName() + "')*100)*" + objSalesInvoiceGlobal.getTotalDiscountAmt() + "/100),2) where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                if (b_Result == false) {
                    return false;
                }
                b_Result = dbConnection.insertUpdate("update tmpSinvoiceItems set TaxPer=" + objPosGlobal.getVatPercentage() + ",TaxCalcAmt=" + objPosGlobal.getVatCalcAmount() + " where " +
                        "DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                if (b_Result == false) {
                    return false;
                }
                b_Result = dbConnection.insertUpdate("update tmpSinvoiceItems set TaxPer=b.per,TaxCalcAmt=b.vatamt from tmpSinvoiceItems a,VatItems b where a.itemcode=b.itemcode and " +
                        "a.DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                if (b_Result == false) {
                    return false;
                }
                b_Result = dbConnection.insertUpdate("update tmpSinvoiceItems set vatamt=round(((((total)-(DiscSplitAmt))*TaxPer)/TaxCalcAmt),2) where TaxPer>0 and " +
                        "DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                if (b_Result == false) {
                    return false;
                }
                rs = dbConnection.getResultSet("select vatamt=sum(VatAmt) from tmpSinvoiceItems where DeviceId='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
                if (rs.next()) {
                    vatAmount = rs.getFloat("vatamt");
                }
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("SalesInvoiceControl:saveInvoice:ex1:" + ex);
            return false;
        }

        try {
            objGlobal.getConnection().setAutoCommit(false);
            b_Result = dbConnection.insertUpdate("insert into SalesHeader(InvoiceNo,InvoiceDate,TrnType,DebitCode,CreditCode,CustCode,CostCode,LocCode,RepCode,PaymentTerms,GrossAmount,TotalDiscount," +
                    "Expenses,Netamount,PaidAmount,SalesType,DONo,LPONo,LPODate,FCCode,FCRate,UserId,PreparedBy,ApprovedBy,ReturnAmount,details,EntryMode,costofsales,Name,Addr1,Addr2,Addr3,pobox,Tel," +
                    "Fax,AreaCode,LockInvoice,Time1,CardName,CardNo,CashAmt,CreditAmt,MobileNo,ManualCard,CrYes,CreditNoteNo,CreditNoteAmt,Export,LoyaltyCardNo,LoyaltyPoint,VoucherNo,VoucherAmt,BeamCardNo," +
                    "BeamAmt,trndate,CardAppCode,CardRecptNo,CreditNoteNo1,CreditNoteAmt1,CreditNoteNo2,CreditNoteAmt2,CreditNoteNo3,CreditNoteAmt3,CreditNoteNo4,CreditNoteAmt4,TotVohAmount) " +
                    "values ('" + objSalesInvoiceGlobal.getInvoiceNumber() + "','" + objGlobal.getServerDate() + "','" + trnType + "','" + dbCode + "','" + crCode + "','" + custCode + "'," +
                    "'" + objPosGlobal.getCostCode() + "','PDA','" + objGlobal.getUserRepCode() + "','" + paymentTerms + "'," + finalGrossAmt + "," + objSalesInvoiceGlobal.getTotalDiscountAmt() + "," +
                    "" + totCreditNoteAmt + "," + finalNetAmt + "," + paidAmount + ",'G','" + change + "','" + lpoNo + "'," + lpoDate + ",'" + objGlobal.getFcCode() + "'," + objGlobal.getFcRate() + "," +
                    "'" + objGlobal.getUserId() + "','" + objGlobal.getUserName() + "','" + giftWrapAmt + "'," + returnAmount + ",'" + details + "','" + entryMode + "'," + costOfSales + "," +
                    "'" + customerName + "','" + add1 + "','" + expiryApprove + "','" + creditCard + "','" + coffVchNo + "','" + telPhno + "','" + objGlobal.getEmpCode() + "','" + areaCode + "'," +
                    "'" + mLock + "','" + objGlobal.getServerTime() + "','" + cardName + "','" + cardNos + "'," + objSalesInvoiceGlobal.getCashAmt() + "," + objSalesInvoiceGlobal.getCardAmt() + "," +
                    "'" + cusMobileNo + "','" + isCard + "','" + isCrNote + "','" + crNoteNo1 + "'," + crNoteAmt1 + ",'" + expCntry + "','" + loyaltyCardno + "'," + loyaltyPointEarn + "," +
                    "'" + voucherNo + "'," + voucherAmt + ",'" + bmCrdNo + "'," + bmAmt + ",getdate(),'" + cardAppCode + "','" + cardRecptNo + "','" + crNoteNo2 + "'," + crNoteAmt2 + "," +
                    "'" + crNoteNo3 + "'," + crNoteAmt3 + ",'" + crNoteNo4 + "'," + crNoteAmt4 + ",'" + crNoteNo5 + "'," + crNoteAmt5 + "," + totalVouchAmt + ")", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                return false;
            }
            b_Result = dbConnection.insertUpdate("insert into SalesDetail(InvoiceNo,ItemCode,ItemDescription,Quantity,Rate,Discount,ReturnQty,DONo,QuotNo,UnitCode,BatchNo,BasicQty,BasicRate," +
                    "CostRate,BasicReturnQty,ItemRemarks,LocCode,RowNo,ExactRate,TrfNo,rfid) select '" + objSalesInvoiceGlobal.getInvoiceNumber() + "',ItemCode,Description,Quantity,SalesPrice," +
                    "DiscAmt,0,'',DiscPer,UnitCode,'',Quantity,SalesPrice,CostRate,0,'','" + objPosGlobal.getLocCode() + "',RowNo,SalesPrice,TrfNo,rfid from tmpSinvoiceItems where " +
                    "deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            b_Result = dbConnection.insertUpdate("insert into InvoiceVatItems(invoiceno,Itemcode,Discount,VatPer,VatAmt,VatCode,MRow,Loyalty) select '" + objSalesInvoiceGlobal.getInvoiceNumber() + "'," +
                    "itemcode,DiscSplitAmt,TaxPer,VatAmt,'F',rowno,0 from tmpSinvoiceItems where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            b_Result = dbConnection.insertUpdate("insert into SalesInvoicePayments(InvoiceNo,PayType,RefNo,Amount,RowNo) select '" + objSalesInvoiceGlobal.getInvoiceNumber() + "',PaymentType,ReferenceNo," +
                    "Amount,RowNo from tmpSinvoicepayments where deviceid='" + objGlobal.getDeviceName() + "'", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            b_Result = dbConnection.insertUpdate("select itemcode,qty=sum(quantity) into #locst from tmpSinvoiceItems where deviceid='" + objGlobal.getDeviceName() + "' group by itemcode", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            b_Result = dbConnection.insertUpdate("update locstock set quantity=a.quantity-b.qty from locstock a,#locst b where a.itemcode=b.itemcode and a.costcode='" + objPosGlobal.getCostCode() + "' and " +
                    "a.loccode='" + objPosGlobal.getLocCode() + "'", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            b_Result = dbConnection.insertUpdate("drop table #locst", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            b_Result = dbConnection.insertUpdate("insert into RFUnPair(InvDate,InvNo,Itemcode,RFID,PairDate,Barcode) select " + objGlobal.getServerDate() + "," +
                    "'" + objSalesInvoiceGlobal.getInvoiceNumber() + "',itemcode,rfid,entrydate,barcode from rfpair where rfid in(select rfid from tmpSinvoiceItems where " +
                    "deviceid='" + objGlobal.getDeviceName() + "' and rfid<>'')", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            b_Result = dbConnection.insertUpdate("insert into rfpairdel(ShopName,TrfNo,ItemCode,rfid,EntryDate,Barcode,Shop,TrnTime,DelRemarks,NotScannableRFID) select ShopName,TrfNo,ItemCode,rfid,EntryDate," +
                    "Barcode,Shop,TrnTime,'UNPAIR-SHOP-" + objGlobal.getServerDate() + "',NotScannableRFID from rfpair where rfid in(select rfid from tmpSinvoiceItems where " +
                    "deviceid='" + objGlobal.getDeviceName() + "' and rfid<>'')", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            b_Result = dbConnection.insertUpdate("delete from rfpair where rfid in(select rfid from tmpSinvoiceItems where deviceid='" + objGlobal.getDeviceName() + "' and rfid<>'')", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            b_Result = dbConnection.insertUpdate("insert into AmazoneSales(invoiceno) values ('" + objSalesInvoiceGlobal.getInvoiceNumber() + "')", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            //account posting//
            if (objSalesInvoiceGlobal.getCashAmt() > 0) {
                b_Result = dbConnection.insertUpdate("insert into AccTrnDetail (refno,accode,amount,fcamount,drcr,narration,costcode,remarks,reconciled,assignamt,rowno) " +
                        "values ('" + objSalesInvoiceGlobal.getInvoiceNumber() + "','" + dbCashAcCode + "'," + objSalesInvoiceGlobal.getCashAmt() + "," + objSalesInvoiceGlobal.getCashAmt() + ",'D','Sales'," +
                        "'" + objPosGlobal.getCostCode() + "','Cash - " + remarks + "','N',0," + rowno++ + ")", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
            }
            if (objSalesInvoiceGlobal.getCardAmt() > 0) {
                b_Result = dbConnection.insertUpdate("insert into AccTrnDetail (refno,accode,amount,fcamount,drcr,narration,costcode,remarks,reconciled,assignamt,rowno) " +
                        "values ('" + objSalesInvoiceGlobal.getInvoiceNumber() + "','" + dbCardAcCode + "'," + objSalesInvoiceGlobal.getCardAmt() + "," + objSalesInvoiceGlobal.getCardAmt() + ",'D','Sales'," +
                        "'" + objPosGlobal.getCostCode() + "','Card - " + remarks + "','N',0," + rowno++ + ")", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
            }
            /*if (objSalesInvoiceGlobal.getCreditNoteAmt() > 0) {
                b_Result = dbConnection.insertUpdate("insert into AccTrnDetail (refno,accode,amount,fcamount,drcr,narration,costcode,remarks,reconciled,assignamt,rowno) " +
                        "values ('" + objSalesInvoiceGlobal.getInvoiceNumber() + "','" + accode + "'," + objSalesInvoiceGlobal.getCreditNoteAmt() + "," + objSalesInvoiceGlobal.getCreditNoteAmt() + ",'D','Sales'," +
                        "'" + objPosGlobal.getCostCode() + "','Card - " + remarks + "','N',0," + rowno++ + ")", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
            }*/
            if (objSalesInvoiceGlobal.getStaffPurchaseAmt() > 0) {
                b_Result = dbConnection.insertUpdate("insert into AccTrnDetail (refno,accode,amount,fcamount,drcr,narration,costcode,remarks,reconciled,assignamt,rowno) " +
                        "values ('" + objSalesInvoiceGlobal.getInvoiceNumber() + "','" + dbStaffPAcCode + "'," + objSalesInvoiceGlobal.getStaffPurchaseAmt() + "," + objSalesInvoiceGlobal.getStaffPurchaseAmt() + ",'D','Sales'," +
                        "'" + objPosGlobal.getCostCode() + "','Staff Purchase - " + remarks + "','N',0," + rowno++ + ")", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
            }
            if (finalDiscAmt > 0) {
                b_Result = dbConnection.insertUpdate("insert into AccTrnDetail (refno,accode,amount,fcamount,drcr,narration,costcode,remarks,reconciled,assignamt,rowno) " +
                        "values ('" + objSalesInvoiceGlobal.getInvoiceNumber() + "','" + dbDiscAcCode + "'," + finalDiscAmt + "," + finalDiscAmt + ",'D','Sales'," +
                        "'" + objPosGlobal.getCostCode() + "','Discount - " + remarks + "','N',0," + rowno++ + ")", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
            }
            if (vatAmount > 0) {
                b_Result = dbConnection.insertUpdate("insert into AccTrnDetail (refno,accode,amount,fcamount,drcr,narration,costcode,remarks,reconciled,assignamt,rowno) " +
                        "values ('" + objSalesInvoiceGlobal.getInvoiceNumber() + "','" + crVatAcCode + "'," + vatAmount + "," + vatAmount + ",'C','Sales','" + objPosGlobal.getCostCode() + "'," +
                        "'VAT - " + remarks + "','N',0," + rowno++ + ")", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
            }
            postAmt = (objSalesInvoiceGlobal.getCashAmt() + objSalesInvoiceGlobal.getCardAmt() + objSalesInvoiceGlobal.getStaffPurchaseAmt() + finalDiscAmt) - (vatAmount);
            b_Result = dbConnection.insertUpdate("insert into AccTrnDetail (refno,accode,amount,fcamount,drcr,narration,costcode,remarks,reconciled,assignamt,rowno) " +
                    "values ('" + objSalesInvoiceGlobal.getInvoiceNumber() + "','" + crAcCode + "'," + postAmt + "," + postAmt + ",'C','Sales','" + objPosGlobal.getCostCode() + "'," +
                    "'Sales - " + remarks + "','N',0," + rowno++ + ")", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            postAmt = (objSalesInvoiceGlobal.getCashAmt() + objSalesInvoiceGlobal.getCardAmt() + finalDiscAmt);
            b_Result = dbConnection.insertUpdate("insert into acctrnheader values ('" + objSalesInvoiceGlobal.getInvoiceNumber() + "','" + objGlobal.getServerDate() + "'," +
                    "'Sales Invoice'," + postAmt + "," + postAmt + ",'" + objGlobal.getFcCode() + "'," + objGlobal.getFcRate() + ",'" + objGlobal.getUserId() + "','A')", objGlobal.getConnection());
            if (b_Result == false) {
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
                return false;
            }
            if (objSalesInvoiceGlobal.getStaffPurchaseAmt() > 0) {
                if (objGlobal.getOfficeType().equals("HO")) {
                    b_Result = dbConnection.insertUpdate("insert into payroll.dbo.OtherDeductions values ((select isnull(max(isnull(sn,0)),0)+ 1 from payroll.dbo.OtherDeductions)," +
                            "'" + objSalesInvoiceGlobal.getStaffPurchaseEmpCode() + "',convert(varchar,getdate(),103),Convert(char(3),GetDate(), 0),Datename(year, GetDate())," +
                            "" + objSalesInvoiceGlobal.getStaffPurchaseAmt() + ",'Salary Deduction,Amt: " + objSalesInvoiceGlobal.getStaffPurchaseAmt() + " Sales " +
                            "invoice : " + objSalesInvoiceGlobal.getInvoiceNumber() + " '," + objGlobal.getUserId() + ",'11','')", objGlobal.getConnection());
                    if (b_Result == false) {
                        objGlobal.getConnection().rollback();
                        objGlobal.getConnection().setAutoCommit(true);
                        return false;
                    }
                }
                b_Result = dbConnection.insertUpdate("insert into StaffPurchase values('" + objSalesInvoiceGlobal.getInvoiceNumber() + "','" + objSalesInvoiceGlobal.getStaffPurchaseEmpCode() + "'," +
                        "" + objSalesInvoiceGlobal.getStaffPurchaseAmt() + ")", objGlobal.getConnection());
                if (b_Result == false) {
                    objGlobal.getConnection().rollback();
                    objGlobal.getConnection().setAutoCommit(true);
                    return false;
                }
            }
            objGlobal.getConnection().commit();
            objGlobal.getConnection().setAutoCommit(true);
            return true;
        } catch (Exception ex) {
            try {
                objGlobal.setErrorMessage("SalesInvoiceControl:saveInvoice:ex2:" + ex);
                objGlobal.getConnection().rollback();
                objGlobal.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                objGlobal.setErrorMessage("SalesInvoiceControl:saveInvoice:ex3:" + e);
                return false;
            }
            return false;
        }
    }

    public boolean getSalesInvoiceNumber() {
        int autoSn = 0;
        if (!checkConnection()) {
            return false;
        }
        try {
            rs = dbConnection.getResultSet("select sn=isnull(max(cast(right(invoiceno,7) as int)),0)+1 from salesheader where left(invoiceno,2)='FS' and substring(invoiceno,3,1)<>'D' and " +
                    "substring(invoiceno,3," + objPosGlobal.getShopLetter().length() + ") ='" + objPosGlobal.getShopLetter() + "'", objGlobal.getConnection());
            if (rs.next()) {
                autoSn = rs.getInt("sn");
            }
            objSalesInvoiceGlobal.setInvoiceNumber("FS" + objPosGlobal.getShopLetter() + String.format("%07d", autoSn));
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("SalesInvoiceControl.generateSalesInvoiceNumber : " + e);
            return false;
        }

    }


}
