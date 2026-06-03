package com.bflgroup.bflshop.ui.grntransfer.grnold;

import android.content.Context;
import android.content.SharedPreferences;

public class GrnTransferSharedRef {
    SharedPreferences sharedRef;

    public GrnTransferSharedRef(Context context) {
        sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveTrfNo(String trfNo) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("TrfNo", trfNo);
        editor.commit();
    }

    public void saveShopTrf(boolean tick) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putBoolean("tick", tick);
        editor.commit();
    }

    public void saveViewTick(boolean tick) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putBoolean("view", tick);
        editor.commit();
    }

    public String loadTrfNo() {
        return sharedRef.getString("TrfNo", "");
    }

    public boolean loadTickShopTrf() {
        return sharedRef.getBoolean("tick", false);
    }

    public boolean loadTickView() {
        return sharedRef.getBoolean("view", false);
    }

}
