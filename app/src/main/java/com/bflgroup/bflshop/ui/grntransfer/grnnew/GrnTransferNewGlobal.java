package com.bflgroup.bflshop.ui.grntransfer.grnnew;
public class GrnTransferNewGlobal {
    public static GrnTransferNewGlobal instance;
    private static String trfno;
    private static String toteid;
    private static String trfdate;
    private static String fromshop;
    private static String latestGrnNo;
    private static String latestGrnNoRf;
    private static String scanDescription;
    private static int scanSysStock;
    private static float scanSysPrice;
    private int scanqty;
    private int trfqty;
    private int diffqty;

    private int itemscanqty;
    private int itemtrfqty;
    private int itemdiffqty;

    private int totalExcess;
    private int totalMissing;

    private String scanBarcode;

    public static String getTrfno() {
        return trfno;
    }

    public static void setTrfno(String trfno) {
        GrnTransferNewGlobal.trfno = trfno;
    }

    public static String getToteid() {
        return toteid;
    }

    public static void setToteid(String toteid) {
        GrnTransferNewGlobal.toteid = toteid;
    }

    public static String getTrfdate() {
        return trfdate;
    }

    public static void setTrfdate(String trfdate) {
        GrnTransferNewGlobal.trfdate = trfdate;
    }

    public static String getFromshop() {
        return fromshop;
    }

    public static void setFromshop(String fromshop) {
        GrnTransferNewGlobal.fromshop = fromshop;
    }

    public static String getLatestGrnNo() {
        return latestGrnNo;
    }

    public static void setLatestGrnNo(String latestGrnNo) {
        GrnTransferNewGlobal.latestGrnNo = latestGrnNo;
    }

    public static String getLatestGrnNoRf() {
        return latestGrnNoRf;
    }

    public static void setLatestGrnNoRf(String latestGrnNoRf) {
        GrnTransferNewGlobal.latestGrnNoRf = latestGrnNoRf;
    }

    public int getScanqty() {
        return scanqty;
    }

    public void setScanqty(int scanqty) {
        this.scanqty = scanqty;
    }

    public int getTrfqty() {
        return trfqty;
    }

    public void setTrfqty(int trfqty) {
        this.trfqty = trfqty;
    }

    public int getDiffqty() {
        return diffqty;
    }

    public void setDiffqty(int diffqty) {
        this.diffqty = diffqty;
    }

    public static String getScanDescription() {
        return scanDescription;
    }

    public static void setScanDescription(String scanDescription) {
        GrnTransferNewGlobal.scanDescription = scanDescription;
    }

    public int getItemscanqty() {
        return itemscanqty;
    }

    public void setItemscanqty(int itemscanqty) {
        this.itemscanqty = itemscanqty;
    }

    public int getItemtrfqty() {
        return itemtrfqty;
    }

    public void setItemtrfqty(int itemtrfqty) {
        this.itemtrfqty = itemtrfqty;
    }

    public int getItemdiffqty() {
        return itemdiffqty;
    }

    public void setItemdiffqty(int itemdiffqty) {
        this.itemdiffqty = itemdiffqty;
    }

    public int getTotalExcess() {
        return totalExcess;
    }

    public void setTotalExcess(int totalExcess) {
        this.totalExcess = totalExcess;
    }

    public int getTotalMissing() {
        return totalMissing;
    }

    public void setTotalMissing(int totalMissing) {
        this.totalMissing = totalMissing;
    }

    public String getScanBarcode() {
        return scanBarcode;
    }

    public void setScanBarcode(String scanBarcode) {
        this.scanBarcode = scanBarcode;
    }

    public static int getScanSysStock() {
        return scanSysStock;
    }

    public static void setScanSysStock(int scanSysStock) {
        GrnTransferNewGlobal.scanSysStock = scanSysStock;
    }
    public static float getScanSysPrice() {
        return scanSysPrice;
    }

    public static void setScanSysPrice(float scanSysPrice) {
        GrnTransferNewGlobal.scanSysPrice = scanSysPrice;
    }

    public static synchronized GrnTransferNewGlobal getInstance() {
        if (instance == null) {
            instance = new GrnTransferNewGlobal();
        }
        return instance;
    }
}
