package com.bflgroup.bflshop.ui.grndirectdelivery;

public class GrnDirectDeliveryScanItemsAll {
    public String itemCode;
    public int trfQty;
    public int scanQty;
    public int diffQty;

    public GrnDirectDeliveryScanItemsAll(String itemCode, int scanQty, int trfQty,int diffQty) {
        this.itemCode = itemCode;
        this.trfQty = trfQty;
        this.scanQty = scanQty;
        this.diffQty = diffQty;
    }
}
