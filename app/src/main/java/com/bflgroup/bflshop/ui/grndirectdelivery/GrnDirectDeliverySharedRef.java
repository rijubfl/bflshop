package com.bflgroup.bflshop.ui.grndirectdelivery;

import android.content.Context;
import android.content.SharedPreferences;

public class GrnDirectDeliverySharedRef {

    SharedPreferences sharedRef;

    public GrnDirectDeliverySharedRef(Context context) {
        sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveDirectDeliveryPoNo(String poNo) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("DirectDeliveryPoNo", poNo);
        editor.commit();
    }

    public void saveViewDirectDelivery(boolean tick) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putBoolean("ViewDirectDelivery", tick);
        editor.commit();
    }

    public String loadDirectDeliveryPoNo() {
        return sharedRef.getString("DirectDeliveryPoNo", "");
    }

    public boolean loadViewDirectDelivery() {
        return sharedRef.getBoolean("ViewDirectDelivery", false);
    }

}
