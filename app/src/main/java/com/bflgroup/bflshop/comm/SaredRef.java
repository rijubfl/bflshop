package com.bflgroup.bflshop.comm;

import android.content.Context;
import android.content.SharedPreferences;

public class SaredRef {

    SharedPreferences sharedRef;

    public SaredRef(Context context) {
        sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    public void saveServer(String server) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("Server", server);
        editor.commit();
    }

    public String loadServer() {
        return sharedRef.getString("Server", "192.168.");
    }
}

