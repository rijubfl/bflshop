package com.bflgroup.bflshop.ui.grntransfer.grnrfid;

public class GrnTransferRfidScanDiffItems {
    public String itemcode;
    public int trfQtyRfid;
    public int trfQtyNonRfid;
    public int scanQtyRfid;
    public int scanQtyNonRfid;
    public int diffQtyRfid;
    public int diffQtyNonRfid;

    public GrnTransferRfidScanDiffItems(String itemcode, int trfQtyRfid, int trfQtyNonRfid, int scanQtyRfid, int scanQtyNonRfid, int diffQtyRfid, int diffQtyNonRfid) {
        this.itemcode = itemcode;
        this.trfQtyRfid = trfQtyRfid;
        this.trfQtyNonRfid = trfQtyNonRfid;
        this.scanQtyRfid = scanQtyRfid;
        this.scanQtyNonRfid = scanQtyNonRfid;
        this.diffQtyRfid = diffQtyRfid;
        this.diffQtyNonRfid = diffQtyNonRfid;
    }
}
