package com.bflgroup.bflshop.ui.salesinvoice;

import java.util.List;

public class SalesInvoiceGlobal {
    public static SalesInvoiceGlobal instance;

    private static String invoiceNumber;

    private static List<String> listScanSalesPrice;
    private static List<String> listPayments;

    private static int totalQty;
    private static float totalDisc;
    private static float totalAmt;
    private static float totalPaymentAmt;

    private static float cashAmt;
    private static float cardAmt;
    private static float creditNoteAmt;
    private static float walletAmt;
    private static float giftVoucherAmt;
    private static float mallVoucherAmt;
    private static float staffPurchaseAmt;
    private static float guestPurchaseAmt;
    private static float totalDiscountAmt;
    private static float discountPerForItem;
    private static boolean itemSlashed;
    private static float itemVatPercentage;
    private static float itemVatCalcAmount;
    private static String staffPurchaseEmpCode;
    private static String staffPurchaseEmpName;
    private static double staffPurchaseEmpPurchaseLimit;
    private static double staffPurchaseEmpPurchaseAmt;
    private static String guestPurchaseEmpCode;
    private static String guestPurchaseEmpName;
    private static String crnoteExpDate;
    private static float crnoteTotalAmt;

    private static boolean loadSalesPrice;

    public List<String> getListScanSalesPrice() {
        return listScanSalesPrice;
    }

    public void setListScanSalesPrice(List<String> listScanSalesPrice) {
        this.listScanSalesPrice = listScanSalesPrice;
    }

    public static float getDiscountPerForItem() {
        return discountPerForItem;
    }

    public static void setDiscountPerForItem(float discountPerForItem) {
        SalesInvoiceGlobal.discountPerForItem = discountPerForItem;
    }

    public static boolean isItemSlashed() {
        return itemSlashed;
    }

    public static void setItemSlashed(boolean itemSlashed) {
        SalesInvoiceGlobal.itemSlashed = itemSlashed;
    }

    public static float getItemVatPercentage() {
        return itemVatPercentage;
    }

    public static void setItemVatPercentage(float itemVatPercentage) {
        SalesInvoiceGlobal.itemVatPercentage = itemVatPercentage;
    }

    public static float getItemVatCalcAmount() {
        return itemVatCalcAmount;
    }

    public static void setItemVatCalcAmount(float itemVatCalcAmount) {
        SalesInvoiceGlobal.itemVatCalcAmount = itemVatCalcAmount;
    }

    public static List<String> getListPayments() {
        return listPayments;
    }

    public static void setListPayments(List<String> listPayments) {
        SalesInvoiceGlobal.listPayments = listPayments;
    }

    public static int getTotalQty() {
        return totalQty;
    }

    public static void setTotalQty(int totalQty) {
        SalesInvoiceGlobal.totalQty = totalQty;
    }

    public static float getTotalDisc() {
        return totalDisc;
    }

    public static void setTotalDisc(float totalDisc) {
        SalesInvoiceGlobal.totalDisc = totalDisc;
    }

    public static float getTotalAmt() {
        return totalAmt;
    }

    public static void setTotalAmt(float totalAmt) {
        SalesInvoiceGlobal.totalAmt = totalAmt;
    }

    public static String getInvoiceNumber() {
        return invoiceNumber;
    }

    public static void setInvoiceNumber(String invoiceNumber) {
        SalesInvoiceGlobal.invoiceNumber = invoiceNumber;
    }

    public static float getTotalPaymentAmt() {
        return totalPaymentAmt;
    }

    public static void setTotalPaymentAmt(float totalPaymentAmt) {
        SalesInvoiceGlobal.totalPaymentAmt = totalPaymentAmt;
    }

    public static float getCashAmt() {
        return cashAmt;
    }

    public static void setCashAmt(float cashAmt) {
        SalesInvoiceGlobal.cashAmt = cashAmt;
    }

    public static float getCardAmt() {
        return cardAmt;
    }

    public static void setCardAmt(float cardAmt) {
        SalesInvoiceGlobal.cardAmt = cardAmt;
    }

    public static float getCreditNoteAmt() {
        return creditNoteAmt;
    }

    public static void setCreditNoteAmt(float creditNoteAmt) {
        SalesInvoiceGlobal.creditNoteAmt = creditNoteAmt;
    }

    public static float getWalletAmt() {
        return walletAmt;
    }

