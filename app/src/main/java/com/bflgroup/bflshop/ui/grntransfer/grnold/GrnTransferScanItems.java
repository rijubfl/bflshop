package com.bflgroup.bflshop.ui.grntransfer.grnold;

public class GrnTransferScanItems {
    public String itemCode;
    public int trfQty;
    public int scanQty;

    public GrnTransferScanItems(String itemCode, int trfQty ,int scanQty) {
        this.itemCode = itemCode;
        this.trfQty = trfQty;
        this.scanQty = scanQty;
    }
}
