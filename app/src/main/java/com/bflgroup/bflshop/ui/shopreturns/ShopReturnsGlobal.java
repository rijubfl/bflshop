package com.bflgroup.bflshop.ui.shopreturns;

public class ShopReturnsGlobal {

    public static synchronized ShopReturnsGlobal getInstance() {


        if(instance == null){
            instance = new ShopReturnsGlobal();
        }
        return instance;
    }
    public static Integer Count = 0;
    public static String EntryNo;
    public static String Messsage;

    public static void setInstance(ShopReturnsGlobal instance) {
        ShopReturnsGlobal.instance = instance;
    }


    public static ShopReturnsGlobal instance;


    public static String getEntryNo() {
        return EntryNo;
    }
    public static String getMessage() {
        return String.valueOf(Messsage);
    }

    public static void setEntryNo(String entryNo) {
        EntryNo = entryNo;
    }
    public static void setMessage(String messsage) {
        Messsage = messsage;
}

    public static int getCount() {
        return Count;
    }

    public static Integer setCount(Integer count) {
        Count = count;
        return Count;
    }




}
