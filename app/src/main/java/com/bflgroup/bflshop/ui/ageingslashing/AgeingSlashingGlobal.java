package com.bflgroup.bflshop.ui.ageingslashing;

import com.bflgroup.bflshop.ui.ageingslashing.model.AgeingSlashingScanDetailsGlobal;

import java.util.List;

public class AgeingSlashingGlobal {
    public static AgeingSlashingGlobal instance;
    private static double totalAgeingItemsImport;
    private static double totalRfidTransfer;

    private static double totalScan;
    private static double totalScanExport;

    private static String pdaPrefix;
    private static double pdaPrefixSn;

    private static double rptTotalElgQty;
    private static double rptTotalScanQty;
    private static double rptTotalPrintQty;
    private static double rptTotalDiff;

    private static String batchno;
    private static String arabicDesc;
    private static String arabicBrand;


    public static String getPdaPrefix() {
        return pdaPrefix;
    }

    public static void setPdaPrefix(String pdaPrefix) {
        AgeingSlashingGlobal.pdaPrefix = pdaPrefix;
    }

    public static double getPdaPrefixSn() {
        return pdaPrefixSn;
    }

    public static void setPdaPrefixSn(double pdaPrefixSn) {
        AgeingSlashingGlobal.pdaPrefixSn = pdaPrefixSn;
    }

    public static double getTotalAgeingItemsImport() {
        return totalAgeingItemsImport;
    }

    public static void setTotalAgeingItemsImport(double totalAgeingItemsImport) {
        AgeingSlashingGlobal.totalAgeingItemsImport = totalAgeingItemsImport;
    }

    public static double getTotalRfidTransfer() {
        return totalRfidTransfer;
    }

    public static void setTotalRfidTransfer(double totalRfidTransfer) {
        AgeingSlashingGlobal.totalRfidTransfer = totalRfidTransfer;
    }

    public static double getTotalScan() {
        return totalScan;
    }

    public static void setTotalScan(double totalScan) {
        AgeingSlashingGlobal.totalScan = totalScan;
    }

    public static double getTotalScanExport() {
        return totalScanExport;
    }

    public static void setTotalScanExport(double totalScanExport) {
        AgeingSlashingGlobal.totalScanExport = totalScanExport;
    }

    public static String getBatchno() {
        return batchno;
    }

    public static void setBatchno(String batchno) {
        AgeingSlashingGlobal.batchno = batchno;
    }

    public static double getRptTotalElgQty() {
        return rptTotalElgQty;
    }

    public static void setRptTotalElgQty(double rptTotalElgQty) {
        AgeingSlashingGlobal.rptTotalElgQty = rptTotalElgQty;
    }

    public static double getRptTotalScanQty() {
        return rptTotalScanQty;
    }

    public static void setRptTotalScanQty(double rptTotalScanQty) {
        AgeingSlashingGlobal.rptTotalScanQty = rptTotalScanQty;
    }

    public static double getRptTotalPrintQty() {
        return rptTotalPrintQty;
    }

    public static void setRptTotalPrintQty(double rptTotalPrintQty) {
        AgeingSlashingGlobal.rptTotalPrintQty = rptTotalPrintQty;
    }

    public static double getRptTotalDiff() {
        return rptTotalDiff;
    }

    public static void setRptTotalDiff(double rptTotalDiff) {
        AgeingSlashingGlobal.rptTotalDiff = rptTotalDiff;
    }


    public static String getArabicDesc() {
        return arabicDesc;
    }

    public static void setArabicDesc(String arabicDesc) {
        AgeingSlashingGlobal.arabicDesc = arabicDesc;
    }
    public static String getArabicBrand() {
        return arabicBrand;
    }

    public static void setArabicBrand(String arabicBrand) {
        AgeingSlashingGlobal.arabicBrand = arabicBrand;
    }
    public static synchronized AgeingSlashingGlobal getInstance() {
        if (instance == null) {
            instance = new AgeingSlashingGlobal();
        }
        return instance;
    }

}
