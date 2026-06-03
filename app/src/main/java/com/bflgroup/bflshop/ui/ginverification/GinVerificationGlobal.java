package com.bflgroup.bflshop.ui.ginverification;

public class GinVerificationGlobal {

    public static GinVerificationGlobal instance;
    private static String trfno;
    private static String scanCount;
    private static String skipsku;

    public static String getScanCount() { return scanCount; }
    public static void setScanCount(String scanCount) { GinVerificationGlobal.scanCount = scanCount; }

    public static String getSkipsku() {
        return skipsku;
    }

    public static void setSkipsku(String skipsku) {
        GinVerificationGlobal.skipsku = skipsku;
    }

    public static String getTrfno() {
        return trfno;
    }

    public static void setTrfno(String trfno) {
        GinVerificationGlobal.trfno = trfno;
    }

    public static synchronized GinVerificationGlobal getInstance() {
        if (instance == null) {
            instance = new GinVerificationGlobal();
        }
        return instance;
    }
}
