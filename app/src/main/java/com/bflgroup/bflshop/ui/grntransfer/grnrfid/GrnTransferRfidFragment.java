package com.bflgroup.bflshop.ui.grntransfer.grnrfid;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.rfidreadercw.UhfInfo;
import com.bflgroup.bflshop.rfidreadercw.tools.NumberTool;
import com.bflgroup.bflshop.rfidreadercw.tools.StringUtils;
import com.bflgroup.bflshop.rfidreadercw.tools.UIHelper;
import com.bflgroup.bflshop.ui.grntransfer.grnrfid.view.UhfLocationCanvasView;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.interfaces.IUHF;
import com.rscja.deviceapi.interfaces.IUHFInventoryCallback;
import com.rscja.deviceapi.interfaces.IUHFLocationCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GrnTransferRfidFragment extends Fragment {

    //reader relared
    public RFIDWithUHFUART mReader;
    public boolean loopFlag = false;
    private long time;
    public static HashMap<String, String> map;
    public static final String TAG_EPC = "tagEPC";
    public static final String TAG_EPC_TID = "tagEpcTID";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_RSSI = "tagRssi";
    private SoundPool soundPool;
    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    public ArrayList<HashMap<String, String>> tagList = new ArrayList<HashMap<String, String>>();
    public static List<String> tempDatas = new ArrayList<>();
    public static ArrayList<String> epcTidUser = new ArrayList<>();
    private int total;
    MyGrnRfidTransferScanRfidsAdp objMyGrnRfidTransferScanRfidsAdp;
    public UhfInfo uhfInfo=new UhfInfo();
    private PlaySoundThread playSoundThread;
    private AudioManager am;
    private float volumnRatio;
    private int  selectItem=-1;
    //reader relared

    private TextView tv_grn_transfer_rfid_user;
    private TextView tv_grn_transfer_rfid_shop;
    private TextView tv_ageing_slashing_battery_percentage;
    private EditText et_grn_transfer_rfid_ginno;
    private TextView tv_grn_transfer_rfid_din_rec_date;
    private Button bt_grn_transfer_rfid_load_gin;
    private Button bt_grn_transfer_rfid_add;
    private TextView tv_grn_transfer_rfid_total_totes;
    private TextView tv_grn_transfer_rfid_total_quantity;
    private TextView tv_grn_transfer_rfid_total_rfid_system;
    private TextView tv_grn_transfer_rfid_total_nonrfid_system;
    private TextView tv_grn_transfer_rfid_total_rfid_scan;
    private TextView tv_grn_transfer_rfid_total_nonrfid_scan;
    private TextView tv_grn_transfer_rfid_total_rfid_diff;
    private TextView tv_grn_transfer_rfid_total_nonrfid_diff;
    private ListView lv_grn_transfer_rfid_details;
    private Button bt_grn_transfer_rfid_clear_all;
    private Button bt_grn_transfer_rfid_save;
    private Button bt_grn_transfer_rfid_view_excess;
    private Button bt_grn_transfer_rfid_view_verify_totes;
    private CheckBox ch_grn_transfer_rfid_view_showpending;

    private RadioButton rb_popup_grn_rfid_transfer_single;
    private RadioButton rb_popup_grn_rfid_transfer_auto;
    private Button bt_popup_grn_rfid_transfer_connect;
    private Button bt_popup_grn_rfid_transfer_start;
    private Button bt_popup_grn_rfid_transfer_options;
    private ListView lv_popup_grn_rfid_transfer_rfids;
    private Button bt_popup_grn_rfid_transfer_clear;
    private Button bt_popup_grn_rfid_transfer_close;
    private TextView tv_popup_grn_rfid_transfer_scantime;
    private TextView tv_popup_grn_rfid_transfer_epc_count;
    private TextView tv_popup_grn_rfid_transfer_total;

    private TextView tv_popup_grn_rfid_transfer_diff_trfno;
    private TextView tv_popup_grn_rfid_transfer_total_rfidqty;
    private TextView tv_popup_grn_rfid_transfer_total_nonrfidqty;
    private TextView tv_popup_grn_rfid_transfer_scan_rfidqty;
    private TextView tv_popup_grn_rfid_transfer_scan_nonrfidqty;
    private TextView tv_popup_grn_rfid_transfer_diff_rfidqty;
    private TextView tv_popup_grn_rfid_transfer_diff_nonrfidqty;
    private Button bt_popup_grn_rfid_transfer_diff_close;
    private Button bt_popup_grn_rfid_transfer_diff_trfno_scan_barcode;
    private Button bt_popup_grn_rfid_transfer_diff_trfno_scan_rfid;
    private CheckBox ch_popup_grn_transfer_rfid_view_showpending;
    private ListView lv_popup_grn_rfid_transfer_diff_trfdetail;

    private TextView tv_popup_rfid_grn_transfer_barcode_trfno;
    private EditText et_popup_rfid_grn_transfer_barcode;
    private Button bt_popup_rfid_grn_transfer_add;
    private TextView tv_popup_rfid_grn_transfer_result;
    private TextView tv_popup_rfid_grn_transfer_last;
    private Button bt_popup_rfid_grn_transfer_close;

    private ListView lv_popup_grn_rfid_transfer_popup_excess_list;
    private Button bt_popup_grn_rfid_transfer_popup_excess_close;

    private EditText et_grn_rfid_transfer_verify_transfernumber;
    private Button bt_grn_rfid_transfer_verify_load;
    private Button bt_grn_rfid_transfer_verify_close;

    private TextView tv_popup_grn_rfid_transfer_popup_excess_locate_upc;
    private Button bt_popup_grn_rfid_transfer_popup_excess_start_locate;
    private Button bt_popup_grn_rfid_transfer_popup_excess_close_locate;
    private UhfLocationCanvasView llChart;
    private Object objectLock = new Object();

    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();

    private GrnTransferRfidControl objGrnTransferRfidControl = new GrnTransferRfidControl();
    private GrnTransferRfidGlobal objGrnTransferRfidGlobal=GrnTransferRfidGlobal.getInstance();
    ArrayList<GrnTransferRfidScanItems> listGrnTransferRfidScanItems = new ArrayList<GrnTransferRfidScanItems>();
    ArrayList<GrnTransferRfidScanDiffItems> listGrnTransferRfidScanDiffItems = new ArrayList<GrnTransferRfidScanDiffItems>();
    ArrayList<GrnTransferRfidExcessItems> listGrnTransferRfidExcessItems = new ArrayList<GrnTransferRfidExcessItems>();
    private boolean b_Result;
    private String msgHead="RFID GRN";

    MyGrnRfidTransferScanItemsAdp objMyGrnRfidTransferScanItemsAdp;
    MyGrnRfidTransferScanDiffAdp objMyGrnRfidTransferScanDiffAdp;
    MyGrnRfidTransferExcessAdp objMyGrnRfidTransferExcessAdp;
    GrnTransferRfidSharedRef objGrnTransferRfidSharedRef;
    Dialog myDialogRfidScan;
    DecimalFormat numformat = new DecimalFormat("###,###");

    private String scanItemcode="";
    private float scanPrice=0;
    private String scanTrfno="";

    public GrnTransferRfidFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grn_transfer_rfid, container, false);

        tv_grn_transfer_rfid_user = (TextView) view.findViewById(R.id.tv_grn_transfer_rfid_user);
        tv_grn_transfer_rfid_shop = (TextView) view.findViewById(R.id.tv_grn_transfer_rfid_shop);
        tv_ageing_slashing_battery_percentage = (TextView) view.findViewById(R.id.tv_ageing_slashing_battery_percentage);
        et_grn_transfer_rfid_ginno = (EditText) view.findViewById(R.id.et_grn_transfer_rfid_ginno);
        tv_grn_transfer_rfid_din_rec_date = (TextView) view.findViewById(R.id.tv_grn_transfer_rfid_din_rec_date);
        bt_grn_transfer_rfid_load_gin = (Button) view.findViewById(R.id.bt_grn_transfer_rfid_load_gin);
        bt_grn_transfer_rfid_add = (Button) view.findViewById(R.id.bt_grn_transfer_rfid_add);
        tv_grn_transfer_rfid_total_totes = (TextView) view.findViewById(R.id.tv_grn_transfer_rfid_total_totes);
        tv_grn_transfer_rfid_total_quantity = (TextView) view.findViewById(R.id.tv_grn_transfer_rfid_total_quantity);
        tv_grn_transfer_rfid_total_rfid_system = (TextView) view.findViewById(R.id.tv_grn_transfer_rfid_total_rfid_system);
        tv_grn_transfer_rfid_total_nonrfid_system = (TextView) view.findViewById(R.id.tv_grn_transfer_rfid_total_nonrfid_system);
        tv_grn_transfer_rfid_total_rfid_scan = (TextView) view.findViewById(R.id.tv_grn_transfer_rfid_total_rfid_scan);
        tv_grn_transfer_rfid_total_nonrfid_scan = (TextView) view.findViewById(R.id.tv_grn_transfer_rfid_total_nonrfid_scan);
        tv_grn_transfer_rfid_total_rfid_diff = (TextView) view.findViewById(R.id.tv_grn_transfer_rfid_total_rfid_diff);
        tv_grn_transfer_rfid_total_nonrfid_diff = (TextView) view.findViewById(R.id.tv_grn_transfer_rfid_total_nonrfid_diff);
        lv_grn_transfer_rfid_details = (ListView) view.findViewById(R.id.lv_grn_transfer_rfid_details);
        bt_grn_transfer_rfid_clear_all = (Button) view.findViewById(R.id.bt_grn_transfer_rfid_clear_all);
        bt_grn_transfer_rfid_save = (Button) view.findViewById(R.id.bt_grn_transfer_rfid_save);
        bt_grn_transfer_rfid_view_excess = (Button) view.findViewById(R.id.bt_grn_transfer_rfid_view_excess);
        bt_grn_transfer_rfid_view_verify_totes = (Button) view.findViewById(R.id.bt_grn_transfer_rfid_view_verify_totes);
        ch_grn_transfer_rfid_view_showpending = (CheckBox) view.findViewById(R.id.ch_grn_transfer_rfid_view_showpending);

        objGrnTransferRfidSharedRef=new GrnTransferRfidSharedRef(getContext());
        objMyGrnRfidTransferScanRfidsAdp=new MyGrnRfidTransferScanRfidsAdp(getContext());

        tv_grn_transfer_rfid_shop.setText(PosGlobal.getShopName());
        tv_grn_transfer_rfid_user.setText(objGlobal.getUserName());

        setEnable(true);
        if(objGrnTransferRfidSharedRef.loadGinNo()!="") {
            //sp_grn_transfer_rfid_device.setSelected();
            tv_grn_transfer_rfid_din_rec_date.setText(objGrnTransferRfidSharedRef.loadRecDate());
            et_grn_transfer_rfid_ginno.setText(objGrnTransferRfidSharedRef.loadGinNo());
            setEnable(false);
            if (!loadScanTrfRfidItems())
                okMessage(objGlobal.getErrorMessage());
        }
        bt_grn_transfer_rfid_load_gin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ginNo = et_grn_transfer_rfid_ginno.getText().toString();
                if(ginNo.isEmpty()){
                    okMessage("Please enter GIN Number");
                } else {
                    new LoadGinVerification().execute();
                }
            }
        });
        bt_grn_transfer_rfid_view_excess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupRfidExcessWindow();
            }
        });

        bt_grn_transfer_rfid_view_verify_totes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupTransferVerify();
            }
        });

        ch_grn_transfer_rfid_view_showpending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loadScanTrfRfidItems();
            }
        });

        bt_grn_transfer_rfid_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupScanWindow("B");
            }
        });

        bt_grn_transfer_rfid_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!clearAll()) okMessage(objGlobal.getErrorMessage());
                                et_grn_transfer_rfid_ginno.requestFocus();
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

        bt_grn_transfer_rfid_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGrn();
            }
        });
        return view;
    }

    private void saveGrn() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Are You sure to save?")
                .setTitle("Conformation")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SaveGrn().execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private class SaveGrn extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        public SaveGrn() {
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
                b_Result = objGrnTransferRfidControl.grnSave(et_grn_transfer_rfid_ginno.getText().toString());
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
                bt_grn_transfer_rfid_save.requestFocus();
            } else {
                //objGrnTransferNewSharedRef.saveLastSave(tv_grn_transfer_last_save.getText().toString());
                b_Result = clearAll();
                if (!b_Result) {
                    okMessage(objGlobal.getErrorMessage());
                } else {
                    et_grn_transfer_rfid_ginno.requestFocus();
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    void openPopupScanWindowBarcode(String trfno) {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_grn_rfid_transfer_scan_details_barcode);

        tv_popup_rfid_grn_transfer_barcode_trfno = (TextView) myDialog.findViewById(R.id.tv_popup_rfid_grn_transfer_barcode_trfno);
        et_popup_rfid_grn_transfer_barcode = (EditText) myDialog.findViewById(R.id.et_popup_rfid_grn_transfer_barcode);
        bt_popup_rfid_grn_transfer_add = (Button) myDialog.findViewById(R.id.bt_popup_rfid_grn_transfer_add);
        tv_popup_rfid_grn_transfer_result = (TextView) myDialog.findViewById(R.id.tv_popup_rfid_grn_transfer_result);
        tv_popup_rfid_grn_transfer_last = (TextView) myDialog.findViewById(R.id.tv_popup_rfid_grn_transfer_last);
        bt_popup_rfid_grn_transfer_close = (Button) myDialog.findViewById(R.id.bt_popup_rfid_grn_transfer_close);
        tv_popup_rfid_grn_transfer_barcode_trfno.setText(trfno);
        et_popup_rfid_grn_transfer_barcode.setOnTouchListener(new View.OnTouchListener() {
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

        et_popup_rfid_grn_transfer_barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    b_Result = grnItemScan(trfno,et_popup_rfid_grn_transfer_barcode.getText().toString().trim().toUpperCase());
                }
                return false;
            }
        });

        bt_popup_rfid_grn_transfer_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_Result = grnItemScan(trfno,et_popup_rfid_grn_transfer_barcode.getText().toString().trim().toUpperCase());
            }
        });

        bt_popup_rfid_grn_transfer_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        et_popup_rfid_grn_transfer_barcode.requestFocus();

        myDialog.show();
    }

    private boolean grnItemScan(String trfno, String scan) {
        String barcode = "", rfid = "";
        String itemcode = "";
        tv_popup_rfid_grn_transfer_last.setText(scan);
        et_popup_rfid_grn_transfer_barcode.setText("");
        tv_popup_rfid_grn_transfer_result.setText("");
        if (TextUtils.isEmpty(trfno) || trfno == "") {
            tv_popup_rfid_grn_transfer_result.setText("Please Scan Barcode / RFID");
            et_popup_rfid_grn_transfer_barcode.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(scan) || scan.equals("")) {
            tv_popup_rfid_grn_transfer_result.setText("Please Scan Barcode / RFID");
            et_popup_rfid_grn_transfer_barcode.requestFocus();
            return false;
        }
        try {
            barcode = scan;
            seperateBarcode(barcode);
            itemcode = scanItemcode;
            if (!objGrnTransferRfidControl.validateScanItem(trfno, itemcode)) {
                tv_popup_rfid_grn_transfer_result.setText(objGlobal.getErrorMessage() + ", Itemcode: " + itemcode);
                et_popup_rfid_grn_transfer_barcode.requestFocus();
                return false;
            }
            if (itemcode.length() > 15) {
                tv_popup_rfid_grn_transfer_result.setText("Invalid itemcode, itemcode length is more than 15 (" + itemcode + ")");
                tv_popup_rfid_grn_transfer_result.requestFocus();
                return false;
            }

            boolean diff=false;
            if(ch_popup_grn_transfer_rfid_view_showpending.isChecked()) diff=true;
            listGrnTransferRfidScanDiffItems.clear();
            listGrnTransferRfidScanDiffItems = objGrnTransferRfidControl.loadScanRfidDetailsDiff(trfno,diff);
            objMyGrnRfidTransferScanDiffAdp = new MyGrnRfidTransferScanDiffAdp(listGrnTransferRfidScanDiffItems);
            lv_popup_grn_rfid_transfer_diff_trfdetail.setAdapter(objMyGrnRfidTransferScanDiffAdp);
            tv_popup_rfid_grn_transfer_barcode_trfno.requestFocus();

            tv_popup_grn_rfid_transfer_total_rfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfTrfQtyRfid()));
            tv_popup_grn_rfid_transfer_total_nonrfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfTrfQtyNonRfid()));
            tv_popup_grn_rfid_transfer_scan_rfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfScanQtyRfid()));
            tv_popup_grn_rfid_transfer_scan_nonrfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfScanQtyNonRfid()));
            tv_popup_grn_rfid_transfer_diff_rfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfDiffQtyRfid()));
            tv_popup_grn_rfid_transfer_diff_nonrfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfDiffQtyNonRfid()));

            diff=false;
            if(ch_grn_transfer_rfid_view_showpending.isChecked()) diff=true;
            listGrnTransferRfidScanItems.clear();
            listGrnTransferRfidScanItems = objGrnTransferRfidControl.loadScanRfidDetails(diff);
            objMyGrnRfidTransferScanItemsAdp = new MyGrnRfidTransferScanItemsAdp(listGrnTransferRfidScanItems);
            lv_grn_transfer_rfid_details.setAdapter(objMyGrnRfidTransferScanItemsAdp);

            return true;
        } catch (Exception e) {
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

    void openPopupScanWindow(String scanType) {
        myDialogRfidScan = new Dialog(getContext());
        myDialogRfidScan.setCancelable(false);
        myDialogRfidScan.setContentView(R.layout.popup_grn_rfid_transfer_scan_details);

        tv_popup_grn_rfid_transfer_scantime = (TextView) myDialogRfidScan.findViewById(R.id.tv_popup_grn_rfid_transfer_scantime);
        tv_popup_grn_rfid_transfer_epc_count = (TextView) myDialogRfidScan.findViewById(R.id.tv_popup_grn_rfid_transfer_epc_count);
        tv_popup_grn_rfid_transfer_total = (TextView) myDialogRfidScan.findViewById(R.id.tv_popup_grn_rfid_transfer_total);
        rb_popup_grn_rfid_transfer_single = (RadioButton) myDialogRfidScan.findViewById(R.id.rb_popup_grn_rfid_transfer_single);
        rb_popup_grn_rfid_transfer_auto = (RadioButton) myDialogRfidScan.findViewById(R.id.rb_popup_grn_rfid_transfer_auto);
        bt_popup_grn_rfid_transfer_connect = (Button) myDialogRfidScan.findViewById(R.id.bt_popup_grn_rfid_transfer_connect);
        bt_popup_grn_rfid_transfer_start = (Button) myDialogRfidScan.findViewById(R.id.bt_popup_grn_rfid_transfer_start);
        bt_popup_grn_rfid_transfer_options = (Button) myDialogRfidScan.findViewById(R.id.bt_popup_grn_rfid_transfer_options);
        lv_popup_grn_rfid_transfer_rfids = (ListView) myDialogRfidScan.findViewById(R.id.lv_popup_grn_rfid_transfer_rfids);
        bt_popup_grn_rfid_transfer_clear = (Button) myDialogRfidScan.findViewById(R.id.bt_popup_grn_rfid_transfer_clear);
        bt_popup_grn_rfid_transfer_close = (Button) myDialogRfidScan.findViewById(R.id.bt_popup_grn_rfid_transfer_close);

        if(scanType.equals("B")) rb_popup_grn_rfid_transfer_auto.setChecked(true);
        if(scanType.equals("S")) rb_popup_grn_rfid_transfer_single.setChecked(true);

        bt_popup_grn_rfid_transfer_options.setEnabled(false);
        bt_popup_grn_rfid_transfer_start.setEnabled(false);
        rb_popup_grn_rfid_transfer_single.setEnabled(false);
        rb_popup_grn_rfid_transfer_auto.setEnabled(false);

        lv_popup_grn_rfid_transfer_rfids.setAdapter(objMyGrnRfidTransferScanRfidsAdp);
        bt_popup_grn_rfid_transfer_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initUHF() && initSound()) {
                    mReader.setPower(30);
                    bt_popup_grn_rfid_transfer_connect.setEnabled(false);
                    bt_popup_grn_rfid_transfer_options.setEnabled(true);
                    bt_popup_grn_rfid_transfer_start.setEnabled(true);
                    rb_popup_grn_rfid_transfer_single.setEnabled(true);
                    rb_popup_grn_rfid_transfer_auto.setEnabled(true);
                    bt_popup_grn_rfid_transfer_connect.setText("Connected");
                } else {
                    bt_popup_grn_rfid_transfer_connect.setEnabled(true);
                    bt_popup_grn_rfid_transfer_options.setEnabled(false);
                    bt_popup_grn_rfid_transfer_start.setEnabled(false);
                    rb_popup_grn_rfid_transfer_single.setEnabled(false);
                    rb_popup_grn_rfid_transfer_auto.setEnabled(false);
                    bt_popup_grn_rfid_transfer_connect.setText("Connect");
                }
            }
        });
        bt_popup_grn_rfid_transfer_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
                selectItem = -1;
                uhfInfo = new UhfInfo();
            }
        });
        bt_popup_grn_rfid_transfer_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inventoryType = 1;
                if (rb_popup_grn_rfid_transfer_single.isChecked()) inventoryType = 0;
                readTag(bt_popup_grn_rfid_transfer_start.getText().toString(), inventoryType);
            }
        });

        bt_popup_grn_rfid_transfer_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateScanedRfids().execute();
            }
        });

        myDialogRfidScan.show();
    }

    void openPopupTrfDetailWindow(String trfno) {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_grn_rfid_transfer_scan_diff);

        tv_popup_grn_rfid_transfer_diff_trfno = (TextView) myDialog.findViewById(R.id.tv_popup_grn_rfid_transfer_diff_trfno);
        tv_popup_grn_rfid_transfer_total_rfidqty = (TextView) myDialog.findViewById(R.id.tv_popup_grn_rfid_transfer_total_rfidqty);
        tv_popup_grn_rfid_transfer_total_nonrfidqty = (TextView) myDialog.findViewById(R.id.tv_popup_grn_rfid_transfer_total_nonrfidqty);
        tv_popup_grn_rfid_transfer_scan_rfidqty = (TextView) myDialog.findViewById(R.id.tv_popup_grn_rfid_transfer_scan_rfidqty);
        tv_popup_grn_rfid_transfer_scan_nonrfidqty = (TextView) myDialog.findViewById(R.id.tv_popup_grn_rfid_transfer_scan_nonrfidqty);
        tv_popup_grn_rfid_transfer_diff_rfidqty = (TextView) myDialog.findViewById(R.id.tv_popup_grn_rfid_transfer_diff_rfidqty);
        tv_popup_grn_rfid_transfer_diff_nonrfidqty = (TextView) myDialog.findViewById(R.id.tv_popup_grn_rfid_transfer_diff_nonrfidqty);
        bt_popup_grn_rfid_transfer_diff_close = (Button) myDialog.findViewById(R.id.bt_popup_grn_rfid_transfer_diff_close);
        bt_popup_grn_rfid_transfer_diff_trfno_scan_barcode = (Button) myDialog.findViewById(R.id.bt_popup_grn_rfid_transfer_diff_trfno_scan_barcode);
        bt_popup_grn_rfid_transfer_diff_trfno_scan_rfid = (Button) myDialog.findViewById(R.id.bt_popup_grn_rfid_transfer_diff_trfno_scan_rfid);
        ch_popup_grn_transfer_rfid_view_showpending = (CheckBox) myDialog.findViewById(R.id.ch_popup_grn_transfer_rfid_view_showpending);
        lv_popup_grn_rfid_transfer_diff_trfdetail = (ListView) myDialog.findViewById(R.id.lv_popup_grn_rfid_transfer_diff_trfdetail);

        tv_popup_grn_rfid_transfer_diff_trfno.setText(trfno);

        boolean diff = false;
        if (ch_popup_grn_transfer_rfid_view_showpending.isChecked()) diff = true;
        listGrnTransferRfidScanDiffItems.clear();
        listGrnTransferRfidScanDiffItems = objGrnTransferRfidControl.loadScanRfidDetailsDiff(trfno,diff);
        objMyGrnRfidTransferScanDiffAdp = new MyGrnRfidTransferScanDiffAdp(listGrnTransferRfidScanDiffItems);
        lv_popup_grn_rfid_transfer_diff_trfdetail.setAdapter(objMyGrnRfidTransferScanDiffAdp);

        tv_popup_grn_rfid_transfer_total_rfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfTrfQtyRfid()));
        tv_popup_grn_rfid_transfer_total_nonrfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfTrfQtyNonRfid()));
        tv_popup_grn_rfid_transfer_scan_rfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfScanQtyRfid()));
        tv_popup_grn_rfid_transfer_scan_nonrfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfScanQtyNonRfid()));
        tv_popup_grn_rfid_transfer_diff_rfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfDiffQtyRfid()));
        tv_popup_grn_rfid_transfer_diff_nonrfidqty.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfDiffQtyNonRfid()));

        diff = false;
        if (ch_grn_transfer_rfid_view_showpending.isChecked()) diff = true;
        listGrnTransferRfidScanItems.clear();
        listGrnTransferRfidScanItems = objGrnTransferRfidControl.loadScanRfidDetails(diff);
        objMyGrnRfidTransferScanItemsAdp = new MyGrnRfidTransferScanItemsAdp(listGrnTransferRfidScanItems);
        lv_grn_transfer_rfid_details.setAdapter(objMyGrnRfidTransferScanItemsAdp);

        bt_popup_grn_rfid_transfer_diff_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        bt_popup_grn_rfid_transfer_diff_trfno_scan_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupScanWindowBarcode(trfno);
            }
        });

        bt_popup_grn_rfid_transfer_diff_trfno_scan_rfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupScanWindow("S");
            }
        });

        ch_popup_grn_transfer_rfid_view_showpending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean diff = false;
                if (ch_popup_grn_transfer_rfid_view_showpending.isChecked()) diff = true;
                listGrnTransferRfidScanDiffItems.clear();
                listGrnTransferRfidScanDiffItems = objGrnTransferRfidControl.loadScanRfidDetailsDiff(trfno,diff);
                objMyGrnRfidTransferScanDiffAdp = new MyGrnRfidTransferScanDiffAdp(listGrnTransferRfidScanDiffItems);
                lv_popup_grn_rfid_transfer_diff_trfdetail.setAdapter(objMyGrnRfidTransferScanDiffAdp);
            }
        });

        myDialog.show();
    }

    void openPopupRfidExcessLocateWindow(String epc) {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_grn_rfid_transfer_rfid_excess_rfid_locate);

        tv_popup_grn_rfid_transfer_popup_excess_locate_upc = (TextView) myDialog.findViewById(R.id.tv_popup_grn_rfid_transfer_popup_excess_locate_upc);
        bt_popup_grn_rfid_transfer_popup_excess_start_locate = (Button) myDialog.findViewById(R.id.bt_popup_grn_rfid_transfer_popup_excess_start_locate);
        bt_popup_grn_rfid_transfer_popup_excess_close_locate = (Button) myDialog.findViewById(R.id.bt_popup_grn_rfid_transfer_popup_excess_close_locate);
        llChart = myDialog.findViewById(R.id.llChart);

        tv_popup_grn_rfid_transfer_popup_excess_locate_upc.setText(epc);
        playSoundThread = new PlaySoundThread();
        playSoundThread.start();
        bt_popup_grn_rfid_transfer_popup_excess_start_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (initUHF() && initSound()) {
                        if (bt_popup_grn_rfid_transfer_popup_excess_start_locate.getText().toString().equals("Start")) {
                            bt_popup_grn_rfid_transfer_popup_excess_start_locate.setText("Stop");
                            bt_popup_grn_rfid_transfer_popup_excess_close_locate.setEnabled(false);
                            startLocation(tv_popup_grn_rfid_transfer_popup_excess_locate_upc.getText().toString());
                        } else {
                            bt_popup_grn_rfid_transfer_popup_excess_start_locate.setText("Start");
                            bt_popup_grn_rfid_transfer_popup_excess_close_locate.setEnabled(true);
                            stopLocation();
                        }
                    }
                } catch (Exception e) {
                    okMessage(e.getMessage());
                }
            }
        });

        bt_popup_grn_rfid_transfer_popup_excess_close_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocation();
                playSoundThread.stopPlay();
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }

    private void startLocation(String epc) {
        if (epc.equals("")) {
            UIHelper.ToastMessage(getContext(), "Faild");
            return;
        }
        boolean result = mReader.startLocation(getContext(), epc, IUHF.Bank_EPC, 32, new IUHFLocationCallback() {
            @Override
            public void getLocationValue(int i, boolean b) {
                llChart.setData(i);
                //Log.i(TAG, "value:" + i);
                if (i <= 10) {
                    playSoundThread.play(Integer.MAX_VALUE);
                } else if (i <= 30) {
                    playSoundThread.play(1600);
                } else if (i <= 50) {
                    playSoundThread.play(1100);
                } else if (i <= 70) {
                    playSoundThread.play(600);
                } else if (i <= 90) {
                    playSoundThread.play(100);
                }
            }

        });
        if (!result) {
            UIHelper.ToastMessage(getContext(), "Faild");
        }
    }

    public void stopLocation(){
        mReader.stopLocation();
    }

    private class PlaySoundThread extends Thread {
        private boolean isStop = false;
        int waitTime = Integer.MAX_VALUE;

        @Override
        public void run() {
            while (!isStop) {
                synchronized (objectLock) {
                    try {
                        objectLock.wait(waitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                playSound(1);
            }
        }

        public void play(int waitTime) {
            this.waitTime = waitTime;
            synchronized (objectLock) {
                objectLock.notifyAll();
            }
        }


        public void stopPlay() {
            isStop = true;
            synchronized (objectLock) {
                objectLock.notifyAll();
            }
            interrupt();
        }
    }

    void openPopupTransferVerify() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_grn_rfid_transfer_trfno_verify);

        et_grn_rfid_transfer_verify_transfernumber = (EditText) myDialog.findViewById(R.id.et_grn_rfid_transfer_verify_transfernumber);
        bt_grn_rfid_transfer_verify_load = (Button) myDialog.findViewById(R.id.bt_grn_rfid_transfer_verify_load);
        bt_grn_rfid_transfer_verify_close = (Button) myDialog.findViewById(R.id.bt_grn_rfid_transfer_verify_close);
        bt_grn_rfid_transfer_verify_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        bt_grn_rfid_transfer_verify_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_grn_rfid_transfer_verify_transfernumber.getText().toString().isEmpty()){
                    okMessage("Please enter transfer number / toteid");
                    et_grn_rfid_transfer_verify_transfernumber.requestFocus();
                } else {
                    b_Result=objGrnTransferRfidControl.validateVerifyTransfer(et_grn_rfid_transfer_verify_transfernumber.getText().toString().toUpperCase());
                    if(!b_Result){
                        okMessage(objGlobal.getErrorMessage());
                    } else {
                        openPopupTrfDetailWindow(objGrnTransferRfidGlobal.getVerifyTrfNo());
                        myDialog.dismiss();
                    }
                }
            }
        });

        et_grn_rfid_transfer_verify_transfernumber.requestFocus();

        myDialog.show();
    }

    void openPopupRfidExcessWindow() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_grn_rfid_transfer_rfid_excess);

        bt_popup_grn_rfid_transfer_popup_excess_close = (Button) myDialog.findViewById(R.id.bt_popup_grn_rfid_transfer_popup_excess_close);
        lv_popup_grn_rfid_transfer_popup_excess_list = (ListView) myDialog.findViewById(R.id.lv_popup_grn_rfid_transfer_popup_excess_list);

        listGrnTransferRfidExcessItems.clear();
        listGrnTransferRfidExcessItems = objGrnTransferRfidControl.loadRfidExcess();
        objMyGrnRfidTransferExcessAdp = new MyGrnRfidTransferExcessAdp(listGrnTransferRfidExcessItems);
        lv_popup_grn_rfid_transfer_popup_excess_list.setAdapter(objMyGrnRfidTransferExcessAdp);

        bt_popup_grn_rfid_transfer_popup_excess_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }
    void clearData() {
        tv_popup_grn_rfid_transfer_total.setText("0");
        tv_popup_grn_rfid_transfer_epc_count.setText("0");
        tv_popup_grn_rfid_transfer_scantime.setText("0s");
        total = 0;
        tagList.clear();
        tempDatas.clear();
        objMyGrnRfidTransferScanRfidsAdp.notifyDataSetChanged();
    }
    private void readTag(String scanType, int inventoryType) {
        if (scanType.equals("Start")) {
            switch (inventoryType) {
                case 0:// single
                    time = System.currentTimeMillis();
                    UHFTAGInfo uhftagInfo = mReader.inventorySingleTag();
                    if (uhftagInfo != null) {
                        String tid = uhftagInfo.getTid();
                        String epc = uhftagInfo.getEPC();
                        String user=uhftagInfo.getUser();
                        addDataToList(epc,mergeTidEpc(tid, epc, user), uhftagInfo.getRssi());
                        setTotalTime();
                        playSound(1);
                    } else {
                        UIHelper.ToastMessage(getContext(), "Inventory failure");
                    }
                    break;
                case 1:// loop
                    mReader.setInventoryCallback(new IUHFInventoryCallback() {
                        @Override
                        public void callback(UHFTAGInfo uhftagInfo) {
                            Message msg = handler.obtainMessage();
                            msg.obj = uhftagInfo;
                            msg.what=1;
                            handler.sendMessage(msg);
                            playSound(1);
                        }
                    });
                    if (mReader.startInventoryTag()) {
                        bt_popup_grn_rfid_transfer_start.setText("Stop");
                        loopFlag = true;
                        setEnablePopup(false);
                        time = System.currentTimeMillis();
                        handler.sendEmptyMessageDelayed(2,10);
                    } else {
                        stopInventory();
                        UIHelper.ToastMessage(getContext(), "Open failure");
                    }
                    break;
                default:
                    break;
            }
        } else {// 停止识别
            stopInventory();
            setTotalTime();
        }
    }

    private void stopInventory() {
        if (loopFlag) {
            loopFlag = false;
            setEnablePopup(true);
            if (mReader.stopInventory()) {
                bt_popup_grn_rfid_transfer_start.setText("Start");
            } else {
                UIHelper.ToastMessage(getContext(), "Stop failure");
            }
        }
    }

    void setEnablePopup(boolean val){
        bt_popup_grn_rfid_transfer_clear.setEnabled(val);
        bt_popup_grn_rfid_transfer_close.setEnabled(val);
        bt_popup_grn_rfid_transfer_options.setEnabled(val);
        rb_popup_grn_rfid_transfer_single.setEnabled(val);
        rb_popup_grn_rfid_transfer_auto.setEnabled(val);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                UHFTAGInfo info = (UHFTAGInfo) msg.obj;
                String tid = info.getTid();
                String epc = info.getEPC();
                String user=info.getUser();
                addDataToList(epc,mergeTidEpc(tid, epc,user), info.getRssi());
            }else if (msg.what==2){
                if(loopFlag){
                    handler.sendEmptyMessageDelayed(2,10);
                    setTotalTime();
                }else {
                    handler.removeMessages(2);
                }
            }
        }
    };

    private void setTotalTime() {
        float useTime = (System.currentTimeMillis() - time) / 1000.0F;
        tv_popup_grn_rfid_transfer_scantime.setText(NumberTool.getPointDouble(1, useTime) + "s");
    }

    private void addDataToList(String epc,String epcAndTidUser, String rssi) {
        try {
            if (StringUtils.isNotEmpty(epc)) {
                int index = checkIsExist(epc);
                map = new HashMap<String, String>();
                map.put(TAG_EPC, epc);
                map.put(TAG_EPC_TID, epcAndTidUser);
                map.put(TAG_COUNT, String.valueOf(1));
                map.put(TAG_RSSI, rssi);
                if (index == -1) {
                    tagList.add(map);
                    tempDatas.add(epc);
                    tv_popup_grn_rfid_transfer_epc_count.setText(String.valueOf(objMyGrnRfidTransferScanRfidsAdp.getCount()));
                } else {
                    int tagCount = Integer.parseInt(tagList.get(index).get(TAG_COUNT), 10) + 1;
                    map.put(TAG_COUNT, String.valueOf(tagCount));
                    map.put(TAG_EPC_TID, epcAndTidUser);
                    // epcTidUser.add(epcAndTidUser);
                    tagList.set(index, map);
                }
                tv_popup_grn_rfid_transfer_total.setText(String.valueOf(++total));
                objMyGrnRfidTransferScanRfidsAdp.notifyDataSetChanged();
                //----------
                uhfInfo.setTempDatas(tempDatas);
                uhfInfo.setTagList(tagList);
                uhfInfo.setCount(total);
                uhfInfo.setTagNumber(objMyGrnRfidTransferScanRfidsAdp.getCount());
            }
        } catch (Exception e){
            okMessage(e.getMessage());
        }
    }

    private String mergeTidEpc(String tid, String epc,String user) {
        epcTidUser.add(epc);
        String data=epc;
        if (!TextUtils.isEmpty(tid) && !tid.equals("0000000000000000") && !tid.equals("000000000000000000000000")) {
            epcTidUser.add(tid);
            data+= "\nTID:" + tid ;
        }
        if(user!=null && user.length()>0) {
            epcTidUser.add(user);
            data+="\nUSER:"+user;
        }
        return  data;
    }

    public int checkIsExist(String epc) {
        if (StringUtils.isEmpty(epc)) {
            return -1;
        }
        for(int k=0;k<tempDatas.size();k++){
            if(epc.equals(tempDatas.get(k))){
                return k;
            }
        }
        return -1;
    }

    private boolean initSound() {
        try {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
            soundMap.put(1, soundPool.load(getContext(), R.raw.barcodebeep, 1));
            soundMap.put(2, soundPool.load(getContext(), R.raw.serror, 1));
            am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        } catch (Exception e){
            okMessage(e.getMessage());
            return false;
        }
        return true;
    }

    public void playSound(int id) {
        float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // Returns the maximum volume value of the current AudioManager object
        float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);// Returns the volume value of the current AudioManager object
        volumnRatio = audioCurrentVolume / audioMaxVolume;
        try {
            soundPool.play(soundMap.get(id), volumnRatio, // left channel volume
                    volumnRatio, // right channel volume
                    1, // Priority, 0 is the lowest
                    0, // Number of loops, 0 does not loop, -1 loops forever
                    1 // Playback speed, the value is between 0.5-2.0, 1 is normal speed
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean initUHF() {
        try {
            mReader = RFIDWithUHFUART.getInstance();
        } catch (Exception ex) {
            okMessage(ex.getMessage());
            return false;
        }
        if (mReader != null) {
            new InitTask().execute();
        }
        return true;
    }

    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mypDialog.cancel();
            if (!result) {
                Toast.makeText(getContext(), "Connecting....", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mypDialog = new ProgressDialog(getContext());
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }
    }

    private class UpdateScanedRfids extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;

        public UpdateScanedRfids() {
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
                b_Result = objGrnTransferRfidControl.saveScannedRfid(tempDatas);
                if (!b_Result) return 0;
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
                et_grn_transfer_rfid_ginno.setText("");
                et_grn_transfer_rfid_ginno.requestFocus();
            } else {
                if (!loadScanTrfRfidItems())
                    okMessage(objGlobal.getErrorMessage());
                else {
                    dialog.dismiss();
                    myDialogRfidScan.dismiss();
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
                myDialogRfidScan.dismiss();
            }
        }
    }

    private class LoadGinVerification extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;

        public LoadGinVerification() {
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
                b_Result = objGrnTransferRfidControl.validateGinVerification(et_grn_transfer_rfid_ginno.getText().toString());
                if (!b_Result) return 0;
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
                et_grn_transfer_rfid_ginno.setText("");
                et_grn_transfer_rfid_ginno.requestFocus();
            } else {
                if (!loadScanTrfRfidItems())
                    okMessage(objGlobal.getErrorMessage());
                else {
                    tv_grn_transfer_rfid_din_rec_date.setText(objGrnTransferRfidGlobal.getGindate());
                    objGrnTransferRfidSharedRef.saveGinNo(et_grn_transfer_rfid_ginno.getText().toString());
                    objGrnTransferRfidSharedRef.saveRecDate(objGrnTransferRfidGlobal.getGindate());
                    //objGrnTransferRfidSharedRef.saveScanner(sp_grn_transfer_rfid_device.getSelectedItem().toString());
                    dialog.dismiss();
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public class MyGrnRfidTransferScanRfidsAdp extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyGrnRfidTransferScanRfidsAdp(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        public int getCount() {
            // TODO Auto-generated method stub
            return tagList.size();
        }
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return tagList.get(arg0);
        }
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.ticket_grn_transfer_rfids_scan, null);
                holder.tvEPCTID = (TextView) convertView.findViewById(R.id.tv_ticket_grn_transfer_rfids_epc);
                holder.tvTagCount = (TextView) convertView.findViewById(R.id.tv_ticket_grn_transfer_rfids_count);
                holder.tvTagRssi = (TextView) convertView.findViewById(R.id.tv_ticket_grn_transfer_rfids_rss);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvEPCTID.setText((String) tagList.get(position).get(TAG_EPC_TID));
            holder.tvTagCount.setText((String) tagList.get(position).get(TAG_COUNT));
            holder.tvTagRssi.setText((String) tagList.get(position).get(TAG_RSSI));

            if (position == selectItem) {
                convertView.setBackgroundColor(getResources().getColor(R.color.colorSkyBlue));
            }
            else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }
        public  void setSelectItem(int select) {
            if(selectItem==select){
                selectItem=-1;
                uhfInfo.setSelectItem("");
                uhfInfo.setSelectIndex(selectItem);
            }else {
                selectItem = select;
                uhfInfo.setSelectItem(tagList.get(select).get(TAG_EPC));
                uhfInfo.setSelectIndex(selectItem);
            }
        }
    }

    public final class ViewHolder {
        public TextView tvEPCTID;
        public TextView tvTagCount;
        public TextView tvTagRssi;
    }

    private class MyGrnRfidTransferScanItemsAdp extends BaseAdapter {
        public ArrayList<GrnTransferRfidScanItems> listGrnTransferRfidScanItems;

        public MyGrnRfidTransferScanItemsAdp(ArrayList<GrnTransferRfidScanItems> listGrnTransferRfidScanItems) {
            this.listGrnTransferRfidScanItems = listGrnTransferRfidScanItems;
        }

        @Override
        public int getCount() {
            return listGrnTransferRfidScanItems.size();
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
            View myView = mInflater.inflate(R.layout.ticket_grn_transfer_rfid_load_detail, null);
            final GrnTransferRfidScanItems s = listGrnTransferRfidScanItems.get(position);

            TextView bt_grn_transfer_rfid_details_trfno = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_trfno);
            bt_grn_transfer_rfid_details_trfno.setText(String.valueOf(s.trfno));

            TextView bt_grn_transfer_rfid_details_total_rfid = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_total_rfid);
            bt_grn_transfer_rfid_details_total_rfid.setText(String.valueOf(s.totalRfidQty));

            TextView bt_grn_transfer_rfid_details_total_nonrfid = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_total_nonrfid);
            bt_grn_transfer_rfid_details_total_nonrfid.setText(String.valueOf(s.totalNonRfidQty));

            TextView bt_grn_transfer_rfid_details_scan_rfid = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_scan_rfid);
            bt_grn_transfer_rfid_details_scan_rfid.setText(String.valueOf(s.scanRfidQty));

            TextView bt_grn_transfer_rfid_details_scan_nonrfid = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_scan_nonrfid);
            bt_grn_transfer_rfid_details_scan_nonrfid.setText(String.valueOf(s.scanNonRfidQty));

            TextView bt_grn_transfer_rfid_details_diff_rfid = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_diff_rfid);
            bt_grn_transfer_rfid_details_diff_rfid.setText(String.valueOf(s.diffRfidQty));

            TextView bt_grn_transfer_rfid_details_diff_nonrfid = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_diff_nonrfid);
            bt_grn_transfer_rfid_details_diff_nonrfid.setText(String.valueOf(s.diffNonRfidQty));

            Button bt_grn_transfer_rfid_details_load = (Button) myView.findViewById(R.id.bt_grn_transfer_rfid_details_load);
            bt_grn_transfer_rfid_details_load.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPopupTrfDetailWindow(s.trfno);
                }
            });

            /*if (s.diffqty != 0) {
                tv_grn_transfer_new_itemcode.setTextColor(Color.RED);
                tv_grn_transfer_new_scan_qty.setTextColor(Color.RED);
                tv_grn_transfer_new_trf_qty.setTextColor(Color.RED);
                tv_grn_transfer_new_diff_qty.setTextColor(Color.RED);
            } else {
                tv_grn_transfer_new_scan_qty.setTextColor(Color.rgb(41,102,0));
                tv_grn_transfer_new_itemcode.setTextColor(Color.rgb(41,102,0));
                tv_grn_transfer_new_trf_qty.setTextColor(Color.rgb(41,102,0));
                tv_grn_transfer_new_diff_qty.setTextColor(Color.rgb(41,102,0));
                //tv_grn_transfer_new_itemcode.setPaintFlags(tv_grn_transfer_new_diff_qty.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            }*/

            return myView;
        }
    }

    private class MyGrnRfidTransferExcessAdp extends BaseAdapter {
        public ArrayList<GrnTransferRfidExcessItems> listGrnTransferRfidExcessItems;

        public MyGrnRfidTransferExcessAdp(ArrayList<GrnTransferRfidExcessItems> listGrnTransferRfidExcessItems) {
            this.listGrnTransferRfidExcessItems = listGrnTransferRfidExcessItems;
        }

        @Override
        public int getCount() {
            return listGrnTransferRfidExcessItems.size();
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
            View myView = mInflater.inflate(R.layout.ticket_grn_transfer_rfid_excess_detail, null);
            final GrnTransferRfidExcessItems s = listGrnTransferRfidExcessItems.get(position);

            TextView tv_grn_transfer_rfid_excess_rfid = (TextView) myView.findViewById(R.id.tv_grn_transfer_rfid_excess_rfid);
            tv_grn_transfer_rfid_excess_rfid.setText(String.valueOf(s.rfid));

            Button bt_grn_transfer_rfid_excess = (Button) myView.findViewById(R.id.bt_grn_transfer_rfid_excess);

            bt_grn_transfer_rfid_excess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPopupRfidExcessLocateWindow(s.rfid);
                }
            });

            return myView;
        }
    }

    private class MyGrnRfidTransferScanDiffAdp extends BaseAdapter {
        public ArrayList<GrnTransferRfidScanDiffItems> listGrnTransferRfidScanDiffItems;

        public MyGrnRfidTransferScanDiffAdp(ArrayList<GrnTransferRfidScanDiffItems> listGrnTransferRfidScanDiffItems) {
            this.listGrnTransferRfidScanDiffItems = listGrnTransferRfidScanDiffItems;
        }

        @Override
        public int getCount() {
            return listGrnTransferRfidScanDiffItems.size();
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
            View myView = mInflater.inflate(R.layout.ticket_grn_transfer_rfids_scan_diff_details, null);
            final GrnTransferRfidScanDiffItems s = listGrnTransferRfidScanDiffItems.get(position);

            TextView bt_grn_transfer_rfid_details_scan_itemcode = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_scan_itemcode);
            bt_grn_transfer_rfid_details_scan_itemcode.setText(String.valueOf(s.itemcode));

            TextView bt_grn_transfer_rfid_details_scan_rfid = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_scan_rfid);
            bt_grn_transfer_rfid_details_scan_rfid.setText(String.valueOf(s.scanQtyRfid));

            TextView bt_grn_transfer_rfid_details_scan_nonrfid = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_scan_nonrfid);
            bt_grn_transfer_rfid_details_scan_nonrfid.setText(String.valueOf(s.scanQtyNonRfid));

            TextView bt_grn_transfer_rfid_details_diff_rfid = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_diff_rfid);
            bt_grn_transfer_rfid_details_diff_rfid.setText(String.valueOf(s.diffQtyRfid));

            TextView bt_grn_transfer_rfid_details_diff_nonrfid = (TextView) myView.findViewById(R.id.bt_grn_transfer_rfid_details_diff_nonrfid);
            bt_grn_transfer_rfid_details_diff_nonrfid.setText(String.valueOf(s.diffQtyNonRfid));

            Button bt_grn_transfer_rfid_details_load = (Button) myView.findViewById(R.id.bt_grn_transfer_rfid_details_load);
            bt_grn_transfer_rfid_details_load.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return myView;
        }
    }

    boolean loadScanTrfRfidItems() {
        try {
            b_Result = objGrnTransferRfidControl.findRfidScanTotal();
            if (!b_Result) return false;
            b_Result = setEnable(false);
            if (!b_Result) return false;

            tv_grn_transfer_rfid_total_totes.setText(String.valueOf(objGrnTransferRfidGlobal.getTotTotes()));
            tv_grn_transfer_rfid_total_quantity.setText(String.valueOf(objGrnTransferRfidGlobal.getTotTrfQty()));
            tv_grn_transfer_rfid_total_rfid_system.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfQtyRfid()));
            tv_grn_transfer_rfid_total_nonrfid_system.setText(String.valueOf(objGrnTransferRfidGlobal.getTrfQtyNonRfid()));
            tv_grn_transfer_rfid_total_rfid_scan.setText(String.valueOf(objGrnTransferRfidGlobal.getScanQtyRfid()));
            tv_grn_transfer_rfid_total_nonrfid_scan.setText(String.valueOf(objGrnTransferRfidGlobal.getScanQtyNonRfid()));
            tv_grn_transfer_rfid_total_rfid_diff.setText(String.valueOf(objGrnTransferRfidGlobal.getDiffQtyRfid()));
            tv_grn_transfer_rfid_total_nonrfid_diff.setText(String.valueOf(objGrnTransferRfidGlobal.getDiffQtyNonRfid()));

            listGrnTransferRfidScanItems.clear();
            boolean diff=false;
            if(ch_grn_transfer_rfid_view_showpending.isChecked()) diff=true;
            listGrnTransferRfidScanItems = objGrnTransferRfidControl.loadScanRfidDetails(diff);
            if(listGrnTransferRfidScanItems==null) return false;
            objMyGrnRfidTransferScanItemsAdp = new MyGrnRfidTransferScanItemsAdp(listGrnTransferRfidScanItems);
            lv_grn_transfer_rfid_details.setAdapter(objMyGrnRfidTransferScanItemsAdp);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.getMessage());
            return false;
        }
    }
    boolean setEnable(boolean val) {
        bt_grn_transfer_rfid_load_gin.setEnabled(val);
        et_grn_transfer_rfid_ginno.setEnabled(val);
        bt_grn_transfer_rfid_add.setEnabled(!val);
        bt_grn_transfer_rfid_view_excess.setEnabled(!val);
        bt_grn_transfer_rfid_view_verify_totes.setEnabled(!val);
        ch_grn_transfer_rfid_view_showpending.setEnabled(!val);
        bt_grn_transfer_rfid_clear_all.setEnabled(!val);
        bt_grn_transfer_rfid_save.setEnabled(!val);
        return true;
    }

    boolean clearAll() {
        b_Result = objGrnTransferRfidControl.deleteAll();
        if (!b_Result) return false;
        et_grn_transfer_rfid_ginno.setText("");
        tv_grn_transfer_rfid_din_rec_date.setText("");
        objGrnTransferRfidSharedRef.saveGinNo("");
        objGrnTransferRfidSharedRef.saveRecDate("");
        objGrnTransferRfidSharedRef.saveScanner("");
        tv_grn_transfer_rfid_total_totes.setText("");
        tv_grn_transfer_rfid_total_nonrfid_system.setText("");
        tv_grn_transfer_rfid_total_quantity.setText("");
        tv_grn_transfer_rfid_total_rfid_system.setText("");
        tv_grn_transfer_rfid_total_rfid_scan.setText("");
        tv_grn_transfer_rfid_total_nonrfid_scan.setText("");
        tv_grn_transfer_rfid_total_rfid_diff.setText("");
        tv_grn_transfer_rfid_total_nonrfid_diff.setText("");
        boolean diff=false;
        if(ch_grn_transfer_rfid_view_showpending.isChecked()) diff=true;
        listGrnTransferRfidScanItems.clear();
        listGrnTransferRfidScanItems = objGrnTransferRfidControl.loadScanRfidDetails(diff);
        if(listGrnTransferRfidScanItems==null) return false;
        objMyGrnRfidTransferScanItemsAdp = new MyGrnRfidTransferScanItemsAdp(listGrnTransferRfidScanItems);
        lv_grn_transfer_rfid_details.setAdapter(objMyGrnRfidTransferScanItemsAdp);
        return setEnable(true);
    }

    void vibrate(int duration){
        Vibrator v = (Vibrator) getContext().getSystemService(Context. VIBRATOR_SERVICE );
        assert v != null;
        if (Build.VERSION. SDK_INT >= Build.VERSION_CODES. O ) {
            v.vibrate(VibrationEffect. createOneShot (duration , VibrationEffect. DEFAULT_AMPLITUDE )) ;
        } else {
            v.vibrate( duration ) ;
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

    private void okMessage(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(msgHead);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
        vibrate(500);
    }

}