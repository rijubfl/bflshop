package com.bflgroup.bflshop.ui.grntransfer.grnnew;

import android.content.Context;
import android.content.SharedPreferences;

public class GrnTransferNewSharedRef {

    SharedPreferences sharedRef;
    public GrnTransferNewSharedRef(Context context) {
        sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveTrfNo(String trfNo) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("TrfNo", trfNo);
        editor.commit();
    }

    public String loadGinNo() {
        return sharedRef.getString("GinNo", "");
    }

    public void saveGinNo(String GinNo) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("GinNo", GinNo);
        editor.commit();
    }

    public String loadTrfNo() {
        return sharedRef.getString("TrfNo", "");
    }

    public void saveTickShopTransfer(boolean tick) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putBoolean("tickS", tick);
        editor.commit();
    }

    public boolean loadTickShopTransfer() {
        return sharedRef.getBoolean("tickS", false);
    }

    public void saveTickView(boolean tick) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putBoolean("tickV", tick);
        editor.commit();
    }

    public boolean loadTickView() {
        return sharedRef.getBoolean("tickV", false);
    }

    public void saveShopTrfName(String shopname) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("shopname", shopname);
        editor.commit();
    }

    public String loadShopTrfName() {
        return sharedRef.getString("shopname", "");
    }

    public void saveTrfDate(String trfdate) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("trfdate", trfdate);
        editor.commit();
    }

    public String loadTrfDate() {
        return sharedRef.getString("trfdate", "");
    }

    public void saveToteId(String toteid) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("toteid", toteid);
        editor.commit();
    }

    public String loadToteId() {
        return sharedRef.getString("toteid", "");
    }

    public void saveLastSave(String lastsave) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("lastsave", lastsave);
        editor.commit();
    }

    public String loadLastSave() {
        return sharedRef.getString("lastsave", "");
    }

}
