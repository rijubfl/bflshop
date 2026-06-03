package com.bflgroup.bflshop.ui.ageingstocktaking.manual;

import java.util.List;

public class AgeingStockTakingGlobal {

    public static AgeingStockTakingGlobal instance;
    private static double totalScan;
    private static double totalScanExport;
    private static double totalMan;
    private static double totalScanDelQty;
    private static String barcode;
    private static String rfid;
    private static String dtFrom;
    private static String dtTo;
    private static List<String> zoneList;

    public static List<String> getZoneList() {
        return zoneList;
    }

    public static void setZoneList(List<String> zoneList) {
        AgeingStockTakingGlobal.zoneList = zoneList;
    }

    public static double getTotalScan() {
        return totalScan;
    }

    public static void setTotalScan(double totalScan) {
        AgeingStockTakingGlobal.totalScan = totalScan;
    }

    public static double getTotalScanExport() {
        return totalScanExport;
    }

    public static void setTotalScanExport(double totalScanExport) {
        AgeingStockTakingGlobal.totalScanExport = totalScanExport;
    }

    public static double getTotalMan() {
        return totalMan;
    }

    public static void setTotalMan(double totalMan) {
        AgeingStockTakingGlobal.totalMan = totalMan;
    }

    public static String getBarcode() {
        return barcode;
    }

    public static void setBarcode(String barcode) {
        AgeingStockTakingGlobal.barcode = barcode;
    }

    public static String getRfid() {
        return rfid;
    }

    public static void setRfid(String rfid) {
        AgeingStockTakingGlobal.rfid = rfid;
    }

    public static String getDtFrom() {
        return dtFrom;
    }

    public static void setDtFrom(String dtFrom) {
        AgeingStockTakingGlobal.dtFrom = dtFrom;
    }

    public static String getDtTo() {
        return dtTo;
    }

    public static void setDtTo(String dtTo) {
        AgeingStockTakingGlobal.dtTo = dtTo;
    }

    public static double getTotalScanDelQty() {
        return totalScanDelQty;
    }

    public static void setTotalScanDelQty(double totalScanDelQty) {
        AgeingStockTakingGlobal.totalScanDelQty = totalScanDelQty;
    }

    public static synchronized AgeingStockTakingGlobal getInstance() {
        if (instance == null) {
            instance = new AgeingStockTakingGlobal();
        }
        return instance;
    }
}
