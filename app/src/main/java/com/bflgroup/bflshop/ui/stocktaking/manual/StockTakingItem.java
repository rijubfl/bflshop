package com.bflgroup.bflshop.ui.stocktaking.manual;

public class StockTakingItem {
    public String itemcode;
    public String date;
    public String time;

    public StockTakingItem(String itemcode, String date, String time) {
        this.itemcode = itemcode;
        this.date = date;
        this.time = time;
    }
}
