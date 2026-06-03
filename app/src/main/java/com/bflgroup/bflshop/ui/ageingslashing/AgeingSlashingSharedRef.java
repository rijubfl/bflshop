package com.bflgroup.bflshop.ui.ageingslashing;

import android.content.Context;
import android.content.SharedPreferences;

public class AgeingSlashingSharedRef {
    static SharedPreferences sharedRef;

    public AgeingSlashingSharedRef(Context context) {
        sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void savePrinter(String printer) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("Printer", printer);
        editor.commit();
    }
    public static String loadPrinter() {
        return sharedRef.getString("Printer", "");
    }

    public void savePrinterYellow(String yellow) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("PrinterYellow", yellow);
        editor.commit();
    }
    public static String loadPrinterYellow() {
        return sharedRef.getString("PrinterYellow", "");
    }

    public void savePrinterYellowActive(String yellow) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("PrinterYellowActive", yellow);
        editor.commit();
    }
    public static String loadPrinterYellowActive() {
        return sharedRef.getString("PrinterYellowActive", "");
    }

    public void savePrinterRed(String red) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("PrinterRed", red);
        editor.commit();
    }
    public static String loadPrinterRed() { return sharedRef.getString("PrinterRed", ""); }

    public void savePrinterRedActive(String red) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("PrinterRedActive", red);
        editor.commit();
    }
    public static String loadPrinterRedActive() { return sharedRef.getString("PrinterRedActive", ""); }

    public void savePrinterWhite(String white) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("PrinterWhite",white);
        editor.commit();
    }
    public static String loadPrinterWhite() { return sharedRef.getString("PrinterWhite", ""); }

    public void savePrinterWhiteActive(String white) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("PrinterWhiteActive",white);
        editor.commit();
    }
    public static String loadPrinterWhiteActive() { return sharedRef.getString("PrinterWhiteActive", ""); }


}
