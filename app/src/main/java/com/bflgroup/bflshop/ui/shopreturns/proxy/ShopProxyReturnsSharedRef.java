package com.bflgroup.bflshop.ui.shopreturns.proxy;

import android.content.Context;
import android.content.SharedPreferences;

public class ShopProxyReturnsSharedRef {

     static SharedPreferences sharedRef;

     public ShopProxyReturnsSharedRef(Context context) {
          sharedRef = context.getSharedPreferences("myRefProxy", Context.MODE_PRIVATE);
     }

    public void SaveCategory(String cat) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("ProxyCategory", cat);
        editor.commit();
    }

    public void SaveCategoryName(String cat) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("ProxyCategoryName", cat);
        editor.commit();
    }

    public void SaveShopName(String Shop){
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("ProxyShopName", Shop);
        editor.commit();
    }

    public static String LoadCategory() {
        return sharedRef.getString("ProxyCategory", "");
    }
    public static String LoadCategoryName() {
        return sharedRef.getString("ProxyCategoryName", "");
    }
    public static String LoadShopName() {
        return sharedRef.getString("ProxyShopName", "");
    }





}
