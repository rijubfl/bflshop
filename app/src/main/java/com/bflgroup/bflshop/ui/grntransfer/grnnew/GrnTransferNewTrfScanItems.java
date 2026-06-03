package com.bflgroup.bflshop.ui.grntransfer.grnnew;

public class GrnTransferNewTrfScanItems {
    public String itemcode;
    public int scanqty;
    public int trfqty;
    public int diffqty;

    public GrnTransferNewTrfScanItems(String itemcode, int scanqty, int trfqty, int diffqty) {
        this.itemcode = itemcode;
        this.scanqty = scanqty;
        this.trfqty = trfqty;
        this.diffqty = diffqty;
    }
}
