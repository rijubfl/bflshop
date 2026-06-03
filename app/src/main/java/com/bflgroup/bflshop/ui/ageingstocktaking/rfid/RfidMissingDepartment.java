package com.bflgroup.bflshop.ui.ageingstocktaking.rfid;

public class RfidMissingDepartment {
    public String Department;
    public int sysQty;
    public int scanQty;
    public int DiffQty;


    public RfidMissingDepartment(String Department,int sysQty, int scanQty, int DiffQty) {
        this.Department = Department;
        this.sysQty = sysQty;
        this.scanQty = scanQty;
        this.DiffQty = DiffQty;
    }
}
