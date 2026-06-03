package com.bflgroup.bflshop.ui.ageingstocktaking.model;

public class AgeingStockTakingReportsForDelete {
    public String itemcode;
    public String barcode;
    public String srid;

    public AgeingStockTakingReportsForDelete(String itemcode, String barcode, String srid) {
        this.itemcode = itemcode;
        this.barcode = barcode;
        this.srid = srid;
    }
}
