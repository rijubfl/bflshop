package com.bflgroup.bflshop.ui.ageingslashing.model;

public class AgeingSlashingItemsReports {
    public String itemcode;
    public int eligible;
    public int scan;
    public int print;
    public int bal;

    public AgeingSlashingItemsReports(String itemcode, int eligible, int scan, int print, int bal) {
        this.itemcode = itemcode;
        this.eligible = eligible;
        this.scan = scan;
        this.print = print;
        this.bal = bal;
    }
}
