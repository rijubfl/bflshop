package com.bflgroup.bflshop.ui.stocktaking.manual;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Global;

import java.sql.SQLException;
import java.util.ArrayList;

public class StockTakingFragment extends Fragment {

    Global objGlobal = Global.getInstance();
    StockTakingControl objStockTakingControl = new StockTakingControl();
    StockTakingGlobal objStockTakingGlobal = new StockTakingGlobal();

    ArrayList<StockTakingItem> listStockTakingItem = new ArrayList<StockTakingItem>();
    MyStockTakingItemAdp objMyStockTakingItemAdp;

    private ListView lv_stock_taking;
    private EditText et_stock_taking_itemcode;
    private TextView tv_stock_taking_main_itemcode;
    private TextView tv_stock_taking_total_count;
    private TextView tv_stock_taking_user_count;
    private TextView tv_stock_taking_item_count;
    private TextView tv_stock_taking_description;
    private TextView textView4;

    public StockTakingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stock_taking, container, false);

        lv_stock_taking = (ListView) view.findViewById(R.id.lv_stock_taking);
        et_stock_taking_itemcode = (EditText) view.findViewById(R.id.et_stock_taking_itemcode);
        tv_stock_taking_main_itemcode = (TextView) view.findViewById(R.id.tv_stock_taking_main_itemcode);
        tv_stock_taking_description = (TextView) view.findViewById(R.id.tv_stock_taking_description);
        tv_stock_taking_total_count = (TextView) view.findViewById(R.id.tv_stock_taking_total_count);
        tv_stock_taking_user_count = (TextView) view.findViewById(R.id.tv_stock_taking_user_count);
        tv_stock_taking_item_count = (TextView) view.findViewById(R.id.tv_stock_taking_item_count);
        textView4 = (TextView) view.findViewById(R.id.textView4);
        textView4.setText(textView4.getText()+objGlobal.getUserName());

        et_stock_taking_itemcode.requestFocus();


        et_stock_taking_itemcode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });



        et_stock_taking_itemcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    tv_stock_taking_main_itemcode.setText(seperateBarcode(et_stock_taking_itemcode.getText().toString().trim().toUpperCase()));
                    tv_stock_taking_total_count.setText("");
                    tv_stock_taking_user_count.setText("");
                    tv_stock_taking_item_count.setText("");
                    if (!objStockTakingControl.validateItem(tv_stock_taking_main_itemcode.getText().toString())) {
                        okMessage("Stock Taking", objGlobal.getErrorMessage());
                        vibrate(500);
                        et_stock_taking_itemcode.setText("");
                        et_stock_taking_itemcode.requestFocus();
                        return false;
                    } else {
                        if (!objStockTakingControl.saveScan(et_stock_taking_itemcode.getText().toString().trim().toUpperCase(),
                                tv_stock_taking_main_itemcode.getText().toString())) {
                            okMessage("Stock Taking", objGlobal.getErrorMessage());
                            vibrate(500);
                            et_stock_taking_itemcode.setText("");
                            et_stock_taking_itemcode.requestFocus();
                            return true;
                        }
                        et_stock_taking_itemcode.setText("");
                        tv_stock_taking_description.setText(objStockTakingGlobal.getDescription());
                        if (!loadItemsStockTaking()) {
                            okMessage("LoadError", objGlobal.getErrorMessage());
                            vibrate(500);
                            et_stock_taking_itemcode.requestFocus();
                        }
                        et_stock_taking_itemcode.requestFocus();
                    }
                    return true;
                }
                et_stock_taking_itemcode.requestFocus();
                return false;
            }
        });

        if (!loadItemsStockTaking()) {
            okMessage("LoadError", objGlobal.getErrorMessage());
            vibrate(500);
        }

        et_stock_taking_itemcode.requestFocus();
        return view;
    }

    String seperateBarcode(String barcode) {
        String[] parts;
        String part1;
        int i;
        if (barcode.contains("/")) {
            parts = barcode.split("/");
            part1 = parts[0];
        } else {
            part1 = barcode;
        }
        for (i = 0; i < part1.length() - 1; i++) {
            if (part1.charAt(i) != '0') {
                break;
            }
        }
        return part1.substring(i);
    }

    private boolean loadItemsStockTaking() {
        try {
            listStockTakingItem.clear();
            listStockTakingItem = objStockTakingControl.loadStockTakingItem();
            objMyStockTakingItemAdp = new MyStockTakingItemAdp(listStockTakingItem);
            lv_stock_taking.setAdapter(objMyStockTakingItemAdp);
            tv_stock_taking_total_count.setText(objStockTakingGlobal.getTotalCount());
            tv_stock_taking_user_count.setText(objStockTakingGlobal.getTotalCountByUser());
            tv_stock_taking_item_count.setText(objStockTakingGlobal.getTotalCountByItem());
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("loadItemsStockTaking:catch: " + e.toString());
            return false;
        }
    }

    private class MyStockTakingItemAdp extends BaseAdapter {
        public ArrayList<StockTakingItem> listStockTakingItem;

        public MyStockTakingItemAdp(ArrayList<StockTakingItem> listStockTakingItem) {
            this.listStockTakingItem = listStockTakingItem;
        }

        @Override
        public int getCount() {
            return listStockTakingItem.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.stocktaking_items_ticket, null);
            final StockTakingItem s = listStockTakingItem.get(position);

            TextView tv_check_in_check_out_tickte_itemcode = (TextView) myView.findViewById(R.id.tv_stocktaking_tickte_itemcode);
            tv_check_in_check_out_tickte_itemcode.setText(String.valueOf(s.itemcode));

            TextView tv_check_in_check_out_tickte_description = (TextView) myView.findViewById(R.id.tv_stocktaking_tickte_date);
            tv_check_in_check_out_tickte_description.setText(String.valueOf(s.date));

            TextView tv_check_in_check_out_tickte_qty = (TextView) myView.findViewById(R.id.tv_stocktaking_tickte_time);
            tv_check_in_check_out_tickte_qty.setText(String.valueOf(s.time));

            return myView;
        }
    }

    void vibrate(int duration) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }

    void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }
}
