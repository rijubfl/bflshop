package com.bflgroup.bflshop.ui.ageingstocktaking.rfid;

public class RfidMissingDivision {
    public String Division;
    public int sysQty;
    public int scanQty;
    public int DiffQty;


    public RfidMissingDivision(String division,int sysQty, int scanQty, int DiffQty) {
        this.Division = division;
        this.sysQty = sysQty;
        this.scanQty = scanQty;
        this.DiffQty = DiffQty;
    }
}
