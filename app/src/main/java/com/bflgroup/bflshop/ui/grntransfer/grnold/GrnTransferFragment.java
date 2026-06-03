package com.bflgroup.bflshop.ui.grntransfer.grnold;

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

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Global;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class GrnTransferFragment extends Fragment {

    Dialog myDialog;
    Global objGlobal = Global.getInstance();
    GrnTransferSharedRef objGrnTransferSharedRef;
    Boolean flagEdit;
    boolean result;
    String query;
    int missing, excess;

    GrnTransferDbManager objGrnTransferDbManager;
    ArrayList<GrnTransferScanItems> listGrnTransferScan = new ArrayList<GrnTransferScanItems>();
    ArrayList<GrnTransferScanItemsAll> listGrnTransferScanAll = new ArrayList<GrnTransferScanItemsAll>();
    MyGrnTransferScanItemAdapter objMyGrnTransferScanItemAdapter;
    MyGrnTransferScanItemAllAdapter objMyGrnTransferScanItemAllAdapter;
    GrnTransferControl objGrnTransferControl = new GrnTransferControl();

    Statement stmt;
    ResultSet rs;

    //Grn_Transfer_Fragment
    private TextView tv_grn_transfer_scan_total;
    private TextView tv_grn_transfer_trf_total;
    private TextView tv_grn_transfer_diff_total;
    private EditText et_grn_transfer_transfer_number;
    private Button bt_grn_transfer_scan;
    private Button bt_grn_transfer_save;
    private Button btn_grn_transfer_clear;
    private ListView lv_grn_transfer_det;
    private CheckBox chk_grn_transfer_from_shop;
    private CheckBox chk_grn_view_saved;

    //
    private TextView tv_grn_transfer_popup_transfer_number;
    private EditText et_grn_transfer_popup_itemcode;
    private ListView li_grn_transfer_popup_details;
    private EditText et_grn_transfer_popup_qty;
    private Button bt_grn_transfer_popup_scan;
    private Button bt_grn_transfer_popup_ok;

    //
    private EditText et_grn_transfer_popup_excess_qty;
    private EditText et_grn_transfer_popup_missing_qty;
    private Button bt_grn_transfer_popup_miss_ok;
    private EditText et_grn_transfer_popup_diff_verify;

    public GrnTransferFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_grn_transfer, container, false);

        objGrnTransferDbManager = new GrnTransferDbManager(getContext());

        bt_grn_transfer_scan = (Button) view.findViewById(R.id.bt_grn_transfer_scan);
        tv_grn_transfer_scan_total = (TextView) view.findViewById(R.id.tv_grn_transfer_scan_total);
        tv_grn_transfer_diff_total = (TextView) view.findViewById(R.id.tv_grn_transfer_diff_total);
        tv_grn_transfer_trf_total = (TextView) view.findViewById(R.id.tv_grn_transfer_trf_total);
        et_grn_transfer_transfer_number = (EditText) view.findViewById(R.id.et_grn_transfer_transfer_number);
        bt_grn_transfer_save = (Button) view.findViewById(R.id.bt_grn_transfer_save);
        btn_grn_transfer_clear = (Button) view.findViewById(R.id.btn_grn_transfer_clear);
        lv_grn_transfer_det = (ListView) view.findViewById(R.id.lv_grn_transfer_det);
        chk_grn_transfer_from_shop = (CheckBox) view.findViewById(R.id.chk_grn_transfer_from_shop);
        chk_grn_view_saved = (CheckBox) view.findViewById(R.id.chk_grn_view_saved);
        flagEdit = false;

        objGrnTransferSharedRef = new GrnTransferSharedRef(getContext());
        et_grn_transfer_transfer_number.setEnabled(true);
        chk_grn_transfer_from_shop.setEnabled(true);
        chk_grn_view_saved.setEnabled(true);
        et_grn_transfer_transfer_number.setText(objGrnTransferSharedRef.loadTrfNo());
        if (objGrnTransferSharedRef.loadTrfNo() != "") {
            et_grn_transfer_transfer_number.setEnabled(false);
            chk_grn_transfer_from_shop.setEnabled(false);
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
            chk_grn_transfer_from_shop.setChecked(objGrnTransferSharedRef.loadTickShopTrf());
            chk_grn_view_saved.setChecked(objGrnTransferSharedRef.loadTickView());
            if (objGrnTransferSharedRef.loadTickView()) {
                bt_grn_transfer_save.setEnabled(false);
                bt_grn_transfer_scan.setEnabled(false);
            }
        }

        et_grn_transfer_transfer_number.setOnTouchListener(new View.OnTouchListener() {
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

        et_grn_transfer_transfer_number.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    et_grn_transfer_transfer_number.setText(et_grn_transfer_transfer_number.getText().toString().toUpperCase());
                    result = grnProceed(et_grn_transfer_transfer_number.getText().toString());
                    if (result == true) {
                        et_grn_transfer_transfer_number.setEnabled(false);
                        chk_grn_transfer_from_shop.setEnabled(false);
                        chk_grn_view_saved.setEnabled(false);
                        objGrnTransferSharedRef.saveTrfNo(et_grn_transfer_transfer_number.getText().toString());
                        objGrnTransferSharedRef.saveShopTrf(false);
                        if (chk_grn_transfer_from_shop.isChecked()) {
                            objGrnTransferSharedRef.saveShopTrf(true);
                        }
                        if (chk_grn_view_saved.isChecked()) {
                            objGrnTransferSharedRef.saveViewTick(true);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        bt_grn_transfer_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_grn_transfer_transfer_number.setText(et_grn_transfer_transfer_number.getText().toString().toUpperCase());
                result = grnProceed(et_grn_transfer_transfer_number.getText().toString());
                if (result == false) {
                } else {
                    et_grn_transfer_transfer_number.setEnabled(false);
                    chk_grn_transfer_from_shop.setEnabled(false);
                    chk_grn_view_saved.setEnabled(false);
                    objGrnTransferSharedRef.saveTrfNo(et_grn_transfer_transfer_number.getText().toString());
                    objGrnTransferSharedRef.saveViewTick(false);
                    objGrnTransferSharedRef.saveShopTrf(false);
                    if (chk_grn_transfer_from_shop.isChecked()) {
                        objGrnTransferSharedRef.saveShopTrf(true);
                    }
                    if (chk_grn_view_saved.isChecked()) {
                        objGrnTransferSharedRef.saveViewTick(true);
                    }
                }
            }
        });

        btn_grn_transfer_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear all?")
                        .setTitle("Conformation")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result = clearAll();
                                if (result == false) {
                                    okMessage("GRN:btn_grn_transfer_clear", objGlobal.getErrorMessage());
                                    vibrate(500);
                                }
                                et_grn_transfer_transfer_number.requestFocus();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });

        bt_grn_transfer_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                missing = 0;
                excess = 0;
                listGrnTransferScanAll = loadTransferItemsAll();
                if (missing > 0 || excess > 0) {
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
                        if (chk_grn_transfer_from_shop.isChecked()) {
                            result = objGrnTransferControl.saveShopTransfer(et_grn_transfer_transfer_number.getText().toString(), listGrnTransferScanAll);
                        } else {
                            result = objGrnTransferControl.saveGrn(et_grn_transfer_transfer_number.getText().toString(), listGrnTransferScanAll);
                        }
                        if (result == false) {
                            okMessage("GRN:bt_grn_transfer_save", objGlobal.getErrorMessage());
                            vibrate(500);
                            bt_grn_transfer_save.requestFocus();
                        } else {
                            result = clearAll();
                            if (result == false) {
                                okMessage("GRN:bt_grn_transfer_save,else", objGlobal.getErrorMessage());
                                vibrate(500);
                            } else {
                                okMessage("Done", "Entry.No: " + objGrnTransferControl.getGrnRfEnGlb() + ", Trf.No: " + objGrnTransferControl.getTrfNo());
                                et_grn_transfer_transfer_number.requestFocus();
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
            objGrnTransferDbManager.delete(null, null);
            result = loadScanGrnAll();
            result = loadTotal();
            tv_grn_transfer_scan_total.setText("");
            tv_grn_transfer_trf_total.setText("");
            tv_grn_transfer_diff_total.setText("");
            et_grn_transfer_transfer_number.setText("");
            et_grn_transfer_transfer_number.setEnabled(true);
            chk_grn_transfer_from_shop.setEnabled(true);
            chk_grn_transfer_from_shop.setChecked(false);
            chk_grn_view_saved.setEnabled(true);
            chk_grn_view_saved.setChecked(false);
            bt_grn_transfer_save.setEnabled(true);
            bt_grn_transfer_scan.setEnabled(true);
            objGrnTransferSharedRef.saveTrfNo("");
            objGrnTransferSharedRef.saveShopTrf(false);
            objGrnTransferSharedRef.saveViewTick(false);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferFragment:clearAll:" + ex.toString());
            return false;
        }
        return true;
    }

    boolean grnProceed(String trfNo) {
        if (TextUtils.isEmpty(trfNo)) {
            okMessage("GRN:grnProceed", "Please enter Transfer Number");
            vibrate(500);
            et_grn_transfer_transfer_number.requestFocus();
            return false;
        }
        objGlobal.setToteTrfNo("");
        Boolean view = false;
        if (chk_grn_view_saved.isChecked()) view = true;
        if (chk_grn_transfer_from_shop.isChecked()) {
            result = objGrnTransferControl.validateShopTransfer(trfNo, view);
        } else {
            result = objGrnTransferControl.validateTransferNumber(trfNo, view);
        }
        if (result == false) {
            okMessage("GRN:grnProceed", objGlobal.getErrorMessage());
            vibrate(500);
            et_grn_transfer_transfer_number.setText("");
            et_grn_transfer_transfer_number.requestFocus();
            return false;
        }
        et_grn_transfer_transfer_number.setText(objGlobal.getToteTrfNo());
        trfNo = objGlobal.getToteTrfNo();
        if (chk_grn_transfer_from_shop.isChecked()) {
            result = loadShopTransferDetails(trfNo, view);
        } else {
            result = loadTransferDetails(trfNo, view);
        }
        if (result == false) {
            okMessage("GRN:grnProceed", objGlobal.getErrorMessage());
            vibrate(500);
            et_grn_transfer_transfer_number.setText("");
            et_grn_transfer_transfer_number.requestFocus();
            return false;
        }
        if (view) {
            result = loadScanGrnAll();
            if (result == false) {
                okMessage("GRN:grnProceed:loadScanGrnAll", objGlobal.getErrorMessage());
                vibrate(500);
                return false;
            }
            result = loadTotal();
            if (result == false) {
                okMessage("GRN:grnProceed:loadScanGrnAll", objGlobal.getErrorMessage());
                vibrate(500);
                return false;
            }
            bt_grn_transfer_save.setEnabled(false);
            bt_grn_transfer_scan.setEnabled(false);
        } else {
            openPopupWindow(trfNo);
            result = loadTransferItems();
            if (result == false) {
                okMessage("GRN:grnProceed", objGlobal.getErrorMessage());
                vibrate(500);
                et_grn_transfer_transfer_number.setText("");
                et_grn_transfer_transfer_number.requestFocus();
                return false;
            }
            bt_grn_transfer_save.setEnabled(true);
            bt_grn_transfer_scan.setEnabled(true);
        }
        return true;
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

    boolean grnProcessPopup(String trfNo, String itemcode, int qty) {
        if (TextUtils.isEmpty(itemcode) || itemcode == "") {
            objGlobal.setErrorMessage("Please enter Itemcode");
            et_grn_transfer_popup_itemcode.setText("");
            et_grn_transfer_popup_itemcode.requestFocus();
            return false;
        }
        itemcode = seperateBarcode(itemcode);
        if (objGrnTransferControl.validItemcode(itemcode) == false) {
            objGlobal.setErrorMessage("Invalid itemcode, please double check, itemcode: " + itemcode);
            et_grn_transfer_popup_itemcode.setText("");
            et_grn_transfer_popup_itemcode.requestFocus();
            return false;
        }
        if (itemcode.length() > 15) {
            objGlobal.setErrorMessage("Invalid itemcode, itemcode length is more than 15");
            et_grn_transfer_popup_itemcode.setText("");
            et_grn_transfer_popup_itemcode.requestFocus();
            return false;
        }
        result = scanBarcode(itemcode, qty);
        if (result == false) {
            et_grn_transfer_popup_itemcode.setText("");
            et_grn_transfer_popup_itemcode.requestFocus();
            return false;
        }
        result = loadTransferItems();
        if (result == false) {
            et_grn_transfer_popup_itemcode.setText("");
            et_grn_transfer_popup_itemcode.requestFocus();
            return false;
        }
        et_grn_transfer_popup_qty.setText("1");
        et_grn_transfer_popup_itemcode.setText("");
        et_grn_transfer_popup_itemcode.requestFocus();
        return true;
    }

    void openPopupMisMatchWindow() {
        myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.grn_transfer_popup_diff_window);
        et_grn_transfer_popup_excess_qty = (EditText) myDialog.findViewById(R.id.et_grn_transfer_popup_excess_qty);
        et_grn_transfer_popup_missing_qty = (EditText) myDialog.findViewById(R.id.et_grn_transfer_popup_missing_qty);
        et_grn_transfer_popup_diff_verify = (EditText) myDialog.findViewById(R.id.et_grn_transfer_popup_diff_verify);
        bt_grn_transfer_popup_miss_ok = (Button) myDialog.findViewById(R.id.bt_grn_transfer_popup_miss_ok);

        bt_grn_transfer_popup_miss_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int txMiss = 0, txExce = 0;
                String verifyMgrname="";
                if (TextUtils.isEmpty(et_grn_transfer_popup_missing_qty.getText().toString())) {
                    txMiss = 0;
                } else {
                    txMiss = Integer.parseInt(et_grn_transfer_popup_missing_qty.getText().toString());
                }
                if (TextUtils.isEmpty(et_grn_transfer_popup_excess_qty.getText().toString())) {
                    txExce = 0;
                } else {
                    txExce = Integer.parseInt(et_grn_transfer_popup_excess_qty.getText().toString());
                }
                if (excess != txExce) {
                    okMessage("GRN:et_grn_transfer_popup_itemcode", "Excess quantity missmatch, please check");
                    vibrate(500);
                    et_grn_transfer_popup_excess_qty.requestFocus();
                } else if (missing != txMiss) {
                    okMessage("GRN:et_grn_transfer_popup_itemcode", "Missing quantity missmatch, please check");
                    vibrate(500);
                    et_grn_transfer_popup_missing_qty.requestFocus();
                } else {
                    verifyMgrname = objGrnTransferControl.validateManagerVerify(et_grn_transfer_popup_diff_verify.getText().toString());
                    if(verifyMgrname.isEmpty()){
                        okMessage("GRN:et_grn_transfer_popup_diff_verify", objGlobal.getErrorMessage());
                        vibrate(500);
                        et_grn_transfer_popup_diff_verify.requestFocus();
                    } else {
                        grnSave();
                        et_grn_transfer_popup_missing_qty.setText("0");
                        et_grn_transfer_popup_excess_qty.setText("0");
                        myDialog.dismiss();
                    }
                }
            }
        });
        myDialog.show();
        et_grn_transfer_popup_excess_qty.requestFocus();
    }

    void openPopupWindow(String trfNo) {
        myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.grn_transfer_popup_scan_window);
        tv_grn_transfer_popup_transfer_number = (TextView) myDialog.findViewById(R.id.tv_grn_transfer_popup_transfer_number);
        et_grn_transfer_popup_itemcode = (EditText) myDialog.findViewById(R.id.et_grn_transfer_popup_itemcode);
        li_grn_transfer_popup_details = (ListView) myDialog.findViewById(R.id.li_grn_transfer_popup_details);
        et_grn_transfer_popup_qty = (EditText) myDialog.findViewById(R.id.et_grn_transfer_popup_qty);
        bt_grn_transfer_popup_scan = (Button) myDialog.findViewById(R.id.bt_grn_transfer_popup_scan);
        bt_grn_transfer_popup_ok = (Button) myDialog.findViewById(R.id.bt_grn_transfer_popup_ok);
        tv_grn_transfer_popup_transfer_number.setText(trfNo);
        et_grn_transfer_popup_qty.setText("1");

        et_grn_transfer_popup_qty.setEnabled(objGlobal.getEnterQty());

        et_grn_transfer_popup_itemcode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                InputMethodManager imm = (InputMethodManager) myDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return objGlobal.getHideKeyPad();
            }
        });

        et_grn_transfer_popup_itemcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (TextUtils.isEmpty(et_grn_transfer_popup_qty.getText())) {
                        et_grn_transfer_popup_qty.setText("0");
                    }
                    result = grnProcessPopup(tv_grn_transfer_popup_transfer_number.getText().toString().trim().toUpperCase(), et_grn_transfer_popup_itemcode.getText().toString().trim().toUpperCase(),
                            Integer.parseInt(et_grn_transfer_popup_qty.getText().toString()));
                    if (result == false) {
                        okMessage("GRN:et_grn_transfer_popup_itemcode", objGlobal.getErrorMessage());
                        vibrate(500);
                        return false;
                    }
                    et_grn_transfer_popup_itemcode.requestFocus();
                    return true;
                }
                return false;
            }
        });

        et_grn_transfer_popup_qty.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (TextUtils.isEmpty(et_grn_transfer_popup_qty.getText())) {
                        et_grn_transfer_popup_qty.setText("0");
                    }
                    result = grnProcessPopup(tv_grn_transfer_popup_transfer_number.getText().toString().trim().toUpperCase(), et_grn_transfer_popup_itemcode.getText().toString().trim().toUpperCase(),
                            Integer.parseInt(et_grn_transfer_popup_qty.getText().toString()));
                    if (result == false) {
                        okMessage("GRN:et_grn_transfer_popup_qty", objGlobal.getErrorMessage());
                        vibrate(500);
                        return false;
                    }
                    return true;
                }
                return false;
            }
        });

        bt_grn_transfer_popup_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_grn_transfer_popup_qty.getText())) {
                    et_grn_transfer_popup_qty.setText("0");
                }
                result = grnProcessPopup(tv_grn_transfer_popup_transfer_number.getText().toString().trim().toUpperCase(), et_grn_transfer_popup_itemcode.getText().toString().trim().toUpperCase(),
                        Integer.parseInt(et_grn_transfer_popup_qty.getText().toString()));
                if (result == false) {
                }
            }
        });

        bt_grn_transfer_popup_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = loadScanGrnAll();
                if (result == false) {
                    okMessage("GRN:bt_grn_transfer_popup_ok", objGlobal.getErrorMessage());
                    vibrate(500);
                } else {
                    result = loadTotal();
                    if (result == false) {
                        okMessage("GRN:bt_grn_transfer_popup_ok", objGlobal.getErrorMessage());
                        vibrate(500);
                    } else {
                        myDialog.dismiss();
                    }
                }
            }
        });
        myDialog.show();
        et_grn_transfer_popup_itemcode.requestFocus();
    }

    boolean scanBarcode(String itemcode, int qty) {
        try {
            if (flagEdit == true) {
                String[] SelectionArgs = {itemcode, "0"};
                objGrnTransferDbManager.delete(objGrnTransferDbManager.colItemcode + "=? and " + objGrnTransferDbManager.colScanfQty + ">?", SelectionArgs);
                flagEdit = false;
            }
            if (qty > 0) {
                ContentValues values = new ContentValues();
                values.put(objGrnTransferDbManager.colItemcode, itemcode);
                values.put(objGrnTransferDbManager.colScanfQty, qty);
                values.put(objGrnTransferDbManager.colTrfQty, 0);
                long id = objGrnTransferDbManager.insertData(values);
            }
            et_grn_transfer_popup_itemcode.setEnabled(true);
            et_grn_transfer_popup_qty.setEnabled(false);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferFragment:scanBarcode:" + ex.toString());
            return false;
        }
        return true;
    }

    ArrayList<GrnTransferScanItemsAll> loadTransferItemsAll() {
        try {
            excess = 0;
            missing = 0;
            String[] projection = {GrnTransferDbManager.colItemcode, "sum(" + GrnTransferDbManager.colScanfQty + ") as " + GrnTransferDbManager.colScanfQty,
                    "sum(" + GrnTransferDbManager.colTrfQty + ") as " + GrnTransferDbManager.colTrfQty,
                    "sum(" + GrnTransferDbManager.colScanfQty + "-" + GrnTransferDbManager.colTrfQty + ") as " + GrnTransferDbManager.colDiffQty};
            String[] SelectionsArgs = {"0"};
            listGrnTransferScanAll.clear();
            Cursor cursor = objGrnTransferDbManager.query(projection, null, null, GrnTransferDbManager.colItemcode,
                    GrnTransferDbManager.colItemcode);
            if (cursor.moveToFirst()) {
                do {
                    listGrnTransferScanAll.add(new GrnTransferScanItemsAll(cursor.getString(cursor.getColumnIndex(GrnTransferDbManager.colItemcode)),
                            cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colScanfQty)), cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colTrfQty)),
                            cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colDiffQty))));
                    if (cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colDiffQty)) > 0) {
                        excess += Math.abs(cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colDiffQty)));
                    }
                    if (cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colDiffQty)) < 0) {
                        missing += Math.abs(cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colDiffQty)));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferFragment:loadTransferItemsAll:" + ex.toString());
            return null;
        }
        return listGrnTransferScanAll;
    }

    boolean loadScanGrnAll() {
        try {
            String[] projection = {GrnTransferDbManager.colItemcode, "sum(" + GrnTransferDbManager.colScanfQty + ") as " + GrnTransferDbManager.colScanfQty,
                    "sum(" + GrnTransferDbManager.colTrfQty + ") as " + GrnTransferDbManager.colTrfQty,
                    "sum(" + GrnTransferDbManager.colScanfQty + "-" + GrnTransferDbManager.colTrfQty + ") as " + GrnTransferDbManager.colDiffQty};
            String[] SelectionsArgs = {"0"};
            listGrnTransferScanAll.clear();
            Cursor cursor = objGrnTransferDbManager.query(projection, null, null, GrnTransferDbManager.colItemcode,
                    GrnTransferDbManager.colItemcode + " DESC," + GrnTransferDbManager.colItemcode);
            if (cursor.moveToFirst()) {
                do {
                    listGrnTransferScanAll.add(new GrnTransferScanItemsAll(cursor.getString(cursor.getColumnIndex(GrnTransferDbManager.colItemcode)),
                            cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colScanfQty)), cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colTrfQty)),
                            cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colDiffQty))));
                } while (cursor.moveToNext());
            }
            objMyGrnTransferScanItemAllAdapter = new MyGrnTransferScanItemAllAdapter(listGrnTransferScanAll);
            lv_grn_transfer_det.setAdapter(objMyGrnTransferScanItemAllAdapter);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferFragment:loadTransferItemsAll:" + ex.toString());
            return false;
        }
        return true;
    }

    boolean loadTransferItems() {
        try {
            listGrnTransferScan.clear();
            String[] projection = {GrnTransferDbManager.colItemcode, "sum(" + GrnTransferDbManager.colScanfQty + ") as " + GrnTransferDbManager.colScanfQty,
                    "sum(" + GrnTransferDbManager.colTrfQty + ") as " + GrnTransferDbManager.colTrfQty,
                    "sum(" + GrnTransferDbManager.colScanfQty + "-" + GrnTransferDbManager.colTrfQty + ") as " + GrnTransferDbManager.colDiffQty};
            String[] SelectionsArgs = {"0"};
            Cursor cursor = objGrnTransferDbManager.query(projection, null, null, GrnTransferDbManager.colItemcode,
                    GrnTransferDbManager.colItemcode + " DESC," + GrnTransferDbManager.colItemcode);
            if (cursor.moveToFirst()) {
                do {
                    if(cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colScanfQty))>0) {
                        listGrnTransferScan.add(new GrnTransferScanItems(cursor.getString(cursor.getColumnIndex(GrnTransferDbManager.colItemcode))
                                , cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colTrfQty)), cursor.getInt(cursor.getColumnIndex(GrnTransferDbManager.colScanfQty))));
                    }
                } while (cursor.moveToNext());
            }
            objMyGrnTransferScanItemAdapter = new MyGrnTransferScanItemAdapter(listGrnTransferScan);
            li_grn_transfer_popup_details.setAdapter(objMyGrnTransferScanItemAdapter);
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferFragment:loadTransferItems:" + ex.toString());
            return false;
        }
        return true;
    }

    boolean loadTotal() {
        try {
            String[] projection = {"sum(" + GrnTransferDbManager.colScanfQty + ") as " + GrnTransferDbManager.colScanfQty,
                    "sum(" + GrnTransferDbManager.colTrfQty + ") as " + GrnTransferDbManager.colTrfQty,
                    "sum(" + GrnTransferDbManager.colScanfQty + "-" + GrnTransferDbManager.colTrfQty + ") as " + GrnTransferDbManager.colDiffQty};
            Cursor cursor = objGrnTransferDbManager.query(projection, null, null, null, null);
            if (cursor.moveToFirst()) {
                tv_grn_transfer_scan_total.setText(cursor.getString(cursor.getColumnIndex(GrnTransferDbManager.colScanfQty)));
                tv_grn_transfer_trf_total.setText(cursor.getString(cursor.getColumnIndex(GrnTransferDbManager.colTrfQty)));
                tv_grn_transfer_diff_total.setText(cursor.getString(cursor.getColumnIndex(GrnTransferDbManager.colDiffQty)));
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferFragment:loadTotal:" + ex.toString());
            return false;
        }
        return true;
    }

    boolean loadShopTransferDetails(String trfNo, Boolean save) {
        String itemcode = "";
        int qty = 0, recQty = 0;
        try {
            String[] SelectionArgs = {"0"};
            objGrnTransferDbManager.delete(objGrnTransferDbManager.colTrfQty + ">?", SelectionArgs);
            if (save)
                query = "select itemcode,quantity,RecQty from storedetail where entryno='" + trfNo + "'";
            else
                query = "select itemcode,quantity,RecQty=0 from storedetail where entryno='" + trfNo + "'";
            stmt = objGlobal.getCloudCon().createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                itemcode = rs.getString("itemcode").trim().toUpperCase();
                qty = rs.getInt("quantity");
                recQty = rs.getInt("RecQty");
                ContentValues values = new ContentValues();
                values.put(objGrnTransferDbManager.colItemcode, itemcode);
                values.put(objGrnTransferDbManager.colScanfQty, recQty);
                values.put(objGrnTransferDbManager.colTrfQty, qty);
                long id = objGrnTransferDbManager.insertData(values);
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferFragment:loadShopTransferDetails:" + ex.toString());
            return false;
        }
        return true;
    }

    boolean loadTransferDetails(String trfNo, Boolean view) {
        String itemcode = "";
        int qty = 0, scanQty = 0;
        try {
            String[] SelectionArgs = {"0"};
            objGrnTransferDbManager.delete(objGrnTransferDbManager.colTrfQty + ">?", SelectionArgs);
            if (view)
                query = "select itemcode=upper(itemcode),quantity=sum(trfqty),scanqty=sum(scanqty) from grndetailrf where trfno='" + trfNo + "' group by itemcode";
            else
                query = "select itemcode=upper(itemcode),quantity,scanQty=0 from transferdetail where trfno='" + trfNo + "'";
            stmt = objGlobal.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                itemcode = rs.getString("itemcode").toString();
                qty = rs.getInt("quantity");
                scanQty = rs.getInt("scanQty");
                ContentValues values = new ContentValues();
                values.put(objGrnTransferDbManager.colItemcode, itemcode);
                values.put(objGrnTransferDbManager.colScanfQty, scanQty);
                values.put(objGrnTransferDbManager.colTrfQty, qty);
                long id = objGrnTransferDbManager.insertData(values);
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferFragment:loadTransferDetails:" + ex.toString());
            return false;
        }
        return true;
    }

    private class MyGrnTransferScanItemAdapter extends BaseAdapter {
        public ArrayList<GrnTransferScanItems> listGrnTransferScanItems;

        public MyGrnTransferScanItemAdapter(ArrayList<GrnTransferScanItems> listGrnItemScanDataAdpater) {
            this.listGrnTransferScanItems = listGrnItemScanDataAdpater;
        }

        @Override
        public int getCount() {
            return listGrnTransferScanItems.size();
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
            View myView = mInflater.inflate(R.layout.grn_transfer_item_scan_ticket, null);
            final GrnTransferScanItems s = listGrnTransferScanItems.get(position);

            TextView tv_grn_transfer_item_scan_ticket_itemcode = (TextView) myView.findViewById(R.id.tv_grn_transfer_item_scan_ticket_itemcode);
            tv_grn_transfer_item_scan_ticket_itemcode.setText(String.valueOf(s.itemCode));

            TextView tv_grn_transfer_item_trf_ticket_qty = (TextView) myView.findViewById(R.id.tv_grn_transfer_item_trf_ticket_qty);
            tv_grn_transfer_item_trf_ticket_qty.setText(String.valueOf(s.trfQty));

            TextView tv_grn_transfer_item_scan_ticket_qty = (TextView) myView.findViewById(R.id.tv_grn_transfer_item_scan_ticket_qty);
            tv_grn_transfer_item_scan_ticket_qty.setText(String.valueOf(s.scanQty));

            Button bt_grn_transfer_item_scan_Select = (Button) myView.findViewById(R.id.bt_grn_transfer_item_scan_Select);
            bt_grn_transfer_item_scan_Select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_grn_transfer_popup_itemcode.setText(String.valueOf(s.itemCode));
                    et_grn_transfer_popup_itemcode.setEnabled(false);
                    et_grn_transfer_popup_qty.setText(String.valueOf(s.scanQty));
                    et_grn_transfer_popup_qty.setEnabled(true);
                    et_grn_transfer_popup_qty.requestFocus();
                    flagEdit = true;
                }
            });
            return myView;
        }
    }

    private class MyGrnTransferScanItemAllAdapter extends BaseAdapter {
        public ArrayList<GrnTransferScanItemsAll> listGrnTransferScanItemsAll;

        public MyGrnTransferScanItemAllAdapter(ArrayList<GrnTransferScanItemsAll> listGrnTransferScanItemsAll) {
            this.listGrnTransferScanItemsAll = listGrnTransferScanItemsAll;
        }

        @Override
        public int getCount() {
            return listGrnTransferScanItemsAll.size();
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
            View myView = mInflater.inflate(R.layout.grn_transfer_item_ticket, null);
            final GrnTransferScanItemsAll s = listGrnTransferScanItemsAll.get(position);

            TextView tv_grn_transfer_ticket_itemcode = (TextView) myView.findViewById(R.id.tv_grn_transfer_ticket_itemcode);
            tv_grn_transfer_ticket_itemcode.setText(String.valueOf(s.itemCode));

            TextView tv_grn_transfer_ticket_scanqty = (TextView) myView.findViewById(R.id.tv_grn_transfer_ticket_scanqty);
            tv_grn_transfer_ticket_scanqty.setText(String.valueOf(s.scanQty));

            TextView tv_grn_transfer_ticket_trfqty = (TextView) myView.findViewById(R.id.tv_grn_transfer_ticket_trfqty);
            tv_grn_transfer_ticket_trfqty.setText(String.valueOf(s.trfQty));

            TextView tv_grn_transfer_ticket_diffqty = (TextView) myView.findViewById(R.id.tv_grn_transfer_ticket_diffqty);
            tv_grn_transfer_ticket_diffqty.setText(String.valueOf(s.diffQty));

            if (s.diffQty != 0) {
                //tv_grn_transfer_ticket_diffqty.setTextColor(Color.WHITE);
                tv_grn_transfer_ticket_itemcode.setTextColor(Color.RED);
                tv_grn_transfer_ticket_scanqty.setTextColor(Color.RED);
                tv_grn_transfer_ticket_trfqty.setTextColor(Color.RED);
                tv_grn_transfer_ticket_diffqty.setTextColor(Color.RED);
                //tv_grn_transfer_ticket_diffqty.setBackgroundColor(Color.RED);
            }
            return myView;
        }
    }

    void playSound(int type) {
        try {
            Uri notification;
            if (type == 1) {
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            } else if (type == 2) {
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            } else if (type == 3) {
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
