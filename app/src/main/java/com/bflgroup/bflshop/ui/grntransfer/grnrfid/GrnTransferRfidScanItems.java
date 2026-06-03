package com.bflgroup.bflshop.ui.grntransfer.grnrfid;
public class GrnTransferRfidScanItems {
    public int slno;
    public String trfno;
    public String toteid;
    public int totalTrfQty;
    public int totalRfidQty;
    public int totalNonRfidQty;
    public int scanRfidQty;
    public int scanNonRfidQty;
    public int diffRfidQty;
    public int diffNonRfidQty;

    public GrnTransferRfidScanItems(int slno, String trfno, String toteid, int totalTrfQty, int totalRfidQty, int totalNonRfidQty, int scanRfidQty, int scanNonRfidQty, int diffRfidQty, int diffNonRfidQty) {
        this.slno = slno;
        this.trfno = trfno;
        this.toteid = toteid;
        this.totalTrfQty = totalTrfQty;
        this.totalRfidQty = totalRfidQty;
        this.totalNonRfidQty = totalNonRfidQty;
        this.scanRfidQty = scanRfidQty;
        this.scanNonRfidQty = scanNonRfidQty;
        this.diffRfidQty = diffRfidQty;
        this.diffNonRfidQty = diffNonRfidQty;
    }
}
