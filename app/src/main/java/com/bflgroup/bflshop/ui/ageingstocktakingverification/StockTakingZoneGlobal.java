package com.bflgroup.bflshop.ui.ageingstocktakingverification;

import java.util.List;

public class StockTakingZoneGlobal {

    public static StockTakingZoneGlobal instance;
    private static List<String> zoneList;
    private static int quantity;
    private static String Username;

    private static String dtFrom;
    private static String dtTo;

    public static List<String> getZoneList() {
        return zoneList;
    }
    public static void setZoneList(List<String> zoneList) {
        StockTakingZoneGlobal.zoneList = zoneList;
    }

    public static synchronized StockTakingZoneGlobal getInstance() {
        if (instance == null) {
            instance = new StockTakingZoneGlobal();
        }
        return instance;
    }

    public static String getDtFrom() {
        return dtFrom;
    }

    public static void setDtFrom(String dtFrom) {
        StockTakingZoneGlobal.dtFrom = dtFrom;
    }

    public static String getDtTo() {
        return dtTo;
    }

    public static void setDtTo(String dtTo) {
        StockTakingZoneGlobal.dtTo = dtTo;
    }

    public static int getQuantity() {
        return quantity;
    }

    public static void setQuantity(int quantity) {
        StockTakingZoneGlobal.quantity = quantity;
    }

    public static String getUsername() {
        return Username;
    }

    public static void setUsername(String username) {
        StockTakingZoneGlobal.Username = username;
    }
}
