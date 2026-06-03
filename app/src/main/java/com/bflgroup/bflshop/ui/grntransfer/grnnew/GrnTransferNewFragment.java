package com.bflgroup.bflshop.ui.grntransfer.grnnew;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Global;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GrnTransferNewFragment extends Fragment {

    private Global objGlobal = Global.getInstance();
    private GrnTransferNewGlobal objGrnTransferNewGlobal = GrnTransferNewGlobal.getInstance();
    private GrnTransferNewControl objGrnTransferNewControl = new GrnTransferNewControl();
    private GrnShopTransferControl objGrnShopTransferControl = new GrnShopTransferControl();

    private boolean b_Result;

    private String scanItemcode = "";
    private float scanPrice = 0;
    private String scanTrfno = "";

    //Main
    private CheckBox ch_grn_transfer_trffrom_oth_shop;
    private CheckBox ch_grn_transfer_view;
    private EditText et_grn_transfer_trfno_entryno;
    private EditText et_grn_transfer_rfid_ginno;
    private Button bt_grn_transfer_load;
    private Button bt_grn_transfer_scan;
    private TextView tv_grn_transfer_shopname;
    private TextView tv_grn_transfer_trfdate;
    private TextView tv_grn_transfer_toteid;
    private ListView lv_grn_transfer;
    private TextView tv_grn_transfer_total_scan_qty;
    private TextView tv_grn_transfer_total_trf_qty;
    private TextView tv_grn_transfer_total_diff_qty;
    private TextView tv_grn_transfer_last_save;
    private Button bt_grn_transfer_clear_all;
    private Button bt_grn_transfer_save;
    //E Main

    //popup scan
    private EditText et_popup_grn_transfer_barcode;
    private TextView tv_popup_grn_transfer_last;
    private EditText et_popup_grn_transfer_qty;
    private Button bt_popup_grn_transfer_add;
    private TextView tv_popup_grn_transfer_result;
    private TextView tv_popup_grn_transfer_itemcode;
    private TextView tv_popup_grn_transfer_current_stock;
    private TextView tv_popup_grn_transfer_system_price;
    private TextView tv_popup_grn_transfer_scan_price;
    private TextView tv_popup_grn_transfer_description;
    private TextView tv_popup_grn_transfer_scan_qty;
    private TextView tv_popup_grn_transfer_trf_qty;
    private TextView tv_popup_grn_transfer_diff_qty;
    private Button bt_popup_grn_transfer_close;
    //popup E

    //
    private EditText et_grn_transfer_popup_excess_qty;
    private EditText et_grn_transfer_popup_missing_qty;
    private EditText et_grn_transfer_popup_diff_verify;
    private Button bt_grn_transfer_popup_miss_ok;
    private Button bt_grn_transfer_popup_miss_cancel;
    //

    private String msgHead = "GRN";
    private boolean flagEdit;
    DecimalFormat numformat = new DecimalFormat("###,###");
    DecimalFormat numformatdec = new DecimalFormat("###,###.##");

    Dialog myDialog;

    ArrayList<GrnTransferNewTrfScanItems> listGrnTransferNewTrfScanItems = new ArrayList<GrnTransferNewTrfScanItems>();
    MyGrnNewTransferScanItemsAdp objMyGrnNewTransferScanItemsAdp;
    GrnTransferNewSharedRef objGrnTransferNewSharedRef;

    public GrnTransferNewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grn_transfer_new, container, false);

        ch_grn_transfer_trffrom_oth_shop = (CheckBox) view.findViewById(R.id.ch_grn_transfer_trffrom_oth_shop);
        ch_grn_transfer_view = (CheckBox) view.findViewById(R.id.ch_grn_transfer_view);
        et_grn_transfer_trfno_entryno = (EditText) view.findViewById(R.id.et_grn_transfer_trfno_entryno);
        et_grn_transfer_rfid_ginno = (EditText) view.findViewById(R.id.et_grn_transfer_rfid_ginno);
        bt_grn_transfer_load = (Button) view.findViewById(R.id.bt_grn_transfer_load);
        bt_grn_transfer_scan = (Button) view.findViewById(R.id.bt_grn_transfer_scan);
        tv_grn_transfer_shopname = (TextView) view.findViewById(R.id.tv_grn_transfer_shopname);
        tv_grn_transfer_trfdate = (TextView) view.findViewById(R.id.tv_grn_transfer_trfdate);
        tv_grn_transfer_toteid = (TextView) view.findViewById(R.id.tv_grn_transfer_toteid);
        lv_grn_transfer = (ListView) view.findViewById(R.id.lv_grn_transfer);
        tv_grn_transfer_total_scan_qty = (TextView) view.findViewById(R.id.tv_grn_transfer_total_scan_qty);
        tv_grn_transfer_total_trf_qty = (TextView) view.findViewById(R.id.tv_grn_transfer_total_trf_qty);
        tv_grn_transfer_total_diff_qty = (TextView) view.findViewById(R.id.tv_grn_transfer_total_diff_qty);
        bt_grn_transfer_clear_all = (Button) view.findViewById(R.id.bt_grn_transfer_clear_all);
        tv_grn_transfer_last_save = (TextView) view.findViewById(R.id.tv_grn_transfer_last_save);
        bt_grn_transfer_save = (Button) view.findViewById(R.id.bt_grn_transfer_save);

        bt_grn_transfer_scan.setEnabled(false);
        objGrnTransferNewSharedRef = new GrnTransferNewSharedRef(getContext());
        if (objGrnTransferNewSharedRef.loadTrfNo() != "") {
            et_grn_transfer_trfno_entryno.setText(objGrnTransferNewSharedRef.loadTrfNo());
            et_grn_transfer_rfid_ginno.setText(objGrnTransferNewSharedRef.loadGinNo());
            tv_grn_transfer_shopname.setText(objGrnTransferNewSharedRef.loadShopTrfName());
            tv_grn_transfer_trfdate.setText(objGrnTransferNewSharedRef.loadTrfDate());
            tv_grn_transfer_toteid.setText(objGrnTransferNewSharedRef.loadToteId());
            ch_grn_transfer_trffrom_oth_shop.setChecked(objGrnTransferNewSharedRef.loadTickShopTransfer());
            ch_grn_transfer_view.setChecked(objGrnTransferNewSharedRef.loadTickView());
            et_grn_transfer_trfno_entryno.setEnabled(false);
            et_grn_transfer_rfid_ginno.setEnabled(false);
            bt_grn_transfer_load.setEnabled(false);
            bt_grn_transfer_scan.setEnabled(true);
            ch_grn_transfer_trffrom_oth_shop.setEnabled(false);
            ch_grn_transfer_view.setEnabled(false);
            objGrnTransferNewGlobal.setTrfno(objGrnTransferNewSharedRef.loadTrfNo());
            if (!loadScanTrfScanItems()) {
                okMessage(objGlobal.getErrorMessage());
            }
        }
        if (objGrnTransferNewSharedRef.loadLastSave() != "") {
            tv_grn_transfer_last_save.setText(objGrnTransferNewSharedRef.loadLastSave());
        }

        et_grn_transfer_trfno_entryno.setOnTouchListener(new View.OnTouchListener() {
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

        et_grn_transfer_trfno_entryno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    new LoadTransferDetails().execute();
                    return true;
                }
                return false;
            }
        });

        ch_grn_transfer_trffrom_oth_shop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    et_grn_transfer_trfno_entryno.setHint("Transfer No. / Tote Id");
                    et_grn_transfer_rfid_ginno.setHint("");
                    et_grn_transfer_rfid_ginno.setEnabled(false);
                    et_grn_transfer_rfid_ginno.requestFocus();
                }
                else {
                    et_grn_transfer_trfno_entryno.setHint("Entry No.");
                    et_grn_transfer_rfid_ginno.setHint("GIN Number");
                    et_grn_transfer_rfid_ginno.setEnabled(true);
                    et_grn_transfer_trfno_entryno.requestFocus();
                }
            }
        });

        bt_grn_transfer_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadTransferDetails().execute();
            }
        });

        bt_grn_transfer_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupScanWindow();
            }
        });

        bt_grn_transfer_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear all?")
                        .setTitle("Conformation")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearAll(true);
                                if(ch_grn_transfer_trffrom_oth_shop.isChecked()) {
                                    et_grn_transfer_trfno_entryno.requestFocus();
                                } else {
                                    et_grn_transfer_rfid_ginno.requestFocus();
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
        });

        bt_grn_transfer_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_Result = objGrnTransferNewControl.findExcessAndMissing();
                if (!b_Result) {
                    okMessage(objGlobal.getErrorMessage());
                } else {
                    if (objGrnTransferNewGlobal.getTotalExcess() > 0 || objGrnTransferNewGlobal.getTotalMissing() > 0) {
                        openPopupMissingExcessVerify();
                    } else {
                        saveGrn();
                    }
                }
            }
        });
        if(ch_grn_transfer_trffrom_oth_shop.isChecked()) {
            et_grn_transfer_trfno_entryno.requestFocus();
        } else {
            et_grn_transfer_rfid_ginno.requestFocus();
        }
        return view;
    }

    private void saveGrn() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Are You sure to save?")
                .setTitle("Conformation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SaveGrnShopTransfer().execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private class LoadTransferDetails extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;

        public LoadTransferDetails() {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                if (ch_grn_transfer_trffrom_oth_shop.isChecked()) {
                    b_Result = objGrnShopTransferControl.validateShopTransfer(false, false, et_grn_transfer_trfno_entryno.getText().toString().toUpperCase());
                } else {
                    b_Result = objGrnTransferNewControl.validateTransferNumber(false, false, et_grn_transfer_trfno_entryno.getText().toString().toUpperCase(),
                            et_grn_transfer_rfid_ginno.getText().toString().toUpperCase());
                }
                if (!b_Result) {
                    return 0;
                }
            } catch (Exception e) {
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                okMessage(objGlobal.getErrorMessage());
                vibrate(500);
                et_grn_transfer_trfno_entryno.setText("");
                et_grn_transfer_trfno_entryno.requestFocus();
            } else {
                if (!loadScanTrfScanItems()) {
                    okMessage(objGlobal.getErrorMessage());
                } else {
                    et_grn_transfer_trfno_entryno.setText(objGrnTransferNewGlobal.getTrfno());
                    tv_grn_transfer_shopname.setText(objGrnTransferNewGlobal.getFromshop());
                    tv_grn_transfer_trfdate.setText(objGrnTransferNewGlobal.getTrfdate());
                    tv_grn_transfer_toteid.setText(objGrnTransferNewGlobal.getToteid());

                    objGrnTransferNewSharedRef.saveTrfNo(et_grn_transfer_trfno_entryno.getText().toString().toUpperCase());
                    objGrnTransferNewSharedRef.saveGinNo(et_grn_transfer_rfid_ginno.getText().toString());
                    objGrnTransferNewSharedRef.saveShopTrfName(tv_grn_transfer_shopname.getText().toString().toUpperCase());
                    objGrnTransferNewSharedRef.saveTrfDate(tv_grn_transfer_trfdate.getText().toString().toUpperCase());
                    objGrnTransferNewSharedRef.saveToteId(tv_grn_transfer_toteid.getText().toString().toUpperCase());
                    objGrnTransferNewSharedRef.saveTickShopTransfer(false);

                    if (ch_grn_transfer_trffrom_oth_shop.isChecked()) {
                        objGrnTransferNewSharedRef.saveTickShopTransfer(true);
                    }
                    et_grn_transfer_trfno_entryno.setEnabled(false);
                    et_grn_transfer_rfid_ginno.setEnabled(false);
                    bt_grn_transfer_load.setEnabled(false);
                    bt_grn_transfer_scan.setEnabled(true);
                    ch_grn_transfer_trffrom_oth_shop.setEnabled(false);
                    ch_grn_transfer_view.setEnabled(false);
                    openPopupScanWindow();
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class SaveGrnShopTransfer extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;

        public SaveGrnShopTransfer() {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                if (ch_grn_transfer_trffrom_oth_shop.isChecked()) {
                    b_Result = objGrnShopTransferControl.saveShopTransfer(et_grn_transfer_trfno_entryno.getText().toString());
                } else {
                    b_Result = objGrnTransferNewControl.grnSave(et_grn_transfer_trfno_entryno.getText().toString(), tv_grn_transfer_trfdate.getText().toString(),
                            et_grn_transfer_rfid_ginno.getText().toString());
                }
                if (!b_Result) {
                    return 0;
                }
            } catch (Exception e) {
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                okMessage(objGlobal.getErrorMessage());
                vibrate(500);
                bt_grn_transfer_save.requestFocus();
            } else {
                if (ch_grn_transfer_trffrom_oth_shop.isChecked()) {
                    tv_grn_transfer_last_save.setText("Entry.No: " + objGrnTransferNewGlobal.getTrfno());
                } else {
                    tv_grn_transfer_last_save.setText("Entry.No: " + objGrnTransferNewGlobal.getLatestGrnNo() + " : " + objGrnTransferNewGlobal.getLatestGrnNoRf() + ", Trf.No: " + objGrnTransferNewGlobal.getTrfno());
                }
                objGrnTransferNewSharedRef.saveLastSave(tv_grn_transfer_last_save.getText().toString());
                b_Result = clearAll(false);
                if (!b_Result) {
                    okMessage(objGlobal.getErrorMessage());
                } else {
                    et_grn_transfer_trfno_entryno.requestFocus();
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    boolean clearAll(boolean all) {
        try {
            b_Result = objGrnTransferNewControl.deleteAll();
            if (!b_Result) {
                okMessage(objGlobal.getErrorMessage());
            }
            b_Result = loadScanTrfScanItems();
            if (!b_Result) {
                okMessage(objGlobal.getErrorMessage());
            }
            et_grn_transfer_trfno_entryno.setText("");
            tv_grn_transfer_shopname.setText("");
            tv_grn_transfer_trfdate.setText("");
            tv_grn_transfer_toteid.setText("");
            tv_grn_transfer_total_scan_qty.setText("");
            tv_grn_transfer_total_trf_qty.setText("");
            tv_grn_transfer_total_diff_qty.setText("");
            objGrnTransferNewSharedRef.saveTrfNo("");
            objGrnTransferNewSharedRef.saveShopTrfName("");
            objGrnTransferNewSharedRef.saveTrfDate("");
            objGrnTransferNewSharedRef.saveToteId("");
            et_grn_transfer_trfno_entryno.setEnabled(true);
            bt_grn_transfer_load.setEnabled(true);
            if(ch_grn_transfer_trffrom_oth_shop.isChecked()) {
                et_grn_transfer_rfid_ginno.setText("");
                objGrnTransferNewSharedRef.saveGinNo("");
                et_grn_transfer_rfid_ginno.setEnabled(false);
            } else {
                if(all) {
                    et_grn_transfer_rfid_ginno.setText("");
                    objGrnTransferNewSharedRef.saveGinNo("");
                    et_grn_transfer_rfid_ginno.setEnabled(true);
                } else {
                    et_grn_transfer_rfid_ginno.setEnabled(false);
                }
            }
            bt_grn_transfer_scan.setEnabled(false);
            ch_grn_transfer_trffrom_oth_shop.setEnabled(true);
            ch_grn_transfer_view.setEnabled(true);
            flagEdit = false;
        } catch (Exception ex) {
            objGlobal.setErrorMessage("GrnTransferFragment:clearAll:" + ex.toString());
            return false;
        }
        return true;
    }

    private boolean loadScanTrfScanItems() {
        try {
            listGrnTransferNewTrfScanItems.clear();
            listGrnTransferNewTrfScanItems = objGrnTransferNewControl.loadGrnTrfScanItems();
            if (listGrnTransferNewTrfScanItems == null) {
                return false;
            }
            objMyGrnNewTransferScanItemsAdp = new MyGrnNewTransferScanItemsAdp(listGrnTransferNewTrfScanItems);
            lv_grn_transfer.setAdapter(objMyGrnNewTransferScanItemsAdp);
            tv_grn_transfer_total_scan_qty.setText(numformat.format(objGrnTransferNewGlobal.getScanqty()));
            tv_grn_transfer_total_trf_qty.setText(numformat.format(objGrnTransferNewGlobal.getTrfqty()));
            tv_grn_transfer_total_diff_qty.setText(numformat.format(objGrnTransferNewGlobal.getDiffqty()));
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("GrnTransferNewFragment:loadScanTrfScanItems:catch: " + e.getMessage());
            return false;
        }
    }

    private class MyGrnNewTransferScanItemsAdp extends BaseAdapter {
        public ArrayList<GrnTransferNewTrfScanItems> listGrnTransferNewTrfScanItems;

        public MyGrnNewTransferScanItemsAdp(ArrayList<GrnTransferNewTrfScanItems> listGrnTransferNewTrfScanItems) {
            this.listGrnTransferNewTrfScanItems = listGrnTransferNewTrfScanItems;
        }

        @Override
        public int getCount() {
            return listGrnTransferNewTrfScanItems.size();
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
            View myView = mInflater.inflate(R.layout.grn_transfer_new_scan_trf_items_ticket, null);
            final GrnTransferNewTrfScanItems s = listGrnTransferNewTrfScanItems.get(position);

            TextView tv_grn_transfer_new_itemcode = (TextView) myView.findViewById(R.id.tv_grn_transfer_new_itemcode);
            tv_grn_transfer_new_itemcode.setText(String.valueOf(s.itemcode));

            TextView tv_grn_transfer_new_scan_qty = (TextView) myView.findViewById(R.id.tv_grn_transfer_new_scan_qty);
            tv_grn_transfer_new_scan_qty.setText(String.valueOf(s.scanqty));

            TextView tv_grn_transfer_new_trf_qty = (TextView) myView.findViewById(R.id.tv_grn_transfer_new_trf_qty);
            tv_grn_transfer_new_trf_qty.setText(String.valueOf(s.trfqty));

            TextView tv_grn_transfer_new_diff_qty = (TextView) myView.findViewById(R.id.tv_grn_transfer_new_diff_qty);
            tv_grn_transfer_new_diff_qty.setText(String.valueOf(s.diffqty));

            Button bt_grn_transfer_edit = (Button) myView.findViewById(R.id.bt_grn_transfer_edit);
            bt_grn_transfer_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPopupScanWindow();
                    b_Result = objGrnTransferNewControl.viewExistItem(s.itemcode);
                    if (b_Result) {
                        et_popup_grn_transfer_barcode.setText(s.itemcode);
                        tv_popup_grn_transfer_itemcode.setText(s.itemcode);
                        tv_popup_grn_transfer_description.setText(objGrnTransferNewGlobal.getScanDescription());
                        //tv_popup_grn_transfer_current_stock.setText(objGrnTransferNewGlobal.getScanDescription);
                        //tv_popup_grn_transfer_system_price.setText(objGrnTransferNewGlobal.getScanDescription);
                        //tv_popup_grn_transfer_scan_price.setText(objGrnTransferNewGlobal.getScanDescription);
                        tv_popup_grn_transfer_scan_qty.setText(numformat.format(objGrnTransferNewGlobal.getItemscanqty()));
                        tv_popup_grn_transfer_trf_qty.setText(numformat.format(objGrnTransferNewGlobal.getItemtrfqty()));
                        tv_popup_grn_transfer_diff_qty.setText(numformat.format(objGrnTransferNewGlobal.getItemdiffqty()));
                        if (objGrnTransferNewGlobal.getItemscanqty() > 0) {
                            et_popup_grn_transfer_qty.setText(numformat.format(objGrnTransferNewGlobal.getItemscanqty()));
                        }
                        et_popup_grn_transfer_barcode.setEnabled(false);
                        et_popup_grn_transfer_qty.setEnabled(true);
                        et_popup_grn_transfer_qty.requestFocus();
                    } else {

                    }
                    flagEdit = true;
                }
            });

            if (s.diffqty != 0) {
                tv_grn_transfer_new_itemcode.setTextColor(Color.RED);
                tv_grn_transfer_new_scan_qty.setTextColor(Color.RED);
                tv_grn_transfer_new_trf_qty.setTextColor(Color.RED);
                tv_grn_transfer_new_diff_qty.setTextColor(Color.RED);
            } else {
                tv_grn_transfer_new_scan_qty.setTextColor(Color.rgb(41, 102, 0));
                tv_grn_transfer_new_itemcode.setTextColor(Color.rgb(41, 102, 0));
                tv_grn_transfer_new_trf_qty.setTextColor(Color.rgb(41, 102, 0));
                tv_grn_transfer_new_diff_qty.setTextColor(Color.rgb(41, 102, 0));
                //tv_grn_transfer_new_itemcode.setPaintFlags(tv_grn_transfer_new_diff_qty.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            }

            return myView;
        }
    }

    void openPopupMissingExcessVerify() {
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.grn_transfer_popup_diff_window);
        et_grn_transfer_popup_excess_qty = (EditText) myDialog.findViewById(R.id.et_grn_transfer_popup_excess_qty);
        et_grn_transfer_popup_missing_qty = (EditText) myDialog.findViewById(R.id.et_grn_transfer_popup_missing_qty);
        et_grn_transfer_popup_diff_verify = (EditText) myDialog.findViewById(R.id.et_grn_transfer_popup_diff_verify);
        bt_grn_transfer_popup_miss_ok = (Button) myDialog.findViewById(R.id.bt_grn_transfer_popup_miss_ok);
        bt_grn_transfer_popup_miss_cancel = (Button) myDialog.findViewById(R.id.bt_grn_transfer_popup_miss_cancel);
        bt_grn_transfer_popup_miss_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int txMiss = 0, txExce = 0;
                String verifyMgrname = "";
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
                if (objGrnTransferNewGlobal.getTotalExcess() != txExce) {
                    okMessage("Excess quantity missmatch, please check");
                    vibrate(500);
                    et_grn_transfer_popup_excess_qty.requestFocus();
                } else if (objGrnTransferNewGlobal.getTotalMissing() != txMiss) {
                    okMessage("Missing quantity missmatch, please check");
                    vibrate(500);
                    et_grn_transfer_popup_missing_qty.requestFocus();
                } else {
                    verifyMgrname = objGrnTransferNewControl.validateManagerVerify(et_grn_transfer_popup_diff_verify.getText().toString());
                    if (verifyMgrname.isEmpty()) {
                        okMessage(objGlobal.getErrorMessage());
                        vibrate(500);
                        et_grn_transfer_popup_diff_verify.requestFocus();
                    } else {
                        saveGrn();
                        et_grn_transfer_popup_missing_qty.setText("0");
                        et_grn_transfer_popup_excess_qty.setText("0");
                        myDialog.dismiss();
                    }
                }
            }
        });

        bt_grn_transfer_popup_miss_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
        et_grn_transfer_popup_excess_qty.requestFocus();
    }

    void openPopupScanWindow() {
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_grn_transfer_new_item_scan);

        et_popup_grn_transfer_barcode = (EditText) myDialog.findViewById(R.id.et_popup_grn_transfer_barcode);
        et_popup_grn_transfer_qty = (EditText) myDialog.findViewById(R.id.et_popup_grn_transfer_qty);
        tv_popup_grn_transfer_last = (TextView) myDialog.findViewById(R.id.tv_popup_grn_transfer_last);
        bt_popup_grn_transfer_add = (Button) myDialog.findViewById(R.id.bt_popup_grn_transfer_add);
        tv_popup_grn_transfer_result = (TextView) myDialog.findViewById(R.id.tv_popup_grn_transfer_result);
        tv_popup_grn_transfer_itemcode = (TextView) myDialog.findViewById(R.id.tv_popup_grn_transfer_itemcode);
        tv_popup_grn_transfer_current_stock = (TextView) myDialog.findViewById(R.id.tv_popup_grn_transfer_current_stock);
        tv_popup_grn_transfer_scan_price = (TextView) myDialog.findViewById(R.id.tv_popup_grn_transfer_scan_price);
        tv_popup_grn_transfer_system_price = (TextView) myDialog.findViewById(R.id.tv_popup_grn_transfer_system_price);
        tv_popup_grn_transfer_description = (TextView) myDialog.findViewById(R.id.tv_popup_grn_transfer_description);
        tv_popup_grn_transfer_scan_qty = (TextView) myDialog.findViewById(R.id.tv_popup_grn_transfer_scan_qty);
        tv_popup_grn_transfer_trf_qty = (TextView) myDialog.findViewById(R.id.tv_popup_grn_transfer_trf_qty);
        tv_popup_grn_transfer_diff_qty = (TextView) myDialog.findViewById(R.id.tv_popup_grn_transfer_diff_qty);
        bt_popup_grn_transfer_close = (Button) myDialog.findViewById(R.id.bt_popup_grn_transfer_close);

        et_popup_grn_transfer_qty.setText("1");
        et_popup_grn_transfer_qty.setEnabled(objGlobal.getEnterQty());

        et_popup_grn_transfer_barcode.setOnTouchListener(new View.OnTouchListener() {
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

        et_popup_grn_transfer_barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    if (et_popup_grn_transfer_qty.getText().toString().isEmpty())
                        et_popup_grn_transfer_qty.setText("1");
                    b_Result = grnItemScan(et_popup_grn_transfer_barcode.getText().toString().trim().toUpperCase(), Integer.valueOf(et_popup_grn_transfer_qty.getText().toString()));
                    if (!b_Result) {
                    } else {
                        if (!loadScanTrfScanItems()) {
                            okMessage(objGlobal.getErrorMessage());
                        } else {
                            if (flagEdit) myDialog.dismiss();
                            flagEdit = false;
                        }
                    }
                }
                return false;
            }
        });

        bt_popup_grn_transfer_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_popup_grn_transfer_qty.getText().toString().isEmpty())
                    et_popup_grn_transfer_qty.setText("1");
                b_Result = grnItemScan(et_popup_grn_transfer_barcode.getText().toString().trim().toUpperCase(), Integer.valueOf(et_popup_grn_transfer_qty.getText().toString()));
                if (!b_Result) {
                } else {
                    if (!loadScanTrfScanItems()) {
                        okMessage(objGlobal.getErrorMessage());
                    } else {
                        if (flagEdit) myDialog.dismiss();
                        flagEdit = false;
                    }
                }
            }
        });

        bt_popup_grn_transfer_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
        et_popup_grn_transfer_barcode.requestFocus();
    }

    private boolean grnItemScan(String scan, int qty) {
        String barcode = "", rfid = "";
        String itemcode = "";
        tv_popup_grn_transfer_last.setText(scan);
        et_popup_grn_transfer_barcode.setText("");
        tv_popup_grn_transfer_result.setText("");
        if (TextUtils.isEmpty(scan) || scan == "") {
            tv_popup_grn_transfer_result.setText("Please Scan Barcode / RFID");
            et_popup_grn_transfer_barcode.requestFocus();
            return false;
        }
        try {
            if (flagEdit) {
                itemcode = scan;
                if (objGrnTransferNewControl.editItem(itemcode, qty) == false) {
                    vibrateSound(100);
                    tv_popup_grn_transfer_result.setText(objGlobal.getErrorMessage() + ", Itemcode: " + itemcode);
                    et_popup_grn_transfer_barcode.requestFocus();
                    return false;
                }
                return true;
            }
            b_Result = objGrnTransferNewControl.validateRfid(scan, et_grn_transfer_trfno_entryno.getText().toString());
            if (!b_Result) {
                vibrateSound(100);
                tv_popup_grn_transfer_result.setText(objGlobal.getErrorMessage());
                return false;
            }
            if (objGrnTransferNewGlobal.getScanBarcode().isEmpty()) {
                barcode = scan;
            } else {
                barcode = objGrnTransferNewGlobal.getScanBarcode();
                rfid = scan;
            }
            seperateBarcode(barcode);
            itemcode = scanItemcode;
            if (qty == 0) qty = 1;
            if (objGrnTransferNewControl.validateScanItem(itemcode, qty, rfid, scanPrice) == false) {
                vibrateSound(100);
                tv_popup_grn_transfer_result.setText(objGlobal.getErrorMessage() + ", Itemcode: " + itemcode);
                et_popup_grn_transfer_barcode.requestFocus();
                return false;
            }
            if (itemcode.length() > 15) {
                vibrateSound(100);
                tv_popup_grn_transfer_result.setText("Invalid itemcode, itemcode length is more than 15 (" + itemcode + ")");
                et_popup_grn_transfer_barcode.requestFocus();
                return false;
            }
            tv_popup_grn_transfer_itemcode.setText(itemcode);
            tv_popup_grn_transfer_description.setText(objGrnTransferNewGlobal.getScanDescription());
            tv_popup_grn_transfer_scan_qty.setText(numformat.format(objGrnTransferNewGlobal.getItemscanqty()));
            tv_popup_grn_transfer_trf_qty.setText(numformat.format(objGrnTransferNewGlobal.getItemtrfqty()));
            tv_popup_grn_transfer_diff_qty.setText(numformat.format(objGrnTransferNewGlobal.getItemdiffqty()));
            tv_popup_grn_transfer_current_stock.setText(numformat.format(objGrnTransferNewGlobal.getScanSysStock()));
            tv_popup_grn_transfer_scan_price.setText(numformatdec.format(scanPrice));
            tv_popup_grn_transfer_system_price.setText(numformatdec.format(objGrnTransferNewGlobal.getScanSysPrice()));
            if (objGrnTransferNewGlobal.getItemdiffqty() > 0) {
                vibrateSound(100);
                tv_popup_grn_transfer_result.setText("Excess item scanned.");
            }
            et_popup_grn_transfer_qty.setText("1");
            et_popup_grn_transfer_barcode.requestFocus();
            return true;
        } catch (Exception e) {
            vibrateSound(100);
            okMessage(e.getMessage());
            return false;
        }
    }

    void seperateBarcode(String barcode) {
        String[] parts;
        int i;
        if (barcode.contains("/")) {
            parts = barcode.split("/");
            scanItemcode = parts[0];
            scanPrice = Float.valueOf(parts[1]);
            scanTrfno = parts[2];
        } else {
            scanItemcode = barcode;
        }
        for (i = 0; i < scanItemcode.length() - 1; i++) {
            if (scanItemcode.charAt(i) != '0') {
                break;
            }
        }
    }

    private void okMessage(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(msgHead);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
        vibrate(500);
    }

    void vibrate(int duration) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }

    void vibrateSound(int duration) {
        try {
            if (objGlobal.getUserName().equals("BFL")) {
                vibrate(500);
            } else {
                AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                Uri notification = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.errorsound);
                Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                r.play();
                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                assert v != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(duration);
                }
            }
        } catch (Exception e) {
            okMessage(e.getMessage());
        }
    }
}