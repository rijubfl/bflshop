package com.bflgroup.bflshop.ui.grntransfer.grnrfid;

import android.content.Context;
import android.content.SharedPreferences;

public class GrnTransferRfidSharedRef {
    SharedPreferences sharedRef;
    public GrnTransferRfidSharedRef(Context context) {
        sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveGinNo(String ginNo) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("ginNo", ginNo);
        editor.commit();
    }

    public String loadGinNo() {
        return sharedRef.getString("ginNo", "");
    }

    public void saveRecDate(String recDate) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("recDate", recDate);
        editor.commit();
    }

    public String loadRecDate() {
        return sharedRef.getString("recDate", "");
    }

    public void saveScanner(String scanner) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("scanner", scanner);
        editor.commit();
    }

    public String loadScanner() {
        return sharedRef.getString("scanner", "");
    }

}
