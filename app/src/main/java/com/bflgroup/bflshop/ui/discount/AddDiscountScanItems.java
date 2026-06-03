package com.bflgroup.bflshop.ui.discount;

public class AddDiscountScanItems {
    public String itemCode;
    public float price;
    public float discPer;

    public AddDiscountScanItems(String itemCode, float price, float discPer) {
        this.itemCode = itemCode;
        this.price = price;
        this.discPer = discPer;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getDiscPer() {
        return discPer;
    }

    public void setDiscPer(float discPer) {
        this.discPer = discPer;
    }


}