    public static void setWalletAmt(float walletAmt) {
        SalesInvoiceGlobal.walletAmt = walletAmt;
    }

    public static float getGiftVoucherAmt() {
        return giftVoucherAmt;
    }

    public static void setGiftVoucherAmt(float giftVoucherAmt) {
        SalesInvoiceGlobal.giftVoucherAmt = giftVoucherAmt;
    }

    public static float getMallVoucherAmt() {
        return mallVoucherAmt;
    }

    public static void setMallVoucherAmt(float mallVoucherAmt) {
        SalesInvoiceGlobal.mallVoucherAmt = mallVoucherAmt;
    }

    public static float getStaffPurchaseAmt() {
        return staffPurchaseAmt;
    }

    public static void setStaffPurchaseAmt(float staffPurchaseAmt) {
        SalesInvoiceGlobal.staffPurchaseAmt = staffPurchaseAmt;
    }

    public static double getStaffPurchaseEmpPurchaseLimit() {
        return staffPurchaseEmpPurchaseLimit;
    }

    public static void setStaffPurchaseEmpPurchaseLimit(double staffPurchaseEmpPurchaseLimit) {
        SalesInvoiceGlobal.staffPurchaseEmpPurchaseLimit = staffPurchaseEmpPurchaseLimit;
    }

    public static boolean isLoadSalesPrice() {
        return loadSalesPrice;
    }

    public static void setLoadSalesPrice(boolean loadSalesPrice) {
        SalesInvoiceGlobal.loadSalesPrice = loadSalesPrice;
    }

    public static String getStaffPurchaseEmpCode() {
        return staffPurchaseEmpCode;
    }

    public static void setStaffPurchaseEmpCode(String staffPurchaseEmpCode) {
        SalesInvoiceGlobal.staffPurchaseEmpCode = staffPurchaseEmpCode;
    }

    public static String getStaffPurchaseEmpName() {
        return staffPurchaseEmpName;
    }

    public static void setStaffPurchaseEmpName(String staffPurchaseEmpName) {
        SalesInvoiceGlobal.staffPurchaseEmpName = staffPurchaseEmpName;
    }

    public static float getGuestPurchaseAmt() {
        return guestPurchaseAmt;
    }

    public static void setGuestPurchaseAmt(float guestPurchaseAmt) {
        SalesInvoiceGlobal.guestPurchaseAmt = guestPurchaseAmt;
    }

    public static String getGuestPurchaseEmpCode() {
        return guestPurchaseEmpCode;
    }

    public static void setGuestPurchaseEmpCode(String guestPurchaseEmpCode) {
        SalesInvoiceGlobal.guestPurchaseEmpCode = guestPurchaseEmpCode;
    }

    public static String getGuestPurchaseEmpName() {
        return guestPurchaseEmpName;
    }

    public static void setGuestPurchaseEmpName(String guestPurchaseEmpName) {
        SalesInvoiceGlobal.guestPurchaseEmpName = guestPurchaseEmpName;
    }

    public static float getTotalDiscountAmt() {
        return totalDiscountAmt;
    }

    public static void setTotalDiscountAmt(float totalDiscountAmt) {
        SalesInvoiceGlobal.totalDiscountAmt = totalDiscountAmt;
    }

    public static String getCrnoteExpDate() {
        return crnoteExpDate;
    }

    public static void setCrnoteExpDate(String crnoteExpDate) {
        SalesInvoiceGlobal.crnoteExpDate = crnoteExpDate;
    }

    public static float getCrnoteTotalAmt() {
        return crnoteTotalAmt;
    }

    public static void setCrnoteTotalAmt(float crnoteTotalAmt) {
        SalesInvoiceGlobal.crnoteTotalAmt = crnoteTotalAmt;
    }

    public static double getStaffPurchaseEmpPurchaseAmt() {
        return staffPurchaseEmpPurchaseAmt;
    }

    public static void setStaffPurchaseEmpPurchaseAmt(double staffPurchaseEmpPurchaseAmt) {
        SalesInvoiceGlobal.staffPurchaseEmpPurchaseAmt = staffPurchaseEmpPurchaseAmt;
    }

    public static synchronized SalesInvoiceGlobal getInstance() {
        if (instance == null) {
            instance = new SalesInvoiceGlobal();
        }
        return instance;
    }

}