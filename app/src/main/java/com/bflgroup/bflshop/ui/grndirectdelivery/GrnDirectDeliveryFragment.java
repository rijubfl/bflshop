package com.bflgroup.bflshop.ui.grndirectdelivery;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class GrnDirectDeliveryFragment extends Fragment {

    Dialog myDialog;
    Global objGlobal=Global.getInstance();
    GrnDirectDeliverySharedRef objGrnDirectDeliverySharedRef;
    Boolean flagEdit;
    boolean result;
    String query;
    int missing,excess;

    GrnDirectDeliveryDbManager objGrnDirectDeliveryDbManager;
    ArrayList<GrnDirectDeliveryScanItems> listGrnDirectDeliveryScan = new ArrayList<GrnDirectDeliveryScanItems>();
    ArrayList<GrnDirectDeliveryScanItemsAll> listGrnDirectDeliveryScanAll  = new ArrayList<GrnDirectDeliveryScanItemsAll>();
    GrnDirectDeliveryFragment.MyGrnDirectDeliveryScanItemAdapter objMyGrnDirectDeliveryScanItemAdapter;
    GrnDirectDeliveryFragment.MyGrnDirectDeliveryScanItemAllAdapter objMyGrnDirectDeliveryScanItemAllAdapter;
    GrnDirectDeliveryControl objGrnDirectDeliveryControl=new GrnDirectDeliveryControl();
    PosGlobal objPosGlobal = PosGlobal.getInstance();

    Statement stmt;
    ResultSet rs;

    //Grn_DirectDelivery_Fragment
    private TextView tv_grn_direct_delivery_scan_total;
    private TextView tv_grn_direct_delivery_trf_total;
    private TextView tv_grn_direct_delivery_diff_total;
    private EditText et_grn_direct_delivery_number;
    private Button bt_grn_direct_delivery_scan;
    private Button bt_grn_direct_delivery_save;
    private Button btn_grn_direct_delivery_clear;
    private ListView lv_grn_direct_delivery_det;
    private CheckBox chk_grn_view_saved;

    //
    private TextView tv_grn_direct_delivery_popup_direct_delivery_number;
    private EditText et_grn_direct_delivery_popup_itemcode;
    private ListView li_grn_direct_delivery_popup_details;
    private EditText et_grn_direct_delivery_popup_qty;
    private Button bt_grn_direct_delivery_popup_scan;
    private Button bt_grn_direct_delivery_popup_ok;

    //
    private EditText et_grn_direct_delivery_popup_excess_qty;
    private EditText et_grn_direct_delivery_popup_missing_qty;
    private Button bt_grn_direct_delivery_popup_miss_ok;

    public GrnDirectDeliveryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_grn_direct_delivery, container, false);

        objGrnDirectDeliveryDbManager = new GrnDirectDeliveryDbManager(getContext());
        bt_grn_direct_delivery_scan=(Button) view.findViewById(R.id.bt_grn_direct_delivery_scan);
        tv_grn_direct_delivery_scan_total=(TextView) view.findViewById(R.id.tv_grn_direct_delivery_scan_total);
        tv_grn_direct_delivery_diff_total=(TextView) view.findViewById(R.id.tv_grn_direct_delivery_diff_total);
        tv_grn_direct_delivery_trf_total=(TextView) view.findViewById(R.id.tv_grn_direct_delivery_trf_total);
        et_grn_direct_delivery_number=(EditText) view.findViewById(R.id.et_grn_direct_delivery_number);
        bt_grn_direct_delivery_save=(Button) view.findViewById(R.id.bt_grn_direct_delivery_save);
        btn_grn_direct_delivery_clear=(Button) view.findViewById(R.id.btn_grn_direct_delivery_clear);
        lv_grn_direct_delivery_det=(ListView) view.findViewById(R.id.lv_grn_direct_delivery_det);
        chk_grn_view_saved=(CheckBox) view.findViewById(R.id.chk_grn_view_saved);
        flagEdit=false;

        objGrnDirectDeliverySharedRef=new GrnDirectDeliverySharedRef(getContext());
        et_grn_direct_delivery_number.setEnabled(true);
        chk_grn_view_saved.setEnabled(true);
        et_grn_direct_delivery_number.setText(objGrnDirectDeliverySharedRef.loadDirectDeliveryPoNo());
        if (objGrnDirectDeliverySharedRef.loadDirectDeliveryPoNo()!="") {
            et_grn_direct_delivery_number.setEnabled(false);
            chk_grn_view_saved.setEnabled(false);
            result = loadScanGrnAll();
            if (result == false) {
                okMessage("GRN:Load", objGlobal.getErrorMessage());
                vibrate(500);
            } else {
                result = loadTotal();
                if (result == false) {
                    okMessage("GRN:Load", objGlobal.getErrorMessage());
                    vibrate(500);
                }
            }
            chk_grn_view_saved.setChecked(objGrnDirectDeliverySharedRef.loadViewDirectDelivery());
            if (objGrnDirectDeliverySharedRef.loadViewDirectDelivery()) {
                bt_grn_direct_delivery_save.setEnabled(false);
                bt_grn_direct_delivery_scan.setEnabled(false);
            }
        }

        et_grn_direct_delivery_number.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_grn_direct_delivery_number.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    et_grn_direct_delivery_number.setText(et_grn_direct_delivery_number.getText().toString().toUpperCase());
                    result = grnProceed(et_grn_direct_delivery_number.getText().toString());
                    if (result == true) {
                        et_grn_direct_delivery_number.setEnabled(false);
                        chk_grn_view_saved.setEnabled(false);
                        objGrnDirectDeliverySharedRef.saveDirectDeliveryPoNo(et_grn_direct_delivery_number.getText().toString());
                        if (chk_grn_view_saved.isChecked()) {
                            objGrnDirectDeliverySharedRef.saveViewDirectDelivery(true);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        bt_grn_direct_delivery_scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                et_grn_direct_delivery_number.setText(et_grn_direct_delivery_number.getText().toString().toUpperCase());
                result = grnProceed(et_grn_direct_delivery_number.getText().toString());
                if (result == false) {
                } else {
                    et_grn_direct_delivery_number.setEnabled(false);
                    chk_grn_view_saved.setEnabled(false);
                    objGrnDirectDeliverySharedRef.saveDirectDeliveryPoNo(et_grn_direct_delivery_number.getText().toString());
                    objGrnDirectDeliverySharedRef.saveViewDirectDelivery(false);
                    if (chk_grn_view_saved.isChecked()) {
                        objGrnDirectDeliverySharedRef.saveViewDirectDelivery(true);
                    }
                }
            }
        });

        btn_grn_direct_delivery_clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear all?")
                        .setTitle("Conformation")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result=clearAll();
                                if(result==false) {
                                    okMessage("GRN:btn_grn_direct_delivery_clear", objGlobal.getErrorMessage());
                                    vibrate(500);
                                }
                                et_grn_direct_delivery_number.requestFocus();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { }
                        })
                        .show();
            }
        });

        bt_grn_direct_delivery_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                missing=0; excess=0;
                listGrnDirectDeliveryScanAll=loadDirectDeliveryItemsAll();
                if (missing>0 || excess>0){
                    vibrate(1000);
                    openPopupMisMatchWindow();
                } else {
                    grnSave();
                }
            }
        });
        return view;

    }

    void grnSave() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Are You sure to save?")
                .setTitle("Conformation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result = objGrnDirectDeliveryControl.saveGrn(et_grn_direct_delivery_number.getText().toString(), listGrnDirectDeliveryScanAll);
                        if (result == false) {
                            okMessage("GRN:bt_grn_direct_delivery_save", objGlobal.getErrorMessage());
                            vibrate(500);
                            bt_grn_direct_delivery_save.requestFocus();
                        } else {
                            result = clearAll();
                            if (result == false) {
                                okMessage("GRN:bt_grn_direct_delivery_save,else", objGlobal.getErrorMessage());
                                vibrate(500);
                            } else {
                                okMessage("Done", "Entry.No: " + objGrnDirectDeliveryControl.getGrnRfEnGlb() + ", Trf.No: " + objGrnDirectDeliveryControl.getTrfNo());
                                et_grn_direct_delivery_number.requestFocus();
                            }
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    boolean clearAll() {
        try {
            objGrnDirectDeliveryDbManager.delete(null, null);
            result = loadScanGrnAll();
            result = loadTotal();
            tv_grn_direct_delivery_scan_total.setText("");
            tv_grn_direct_delivery_trf_total.setText("");
            tv_grn_direct_delivery_diff_total.setText("");
            et_grn_direct_delivery_number.setText("");
            et_grn_direct_delivery_number.setEnabled(true);
            chk_grn_view_saved.setEnabled(true);
            chk_grn_view_saved.setChecked(false);
            bt_grn_direct_delivery_save.setEnabled(true);
            bt_grn_direct_delivery_scan.setEnabled(true);
            objGrnDirectDeliverySharedRef.saveDirectDeliveryPoNo("");
            objGrnDirectDeliverySharedRef.saveViewDirectDelivery(false);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnDirectDeliveryFragment:clearAll:" + ex.toString());
            return false;
        }
        return true;
    }

    boolean grnProceed(String trfNo) {
        if (TextUtils.isEmpty(trfNo)) {
            okMessage("GRN:grnProceed", "Please enter DirectDelivery Number");
            vibrate(500);
            et_grn_direct_delivery_number.requestFocus();
            return false;
        }
        Boolean view=false;
        if(chk_grn_view_saved.isChecked()) view=true;
        result = objGrnDirectDeliveryControl.validateDirectDeliveryNumber(trfNo,view);
        if (result == false) {
            okMessage("GRN:grnProceed", objGlobal.getErrorMessage());
            vibrate(500);
            et_grn_direct_delivery_number.setText("");
            et_grn_direct_delivery_number.requestFocus();
            return false;
        }
        result = loadDirectDeliveryDetails(trfNo,view);
        if (result == false) {
            okMessage("GRN:grnProceed", objGlobal.getErrorMessage());
            vibrate(500);
            et_grn_direct_delivery_number.setText("");
            et_grn_direct_delivery_number.requestFocus();
            return false;
        }
        if(view){
            result=loadScanGrnAll();
            if(result==false) {
                okMessage("GRN:grnProceed:loadScanGrnAll", objGlobal.getErrorMessage());
                vibrate(500);
                return false;
            }
            result=loadTotal();
            if(result==false) {
                okMessage("GRN:grnProceed:loadScanGrnAll", objGlobal.getErrorMessage());
                vibrate(500);
                return false;
            }
            bt_grn_direct_delivery_save.setEnabled(false);
            bt_grn_direct_delivery_scan.setEnabled(false);
        } else {
            openPopupWindow(trfNo);
            result = loadDirectDeliveryItems();
            if (result == false) {
                okMessage("GRN:grnProceed", objGlobal.getErrorMessage());
                vibrate(500);
                et_grn_direct_delivery_number.setText("");
                et_grn_direct_delivery_number.requestFocus();
                return false;
            }
            bt_grn_direct_delivery_save.setEnabled(true);
            bt_grn_direct_delivery_scan.setEnabled(true);
        }
        return true;
    }

    String seperateBarcode(String barcode){
        String[] parts;
        String part1;
        int i;
        if(barcode.contains("/")) {
            parts = barcode.split("/");
            part1=parts[0];
        } else {
            part1=barcode;
        }
        for (i = 0; i < part1.length() - 1; i++) {
            if (part1.charAt(i) != '0') {
                break;
            }
        }
        return part1.substring(i);
    }

    boolean grnProcessPopup(String trfNo,String itemcode,int qty) {
        if (TextUtils.isEmpty(itemcode) || itemcode == "") {
            objGlobal.setErrorMessage("Please enter Itemcode");
            et_grn_direct_delivery_popup_itemcode.requestFocus();
            return false;
        }
        if(itemcode.length()>15){
            objGlobal.setErrorMessage("Invalid itemcode, itemcode length is more than 15");
            et_grn_direct_delivery_popup_itemcode.requestFocus();
            return false;
        }
        result = scanBarcode(itemcode, qty);
        if (result == false) {
            et_grn_direct_delivery_popup_itemcode.requestFocus();
            return false;
        }
        result = loadDirectDeliveryItems();
        if (result == false) {
            et_grn_direct_delivery_number.setText("");
            et_grn_direct_delivery_number.requestFocus();
            return false;
        }
        et_grn_direct_delivery_popup_qty.setText("1");
        et_grn_direct_delivery_popup_itemcode.setText("");
        et_grn_direct_delivery_popup_itemcode.requestFocus();
        return true;
    }

    void openPopupMisMatchWindow() {
        myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.grn_direct_delivery_popup_diff_window);
        et_grn_direct_delivery_popup_excess_qty=(EditText) myDialog.findViewById(R.id.et_grn_direct_delivery_popup_excess_qty);
        et_grn_direct_delivery_popup_missing_qty=(EditText) myDialog.findViewById(R.id.et_grn_direct_delivery_popup_missing_qty);
        bt_grn_direct_delivery_popup_miss_ok=(Button) myDialog.findViewById(R.id.bt_grn_direct_delivery_popup_miss_ok);

        bt_grn_direct_delivery_popup_miss_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int txMiss=0,txExce=0;
                if(TextUtils.isEmpty(et_grn_direct_delivery_popup_missing_qty.getText().toString())) { txMiss=0;
                } else { txMiss=Integer.parseInt(et_grn_direct_delivery_popup_missing_qty.getText().toString()); }
                if(TextUtils.isEmpty(et_grn_direct_delivery_popup_excess_qty.getText().toString())) { txExce=0;
                } else { txExce=Integer.parseInt(et_grn_direct_delivery_popup_excess_qty.getText().toString()); }
                if(excess!=txExce){
                    okMessage("GRN:et_grn_direct_delivery_popup_itemcode", "Excess quantity missmatch, please check");
                    vibrate(500);
                    et_grn_direct_delivery_popup_excess_qty.requestFocus();
                } else if(missing!=txMiss) {
                    okMessage("GRN:et_grn_direct_delivery_popup_itemcode", "Missing quantity missmatch, please check");
                    vibrate(500);
                    et_grn_direct_delivery_popup_missing_qty.requestFocus();
                } else {
                    grnSave();
                    et_grn_direct_delivery_popup_missing_qty.setText("0");
                    et_grn_direct_delivery_popup_excess_qty.setText("0");
                    myDialog.dismiss();
                }
            }
        });
        myDialog.show();
        et_grn_direct_delivery_popup_excess_qty.requestFocus();
    }

    void openPopupWindow(String trfNo){
        myDialog=new Dialog(getContext());
        myDialog.setContentView(R.layout.grn_direct_delivery_popup_scan_window);
        tv_grn_direct_delivery_popup_direct_delivery_number=(TextView) myDialog.findViewById(R.id.tv_grn_direct_delivery_popup_direct_delivery_number);
        et_grn_direct_delivery_popup_itemcode=(EditText) myDialog.findViewById(R.id.et_grn_direct_delivery_popup_itemcode);
        li_grn_direct_delivery_popup_details=(ListView) myDialog.findViewById(R.id.li_grn_direct_delivery_popup_details);
        et_grn_direct_delivery_popup_qty=(EditText) myDialog.findViewById(R.id.et_grn_direct_delivery_popup_qty);
        bt_grn_direct_delivery_popup_scan=(Button) myDialog.findViewById(R.id.bt_grn_direct_delivery_popup_scan);
        bt_grn_direct_delivery_popup_ok=(Button) myDialog.findViewById(R.id.bt_grn_direct_delivery_popup_ok);
        tv_grn_direct_delivery_popup_direct_delivery_number.setText(trfNo);
        et_grn_direct_delivery_popup_qty.setText("1");

        et_grn_direct_delivery_popup_qty.setEnabled(objGlobal.getEnterQty());

        et_grn_direct_delivery_popup_itemcode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager)myDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_grn_direct_delivery_popup_itemcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(TextUtils.isEmpty(et_grn_direct_delivery_popup_qty.getText())){ et_grn_direct_delivery_popup_qty.setText("0"); }
                    result=grnProcessPopup(tv_grn_direct_delivery_popup_direct_delivery_number.getText().toString().trim().toUpperCase(),et_grn_direct_delivery_popup_itemcode.getText().toString().trim().toUpperCase(),
                            Integer.parseInt(et_grn_direct_delivery_popup_qty.getText().toString()));
                    if(result==false){
                        okMessage("GRN:et_grn_direct_delivery_popup_itemcode", objGlobal.getErrorMessage());
                        vibrate(500);
                        return false;
                    }
                    et_grn_direct_delivery_popup_itemcode.requestFocus();
                    return true;
                }
                return false;
            }
        });

        et_grn_direct_delivery_popup_qty.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if(TextUtils.isEmpty(et_grn_direct_delivery_popup_qty.getText())){ et_grn_direct_delivery_popup_qty.setText("0"); }
                    result=grnProcessPopup(tv_grn_direct_delivery_popup_direct_delivery_number.getText().toString().trim().toUpperCase(),et_grn_direct_delivery_popup_itemcode.getText().toString().trim().toUpperCase(),
                            Integer.parseInt(et_grn_direct_delivery_popup_qty.getText().toString()));
                    if(result==false){
                        okMessage("GRN:et_grn_direct_delivery_popup_qty", objGlobal.getErrorMessage());
                        vibrate(500);
                        return false;
                    }
                    return true;
                }
                return false;
            }
        });

        bt_grn_direct_delivery_popup_scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_grn_direct_delivery_popup_qty.getText())) {
                    et_grn_direct_delivery_popup_qty.setText("0");
                }
                result = grnProcessPopup(tv_grn_direct_delivery_popup_direct_delivery_number.getText().toString().trim().toUpperCase(), et_grn_direct_delivery_popup_itemcode.getText().toString().trim().toUpperCase(),
                        Integer.parseInt(et_grn_direct_delivery_popup_qty.getText().toString()));
                if (result == false) {
                }
            }
        });

        bt_grn_direct_delivery_popup_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                result=loadScanGrnAll();
                if(result==false) {
                    okMessage("GRN:bt_grn_direct_delivery_popup_ok", objGlobal.getErrorMessage());
                    vibrate(500);
                } else {
                    result=loadTotal();
                    if(result==false) {
                        okMessage("GRN:bt_grn_direct_delivery_popup_ok", objGlobal.getErrorMessage());
                        vibrate(500);
                    } else {
                        myDialog.dismiss();
                    }
                }
            }
        });
        myDialog.show();
        et_grn_direct_delivery_popup_itemcode.requestFocus();
    }

    boolean scanBarcode(String itemcode,int qty){
        try{
            if(flagEdit==true){
                String[] SelectionArgs={ itemcode,"0" };
                objGrnDirectDeliveryDbManager.delete(objGrnDirectDeliveryDbManager.colItemcode+"=? and "+objGrnDirectDeliveryDbManager.colScanfQty+">?",SelectionArgs);
                flagEdit=false;
            }
            if(qty>0) {
                ContentValues values = new ContentValues();
                values.put(objGrnDirectDeliveryDbManager.colItemcode, itemcode);
                values.put(objGrnDirectDeliveryDbManager.colScanfQty, qty);
                values.put(objGrnDirectDeliveryDbManager.colTrfQty, 0);
                long id = objGrnDirectDeliveryDbManager.insertData(values);
            }
            et_grn_direct_delivery_popup_itemcode.setEnabled(true);
            et_grn_direct_delivery_popup_qty.setEnabled(false);
        } catch(Exception ex) {
            objGlobal.setErrorMessage("GrnDirectDeliveryFragment:scanBarcode:" + ex.toString());
            return false;
        }
        return true;
    }

    ArrayList<GrnDirectDeliveryScanItemsAll> loadDirectDeliveryItemsAll() {
        try{
            excess=0; missing=0;
            String[] projection={GrnDirectDeliveryDbManager.colItemcode,"sum("+GrnDirectDeliveryDbManager.colScanfQty+") as "+GrnDirectDeliveryDbManager.colScanfQty,
                    "sum("+GrnDirectDeliveryDbManager.colTrfQty+") as "+GrnDirectDeliveryDbManager.colTrfQty,
                    "sum("+GrnDirectDeliveryDbManager.colScanfQty+"-"+GrnDirectDeliveryDbManager.colTrfQty+") as "+GrnDirectDeliveryDbManager.colDiffQty};
            String[] SelectionsArgs={ "0" };
            listGrnDirectDeliveryScanAll.clear();
            Cursor cursor=objGrnDirectDeliveryDbManager.query(projection,null,null,GrnDirectDeliveryDbManager.colItemcode,
                    GrnDirectDeliveryDbManager.colItemcode);
            if (cursor.moveToFirst()){
                do {
                    listGrnDirectDeliveryScanAll.add(new GrnDirectDeliveryScanItemsAll(cursor.getString(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colItemcode)),
                            cursor.getInt(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colScanfQty)),cursor.getInt(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colTrfQty)),
                            cursor.getInt(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colDiffQty))));
                    if(cursor.getInt(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colDiffQty))>0) { excess+=Math.abs(cursor.getInt(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colDiffQty))); }
                    if(cursor.getInt(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colDiffQty))<0) { missing+=Math.abs(cursor.getInt(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colDiffQty))); }
                }while (cursor.moveToNext());
            }
        } catch(Exception ex) {
            objGlobal.setErrorMessage("GrnDirectDeliveryFragment:loadDirectDeliveryItemsAll:" + ex.toString());
            return null;
        }
        return listGrnDirectDeliveryScanAll;
    }

    boolean loadScanGrnAll() {
        try{
            String[] projection={GrnDirectDeliveryDbManager.colItemcode,"sum("+GrnDirectDeliveryDbManager.colScanfQty+") as "+GrnDirectDeliveryDbManager.colScanfQty,
                    "sum("+GrnDirectDeliveryDbManager.colTrfQty+") as "+GrnDirectDeliveryDbManager.colTrfQty,
                    "sum("+GrnDirectDeliveryDbManager.colScanfQty+"-"+GrnDirectDeliveryDbManager.colTrfQty+") as "+GrnDirectDeliveryDbManager.colDiffQty};
            String[] SelectionsArgs={ "0" };
            listGrnDirectDeliveryScanAll.clear();
            Cursor cursor=objGrnDirectDeliveryDbManager.query(projection,null,null,GrnDirectDeliveryDbManager.colItemcode,
                    GrnDirectDeliveryDbManager.colItemcode +" DESC,"+GrnDirectDeliveryDbManager.colItemcode);
            if (cursor.moveToFirst()){
                do {
                    listGrnDirectDeliveryScanAll.add(new GrnDirectDeliveryScanItemsAll(cursor.getString(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colItemcode)),
                            cursor.getInt(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colScanfQty)),cursor.getInt(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colTrfQty)),
                            cursor.getInt(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colDiffQty))));
                }while (cursor.moveToNext());
            }
            objMyGrnDirectDeliveryScanItemAllAdapter=new GrnDirectDeliveryFragment.MyGrnDirectDeliveryScanItemAllAdapter(listGrnDirectDeliveryScanAll);
            lv_grn_direct_delivery_det.setAdapter(objMyGrnDirectDeliveryScanItemAllAdapter);
        } catch(Exception ex) {
            objGlobal.setErrorMessage("GrnDirectDeliveryFragment:loadDirectDeliveryItemsAll:" + ex.toString());
            return false;
        }
        return true;
    }

    boolean loadDirectDeliveryItems() {
        try{
            String[] projection={GrnDirectDeliveryDbManager.colItemcode,"sum("+GrnDirectDeliveryDbManager.colScanfQty+") as "+GrnDirectDeliveryDbManager.colScanfQty};
            String[] SelectionsArgs={ "0" };
            listGrnDirectDeliveryScan.clear();
            Cursor cursor=objGrnDirectDeliveryDbManager.query(projection,GrnDirectDeliveryDbManager.colScanfQty+" >?",SelectionsArgs,
                    GrnDirectDeliveryDbManager.colItemcode,GrnDirectDeliveryDbManager.colItemcode);
            if (cursor.moveToFirst()){
                do {
                    listGrnDirectDeliveryScan.add(new GrnDirectDeliveryScanItems(cursor.getString(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colItemcode))
                            ,cursor.getInt(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colScanfQty))));
                }while (cursor.moveToNext());
            }
            objMyGrnDirectDeliveryScanItemAdapter=new GrnDirectDeliveryFragment.MyGrnDirectDeliveryScanItemAdapter(listGrnDirectDeliveryScan);
            li_grn_direct_delivery_popup_details.setAdapter(objMyGrnDirectDeliveryScanItemAdapter);
        } catch(Exception ex) {
            objGlobal.setErrorMessage("GrnDirectDeliveryFragment:loadDirectDeliveryItems:" + ex.toString());
            return false;
        }
        return true;
    }

    boolean loadTotal(){
        try{
            String[] projection={"sum("+GrnDirectDeliveryDbManager.colScanfQty+") as "+GrnDirectDeliveryDbManager.colScanfQty,
                    "sum("+GrnDirectDeliveryDbManager.colTrfQty+") as "+GrnDirectDeliveryDbManager.colTrfQty,
                    "sum("+GrnDirectDeliveryDbManager.colScanfQty+"-"+GrnDirectDeliveryDbManager.colTrfQty+") as "+GrnDirectDeliveryDbManager.colDiffQty};
            Cursor cursor=objGrnDirectDeliveryDbManager.query(projection,null,null,null,null);
            if (cursor.moveToFirst()) {
                tv_grn_direct_delivery_scan_total.setText(cursor.getString(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colScanfQty)));
                tv_grn_direct_delivery_trf_total.setText(cursor.getString(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colTrfQty)));
                tv_grn_direct_delivery_diff_total.setText(cursor.getString(cursor.getColumnIndex(GrnDirectDeliveryDbManager.colDiffQty)));
            }
        } catch(Exception ex) {
            objGlobal.setErrorMessage("GrnDirectDeliveryFragment:loadTotal:" + ex.toString());
            return false;
        }
        return true;
    }

    boolean loadDirectDeliveryDetails(String trfNo,Boolean view) {
        String itemcode="";
        int qty=0,scanQty=0;
        try {
            String[] SelectionArgs={ "0" };
            objGrnDirectDeliveryDbManager.delete(objGrnDirectDeliveryDbManager.colTrfQty+">?",SelectionArgs);
            if(view)
                query = "select itemcode,TrfQty=sum(TrfQty),ScanQty=sum(ScanQty) from DirectDeliveryDetail where trfno='" + trfNo + "' group by itemcode order by itemcode";
            else
                query = "select itemcode,TrfQty=sum(OrgQty),ScanQty=0 from DirectDelivery where ShopName='" + objPosGlobal.getShopName() + "' and PONo='"+trfNo+"' group by itemcode order by itemcode";
            stmt = objGlobal.getCloudCon().createStatement();
            rs = stmt.executeQuery(query);
            while(rs.next()){
                itemcode=rs.getString("itemcode").toString();
                qty=rs.getInt("TrfQty");
                scanQty=rs.getInt("ScanQty");
                ContentValues values=new ContentValues();
                values.put(objGrnDirectDeliveryDbManager.colItemcode,itemcode);
                values.put(objGrnDirectDeliveryDbManager.colScanfQty,scanQty);
                values.put(objGrnDirectDeliveryDbManager.colTrfQty,qty);
                long id = objGrnDirectDeliveryDbManager.insertData(values);
            }
        } catch(Exception ex){
            objGlobal.setErrorMessage("GrnDirectDeliveryFragment:loadDirectDeliveryDetails:"+ex.toString());
            return false;
        }
        return true;
    }

    private class MyGrnDirectDeliveryScanItemAdapter extends BaseAdapter {
        public ArrayList<GrnDirectDeliveryScanItems> listGrnDirectDeliveryScanItems;

        public MyGrnDirectDeliveryScanItemAdapter(ArrayList<GrnDirectDeliveryScanItems>  listGrnItemScanDataAdpater) {
            this.listGrnDirectDeliveryScanItems=listGrnItemScanDataAdpater;
        }

        @Override
        public int getCount() {
            return listGrnDirectDeliveryScanItems.size();
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
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.grn_direct_delivery_item_scan_ticket, null);
            final GrnDirectDeliveryScanItems s = listGrnDirectDeliveryScanItems.get(position);

            TextView tv_grn_direct_delivery_item_scan_ticket_itemcode=(TextView) myView.findViewById(R.id.tv_grn_direct_delivery_item_scan_ticket_itemcode);
            tv_grn_direct_delivery_item_scan_ticket_itemcode.setText(String.valueOf(s.itemCode));

            TextView tv_grn_direct_delivery_item_scan_ticket_qty=(TextView)myView.findViewById(R.id.tv_grn_direct_delivery_item_scan_ticket_qty);
            tv_grn_direct_delivery_item_scan_ticket_qty.setText(String.valueOf(s.scanQty));

            Button bt_grn_direct_delivery_item_scan_Select=(Button)myView.findViewById(R.id.bt_grn_direct_delivery_item_scan_Select);
            bt_grn_direct_delivery_item_scan_Select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_grn_direct_delivery_popup_itemcode.setText(String.valueOf(s.itemCode));
                    et_grn_direct_delivery_popup_itemcode.setEnabled(false);
                    et_grn_direct_delivery_popup_qty.setText(String.valueOf(s.scanQty));
                    et_grn_direct_delivery_popup_qty.setEnabled(true);
                    et_grn_direct_delivery_popup_qty.requestFocus();
                    flagEdit=true;
                }
            });
            return myView;
        }
    }

    private class MyGrnDirectDeliveryScanItemAllAdapter extends BaseAdapter {
        public ArrayList<GrnDirectDeliveryScanItemsAll> listGrnDirectDeliveryScanItemsAll;

        public MyGrnDirectDeliveryScanItemAllAdapter(ArrayList<GrnDirectDeliveryScanItemsAll>  listGrnDirectDeliveryScanItemsAll) {
            this.listGrnDirectDeliveryScanItemsAll=listGrnDirectDeliveryScanItemsAll;
        }

        @Override
        public int getCount() {
            return listGrnDirectDeliveryScanItemsAll.size();
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
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getLayoutInflater();
            View myView = mInflater.inflate(R.layout.grn_direct_delivery_item_ticket, null);
            final GrnDirectDeliveryScanItemsAll s = listGrnDirectDeliveryScanItemsAll.get(position);

            TextView tv_grn_direct_delivery_ticket_itemcode=(TextView) myView.findViewById(R.id.tv_grn_direct_delivery_ticket_itemcode);
            tv_grn_direct_delivery_ticket_itemcode.setText(String.valueOf(s.itemCode));

            TextView tv_grn_direct_delivery_ticket_scanqty=(TextView) myView.findViewById(R.id.tv_grn_direct_delivery_ticket_scanqty);
            tv_grn_direct_delivery_ticket_scanqty.setText(String.valueOf(s.scanQty));

            TextView tv_grn_direct_delivery_ticket_trfqty=(TextView) myView.findViewById(R.id.tv_grn_direct_delivery_ticket_trfqty);
            tv_grn_direct_delivery_ticket_trfqty.setText(String.valueOf(s.trfQty));

            TextView tv_grn_direct_delivery_ticket_diffqty=(TextView) myView.findViewById(R.id.tv_grn_direct_delivery_ticket_diffqty);
            tv_grn_direct_delivery_ticket_diffqty.setText(String.valueOf(s.diffQty));

            if (s.diffQty!=0) {
                //tv_grn_direct_delivery_ticket_diffqty.setTextColor(Color.WHITE);
                tv_grn_direct_delivery_ticket_itemcode.setTextColor(Color.RED);
                tv_grn_direct_delivery_ticket_scanqty.setTextColor(Color.RED);
                tv_grn_direct_delivery_ticket_trfqty.setTextColor(Color.RED);
                tv_grn_direct_delivery_ticket_diffqty.setTextColor(Color.RED);
                //tv_grn_direct_delivery_ticket_diffqty.setBackgroundColor(Color.RED);
            }
            return myView;
        }
    }

    void playSound(int type){
        try {
            Uri notification;
            if(type==1){
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            } else if (type==2) {
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            } else if (type==3) {
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
            } else {
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone r = RingtoneManager.getRingtone(getContext().getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void vibrate(int duration){
        Vibrator v = (Vibrator) getContext().getSystemService(Context. VIBRATOR_SERVICE );
        assert v != null;
        if (Build.VERSION. SDK_INT >= Build.VERSION_CODES. O ) {
            v.vibrate(VibrationEffect. createOneShot ( duration ,
                    VibrationEffect. DEFAULT_AMPLITUDE )) ;
        } else {
            v.vibrate( duration ) ;
        }
    }

    void okMessage(String title,String message){
        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }
}