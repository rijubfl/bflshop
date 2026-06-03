package com.bflgroup.bflshop.ui.grntransfer.grnold;

public class GrnTransferScanItemsAll {
    public String itemCode;
    public int trfQty;
    public int scanQty;
    public int diffQty;

    public GrnTransferScanItemsAll(String itemCode, int scanQty, int trfQty,int diffQty) {
        this.itemCode = itemCode;
        this.trfQty = trfQty;
        this.scanQty = scanQty;
        this.diffQty = diffQty;
    }
}
