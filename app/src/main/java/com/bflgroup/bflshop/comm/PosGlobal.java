package com.bflgroup.bflshop.comm;

import java.util.Date;

public class PosGlobal {

    public static PosGlobal instance;

    private static String shopName;
    private static String costCode;
    private static String locCode;
    private static String shopLetter;
    private static float decimals;
    private static String barcodePrintHead1;
    private static String barcodePrintHead2;
    private static String asIsShop;
    private static String applyVat;
    private static float vatPercentage;
    private static float vatCalcAmount;
    private static float staffCeiling;
    private static String applyGroupDiscount;
    private static String arabicDescription;
    private static String typePassRequired;
    private static String applyItemDiscount;
    private static Date discStartDate;
    private static Date discEndDate;
    private static String slashedItemDisc;
    private static String buyGet;
    private static int buyGetBuyQty;
    private static float buyGetDiscPer;
    private static String printArabicTersm;
    private static String printEnglishTersm;
    private static boolean slashActive;
    private static String printWasNow;
    private static String printWasHead;
    private static String printNowHead;
    private static String printWasNowPerc;
    private static String printAgeSameprice;
    private static String showAgeEligibelQty;
    private static String coffeeInvoice;
    private static String stockTakeValServer;
    private static String grnItemVal;
    private static String grnSpVal;
    private static String grnExcessVal;
    private static String skipScanSkuGrn;
    private static String hoPricingSP;
    private static String hoPricingSPLetter;
    private static String cloudMode;
    private static int hoPricingSPDays;

    public static synchronized PosGlobal getInstance() {
        if (instance == null) {
            instance = new PosGlobal();
        }
        return instance;
    }

    public static String getHoPricingSPLetter() {
        return hoPricingSPLetter;
    }

    public static void setHoPricingSPLetter(String hoPricingSPLetter) {
        PosGlobal.hoPricingSPLetter = hoPricingSPLetter;
    }

    public static int getHoPricingSPDays() {
        return hoPricingSPDays;
    }

    public static void setHoPricingSPDays(int hoPricingSPDays) {
        PosGlobal.hoPricingSPDays = hoPricingSPDays;
    }

    public static String getCostCode() {
        return costCode;
    }

    public static void setCostCode(String costCode) {
        PosGlobal.costCode = costCode;
    }

    public static String getLocCode() {
        return locCode;
    }

    public static void setLocCode(String locCode) {
        PosGlobal.locCode = locCode;
    }

    public static String getShopLetter() {
        return shopLetter;
    }

    public static void setShopLetter(String shopLetter) {
        PosGlobal.shopLetter = shopLetter;
    }

    public static String getShopName() {
        return shopName;
    }

    public static void setShopName(String shopName) {
        PosGlobal.shopName = shopName;
    }

    public static String getApplyGroupDiscount() {
        return applyGroupDiscount;
    }

    public static void setApplyGroupDiscount(String applyGroupDiscount) {
        PosGlobal.applyGroupDiscount = applyGroupDiscount;
    }

    public static String getArabicDescription() {
        return arabicDescription;
    }

    public static void setArabicDescription(String arabicDescription) {
        PosGlobal.arabicDescription = arabicDescription;
    }

    public static String getTypePassRequired() {
        return typePassRequired;
    }

    public static void setTypePassRequired(String typePassRequired) {
        PosGlobal.typePassRequired = typePassRequired;
    }

    public static Date getDiscStartDate() {
        return discStartDate;
    }

    public static void setDiscStartDate(Date discStartDate) {
        PosGlobal.discStartDate = discStartDate;
    }

    public static Date getDiscEndDate() {
        return discEndDate;
    }

    public static void setDiscEndDate(Date discEndDate) {
        PosGlobal.discEndDate = discEndDate;
    }

    public static String getApplyVat() {
        return applyVat;
    }

    public static void setApplyVat(String applyVat) {
        PosGlobal.applyVat = applyVat;
    }

    public static float getVatPercentage() {
        return vatPercentage;
    }

    public static void setVatPercentage(float vatPercentage) {
        PosGlobal.vatPercentage = vatPercentage;
    }

    public static float getVatCalcAmount() {
        return vatCalcAmount;
    }

    public static void setVatCalcAmount(float vatCalcAmount) {
        PosGlobal.vatCalcAmount = vatCalcAmount;
    }

    public static String getPrintAgeSameprice() {
        return printAgeSameprice;
    }

    public static void setPrintAgeSameprice(String printAgeSameprice) {
        PosGlobal.printAgeSameprice = printAgeSameprice;
    }

    public static String getSlashedItemDisc() {
        return slashedItemDisc;
    }

    public static void setSlashedItemDisc(String slashedItemDisc) {
        PosGlobal.slashedItemDisc = slashedItemDisc;
    }

    public static String getApplyItemDiscount() {
        return applyItemDiscount;
    }

    public static void setApplyItemDiscount(String applyItemDiscount) {
        PosGlobal.applyItemDiscount = applyItemDiscount;
    }

