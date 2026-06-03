package com.bflgroup.bflshop.ui.shopreturns;

public class AddScanItemDetails {
        public int SrNo;
        public String ItemCode;
        public String Description;
        public float Qty;
        public float Salesprice;


    public AddScanItemDetails(int srNo, String itemCode, String description, float qty, float salesprice) {
        SrNo = srNo;
        ItemCode = itemCode;
        Description = description;
        Qty = qty;
        Salesprice = salesprice;
    }
}
