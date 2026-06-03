package com.bflgroup.bflshop.ui.ageingslashing.model;

public class AgeingSlashingItemsScan {
    public String scan;
    public float currPrice;
    public float newPrice;
    public String label;
    public String export;

    public AgeingSlashingItemsScan(String scan, float currPrice, float newPrice, String label, String export) {
        this.scan = scan;
        this.currPrice = currPrice;
        this.newPrice = newPrice;
        this.label = label;
        this.export = export;
    }
}
