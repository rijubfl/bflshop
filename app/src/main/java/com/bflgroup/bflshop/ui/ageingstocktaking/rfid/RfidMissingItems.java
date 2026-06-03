package com.bflgroup.bflshop.ui.ageingstocktaking.rfid;

public class RfidMissingItems {
    public String itemcode;
    public String Description;
    public String Brand;
    public int sysQty;
    public int scanQty;
    public int DiffQty;


    public RfidMissingItems(String itemcode, String description,String Brand,int sysQty, int scanQty, int DiffQty) {
        this.itemcode = itemcode;
        this.Description = description;
        this.Brand = Brand;
        this.sysQty = sysQty;
        this.scanQty = scanQty;
        this.DiffQty = DiffQty;
    }
}
