package com.bflgroup.bflshop.ui.stocktaking.manual;

public class StockTakingGlobal {

    public static StockTakingGlobal instance;
    private static String totalCount;
    private static String totalCountByUser;
    private static String totalCountByItem;
    private static String description;


    public static String getTotalCount() {
        return totalCount;
    }

    public static void setTotalCount(String totalCount) {
        StockTakingGlobal.totalCount = totalCount;
    }

    public static String getTotalCountByUser() {
        return totalCountByUser;
    }

    public static void setTotalCountByUser(String totalCountByUser) {
        StockTakingGlobal.totalCountByUser = totalCountByUser;
    }

    public static String getTotalCountByItem() {
        return totalCountByItem;
    }

    public static void setTotalCountByItem(String totalCountByItem) {
        StockTakingGlobal.totalCountByItem = totalCountByItem;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        StockTakingGlobal.description = description;
    }

    public static synchronized StockTakingGlobal getInstance(){
        if(instance==null){
            instance=new StockTakingGlobal();
        }
        return instance;
    }
}
