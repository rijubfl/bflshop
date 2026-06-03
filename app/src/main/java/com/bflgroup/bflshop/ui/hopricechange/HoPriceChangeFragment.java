package com.bflgroup.bflshop.ui.hopricechange;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.BarcodePrinting;
import com.bflgroup.bflshop.comm.BluetoothDevices;
import com.bflgroup.bflshop.comm.Controls;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.ui.ageingslashing.AgeingSlashingGlobal;
import com.bflgroup.bflshop.ui.ageingslashing.AgeingSlashingSharedRef;
import com.bflgroup.bflshop.ui.ageingslashing.model.AgeingSlashingItemsScan;
import com.bflgroup.bflshop.ui.ageingslashing.model.AgeingSlashingScanDetailsGlobal;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sewoo.jpos.command.ZPLConst;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class HoPriceChangeFragment extends Fragment {

    private TextView tv_ageing_slashing_user;
    private TextView tv_ageing_slashing_shop;
    private TextView tv_ageing_slashing_battery_percentage;
    private TextView tv_ageing_slashing_batchno;
    private TextView tv_ageing_slashing_date;
    private Button bt_ageing_slashing_add;
    private Button bt_ageing_slashing_delete;
    private Button bt_ageing_slashing_clear;
    private ListView lv_ageing_slashing;
    private TextView tv_ageing_slashing_total_scan;
    private TextView tv_ageing_slashing_total_exported;
    private Spinner sp_ageing_printer_yellow;
    private Spinner sp_ageing_printer_red;
    private Spinner sp_ageing_printer_name;

    private EditText et_aging_slash_popup_scan_barcode;
    private TextView tv_aging_slash_popup_scan_barcode;
    private Button bt_aging_slash_popup_scan_add;
    private Button bt_aging_slash_popup_scan_close;
    private TextView tv_aging_slash_popup_scan_el_qty;
    private TextView tv_aging_slash_popup_scan_sc_qty;
    private TextView tv_aging_slash_popup_scan_sl_qty;
    private TextView tv_aging_slash_popup_scan_itemcode;
    private TextView tv_aging_slash_popup_scan_results;
    private TextView tv_aging_slash_popup_scan_trfno;
    private TextView tv_aging_slash_popup_scan_itemname;
    private TextView tv_aging_slash_popup_scan_department;
    private TextView tv_aging_slash_popup_scan_currprice;
    private TextView tv_aging_slash_popup_scan_newprice;
    private TextView tv_aging_slash_popup_scan_labeltype;
    private TextView tv_aging_slash_popup_scan_saveperc;
    private TextView tv_aging_slash_popup_scan_slashtype;
    private TextView tv_aging_slash_popup_scan_new_barcode;

    private TextView tv_aging_slash_popup_sticker_comp_name;
    private TextView tv_aging_slash_popup_sticker_prod_name;
    private ImageView img_aging_slash_popup_sticker_barcode;
    private TextView tv_aging_slash_popup_sticker_itemcode;
    private TextView tv_aging_slash_popup_sticker_trfno;
    private TextView tv_aging_slash_popup_sticker_was_price;
    private TextView tv_aging_slash_popup_sticker_now_price;
    private TextView tv_aging_slash_popup_sticker_uid;
    private TextView tv_aging_slash_popup_sticker_mark;
    private Button bt_aging_slash_popup_scan_test_yellow;
    private Button bt_aging_slash_popup_scan_test_red;
    private ConstraintLayout lyt_aging_slash_popup_label;
    private ConstraintLayout lyt_aging_slash_popup_label_color;

    private Button bt_aging_slash_popup_reprint_excess_close;
    private Button bt_aging_slash_popup_reprint_excess_reprint;
    private Button bt_aging_slash_popup_reprint_excess_excess;

    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private Controls objControls = new Controls();
    private HoPriceChangeControl objAgeingSlashingWifyControl = new HoPriceChangeControl();
    private AgeingSlashingGlobal objAgeingSlashingGlobal = AgeingSlashingGlobal.getInstance();
    private AgeingSlashingScanDetailsGlobal objAgeingSlashingScanDetailsGlobal = AgeingSlashingScanDetailsGlobal.getInstance();
    private BluetoothDevices objBluetoothDevices = new BluetoothDevices();

    private ArrayList<AgeingSlashingItemsScan> listAgeingSlashingItemsScan = new ArrayList<AgeingSlashingItemsScan>();
    MyAgeingSlashingScanItemAdp objMyAgeingSlashingScanItemAdp;
    AgeingSlashingSharedRef objAgeingSlashingSharedRef;

    DecimalFormat formatter = new DecimalFormat("###,###");
    DecimalFormat formatterDecimal = new DecimalFormat("###,###.00");
    private boolean b_Result;

    private static final int REQUEST_ENABLE_BT = 2;
    BarcodePrinting objSample_Print;
    private BluetoothPort bluetoothPort;
    private BroadcastReceiver connectDevice;
    private Thread btThread;
    private BluetoothAdapter mBluetoothAdapter;

    private BroadcastReceiver discoveryResult;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver searchFinish;
    boolean searchflags;
    String lastSelectStkType="";
    private boolean testPrint=false;

    private Vector<BluetoothDevice> remoteDevices;
    ArrayAdapter<String> adapter;

    public HoPriceChangeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ho_price_change, container, false);

        tv_ageing_slashing_user = (TextView) view.findViewById(R.id.tv_ageing_slashing_user);
        tv_ageing_slashing_shop = (TextView) view.findViewById(R.id.tv_ageing_slashing_shop);
        tv_ageing_slashing_battery_percentage = (TextView) view.findViewById(R.id.tv_ageing_slashing_battery_percentage);
        tv_ageing_slashing_batchno = (TextView) view.findViewById(R.id.tv_ageing_slashing_batchno);
        tv_ageing_slashing_date = (TextView) view.findViewById(R.id.tv_ageing_slashing_date);
        bt_ageing_slashing_add = (Button) view.findViewById(R.id.bt_ageing_slashing_add);
        bt_ageing_slashing_delete = (Button) view.findViewById(R.id.bt_ageing_slashing_delete);
        bt_ageing_slashing_clear = (Button) view.findViewById(R.id.bt_ageing_slashing_clear);
        lv_ageing_slashing = (ListView) view.findViewById(R.id.lv_ageing_slashing);
        tv_ageing_slashing_total_scan = (TextView) view.findViewById(R.id.tv_ageing_slashing_total_scan);
        tv_ageing_slashing_total_exported = (TextView) view.findViewById(R.id.tv_ageing_slashing_total_exported);
        sp_ageing_printer_yellow = (Spinner) view.findViewById(R.id.sp_ageing_printer_yellow);
        sp_ageing_printer_red = (Spinner) view.findViewById(R.id.sp_ageing_printer_red);
        sp_ageing_printer_name = (Spinner) view.findViewById(R.id.sp_ageing_printer_name);

        tv_ageing_slashing_shop.setText(objPosGlobal.getShopName());
        tv_ageing_slashing_user.setText(objGlobal.getUserName());
        tv_ageing_slashing_date.setText(objGlobal.getServerDate());
        objAgeingSlashingSharedRef=new AgeingSlashingSharedRef(getContext());

        searchflags = false;
        b_Result = objAgeingSlashingWifyControl.getLatestBatchNo();
        if (!b_Result) {
            tv_ageing_slashing_batchno.setText("");
            okMessage("Price Change", objGlobal.getErrorMessage());
        } else {
            tv_ageing_slashing_batchno.setText(objAgeingSlashingGlobal.getBatchno());
        }

        List<String> arr;
        arr = new ArrayList<String>();
        arr.add("Honeywell");
        arr.add("Zebra");
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_ageing_printer_name.setAdapter(arrayAdp);

        if (objAgeingSlashingSharedRef.loadPrinter() != "") {
            sp_ageing_printer_name.setSelection(arrayAdp.getPosition(objAgeingSlashingSharedRef.loadPrinter()));
            sp_ageing_printer_name.setEnabled(false);
        }

        b_Result = objBluetoothDevices.loadBluetoothDevicesArray();
        if (!b_Result) {
            okMessage("Price Change",objGlobal.getErrorMessage());
        } else {
            ArrayAdapter<String> arrayAdpYellow;
            arrayAdpYellow = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevicesYellow());
            sp_ageing_printer_yellow.setAdapter(arrayAdpYellow);

            ArrayAdapter<String> arrayAdpRed;
            arrayAdpRed = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevicesRed());
            sp_ageing_printer_red.setAdapter(arrayAdpRed);
            if (objAgeingSlashingSharedRef.loadPrinterYellow() != "") {
                sp_ageing_printer_yellow.setSelection(arrayAdpYellow.getPosition(objAgeingSlashingSharedRef.loadPrinterYellow()));
                sp_ageing_printer_yellow.setEnabled(false);
            }
            if (objAgeingSlashingSharedRef.loadPrinterRed() != "") {
                sp_ageing_printer_red.setSelection(arrayAdpRed.getPosition(objAgeingSlashingSharedRef.loadPrinterRed()));
                sp_ageing_printer_red.setEnabled(false);
            }
        }
        bt_ageing_slashing_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String batchNo = tv_ageing_slashing_batchno.getText().toString();
                if(batchNo.isEmpty()) {
                    okMessage("Price Change", "Please enter batch number");
                } else if (sp_ageing_printer_yellow.getSelectedItem().toString().equals("--Select--") || sp_ageing_printer_red.getSelectedItem().toString().equals("--Select--") || sp_ageing_printer_yellow.getSelectedItem().toString().isEmpty() || sp_ageing_printer_red.getSelectedItem().toString().isEmpty()) {
                    okMessage("Price Change", "Please select the printer");
                } else {
                    objAgeingSlashingSharedRef.savePrinter(sp_ageing_printer_name.getSelectedItem().toString());
                    objAgeingSlashingSharedRef.savePrinterRed(sp_ageing_printer_red.getSelectedItem().toString());
                    objAgeingSlashingSharedRef.savePrinterYellow(sp_ageing_printer_yellow.getSelectedItem().toString());
                    sp_ageing_printer_name.setEnabled(false);
                    sp_ageing_printer_red.setEnabled(false);
                    sp_ageing_printer_yellow.setEnabled(false);
                    openPopupScan();
                }
            }
        });

        bt_ageing_slashing_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_ageing_slashing_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });

        loadLastScanedItems();
        objSample_Print = new BarcodePrinting();
        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);
        Init_BluetoothSet();
        return view;
    }

     private boolean printBarCode() {
         String printType = sp_ageing_printer_name.getSelectedItem().toString();
         try {
             Bitmap label=null;
             if(!testPrint) label = getBarcodeLabel();
             if (printType.equals("Honeywell")) {
                 byte[] printData = null;
                 if (testPrint) {
                     printData = objSample_Print.getLabelWasNowHoneyWellTestPrint(objAgeingSlashingScanDetailsGlobal.getLabelType());
                 } else {
                     if (objPosGlobal.getPrintWasNowPerc().equals("Y")) {
                         printData = objSample_Print.getSlashingBarcodeWasNowPerc(tv_aging_slash_popup_sticker_comp_name.getText().toString(), tv_aging_slash_popup_sticker_trfno.getText().toString(),
                                 tv_aging_slash_popup_sticker_itemcode.getText().toString(), tv_aging_slash_popup_sticker_prod_name.getText().toString(), tv_aging_slash_popup_scan_new_barcode.getText().toString(),
                                 tv_aging_slash_popup_sticker_trfno.getText().toString(), tv_aging_slash_popup_sticker_was_price.getText().toString(), tv_aging_slash_popup_sticker_now_price.getText().toString(),
                                 tv_aging_slash_popup_sticker_mark.getText().toString(), tv_aging_slash_popup_sticker_uid.getText().toString(), "1", tv_aging_slash_popup_scan_saveperc.getText().toString(),
                                 objPosGlobal.getPrintWasHead(), objPosGlobal.getPrintNowHead(), "");
                     } else if (objPosGlobal.getPrintWasNow().equals("Y")) {
                         printData = objSample_Print.getSlashingBarcodeWasNow(tv_aging_slash_popup_sticker_comp_name.getText().toString(), tv_aging_slash_popup_sticker_trfno.getText().toString(),
                                 tv_aging_slash_popup_sticker_itemcode.getText().toString(), tv_aging_slash_popup_sticker_prod_name.getText().toString(), tv_aging_slash_popup_scan_new_barcode.getText().toString(),
                                 tv_aging_slash_popup_sticker_trfno.getText().toString(), tv_aging_slash_popup_sticker_was_price.getText().toString(), tv_aging_slash_popup_sticker_now_price.getText().toString(),
                                 tv_aging_slash_popup_sticker_mark.getText().toString(), tv_aging_slash_popup_sticker_uid.getText().toString(), "1", objPosGlobal.getPrintWasHead(),
                                 objPosGlobal.getPrintNowHead(), "");
                     } else {
                         printData = objSample_Print.getSlashingBarcode(tv_aging_slash_popup_sticker_comp_name.getText().toString(), tv_aging_slash_popup_sticker_trfno.getText().toString(),
                                 tv_aging_slash_popup_sticker_itemcode.getText().toString(), tv_aging_slash_popup_sticker_prod_name.getText().toString(), tv_aging_slash_popup_scan_new_barcode.getText().toString(),
                                 tv_aging_slash_popup_sticker_trfno.getText().toString(), tv_aging_slash_popup_sticker_was_price.getText().toString(), tv_aging_slash_popup_sticker_now_price.getText().toString(),
                                 tv_aging_slash_popup_sticker_mark.getText().toString(), tv_aging_slash_popup_sticker_uid.getText().toString(), "1", "");
                     }
                 }
                 return objSample_Print.PrintBarcodeByte(printData);
             }
             if (printType.equals("Zebra")) {
                 if (label != null) {
                     return objSample_Print.PrintBarcodeImage(1, ZPLConst.SENSE_GAP, label);
                 } else {
                     okMessage("Error 2", "label error");
                     return false;
                 }
             }
             okMessage("Error 3", "Printer type note set");
             return false;
         } catch (Exception e) {
             okMessage("Error 3", e.toString());
             return false;
         }
     }

    private Bitmap getBarcodeLabel() {
        createBarcode(objAgeingSlashingScanDetailsGlobal.getNewBarcode(), img_aging_slash_popup_sticker_barcode);
        Bitmap bm = getBitMapFromView(lyt_aging_slash_popup_label);
        if (bm != null) {
            return bm;
        } else {
            return null;
        }
    }

    public static Bitmap getBitMapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    private void btConn(final BluetoothDevice btDev) throws IOException {
        new connBT().execute(btDev);
    }

    class connBT extends AsyncTask<BluetoothDevice, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
        String str_temp = "";

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Connecting Device...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {
            Integer retVal = null;
            try {
                bluetoothPort.connect(params[0]);
                str_temp = params[0].getAddress();
                retVal = Integer.valueOf(0);
            } catch (IOException e) {
                e.printStackTrace();
                retVal = Integer.valueOf(-1);
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (dialog.isShowing())
                dialog.dismiss();
            if (result.intValue() == 0) {
                RequestHandler rh = new RequestHandler();
                btThread = new Thread(rh);
                btThread.start();
                getActivity().registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                getActivity().registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
                printBarCode();
            } else {
                alert
                        .setTitle("Error 4")
                        .setMessage("Failed to connect Bluetooth device.")
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            super.onPostExecute(result);
        }
    }

    public void Init_BluetoothSet() {
        bluetoothSetup();
        connectDevice = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    //"BlueTooth Connect"
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    try {
                        if (bluetoothPort.isConnected())
                            bluetoothPort.disconnect();
                    } catch (IOException e) {
                        okMessage("IOException",e.toString());
                    } catch (InterruptedException e) {
                        okMessage("InterruptedException",e.toString());
                    }
                    if ((btThread != null) && (btThread.isAlive())) {
                        btThread.interrupt();
                        btThread = null;
                    }
                }
            }
        };

        discoveryResult = new BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @Override
            public void onReceive(Context context, Intent intent) {
                String key;
                boolean bFlag = true;
                BluetoothDevice btDev;
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (remoteDevice != null) {
                    if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "]";
                    } else {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "] [Paired]";
                    }
                    if (bluetoothPort.isValidAddress(remoteDevice.getAddress())) {
                        for (int i = 0; i < remoteDevices.size(); i++) {
                            btDev = remoteDevices.elementAt(i);
                            if (remoteDevice.getAddress().equals(btDev.getAddress())) {
                                bFlag = false;
                                break;
                            }
                        }
                        if (bFlag) {
                            remoteDevices.add(remoteDevice);
                            adapter.add(key);
                        }
                    }
                }
            }
        };
        getActivity().registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        searchStart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };
        getActivity().registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        searchFinish = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                searchflags = true;
            }
        };
        getActivity().registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    private void bluetoothSetup() {
        clearBtDevData();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void clearBtDevData() {
        remoteDevices = new Vector<BluetoothDevice>();
    }

    private void openPopupScan() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_ageing_slashing_item_scan);

        et_aging_slash_popup_scan_barcode = (EditText) myDialog.findViewById(R.id.et_aging_slash_popup_scan_barcode);
        tv_aging_slash_popup_scan_barcode = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_barcode);
        bt_aging_slash_popup_scan_add = (Button) myDialog.findViewById(R.id.bt_aging_slash_popup_scan_add);
        bt_aging_slash_popup_scan_close = (Button) myDialog.findViewById(R.id.bt_aging_slash_popup_scan_close);
        tv_aging_slash_popup_scan_itemcode = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_itemcode);
        tv_aging_slash_popup_scan_el_qty = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_el_qty);
        tv_aging_slash_popup_scan_sc_qty = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_sc_qty);
        tv_aging_slash_popup_scan_sl_qty = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_sl_qty);
        tv_aging_slash_popup_scan_results = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_results);
        tv_aging_slash_popup_scan_trfno = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_trfno);
        tv_aging_slash_popup_scan_itemname = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_itemname);
        tv_aging_slash_popup_scan_department = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_department);
        tv_aging_slash_popup_scan_currprice = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_currprice);
        tv_aging_slash_popup_scan_newprice = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_newprice);
        tv_aging_slash_popup_scan_labeltype = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_labeltype);
        tv_aging_slash_popup_scan_saveperc = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_saveperc);
        tv_aging_slash_popup_scan_slashtype = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_slashtype);
        tv_aging_slash_popup_scan_new_barcode = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_new_barcode);

        tv_aging_slash_popup_sticker_comp_name = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_sticker_comp_name);
        tv_aging_slash_popup_sticker_prod_name = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_sticker_prod_name);
        img_aging_slash_popup_sticker_barcode = (ImageView) myDialog.findViewById(R.id.img_aging_slash_popup_sticker_barcode);
        tv_aging_slash_popup_sticker_itemcode = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_sticker_itemcode);
        tv_aging_slash_popup_sticker_trfno = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_sticker_trfno);
        tv_aging_slash_popup_sticker_was_price = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_sticker_was_price);
        tv_aging_slash_popup_sticker_now_price = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_sticker_now_price);
        tv_aging_slash_popup_sticker_uid = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_sticker_uid);
        tv_aging_slash_popup_sticker_mark = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_sticker_mark);
        lyt_aging_slash_popup_label = (ConstraintLayout) myDialog.findViewById(R.id.lyt_aging_slash_popup_label);
        lyt_aging_slash_popup_label_color = (ConstraintLayout) myDialog.findViewById(R.id.lyt_aging_slash_popup_label_color);
        bt_aging_slash_popup_scan_test_yellow = (Button) myDialog.findViewById(R.id.bt_aging_slash_popup_scan_test_yellow);
        bt_aging_slash_popup_scan_test_red = (Button) myDialog.findViewById(R.id.bt_aging_slash_popup_scan_test_red);

        et_aging_slash_popup_scan_barcode.setOnTouchListener(new View.OnTouchListener() {
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

        et_aging_slash_popup_scan_barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    scanBarcode();
                }
                return false;
            }
        });

        bt_aging_slash_popup_scan_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });

        bt_aging_slash_popup_scan_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (bluetoothPort.isConnected())
                        bluetoothPort.disconnect();
                } catch (Exception e) {
                    okMessage("Error 1", e.toString());
                }
                myDialog.dismiss();
            }
        });

        bt_aging_slash_popup_scan_test_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    testPrint = true;
                    objAgeingSlashingScanDetailsGlobal.setLabelType("YELLOW");
                    if (!printSticker(sp_ageing_printer_yellow.getSelectedItem().toString())) {
                        tv_aging_slash_popup_scan_results.setText("PRINT ERROR");
                        vibrate(100);
                    }
                } catch (Exception e) {
                    okMessage("", e.toString());
                }
            }
        });

        bt_aging_slash_popup_scan_test_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    testPrint = true;
                    objAgeingSlashingScanDetailsGlobal.setLabelType("RED");
                    if (!printSticker(sp_ageing_printer_red.getSelectedItem().toString())) {
                        tv_aging_slash_popup_scan_results.setText("PRINT ERROR");
                        vibrate(100);
                    }
                } catch (Exception e) {
                    okMessage("", e.toString());
                }
            }
        });

        lyt_aging_slash_popup_label_color.setVisibility(View.INVISIBLE);
        et_aging_slash_popup_scan_barcode.requestFocus();
        myDialog.show();
    }

    private boolean clearAll() {
        tv_ageing_slashing_batchno.setText("");
        b_Result = objAgeingSlashingWifyControl.getLatestBatchNo();
        if (!b_Result) {
            tv_ageing_slashing_batchno.setText("");
            okMessage("Price Change", objGlobal.getErrorMessage());
        } else {
            tv_ageing_slashing_batchno.setText(objAgeingSlashingGlobal.getBatchno());
        }
        b_Result = objBluetoothDevices.loadBluetoothDevicesArray();
        if (!b_Result) {
            okMessage("Price Change",objGlobal.getErrorMessage());
        } else {
            ArrayAdapter<String> arrayAdpYellow;
            arrayAdpYellow = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevicesYellow());
            sp_ageing_printer_yellow.setAdapter(arrayAdpYellow);

            ArrayAdapter<String> arrayAdpRed;
            arrayAdpRed = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevicesRed());
            sp_ageing_printer_red.setAdapter(arrayAdpRed);
            if (objAgeingSlashingSharedRef.loadPrinterYellow() != "") {
                sp_ageing_printer_yellow.setSelection(arrayAdpYellow.getPosition(objAgeingSlashingSharedRef.loadPrinterYellow()));
                sp_ageing_printer_yellow.setEnabled(false);
            }
            if (objAgeingSlashingSharedRef.loadPrinterRed() != "") {
                sp_ageing_printer_red.setSelection(arrayAdpRed.getPosition(objAgeingSlashingSharedRef.loadPrinterRed()));
                sp_ageing_printer_red.setEnabled(false);
            }
        }
        b_Result = objBluetoothDevices.loadBluetoothDevicesArray();
        if (!b_Result) {
            okMessage("Price Change", objGlobal.getErrorMessage());
        } else {
            ArrayAdapter<String> arrayAdpYellow;
            arrayAdpYellow = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevicesYellow());
            sp_ageing_printer_yellow.setAdapter(arrayAdpYellow);

            ArrayAdapter<String> arrayAdpRed;
            arrayAdpRed = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevicesRed());
            sp_ageing_printer_red.setAdapter(arrayAdpRed);
        }
        objAgeingSlashingSharedRef.savePrinter("");
        objAgeingSlashingSharedRef.savePrinterRed("");
        objAgeingSlashingSharedRef.savePrinterYellow("");
        sp_ageing_printer_name.setSelection(0);
        sp_ageing_printer_name.setEnabled(true);
        sp_ageing_printer_yellow.setSelection(0);
        sp_ageing_printer_yellow.setEnabled(true);
        sp_ageing_printer_red.setSelection(0);
        sp_ageing_printer_red.setEnabled(true);
        loadLastScanedItems();
        return true;
    }

    private boolean scanBarcode() {
        tv_aging_slash_popup_scan_itemcode.setText("");
        tv_aging_slash_popup_scan_results.setText("");
        tv_aging_slash_popup_scan_trfno.setText("");
        tv_aging_slash_popup_scan_itemname.setText("");
        tv_aging_slash_popup_scan_department.setText("");
        tv_aging_slash_popup_scan_currprice.setText("");
        tv_aging_slash_popup_scan_newprice.setText("");
        tv_aging_slash_popup_scan_labeltype.setText("");
        tv_aging_slash_popup_scan_saveperc.setText("0");
        tv_aging_slash_popup_scan_slashtype.setText("");
        tv_aging_slash_popup_scan_new_barcode.setText("");
        tv_aging_slash_popup_scan_el_qty.setText("0");
        tv_aging_slash_popup_scan_sc_qty.setText("0");
        tv_aging_slash_popup_scan_sl_qty.setText("0");
        objAgeingSlashingScanDetailsGlobal.setEligibleQty(0);
        objAgeingSlashingScanDetailsGlobal.setScanQty(0);
        objAgeingSlashingScanDetailsGlobal.setSlashQty(0);
        testPrint = false;
        String batchNo = tv_ageing_slashing_batchno.getText().toString();
        if (batchNo.isEmpty()) {
            tv_aging_slash_popup_scan_results.setText("Please enter valid batch number");
            tv_aging_slash_popup_scan_results.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
            vibrate(100);
            return false;
        }
        String scan = objControls.replaceString(et_aging_slash_popup_scan_barcode.getText().toString()).toUpperCase();
        if (scan.isEmpty()) {
            scan = "";
        }
        if (scan.isEmpty()) {
            tv_aging_slash_popup_scan_results.setText("Please scan Barcode");
            tv_aging_slash_popup_scan_results.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
            vibrate(100);
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        tv_aging_slash_popup_scan_barcode.setText(scan);
        b_Result = objAgeingSlashingWifyControl.getAgeingPairBarcodeFromRfid(scan);
        if (!b_Result) {
            tv_aging_slash_popup_scan_results.setText(objGlobal.getErrorMessage());
            tv_aging_slash_popup_scan_results.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
            vibrate(100);
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        scan = objAgeingSlashingScanDetailsGlobal.getRfidScanBarcode();
        if (scan.length() > 30) {
            tv_aging_slash_popup_scan_results.setText("Please Double check the barcode that scan");
            tv_aging_slash_popup_scan_results.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
            vibrate(100);
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        String itemcode = "", trfNo = "";
        float ePrice = 0;
        if (scan.contains("/")) {
            String[] scanAr = scan.split("/");
            itemcode = scanAr[0];
            ePrice = Float.parseFloat(scanAr[1]);
            trfNo = scanAr[2];
        } else {
            itemcode = scan;
            ePrice = 0;
            trfNo = "";
        }
        b_Result = objAgeingSlashingWifyControl.getAgeingItems(batchNo, itemcode);
        if (!b_Result) {
            if (objGlobal.getErrorMessage().contains("Not Aged")) {
                tv_aging_slash_popup_scan_results.setText(objGlobal.getErrorMessage());
                tv_aging_slash_popup_scan_results.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
                vibrate(100);
            } else {
                okMessage("Price Change", objGlobal.getErrorMessage());
            }
            b_Result = objAgeingSlashingWifyControl.saveScanLog(batchNo, scan, itemcode, objGlobal.getErrorMessage());
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        if (trfNo.isEmpty()) {
            objAgeingSlashingScanDetailsGlobal.setTrfNo("");
            objAgeingSlashingScanDetailsGlobal.setTrfDate(null);
            objAgeingSlashingScanDetailsGlobal.setDateDiff(0);
        } else {
            b_Result = objAgeingSlashingWifyControl.validateTrfNo(trfNo);
            if (!b_Result) {
                if (objGlobal.getErrorMessage().contains("No data")) {
                    tv_aging_slash_popup_scan_results.setText(objGlobal.getErrorMessage());
                    tv_aging_slash_popup_scan_results.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
                    vibrate(100);
                } else {
                    okMessage("Price Change", objGlobal.getErrorMessage());
                }
                b_Result = objAgeingSlashingWifyControl.saveScanLog(batchNo, scan, itemcode, objGlobal.getErrorMessage());
                et_aging_slash_popup_scan_barcode.setText("");
                et_aging_slash_popup_scan_barcode.requestFocus();
                return false;
            }
        }
        b_Result = objAgeingSlashingWifyControl.findLastSlno();
        if (!b_Result) {
            okMessage("Price Change", objGlobal.getErrorMessage());
            b_Result = objAgeingSlashingWifyControl.saveScanLog(batchNo, scan, itemcode, objGlobal.getErrorMessage());
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        if (objAgeingSlashingScanDetailsGlobal.getNewTrfNo().isEmpty()) {
            tv_aging_slash_popup_scan_results.setText("NewTrfNo is empty");
            tv_aging_slash_popup_scan_results.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
            vibrate(100);
            b_Result = objAgeingSlashingWifyControl.saveScanLog(batchNo, scan, itemcode, objGlobal.getErrorMessage());
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        lyt_aging_slash_popup_label_color.setVisibility(View.VISIBLE);
        tv_aging_slash_popup_scan_itemcode.setText(itemcode);
        tv_aging_slash_popup_scan_trfno.setText(trfNo);
        tv_aging_slash_popup_scan_el_qty.setText(String.valueOf(objAgeingSlashingScanDetailsGlobal.getEligibleQty()));
        tv_aging_slash_popup_scan_itemname.setText(objAgeingSlashingScanDetailsGlobal.getItemName());
        tv_aging_slash_popup_scan_department.setText(objAgeingSlashingScanDetailsGlobal.getGroupName() + " | " + objAgeingSlashingScanDetailsGlobal.getDepartment() + " | " + objAgeingSlashingScanDetailsGlobal.getDivision());
        tv_aging_slash_popup_scan_currprice.setText(formatterDecimal.format(objAgeingSlashingScanDetailsGlobal.getCurrPrice()));
        tv_aging_slash_popup_scan_newprice.setText(formatterDecimal.format(objAgeingSlashingScanDetailsGlobal.getNewPrice()));
        tv_aging_slash_popup_scan_labeltype.setText(objAgeingSlashingScanDetailsGlobal.getLabelType());
        tv_aging_slash_popup_scan_saveperc.setText(formatter.format(objAgeingSlashingScanDetailsGlobal.getSavePercentage()));
        tv_aging_slash_popup_scan_slashtype.setText(objAgeingSlashingScanDetailsGlobal.getSlashingType());
        objAgeingSlashingScanDetailsGlobal.setNewBarcode(itemcode + "/" + formatter.format(objAgeingSlashingScanDetailsGlobal.getNewPrice()) + "/" + objAgeingSlashingScanDetailsGlobal.getNewTrfNo());
        objAgeingSlashingScanDetailsGlobal.setOldBarcode(scan);
        tv_aging_slash_popup_scan_new_barcode.setText(objAgeingSlashingScanDetailsGlobal.getNewBarcode());
        tv_aging_slash_popup_sticker_comp_name.setText(objPosGlobal.getBarcodePrintHead1());
        tv_aging_slash_popup_sticker_prod_name.setText(objAgeingSlashingScanDetailsGlobal.getItemName());
        tv_aging_slash_popup_sticker_itemcode.setText(itemcode);
        tv_aging_slash_popup_sticker_trfno.setText(objPosGlobal.getBarcodePrintHead2() + "-" + objAgeingSlashingScanDetailsGlobal.getNewTrfNo());
        tv_aging_slash_popup_sticker_was_price.setText(objGlobal.getFcCode() + " " + formatter.format(objAgeingSlashingScanDetailsGlobal.getWasPrice()));
        tv_aging_slash_popup_sticker_now_price.setText(objGlobal.getFcCode() + " " + formatter.format(objAgeingSlashingScanDetailsGlobal.getNewPrice()));
        tv_aging_slash_popup_sticker_uid.setText(String.valueOf(objGlobal.getUserId()));
        tv_aging_slash_popup_sticker_mark.setText(objControls.getMark(objGlobal.getServerDate()));
        et_aging_slash_popup_scan_barcode.setText("");
        et_aging_slash_popup_scan_barcode.requestFocus();
        if (objAgeingSlashingScanDetailsGlobal.getLabelType().equals("YELLOW")) {
            lyt_aging_slash_popup_label_color.setBackgroundColor(getActivity().getResources().getColor(R.color.colorYellow));
        }
        if (objAgeingSlashingScanDetailsGlobal.getLabelType().equals("RED")) {
            lyt_aging_slash_popup_label_color.setBackgroundColor(getActivity().getResources().getColor(R.color.coloRed));
        }
        if (objAgeingSlashingScanDetailsGlobal.getLabelType().equals("WHITE")) {
            lyt_aging_slash_popup_label_color.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));
        }
        b_Result = objAgeingSlashingWifyControl.getExportQty(batchNo, itemcode);
        if (!b_Result) {
            okMessage("Price Change", objGlobal.getErrorMessage());
            return false;
        }
        /*if (objAgeingSlashingScanDetailsGlobal.getSlashQty() > objAgeingSlashingScanDetailsGlobal.getEligibleQty()) {
            openPopupExcessReprint();
            return false;
        }*/
        b_Result = saveScan("SUCCESS");
        if (!b_Result) {
            okMessage("Price Change", objGlobal.getErrorMessage());
            return false;
        }
        return true;
    }

    private boolean saveScan(String pType) {
        String deviceName = "", batchNo = "", itemcode = "";
        try {
            batchNo = tv_ageing_slashing_batchno.getText().toString();
            String scan = tv_aging_slash_popup_scan_barcode.getText().toString();
            itemcode = tv_aging_slash_popup_scan_itemcode.getText().toString();
            if (objAgeingSlashingScanDetailsGlobal.getLabelType().equals("YELLOW")) {
                deviceName = sp_ageing_printer_yellow.getSelectedItem().toString();
            }
            if (objAgeingSlashingScanDetailsGlobal.getLabelType().equals("RED")) {
                deviceName = sp_ageing_printer_red.getSelectedItem().toString();
            }
            if (pType.equals("REPRINT")) {
                b_Result = objAgeingSlashingWifyControl.validateForReprint(batchNo, itemcode);
                if (!b_Result) {
                    okMessage("Price Change", objGlobal.getErrorMessage());
                    return false;
                }
                b_Result = objAgeingSlashingWifyControl.saveScanLog(batchNo, scan, itemcode, "REPRINT");
            } else {
                b_Result = objAgeingSlashingWifyControl.exportToMainServer(batchNo, itemcode, pType);
            }
            if (!b_Result) {
                okMessage("Price Change", objGlobal.getErrorMessage());
                et_aging_slash_popup_scan_barcode.setText("");
                et_aging_slash_popup_scan_barcode.requestFocus();
                return false;
            } else {
                if (!printSticker(deviceName)) {
                    tv_aging_slash_popup_scan_results.setText("PRINT ERROR");
                    tv_aging_slash_popup_scan_results.setTextColor(getActivity().getResources().getColor(R.color.coloRed));
                    vibrate(100);
                    return false;
                }
            }
            tv_aging_slash_popup_scan_sc_qty.setText(String.valueOf(objAgeingSlashingScanDetailsGlobal.getScanQty()));
            tv_aging_slash_popup_scan_sl_qty.setText(String.valueOf(objAgeingSlashingScanDetailsGlobal.getSlashQty()));
            loadLastScanedItems();
            tv_aging_slash_popup_scan_results.setText("SUCCESS - PRINT DONE");
            tv_aging_slash_popup_scan_results.setTextColor(getActivity().getResources().getColor(R.color.colorGreen));
            vibrate(100);
            return true;
        } catch (Exception e) {
            okMessage("Price Change", e.toString());
            return false;
        }
    }

    private void openPopupExcessReprint() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_ageing_slashing_excess_reprint);

        bt_aging_slash_popup_reprint_excess_close = (Button) myDialog.findViewById(R.id.bt_aging_slash_popup_reprint_excess_close);
        bt_aging_slash_popup_reprint_excess_reprint = (Button) myDialog.findViewById(R.id.bt_aging_slash_popup_reprint_excess_reprint);
        bt_aging_slash_popup_reprint_excess_excess = (Button) myDialog.findViewById(R.id.bt_aging_slash_popup_reprint_excess_excess);

        bt_aging_slash_popup_reprint_excess_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        bt_aging_slash_popup_reprint_excess_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to reprint the item?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = saveScan("REPRINT");
                                if (!b_Result) {
                                    okMessage("Price Change", objGlobal.getErrorMessage());
                                }
                                myDialog.dismiss();
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

        bt_aging_slash_popup_reprint_excess_excess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to print excess?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = saveScan("EXCESS");
                                if (!b_Result) {
                                    okMessage("Price Change", objGlobal.getErrorMessage());
                                }
                                myDialog.dismiss();
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

        myDialog.show();
    }

    public Bitmap createBarcode(String itemdetail, ImageView imageView) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.RGB_565);
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(itemdetail, BarcodeFormat.CODE_128, imageView.getWidth(), imageView.getHeight());
            for (int i = 0; i < imageView.getWidth(); i++) {
                for (int j = 0; j < imageView.getHeight(); j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            okMessage("Price Change,createBarcode",e.toString());
        }
        return bitmap;
    }

    private boolean loadLastScanedItems() {
        try {
            listAgeingSlashingItemsScan.clear();
            listAgeingSlashingItemsScan = objAgeingSlashingWifyControl.loadLastScanItems();
            objMyAgeingSlashingScanItemAdp = new MyAgeingSlashingScanItemAdp(listAgeingSlashingItemsScan);
            lv_ageing_slashing.setAdapter(objMyAgeingSlashingScanItemAdp);
            b_Result = objAgeingSlashingWifyControl.loadScannedCountTotal(tv_ageing_slashing_batchno.getText().toString());
            if (!b_Result) {
                okMessage("Price Change", objGlobal.getErrorMessage());
                return false;
            }
            tv_ageing_slashing_total_scan.setText(formatter.format(objAgeingSlashingGlobal.getTotalScan()));
            b_Result = objAgeingSlashingWifyControl.loadScannedCountExportTotal(tv_ageing_slashing_batchno.getText().toString());
            if (!b_Result) {
                okMessage("Stock Taking", objGlobal.getErrorMessage());
                return false;
            }
            tv_ageing_slashing_total_exported.setText(formatter.format(objAgeingSlashingGlobal.getTotalScanExport()));
            int bPer = objControls.getBatteryPercentage(getContext());
            tv_ageing_slashing_battery_percentage.setText(bPer + " %");
            if(bPer>=20)
                tv_ageing_slashing_battery_percentage.setTextColor(Color.rgb(0,145,0));
            else
                tv_ageing_slashing_battery_percentage.setTextColor(Color.RED);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("loadLastScanedItems:catch: " + e.toString());
            return false;
        }
    }

    private class MyAgeingSlashingScanItemAdp extends BaseAdapter {
        public ArrayList<AgeingSlashingItemsScan> listAgeingSlashingItemsScan;

        public MyAgeingSlashingScanItemAdp(ArrayList<AgeingSlashingItemsScan> listAgeingSlashingItemsScan) {
            this.listAgeingSlashingItemsScan = listAgeingSlashingItemsScan;
        }

        @Override
        public int getCount() {
            return listAgeingSlashingItemsScan.size();
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
            View myView = mInflater.inflate(R.layout.ageing_slashing_scan_items_ticket, null);
            final AgeingSlashingItemsScan s = listAgeingSlashingItemsScan.get(position);

            TextView tv_aging_scashing_new_barcode = (TextView) myView.findViewById(R.id.tv_aging_scashing_new_barcode);
            tv_aging_scashing_new_barcode.setText(String.valueOf(s.scan));

            TextView tv_aging_scashing_curr_price = (TextView) myView.findViewById(R.id.tv_aging_scashing_curr_price);
            tv_aging_scashing_curr_price.setText(formatterDecimal.format(s.currPrice));

            TextView tv_aging_scashing_new_price = (TextView) myView.findViewById(R.id.tv_aging_scashing_new_price);
            tv_aging_scashing_new_price.setText(formatterDecimal.format(s.newPrice));

            TextView tv_aging_scashing_label = (TextView) myView.findViewById(R.id.tv_aging_scashing_label);
            tv_aging_scashing_label.setText(String.valueOf(s.label));

            return myView;
        }
    }

    private boolean printSticker(String device) {
        if(!lastSelectStkType.equals(objAgeingSlashingScanDetailsGlobal.getLabelType())) {
            try {
                if (bluetoothPort.isConnected())
                    bluetoothPort.disconnect();
            } catch (Exception e) {
                okMessage("Error 1", e.toString());
                return false;
            }
        }
        lastSelectStkType=objAgeingSlashingScanDetailsGlobal.getLabelType();
        if (!bluetoothPort.isConnected()) {
            try {
                btConn(mBluetoothAdapter.getRemoteDevice(device));
            } catch (Exception e) {
                okMessage("Error 2", e.toString());
                return false;
            }
        }
        printBarCode();
        return true;
    }

    private void vibrate(int duration) {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }

    private void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
        vibrate(500);
    }
}