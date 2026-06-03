package com.bflgroup.bflshop.ui.salesinvoice;

public class SalesInvoicePaymentItems {

    public int slno;
    public int rowno;
    public String paymentType;
    public String refNo;
    public float amount;
    public SalesInvoicePaymentItems(int slno, int rowno, String paymentType, String refNo, float amount) {
        this.slno = slno;
        this.rowno = rowno;
        this.paymentType = paymentType;
        this.refNo = refNo;
        this.amount = amount;
    }
}
