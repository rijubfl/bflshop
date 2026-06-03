package com.bflgroup.bflshop.ui.ageingstocktaking.model;

public class AgeingStockTakingReports {
    public String zone;
    public String user;
    public int scanqty;
    public int manqty;
    public int diff;

    public AgeingStockTakingReports(String zone, String user, int scanqty, int manqty, int diff) {
        this.zone = zone;
        this.user = user;
        this.scanqty = scanqty;
        this.manqty = manqty;
        this.diff = diff;
    }
}
