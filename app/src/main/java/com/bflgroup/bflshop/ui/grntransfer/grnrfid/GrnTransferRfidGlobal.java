package com.bflgroup.bflshop.ui.grntransfer.grnrfid;

public class GrnTransferRfidGlobal {
    public static GrnTransferRfidGlobal instance;

    private String gindate;
    private int TotTotes;
    private int TotTrfQty;
    private int TrfQtyRfid;
    private int TrfQtyNonRfid;
    private int ScanQtyRfid;
    private int ScanQtyNonRfid;
    private int DiffQtyRfid;
    private int DiffQtyNonRfid;
    private static String latestGrnNoRf;
    private static String latestGrnNo;

    private int trfTotTrfQty;
    private int trfTrfQtyRfid;
    private int trfTrfQtyNonRfid;
    private int trfScanQtyRfid;
    private int trfScanQtyNonRfid;
    private int trfDiffQtyRfid;
    private int trfDiffQtyNonRfid;
    private int trfValidExcessQty;

    private String verifyTrfNo;

    public int getTrfValidExcessQty() {
        return trfValidExcessQty;
    }

    public void setTrfValidExcessQty(int trfValidExcessQty) {
        this.trfValidExcessQty = trfValidExcessQty;
    }

    public static String getLatestGrnNo() {
        return latestGrnNo;
    }

    public static void setLatestGrnNo(String latestGrnNo) {
        GrnTransferRfidGlobal.latestGrnNo = latestGrnNo;
    }

    public static String getLatestGrnNoRf() {
        return latestGrnNoRf;
    }

    public static void setLatestGrnNoRf(String latestGrnNoRf) {
        GrnTransferRfidGlobal.latestGrnNoRf = latestGrnNoRf;
    }

    public String getGindate() {
        return gindate;
    }

    public void setGindate(String gindate) {
        this.gindate = gindate;
    }

    public int getTotTotes() {
        return TotTotes;
    }

    public void setTotTotes(int totTotes) {
        TotTotes = totTotes;
    }

    public int getTotTrfQty() {
        return TotTrfQty;
    }

    public void setTotTrfQty(int totTrfQty) {
        TotTrfQty = totTrfQty;
    }

    public int getTrfQtyRfid() {
        return TrfQtyRfid;
    }

    public void setTrfQtyRfid(int trfQtyRfid) {
        TrfQtyRfid = trfQtyRfid;
    }

    public int getTrfQtyNonRfid() {
        return TrfQtyNonRfid;
    }

    public void setTrfQtyNonRfid(int trfQtyNonRfid) {
        TrfQtyNonRfid = trfQtyNonRfid;
    }

    public int getScanQtyRfid() {
        return ScanQtyRfid;
    }

    public void setScanQtyRfid(int scanQtyRfid) {
        ScanQtyRfid = scanQtyRfid;
    }

    public int getScanQtyNonRfid() {
        return ScanQtyNonRfid;
    }

    public void setScanQtyNonRfid(int scanQtyNonRfid) {
        ScanQtyNonRfid = scanQtyNonRfid;
    }

    public int getDiffQtyRfid() {
        return DiffQtyRfid;
    }

    public void setDiffQtyRfid(int diffQtyRfid) {
        DiffQtyRfid = diffQtyRfid;
    }

    public int getDiffQtyNonRfid() {
        return DiffQtyNonRfid;
    }

    public void setDiffQtyNonRfid(int diffQtyNonRfid) {
        DiffQtyNonRfid = diffQtyNonRfid;
    }

    public int getTrfTotTrfQty() {
        return trfTotTrfQty;
    }

    public void setTrfTotTrfQty(int trfTotTrfQty) {
        this.trfTotTrfQty = trfTotTrfQty;
    }

    public int getTrfTrfQtyRfid() {
        return trfTrfQtyRfid;
    }

    public void setTrfTrfQtyRfid(int trfTrfQtyRfid) {
        this.trfTrfQtyRfid = trfTrfQtyRfid;
    }

    public int getTrfTrfQtyNonRfid() {
        return trfTrfQtyNonRfid;
    }

    public void setTrfTrfQtyNonRfid(int trfTrfQtyNonRfid) {
        this.trfTrfQtyNonRfid = trfTrfQtyNonRfid;
    }

    public int getTrfScanQtyRfid() {
        return trfScanQtyRfid;
    }

    public void setTrfScanQtyRfid(int trfScanQtyRfid) {
        this.trfScanQtyRfid = trfScanQtyRfid;
    }

    public int getTrfScanQtyNonRfid() {
        return trfScanQtyNonRfid;
    }

    public void setTrfScanQtyNonRfid(int trfScanQtyNonRfid) {
        this.trfScanQtyNonRfid = trfScanQtyNonRfid;
    }

    public int getTrfDiffQtyRfid() {
        return trfDiffQtyRfid;
    }

    public void setTrfDiffQtyRfid(int trfDiffQtyRfid) {
        this.trfDiffQtyRfid = trfDiffQtyRfid;
    }

    public int getTrfDiffQtyNonRfid() {
        return trfDiffQtyNonRfid;
    }

    public void setTrfDiffQtyNonRfid(int trfDiffQtyNonRfid) {
        this.trfDiffQtyNonRfid = trfDiffQtyNonRfid;
    }

    public String getVerifyTrfNo() {
        return verifyTrfNo;
    }

    public void setVerifyTrfNo(String verifyTrfNo) {
        this.verifyTrfNo = verifyTrfNo;
    }

    public static synchronized GrnTransferRfidGlobal getInstance() {
        if (instance == null) {
            instance = new GrnTransferRfidGlobal();
        }
        return instance;
    }
}
