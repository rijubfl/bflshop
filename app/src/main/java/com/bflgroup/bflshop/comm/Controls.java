package com.bflgroup.bflshop.comm;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.db.DBConnection;
import com.bflgroup.bflshop.ui.salesinvoice.SalesInvoiceGlobal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controls {

    private Global objGlobal = new Global();
    private Company objCompany = new Company();
    private PosGlobal objPosGlobal = new PosGlobal();
    private PosPrintTerms objPosPrintTerms = new PosPrintTerms();
    private DBConnection dbConnection = new DBConnection();

    private ResultSet rs;
    private boolean result;

    public boolean getControl() {
        try {
            objPosGlobal.setShopLetter("");
            objPosGlobal.setShopName("");
            objPosGlobal.setCostCode("");
            objPosGlobal.setDecimals(2);
            objGlobal.setUserRepCode("01");
            objPosGlobal.setSlashActive(false);
            rs = dbConnection.getResultSet("select * from control", objGlobal.getConnection());
            if (rs.next()) {
                objPosGlobal.setShopLetter(rs.getString("branchLetter"));
                objGlobal.setFcCode(rs.getString("LocalCurrency"));
                objGlobal.setFcRate(rs.getFloat("LocalCurrencyFactor"));
            }
            if(objPosGlobal.getCoffeeInvoice().equals("Y")) objPosGlobal.setShopLetter("2");
            rs = dbConnection.getResultSet("select * from control1", objGlobal.getConnection());
            if (rs.next()) {
                objPosGlobal.setDecimals(rs.getFloat("Decimals"));
            }
            rs = dbConnection.getResultSet("select * from bflsettings", objGlobal.getConnection());
            if (rs.next()) {
                objPosGlobal.setVatCalcAmount(rs.getFloat("VatCalcs"));
                objPosGlobal.setVatPercentage(rs.getFloat("VatPercent"));
            }
            rs = dbConnection.getResultSet("select * from bflsettings where startdate<=convert(varchar, getdate(), 103) and enddate>=convert(varchar, getdate(), 103)", objGlobal.getConnection());
            if (!rs.next()) {
                objPosGlobal.setApplyGroupDiscount("N");
                objPosGlobal.setApplyItemDiscount("N");
            }
            rs = dbConnection.getResultSet("select * from AgeingSlashing where Active='Y' And StartDate<=convert(varchar(15),getdate(),103) and EndDate>=convert(varchar(15),getdate(),103)", objGlobal.getConnection());
            if (!rs.next()) {
                objPosGlobal.setSlashActive(true);
            }
            rs = dbConnection.getResultSet("select * from defaultaccount", objGlobal.getConnection());
            while (rs.next()) {
                if (rs.getString("defaultdescr").toUpperCase().equals("SHOP"))
                    objPosGlobal.setShopName(rs.getString("Description"));
                if (rs.getString("defaultdescr").toUpperCase().equals("COSTCODE"))
                    objPosGlobal.setCostCode(rs.getString("DefaultCode"));
                if (rs.getString("defaultdescr").toUpperCase().equals("LOCCODE"))
                    objPosGlobal.setLocCode(rs.getString("DefaultCode"));
            }
            if (TextUtils.isEmpty(objPosGlobal.getShopLetter()) || TextUtils.isEmpty(objPosGlobal.getShopName()) || TextUtils.isEmpty(objPosGlobal.getCostCode())) {
                objGlobal.setErrorMessage("LoginActivity:getControl:Empty control found, contact IT");
                return false;
            }
            result = dbConnection.insertUpdate("insert into bfldata.dbo.shoppdalogin(trndate,trntime,username,country,shop,pdaversion,deviceid) values (convert(varchar,getdate(),103),convert(varchar,getdate(),8)," +
                    "'" + objGlobal.getUserName() + "','" + objGlobal.getCountryCode() + "','" + objPosGlobal.getShopName() + "','" + objGlobal.getPdaversion() + "','" + objGlobal.getDeviceName() + "')", objGlobal.getCloudCon());
            if (!result) {
                objGlobal.setErrorMessage("LoginActivity:getControl:" + objGlobal.getErrorMessage() + "");
                return false;
            }
            if (!loadTerms()) {
                return false;
            }
            if (!loadTerms()) {
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("LoginActivity:getControl:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean getControlMain() {
        try {
            objGlobal.setEnterQty(false);
            objGlobal.setHideKeyPad(true);
            objPosGlobal.setApplyGroupDiscount("N");
            objPosGlobal.setApplyItemDiscount("N");
            objPosGlobal.setSlashedItemDisc("N");
            objPosGlobal.setAsIsShop("N");
            objPosGlobal.setApplyVat("N");
            objPosGlobal.setBarcodePrintHead1("");
            objPosGlobal.setBarcodePrintHead2("");
            objPosGlobal.setStaffCeiling(0);
            objPosGlobal.setBuyGetBuyQty(0);
            objPosGlobal.setBuyGetDiscPer(0);
            objPosGlobal.setBuyGet("N");
            objPosGlobal.setPrintArabicTersm("Y");
            objPosGlobal.setPrintEnglishTersm("Y");
            objPosGlobal.setPrintWasNow("N");
            objPosGlobal.setPrintWasHead("WAS");
            objPosGlobal.setPrintNowHead("NOW");
            objPosGlobal.setPrintWasNowPerc("N");
            objPosGlobal.setCoffeeInvoice("N");
            objPosGlobal.setStockTakeValServer("N");
            objPosGlobal.setGrnItemVal("Y");
            objPosGlobal.setGrnSpVal("Y");
            objPosGlobal.setGrnExcessVal("Y");
            objPosGlobal.setSkipScanSkuGrn("N");
            objPosGlobal.setHoPricingSP("N");
            objPosGlobal.setHoPricingSPLetter("");
            objPosGlobal.setHoPricingSPDays(0);
            objPosGlobal.setPrintAgeSameprice("N");
            objPosGlobal.setShowAgeEligibelQty("N");
            objPosGlobal.setCloudMode("Y");

            rs = dbConnection.getResultSet("select * from settings", objGlobal.getConnection());
            while (rs.next()) {
                if (rs.getString("descr").toUpperCase().equals("GROUPDISCOUNT"))
                    objPosGlobal.setApplyGroupDiscount(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("ITEMDISCOUNT"))
                    objPosGlobal.setApplyItemDiscount(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("SLASHEDITEMDISC"))
                    objPosGlobal.setSlashedItemDisc(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("ASIS"))
                    objPosGlobal.setAsIsShop(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("APPLYVAT"))
                    objPosGlobal.setApplyVat(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("HEAD1"))
                    objPosGlobal.setBarcodePrintHead1(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("HEAD2"))
                    objPosGlobal.setBarcodePrintHead2(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("STFCLNG"))
                    objPosGlobal.setStaffCeiling(rs.getFloat("status"));
                if (rs.getString("descr").toUpperCase().equals("BUYGET_BUYQTY"))
                    objPosGlobal.setBuyGetBuyQty(rs.getInt("status"));
                if (rs.getString("descr").toUpperCase().equals("BUYGET_DISPER"))
                    objPosGlobal.setBuyGetDiscPer(rs.getFloat("status"));
                if (rs.getString("descr").toUpperCase().equals("BUYGET"))
                    objPosGlobal.setBuyGet(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("PTERMSA"))
                    objPosGlobal.setPrintArabicTersm(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("PTERMSE"))
                    objPosGlobal.setPrintEnglishTersm(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("PTERMSE"))
                    objPosGlobal.setPrintEnglishTersm(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("PRINTWASANDNOW"))
                    objPosGlobal.setPrintWasNow(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("PRINTWASNOWPERC"))
                    objPosGlobal.setPrintWasNowPerc(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("VALSTKSERVER"))
                    objPosGlobal.setStockTakeValServer(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("GRNALLDESCR"))
                    objPosGlobal.setGrnItemVal(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("GRNALLPRICE"))
                    objPosGlobal.setGrnSpVal(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("GRNALLOWEXCESS"))
                    objPosGlobal.setGrnExcessVal(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("SKIPSKUGRN"))
                    objPosGlobal.setSkipScanSkuGrn(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("WASHEAD"))
                    objPosGlobal.setPrintWasHead(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("NOWHEAD"))
                    objPosGlobal.setPrintNowHead(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("HOPRICINGSP"))
                    objPosGlobal.setHoPricingSP(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("HOPRICINGSP_LETTER"))
                    objPosGlobal.setHoPricingSPLetter(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("HOPRICINGSP_DAYS"))
                    objPosGlobal.setHoPricingSPDays(rs.getInt("status"));
                if (rs.getString("descr").toUpperCase().equals("AGEPRINTSAMEPRICE"))
                    objPosGlobal.setPrintAgeSameprice(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("SHOWSLASHELIGIBLEQTY"))
                    objPosGlobal.setShowAgeEligibelQty(rs.getString("status"));
                if (rs.getString("descr").toUpperCase().equals("CLOUDMODE"))
                    objPosGlobal.setCloudMode(rs.getString("status"));
            }
            if (objPosGlobal.getSlashedItemDisc().equals("1")) objPosGlobal.setSlashedItemDisc("Y");
            if (objPosGlobal.getPrintWasNow().equals("1")) objPosGlobal.setPrintWasNow("Y");
            if (objPosGlobal.getPrintWasNowPerc().equals("1")) objPosGlobal.setPrintWasNowPerc("Y");
            if (!loadTermsArabic()) {
                return false;
            }
            rs = dbConnection.getResultSet("select * from userdef where userid=" + objGlobal.getUserId(), objGlobal.getConnection());
            if (rs.next()) {
                objCompany.setCompanyShort(rs.getString("ShortName").toUpperCase());
            } else {
                objGlobal.setErrorMessage("Company:1, No Data, " + objGlobal.getUserId());
                return false;
            }
            rs = dbConnection.getResultSet("Select * From Company Where shortname='" + objCompany.getCompanyShort() + "'", objGlobal.getConnection());
            if (rs.next()) {
                objCompany.setTelHead("Tel.No.");
                objCompany.setTaxHead("TAX INVOICE");
                objCompany.setTaxAddressHead("Tax Address");
                objCompany.setTRNNoHead("TRN No");
                objCompany.setCompanyName(rs.getString("companyName"));
                objCompany.setCompanyNameSub("");
                objCompany.setAddress(rs.getString("Address"));
                objCompany.setAddressSub("");
                objCompany.setTelNo(rs.getString("TelNo"));
                objCompany.setTelNoSub("");
                objCompany.setFaxNo(rs.getString("FaxNo"));
                objCompany.setEmail(rs.getString("Email"));
                objCompany.setTaxAddress(rs.getString("TaxAddress"));
                objCompany.setTaxAddress1(rs.getString("TaxAddress1"));
                objCompany.setTRNNo(rs.getString("TRNNo"));
            } else {
                objGlobal.setErrorMessage("Company:2, No Data, " + objGlobal.getUserId());
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("LoginActivity:getControl:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean loadTermsArabic() {
        objPosPrintTerms.setgUserMsgArabic1("");
        objPosPrintTerms.setgUserMsgArabic2("");
        objPosPrintTerms.setgUserMsgArabic3("");
        objPosPrintTerms.setgUserMsgArabic4("");
        objPosPrintTerms.setgUserMsgArabic5("");
        objPosPrintTerms.setgUserMsgArabic6("");
        objPosPrintTerms.setgUserMsgArabic7("");
        objPosPrintTerms.setgUserMsgArabic8("");
        objPosPrintTerms.setgUserMsgArabic9("");
        objPosPrintTerms.setgUserMsgArabic10("");
        objPosPrintTerms.setgUserMsgArabic11("");
        objPosPrintTerms.setgUserMsgArabic12("");
        objPosPrintTerms.setgUserMsgArabic13("");
        objPosPrintTerms.setgUserMsgArabic14("");
        objPosPrintTerms.setgUserMsgArabic15("");
        try {
            rs = dbConnection.getResultSet("Select * From TArabicNames", objGlobal.getConnection());
            if (rs.next()) {
                objPosPrintTerms.setgUserMsgArabic1(rs.getString("ATerms"));
                objPosPrintTerms.setgUserMsgArabic2(rs.getString("ATerms1"));
                objPosPrintTerms.setgUserMsgArabic3(rs.getString("ATerms2"));
                objPosPrintTerms.setgUserMsgArabic4(rs.getString("ATerms3"));
                objPosPrintTerms.setgUserMsgArabic5(rs.getString("ATerms4"));
                objPosPrintTerms.setgUserMsgArabic6(rs.getString("ATerms5"));
                objPosPrintTerms.setgUserMsgArabic7(rs.getString("ATerms6"));
                objPosPrintTerms.setgUserMsgArabic8(rs.getString("ATerms7"));
                objPosPrintTerms.setgUserMsgArabic9(rs.getString("ATerms8"));
                objPosPrintTerms.setgUserMsgArabic10(rs.getString("ATerms9"));
                objPosPrintTerms.setgUserMsgArabic11(rs.getString("ATerms10"));
                objPosPrintTerms.setgUserMsgArabic12(rs.getString("ATerms11"));
                objPosPrintTerms.setgUserMsgArabic13(rs.getString("ATerms12"));

                objPosPrintTerms.setArabicCompName(rs.getString("ACompName"));
                objPosPrintTerms.setArabicAddress(rs.getString("AAddress"));
                objPosPrintTerms.setArabicTelNo(rs.getString("ATelNo"));
                objPosPrintTerms.setArabicUser(rs.getString("AUser"));
                objPosPrintTerms.setArabicInvNo(rs.getString("AInvNo"));
                objPosPrintTerms.setArabicInvDate(rs.getString("AInvDate"));
                objPosPrintTerms.setArabicSlNo(rs.getString("ASlNo"));
                objPosPrintTerms.setArabicItem(rs.getString("AItem"));
                objPosPrintTerms.setArabicQty(rs.getString("AQty"));
                objPosPrintTerms.setArabicRate(rs.getString("ARate"));
                objPosPrintTerms.setArabicAmount(rs.getString("AAmount"));
                objPosPrintTerms.setArabicGross(rs.getString("AGross"));
                objPosPrintTerms.setArabicDiscount(rs.getString("ADiscount"));
                objPosPrintTerms.setArabicNet(rs.getString("ANet"));
                objPosPrintTerms.setArabicCard(rs.getString("ACard"));
                objPosPrintTerms.setArabicCash(rs.getString("ACash"));
                objPosPrintTerms.setArabicChange(rs.getString("AChange"));
                objPosPrintTerms.setArabicLoyaltyCard(rs.getString("ALoyaltyCard"));
                objPosPrintTerms.setArabicPointsEarned(rs.getString("APointsEarned"));
                objPosPrintTerms.setArabicThankyou(rs.getString("AThankyou"));
                objPosPrintTerms.setArabicVisitAgain(rs.getString("AVisitagain"));
                objPosPrintTerms.setArabicEmployee(rs.getString("AEmployee"));
                objPosPrintTerms.setArabicCrNoteNo(rs.getString("ACrNoteNo"));
                objPosPrintTerms.setArabicCrTotalAmt(rs.getString("ACrTotalAmt"));
                objPosPrintTerms.setArabicBalAmount(rs.getString("ABalAmount"));
                objPosPrintTerms.setArabicBeam(rs.getString("ABeam"));
                objPosPrintTerms.setArabicCreditNote(rs.getString("ACreditNote"));
                objPosPrintTerms.setArabicVouchNo(rs.getString("AVouchNo"));
                objPosPrintTerms.setArabicVouchAmt(rs.getString("AVouchAmt"));
                objPosPrintTerms.setArabicafissimoVouchNo(rs.getString("ACafissimoVouchNo"));
                objPosPrintTerms.setArabicEmpName(rs.getString("AEmpName"));
                objPosPrintTerms.setArabicRetInvNo(rs.getString("ARetInvNo"));
                objPosPrintTerms.setArabicPrevCreditNoteNo(rs.getString("APrevCreditNoteNo"));
                objPosPrintTerms.setArabicPrevCreditNoteAmt(rs.getString("APrevCreditNoteAmt"));
                objPosPrintTerms.setArabicCreditNoteAmtUsed(rs.getString("ACreditNoteAmtUsed"));
                objPosPrintTerms.setArabicNewCreditNoteNo(rs.getString("ANewCreditNoteNo"));
                objPosPrintTerms.setArabicCreditBalance(rs.getString("ACreditBalance"));
                objPosPrintTerms.setArabicValidity(rs.getString("AValidity"));
                objPosPrintTerms.setArabicSalesReturn(rs.getString("ASalesReturn"));
                objPosPrintTerms.setArabicCreditNoteno(rs.getString("ACreditNoteNo"));
                objPosPrintTerms.setArabicValidTill(rs.getString("AValidTill"));
                objPosPrintTerms.setArabicHost(rs.getString("AHost"));
                objPosPrintTerms.setArabicOrderTypeForHere(rs.getString("AOrderTypeForhere"));
                objPosPrintTerms.setArabicOrderTypeToGo(rs.getString("AOrderTypeToGo"));
                objPosPrintTerms.setArabicSeeUsAgain(rs.getString("ASeeUsAgain"));
                objPosPrintTerms.setArabicSuggestions(rs.getString("ASuggestions"));
                objPosPrintTerms.setArabicTaxInv(rs.getString("ataxinv"));
                objPosPrintTerms.setArabicTaxAddress(rs.getString("ataxadd"));
                objPosPrintTerms.setArabicVat(rs.getString("ataxvatp"));
                objPosPrintTerms.setArabicTRN(rs.getString("atrnno"));
                objPosPrintTerms.setaVatInv(rs.getString("AVatInv"));
                objPosPrintTerms.setaOrgInvAmt(rs.getString("AOrgInvAmt"));
                objPosPrintTerms.setaSupplVal(rs.getString("ASupplVal"));
                objPosPrintTerms.setArabicBefVat(rs.getString("ArabicBefVat"));
                objPosPrintTerms.setaSubHeading(rs.getString("ASubHeading"));
                objPosPrintTerms.setArabicEWalletRef(rs.getString("AEWalletRef"));
                objPosPrintTerms.setArabicLAmtRedeemed(rs.getString("amountRedeemed"));
                objPosPrintTerms.setArabicLAmtBalance(rs.getString("amountBalance"));
                objPosPrintTerms.setArabicSBagMsg(rs.getString("SurpriseBagMsg"));
                objPosPrintTerms.setAomnRecNo(rs.getString("OmanRecordNo"));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("LoginActivity:loadTermsArabic:" + ex.toString());
            return false;
        }
        return true;
    }

    public boolean loadTerms() {
        objPosPrintTerms.setgUserMsg1("");
        objPosPrintTerms.setgUserMsg2("");
        objPosPrintTerms.setgUserMsg3("");
        objPosPrintTerms.setgUserMsg4("");
        objPosPrintTerms.setgUserMsg5("");
        objPosPrintTerms.setgUserMsg6("");
        objPosPrintTerms.setgUserMsg7("");
        objPosPrintTerms.setgUserMsg8("");
        objPosPrintTerms.setgUserMsg9("");
        objPosPrintTerms.setgUserMsg10("");
        objPosPrintTerms.setgUserMsg11("");
        objPosPrintTerms.setgUserMsg12("");
        objPosPrintTerms.setgUserMsg13("");
        objPosPrintTerms.setgUserMsg14("");
        objPosPrintTerms.setgUserMsg15("");
        objPosPrintTerms.setgUserMsgThankYou("Thank you!");
        objPosPrintTerms.setgUserMsgSeeUsAgain("See us again!");
        objPosPrintTerms.setgUserMsgSuggesions("Suggestions? info@bflgroup.ae");
        try {
            rs = dbConnection.getResultSet("select * from POS_InvMessages where active='Y' and msg_level=0 order by sno", objGlobal.getConnection());
            while (rs.next()) {
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE1"))
                    objPosPrintTerms.setgUserMsg1(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE2"))
                    objPosPrintTerms.setgUserMsg2(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE3"))
                    objPosPrintTerms.setgUserMsg3(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE4"))
                    objPosPrintTerms.setgUserMsg4(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE5"))
                    objPosPrintTerms.setgUserMsg5(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE6"))
                    objPosPrintTerms.setgUserMsg6(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE7"))
                    objPosPrintTerms.setgUserMsg7(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE8"))
                    objPosPrintTerms.setgUserMsg8(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE9"))
                    objPosPrintTerms.setgUserMsg9(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE10"))
                    objPosPrintTerms.setgUserMsg10(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE11"))
                    objPosPrintTerms.setgUserMsg11(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE12"))
                    objPosPrintTerms.setgUserMsg12(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE13"))
                    objPosPrintTerms.setgUserMsg13(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE14"))
                    objPosPrintTerms.setgUserMsg14(rs.getString("msgdesc"));
                if (rs.getString("msgname").toUpperCase().equals("USERMESSAGE15"))
                    objPosPrintTerms.setgUserMsg15(rs.getString("msgdesc"));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("LoginActivity:loadTerms:" + ex.toString());
            return false;
        }
        return true;
    }

    public static int getBatteryPercentage(Context context) {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        double batteryPct = level / (double) scale;
        return (int) (batteryPct * 100);
    }

    public String getMark(String dateN) {
        String markCode = "", dateD = "", dateM = "", dateY = "";
        try {
            if (dateN.contains("/")) {
                String[] dtPart = dateN.split("/");
                dateD = dtPart[0];
                dateM = dtPart[1];
                dateY = dtPart[2];
            } else {
                objGlobal.setErrorMessage("getMark:Date is wrong");
                return "";
            }
            if (dateM.equalsIgnoreCase("01")) {
                markCode = "Z" + dateY.substring(3, 4);
            } else if (dateM.equalsIgnoreCase("02")) {
                markCode = "Y" + dateY.substring(3, 4);
            } else if (dateM.equalsIgnoreCase("03")) {
                markCode = "K" + dateY.substring(3, 4);
            } else if (dateM.equalsIgnoreCase("04")) {
                markCode = "R" + dateY.substring(3, 4);
            } else if (dateM.equalsIgnoreCase("05")) {
                markCode = "T" + dateY.substring(3, 4);
            } else if (dateM.equalsIgnoreCase("06")) {
                markCode = "G" + dateY.substring(3, 4);
            } else if (dateM.equalsIgnoreCase("07")) {
                markCode = "M" + dateY.substring(3, 4);
            } else if (dateM.equalsIgnoreCase("08")) {
                markCode = "P" + dateY.substring(3, 4);
            } else if (dateM.equalsIgnoreCase("09")) {
                markCode = "D" + dateY.substring(3, 4);
            } else if (dateM.equalsIgnoreCase("10")) {
                markCode = "L" + dateY.substring(3, 4);
            } else if (dateM.equalsIgnoreCase("11")) {
                markCode = "U" + dateY.substring(3, 4);
            } else if (dateM.equalsIgnoreCase("12")) {
                markCode = "W" + dateY.substring(3, 4);
            }
            return markCode;
        } catch (Exception e) {
            objGlobal.setErrorMessage("getMark:" + e);
            return "";
        }
    }

    public String replaceStringAg(String str) {
        str = str.replaceAll("\n", "");
        str = str.replaceAll("\r", "");
        return str;
    }

    public String replaceString(String str) {
        str = str.replaceAll("\n", "");
        str = str.replaceAll("\r", "");
        str = seperateBarcode(str);
        return str;
    }

    public String seperateBarcode(String barcode) {
        String[] parts;
        String part1;
        int i;
        if (barcode.contains("/")) {
            parts = barcode.split("/");
            part1 = parts[0];
        } else {
            part1 = barcode;
        }
        for (i = 0; i < part1.length() - 1; i++) {
            if (part1.charAt(i) != '0') {
                break;
            }
        }
        return part1.substring(i);
    }
}