    public static String getAsIsShop() {
        return asIsShop;
    }

    public static void setAsIsShop(String asIsShop) {
        PosGlobal.asIsShop = asIsShop;
    }

    public static float getDecimals() {
        return decimals;
    }

    public static void setDecimals(float decimals) {
        PosGlobal.decimals = decimals;
    }

    public static String getBarcodePrintHead1() {
        return barcodePrintHead1;
    }

    public static void setBarcodePrintHead1(String barcodePrintHead1) {
        PosGlobal.barcodePrintHead1 = barcodePrintHead1;
    }

    public static String getBarcodePrintHead2() {
        return barcodePrintHead2;
    }

    public static void setBarcodePrintHead2(String barcodePrintHead2) {
        PosGlobal.barcodePrintHead2 = barcodePrintHead2;
    }

    public static float getStaffCeiling() {
        return staffCeiling;
    }

    public static void setStaffCeiling(float staffCeiling) {
        PosGlobal.staffCeiling = staffCeiling;
    }

    public static String getBuyGet() {
        return buyGet;
    }

    public static void setBuyGet(String buyGet) {
        PosGlobal.buyGet = buyGet;
    }

    public static int getBuyGetBuyQty() {
        return buyGetBuyQty;
    }

    public static void setBuyGetBuyQty(int buyGetBuyQty) {
        PosGlobal.buyGetBuyQty = buyGetBuyQty;
    }

    public static float getBuyGetDiscPer() {
        return buyGetDiscPer;
    }

    public static void setBuyGetDiscPer(float buyGetDiscPer) {
        PosGlobal.buyGetDiscPer = buyGetDiscPer;
    }

    public static String getPrintArabicTersm() {
        return printArabicTersm;
    }

    public static void setPrintArabicTersm(String printArabicTersm) {
        PosGlobal.printArabicTersm = printArabicTersm;
    }

    public static String getPrintEnglishTersm() {
        return printEnglishTersm;
    }

    public static void setPrintEnglishTersm(String printEnglishTersm) {
        PosGlobal.printEnglishTersm = printEnglishTersm;
    }

    public static boolean getSlashActive() {
        return slashActive;
    }

    public static void setSlashActive(boolean slashActive) {
        PosGlobal.slashActive = slashActive;
    }

    public static String getPrintWasNow() {
        return printWasNow;
    }

    public static void setPrintWasNow(String printWasNow) {
        PosGlobal.printWasNow = printWasNow;
    }

    public static String getPrintWasNowPerc() {
        return printWasNowPerc;
    }

    public static void setPrintWasNowPerc(String printWasNowPerc) {
        PosGlobal.printWasNowPerc = printWasNowPerc;
    }

    public static String getPrintWasHead() {
        return printWasHead;
    }

    public static void setPrintWasHead(String printWasHead) {
        PosGlobal.printWasHead = printWasHead;
    }

    public static String getPrintNowHead() {
        return printNowHead;
    }

    public static void setPrintNowHead(String printNowHead) {
        PosGlobal.printNowHead = printNowHead;
    }

    public static String getCoffeeInvoice() {
        return coffeeInvoice;
    }

    public static void setCoffeeInvoice(String coffeeInvoice) {
        PosGlobal.coffeeInvoice = coffeeInvoice;
    }

    public static String getStockTakeValServer() {
        return stockTakeValServer;
    }

    public static void setStockTakeValServer(String stockTakeValServer) {
        PosGlobal.stockTakeValServer = stockTakeValServer;
    }

    public static boolean isSlashActive() {
        return slashActive;
    }

    public static String getGrnItemVal() {
        return grnItemVal;
    }

    public static void setGrnItemVal(String grnItemVal) {
        PosGlobal.grnItemVal = grnItemVal;
    }

    public static String getGrnSpVal() {
        return grnSpVal;
    }

    public static void setGrnSpVal(String grnSpVal) {
        PosGlobal.grnSpVal = grnSpVal;
    }

    public static String getGrnExcessVal() {
        return grnExcessVal;
    }

    public static void setGrnExcessVal(String grnExcessVal) {
        PosGlobal.grnExcessVal = grnExcessVal;
    }

    public static String getSkipScanSkuGrn() {
        return skipScanSkuGrn;
    }

    public static void setSkipScanSkuGrn(String skipScanSkuGrn) {
        PosGlobal.skipScanSkuGrn = skipScanSkuGrn;
    }

    public static String getHoPricingSP() {
        return hoPricingSP;
    }

    public static void setHoPricingSP(String hoPricingSP) {
        PosGlobal.hoPricingSP = hoPricingSP;
    }

    public static String getShowAgeEligibelQty() {
        return showAgeEligibelQty;
    }

    public static void setShowAgeEligibelQty(String showAgeEligibelQty) {
        PosGlobal.showAgeEligibelQty = showAgeEligibelQty;
    }

    public static String getCloudMode() {
        return cloudMode;
    }

    public static void setCloudMode(String cloudMode) {
        PosGlobal.cloudMode = cloudMode;
    }
}