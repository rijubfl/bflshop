package com.bflgroup.bflshop.ui.salesinvoice;

public class SalesInvoiceScanItems {

    public int slno;
    public int rowno;
    public String itemcode;
    public String description;
    public int quantity;
    public float salesprice;
    public float discount;
    public float total;

    public SalesInvoiceScanItems(int slno, int rowno, String itemcode, String description, int quantity, float salesprice, float discount, float total) {
        this.slno = slno;
        this.rowno = rowno;
        this.itemcode = itemcode;
        this.description = description;
        this.quantity = quantity;
        this.salesprice = salesprice;
        this.discount = discount;
        this.total = total;
    }
}
