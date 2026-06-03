package com.bflgroup.bflshop.ui.shopreturns;

import android.content.Context;
import android.content.SharedPreferences;

public class ShopReturnsSharedRef {

     static SharedPreferences sharedRef;

     public ShopReturnsSharedRef(Context context) {
          sharedRef = context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
     }

    public void SaveCategory(String cat) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("Category", cat);
        editor.commit();
    }

    public void SaveCategoryName(String cat) {
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("CategoryName", cat);
        editor.commit();
    }

    public void SaveShopName(String Shop){
        SharedPreferences.Editor editor = sharedRef.edit();
        editor.putString("ShopName", Shop);
        editor.commit();
    }

    public static String LoadCategory() {
        return sharedRef.getString("Category", "");
    }
    public static String LoadCategoryName() {
        return sharedRef.getString("CategoryName", "");
    }
    public static String LoadShopName() {
        return sharedRef.getString("ShopName", "");
    }





}
