package com.bflgroup.bflshop.ui.ageingslashing.local;

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

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Controls;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.ui.ageingslashing.AgeingSlashingGlobal;
import com.bflgroup.bflshop.ui.ageingslashing.AgeingSlashingSharedRef;
import com.bflgroup.bflshop.comm.BluetoothDevices;
import com.bflgroup.bflshop.comm.BarcodePrinting;
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
import java.util.Vector;

public class AgeingSlashingFragment extends Fragment {

    private TextView tv_ageing_slashing_user;
    private TextView tv_ageing_slashing_shop;
    private TextView tv_ageing_slashing_battery_percentage;
    private TextView et_ageing_slashing_batchno;
    private TextView tv_ageing_slashing_date;
    private Button bt_ageing_slashing_add;
    private Button bt_ageing_slashing_report;
    private Button bt_ageing_slashing_delete;
    private ListView lv_ageing_slashing;
    private TextView tv_ageing_slashing_total_scan;
    private TextView tv_ageing_slashing_total_exported;
    private Button bt_ageing_slashing_import;
    private Button bt_ageing_slashing_clear;
    private Button bt_ageing_slashing_export;
    private Spinner sp_ageing_printer_yellow;
    private Spinner sp_ageing_printer_red;

    private EditText et_aging_slash_popup_scan_barcode;
    private TextView tv_aging_slash_popup_scan_barcode;
    private Button bt_aging_slash_popup_scan_add;
    private Button bt_aging_slash_popup_scan_close;
    private TextView tv_aging_slash_popup_scan_itemcode;
    private TextView tv_aging_slash_popup_scan_trfno;
    private TextView tv_aging_slash_popup_scan_itemname;
    private TextView tv_aging_slash_popup_scan_department;
    private TextView tv_aging_slash_popup_scan_currprice;
    private TextView tv_aging_slash_popup_scan_newprice;
    private TextView tv_aging_slash_popup_scan_labeltype;
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
    private ConstraintLayout lyt_aging_slash_popup_label;
    private ConstraintLayout lyt_aging_slash_popup_label_color;

    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private Controls objControls = new Controls();
    private AgeingSlashingControl objAgeingSlashingControl = new AgeingSlashingControl();
    private AgeingSlashingGlobal objAgeingSlashingGlobal = AgeingSlashingGlobal.getInstance();
    private AgeingSlashingScanDetailsGlobal objAgeingSlashingScanDetailsGlobal = AgeingSlashingScanDetailsGlobal.getInstance();
    private BluetoothDevices objBluetoothDevices = new BluetoothDevices();

    private ArrayList<AgeingSlashingItemsScan> listAgeingSlashingItemsScan = new ArrayList<AgeingSlashingItemsScan>();
    MyAgeingSlashingScanItemAdp objMyAgeingSlashingScanItemAdp;
    AgeingSlashingSharedRef objAgeingSlashingSharedRef;
    private DbManagerAgeing objDbManagerAgeing;

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

    private Vector<BluetoothDevice> remoteDevices;
    ArrayAdapter<String> adapter;

    public AgeingSlashingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ageing_slashing, container, false);

        tv_ageing_slashing_user = (TextView) view.findViewById(R.id.tv_ageing_slashing_user);
        tv_ageing_slashing_shop = (TextView) view.findViewById(R.id.tv_ageing_slashing_shop);
        tv_ageing_slashing_battery_percentage = (TextView) view.findViewById(R.id.tv_ageing_slashing_battery_percentage);
        et_ageing_slashing_batchno = (TextView) view.findViewById(R.id.et_ageing_slashing_batchno);
        tv_ageing_slashing_date = (TextView) view.findViewById(R.id.tv_ageing_slashing_date);
        bt_ageing_slashing_add = (Button) view.findViewById(R.id.bt_ageing_slashing_add);
        bt_ageing_slashing_report = (Button) view.findViewById(R.id.bt_ageing_slashing_report);
        bt_ageing_slashing_delete = (Button) view.findViewById(R.id.bt_ageing_slashing_delete);
        lv_ageing_slashing = (ListView) view.findViewById(R.id.lv_ageing_slashing);
        tv_ageing_slashing_total_scan = (TextView) view.findViewById(R.id.tv_ageing_slashing_total_scan);
        tv_ageing_slashing_total_exported = (TextView) view.findViewById(R.id.tv_ageing_slashing_total_exported);
        bt_ageing_slashing_import = (Button) view.findViewById(R.id.bt_ageing_slashing_import);
        bt_ageing_slashing_clear = (Button) view.findViewById(R.id.bt_ageing_slashing_clear);
        bt_ageing_slashing_export = (Button) view.findViewById(R.id.bt_ageing_slashing_export);
        sp_ageing_printer_yellow = (Spinner) view.findViewById(R.id.sp_ageing_printer_yellow);
        sp_ageing_printer_red = (Spinner) view.findViewById(R.id.sp_ageing_printer_red);

        tv_ageing_slashing_shop.setText(objPosGlobal.getShopName());
        tv_ageing_slashing_user.setText(objGlobal.getUserName());
        tv_ageing_slashing_date.setText(objGlobal.getServerDate());

        searchflags = false;

        objDbManagerAgeing = new DbManagerAgeing(getContext());
        objAgeingSlashingSharedRef=new AgeingSlashingSharedRef(getContext());

        b_Result = objBluetoothDevices.loadBluetoothDevicesArray();
        if (!b_Result) {
            okMessage("Ageing Slashing",objGlobal.getErrorMessage());
        } else {
            ArrayAdapter<String> arrayAdpYellow;
            arrayAdpYellow = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevicesYellow());
            sp_ageing_printer_yellow.setAdapter(arrayAdpYellow);

            ArrayAdapter<String> arrayAdpRed;
            arrayAdpRed = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objGlobal.getBluetoothDevicesRed());
            sp_ageing_printer_red.setAdapter(arrayAdpRed);

            if (objAgeingSlashingSharedRef.loadPrinterYellow()!="") {
                sp_ageing_printer_yellow.setSelection(arrayAdpYellow.getPosition(objAgeingSlashingSharedRef.loadPrinterYellow()));
                sp_ageing_printer_yellow.setEnabled(false);
            }
        }

        b_Result = objDbManagerAgeing.checkNewTrfNoLocalDb();
        if(!b_Result) {
            b_Result = objAgeingSlashingControl.validatePdaRefNo();
            if(!b_Result) {
                okMessage("Ageing Slashing", objGlobal.getErrorMessage());
                et_ageing_slashing_batchno.requestFocus();
            } else {
                b_Result=objDbManagerAgeing.newSlnoUpdate();
                if(!b_Result) {
                    okMessage("Ageing Slashing", objGlobal.getErrorMessage());
                    et_ageing_slashing_batchno.requestFocus();
                }
            }
        }
        b_Result = objDbManagerAgeing.getNewTrfNoLocalDb();
        if (!b_Result) {
            okMessage("Ageing Slashing", objGlobal.getErrorMessage());
            et_ageing_slashing_batchno.requestFocus();
        }
        if (objAgeingSlashingScanDetailsGlobal.getNewTrfNo().isEmpty() || objAgeingSlashingGlobal.getPdaPrefixSn() == 0) {
            okMessage("Ageing Slashing", "Error to generate pda ref number, please contact IT");
            et_ageing_slashing_batchno.requestFocus();
        }

        bt_ageing_slashing_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String batchNo = et_ageing_slashing_batchno.getText().toString();
                if(batchNo.isEmpty()) {
                    okMessage("Ageing Slashing", "Please enter batch number");
                    et_ageing_slashing_batchno.requestFocus();
                } else if (sp_ageing_printer_yellow.getSelectedItem().toString().equals("--Select--") || sp_ageing_printer_red.getSelectedItem().toString().equals("--Select--") || sp_ageing_printer_yellow.getSelectedItem().toString().isEmpty() || sp_ageing_printer_red.getSelectedItem().toString().isEmpty()) {
                    okMessage("Ageing Slashing", "Please select the printer");
                } else {
                    openPopupScan();
                }
            }
        });

        bt_ageing_slashing_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_ageing_slashing_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_ageing_slashing_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_Result = objAgeingSlashingControl.validatePdaRefNo();
                if(!b_Result){
                    okMessage("Ageing Slashing", objGlobal.getErrorMessage());
                    et_ageing_slashing_batchno.requestFocus();
                } else {
                    b_Result=objDbManagerAgeing.newSlnoUpdate();
                    if(!b_Result){
                        okMessage("Ageing Slashing", objGlobal.getErrorMessage());
                        et_ageing_slashing_batchno.requestFocus();
                    } else {
                        if (et_ageing_slashing_batchno.getText().toString().isEmpty()) {
                            okMessage("Ageing Slashing", "Please enter batch number");
                            et_ageing_slashing_batchno.requestFocus();
                        } else if (sp_ageing_printer_yellow.getSelectedItem().toString().equals("--Select--") || sp_ageing_printer_red.getSelectedItem().toString().equals("--Select--") ||
                                sp_ageing_printer_yellow.getSelectedItem().toString().isEmpty() || sp_ageing_printer_red.getSelectedItem().toString().isEmpty()) {
                            okMessage("Ageing Slashing", "Please select the printer");
                        } else if (objAgeingSlashingGlobal.getPdaPrefix().isEmpty()) {
                            okMessage("Ageing Slashing", objGlobal.getErrorMessage());
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setMessage("Are you sure to import to local DB?")
                                    .setTitle("Conformation")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            new ImportAgeingFromMainToLocal().execute();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                        }
                    }
                }
            }
        });

        bt_ageing_slashing_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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

        bt_ageing_slashing_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to export from local DB to main server?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new ExportAgeingFromLocalToMain().execute();
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

        loadLastScanedItems();

        objSample_Print = new BarcodePrinting();
        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);

        Init_BluetoothSet();
        return view;
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

    private boolean printBarCode() {
        try {
            Bitmap label = getBarcodeLabel();
            if (label != null) {
                //return objSample_Print.PrintBarcodeImage(1, ZPLConst.SENSE_GAP, label);
                return false;
            } else {
                okMessage("Error 2", "label error");
                return false;
            }
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
        tv_aging_slash_popup_scan_trfno = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_trfno);
        tv_aging_slash_popup_scan_itemname = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_itemname);
        tv_aging_slash_popup_scan_department = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_department);
        tv_aging_slash_popup_scan_currprice = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_currprice);
        tv_aging_slash_popup_scan_newprice = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_newprice);
        tv_aging_slash_popup_scan_labeltype = (TextView) myDialog.findViewById(R.id.tv_aging_slash_popup_scan_labeltype);
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
                myDialog.dismiss();
            }
        });

        et_aging_slash_popup_scan_barcode.requestFocus();
        myDialog.show();
    }

    boolean scanBarcode() {
        tv_aging_slash_popup_scan_itemcode.setText("");
        tv_aging_slash_popup_scan_trfno.setText("");
        tv_aging_slash_popup_scan_itemname.setText("");
        tv_aging_slash_popup_scan_department.setText("");
        tv_aging_slash_popup_scan_currprice.setText("");
        tv_aging_slash_popup_scan_newprice.setText("");
        tv_aging_slash_popup_scan_labeltype.setText("");
        tv_aging_slash_popup_scan_slashtype.setText("");
        tv_aging_slash_popup_scan_new_barcode.setText("");
        String batchNo = et_ageing_slashing_batchno.getText().toString();
        String deviceName="";
        if (batchNo.isEmpty()) {
            okMessage("Ageing Slashing", "Please enter valid batch number");
            et_ageing_slashing_batchno.requestFocus();
            return false;
        }
        String scan = objControls.replaceString(et_aging_slash_popup_scan_barcode.getText().toString()).toUpperCase();
        if (scan.isEmpty()) {
            scan = "";
        }
        if (scan.isEmpty()) {
            okMessage("Ageing Slashing", "Please scan Barcode");
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        if (scan.length() > 30) {
            okMessage("Ageing Slashing", "Please Double check the barcode that scan");
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        tv_aging_slash_popup_scan_barcode.setText(scan);
        if (!scan.contains("/")) {
            okMessage("Ageing Slashing", "Barcode is not correct format");
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        String[] scanAr = scan.split("/");
        String itemcode = scanAr[0];
        float ePrice = Float.parseFloat(scanAr[1]);
        String trfNo = scanAr[2];
        b_Result = objDbManagerAgeing.getAgeingItems(itemcode, ePrice);
        if (!b_Result) {
            b_Result = objDbManagerAgeing.saveScanLog(batchNo,scan,objGlobal.getErrorMessage());
            okMessage("Ageing Slashing", objGlobal.getErrorMessage());
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        b_Result = objDbManagerAgeing.validateTrfNo(trfNo);
        if (!b_Result) {
            b_Result = objDbManagerAgeing.saveScanLog(batchNo,scan,objGlobal.getErrorMessage());
            okMessage("Ageing Slashing", objGlobal.getErrorMessage());
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        b_Result = objDbManagerAgeing.validateAgeing(itemcode,objAgeingSlashingScanDetailsGlobal.getDepartment(),trfNo,objAgeingSlashingScanDetailsGlobal.getDateDiff());
        if (!b_Result) {
            b_Result = objDbManagerAgeing.saveScanLog(batchNo,scan,objGlobal.getErrorMessage());
            okMessage("Ageing Slashing", objGlobal.getErrorMessage());
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        b_Result = objDbManagerAgeing.getNewTrfNoLocalDb();
        if (!b_Result) {
            b_Result = objDbManagerAgeing.saveScanLog(batchNo,scan,objGlobal.getErrorMessage());
            okMessage("Ageing Slashing", objGlobal.getErrorMessage());
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
            return false;
        }
        try {
            tv_aging_slash_popup_scan_itemcode.setText(itemcode);
            tv_aging_slash_popup_scan_trfno.setText(trfNo);
            tv_aging_slash_popup_scan_itemname.setText(objAgeingSlashingScanDetailsGlobal.getItemName());
            tv_aging_slash_popup_scan_department.setText(objAgeingSlashingScanDetailsGlobal.getDepartment());
            tv_aging_slash_popup_scan_currprice.setText(formatterDecimal.format(objAgeingSlashingScanDetailsGlobal.getCurrPrice()));
            tv_aging_slash_popup_scan_newprice.setText(formatterDecimal.format(objAgeingSlashingScanDetailsGlobal.getNewPrice()));
            tv_aging_slash_popup_scan_labeltype.setText(objAgeingSlashingScanDetailsGlobal.getLabelType());
            tv_aging_slash_popup_scan_slashtype.setText(objAgeingSlashingScanDetailsGlobal.getSlashingType());
            objAgeingSlashingScanDetailsGlobal.setNewBarcode(itemcode + "/" + formatter.format(objAgeingSlashingScanDetailsGlobal.getNewPrice()) + "/" + objAgeingSlashingScanDetailsGlobal.getNewTrfNo());
            tv_aging_slash_popup_scan_new_barcode.setText(objAgeingSlashingScanDetailsGlobal.getNewBarcode());
            tv_aging_slash_popup_sticker_comp_name.setText(objPosGlobal.getBarcodePrintHead1());
            tv_aging_slash_popup_sticker_prod_name.setText(objAgeingSlashingScanDetailsGlobal.getItemName());
            tv_aging_slash_popup_sticker_itemcode.setText(itemcode);
            tv_aging_slash_popup_sticker_trfno.setText(objPosGlobal.getBarcodePrintHead2() + " - " + objAgeingSlashingScanDetailsGlobal.getNewTrfNo());
            tv_aging_slash_popup_sticker_was_price.setText(objGlobal.getFcCode() + " " + formatter.format(objAgeingSlashingScanDetailsGlobal.getWasPrice()));
            tv_aging_slash_popup_sticker_now_price.setText(objGlobal.getFcCode() + " " + formatter.format(objAgeingSlashingScanDetailsGlobal.getNewPrice()));
            tv_aging_slash_popup_sticker_uid.setText(String.valueOf(objGlobal.getUserId()));
            tv_aging_slash_popup_sticker_mark.setText(objControls.getMark(objGlobal.getServerDate()));
            if (objAgeingSlashingScanDetailsGlobal.getLabelType().equals("YELLOW")) {
                lyt_aging_slash_popup_label_color.setBackgroundColor(getActivity().getResources().getColor(R.color.colorYellow));
                deviceName=sp_ageing_printer_yellow.getSelectedItem().toString();
            }
            if (objAgeingSlashingScanDetailsGlobal.getLabelType().equals("RED")) {
                lyt_aging_slash_popup_label_color.setBackgroundColor(getActivity().getResources().getColor(R.color.coloRed));
                deviceName=sp_ageing_printer_red.getSelectedItem().toString();
            }
            b_Result = objDbManagerAgeing.saveScanToLocaldb(batchNo, itemcode, objAgeingSlashingScanDetailsGlobal.getItemName(), objAgeingSlashingScanDetailsGlobal.getDepartment(), objAgeingSlashingScanDetailsGlobal.getDivision(),
                    objAgeingSlashingScanDetailsGlobal.getGroupCode(), objAgeingSlashingScanDetailsGlobal.getGroupName(), objAgeingSlashingScanDetailsGlobal.getCurrPrice(), objAgeingSlashingScanDetailsGlobal.getNewPrice(),
                    objAgeingSlashingScanDetailsGlobal.getLabelType(), objAgeingSlashingScanDetailsGlobal.getSlashingType(), objAgeingSlashingScanDetailsGlobal.getWasPrice(), trfNo,
                    objAgeingSlashingScanDetailsGlobal.getTrfDate(), objAgeingSlashingScanDetailsGlobal.getNewBarcode(), objAgeingSlashingScanDetailsGlobal.getNewTrfNo());
            if (!b_Result) {
                okMessage("Ageing Slashing", objGlobal.getErrorMessage());
                et_aging_slash_popup_scan_barcode.setText("");
                et_aging_slash_popup_scan_barcode.requestFocus();
                return false;
            }
            b_Result = objDbManagerAgeing.saveScanLog(batchNo, scan, "SUCCESS");
            if (!b_Result) {
                okMessage("Ageing Slashing", objGlobal.getErrorMessage());
                et_aging_slash_popup_scan_barcode.setText("");
                et_aging_slash_popup_scan_barcode.requestFocus();
                return false;
            }
            b_Result = objDbManagerAgeing.newSlnoUpdate();
            if (!b_Result) {
                okMessage("Ageing Slashing", objGlobal.getErrorMessage());
                et_aging_slash_popup_scan_barcode.setText("");
                et_aging_slash_popup_scan_barcode.requestFocus();
                return false;
            }

            createBarcode(objAgeingSlashingScanDetailsGlobal.getNewBarcode(), img_aging_slash_popup_sticker_barcode);
            if (!printSticker(deviceName)) {
                return false;
            }
            loadLastScanedItems();
            et_aging_slash_popup_scan_barcode.setText("");
            et_aging_slash_popup_scan_barcode.requestFocus();
        } catch (Exception e){
            okMessage("Ageing Slashing",e.toString());
            return false;
        }
        return true;
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
            e.printStackTrace();
        }
        return bitmap;
    }

    private boolean loadLastScanedItems() {
        try {
            listAgeingSlashingItemsScan.clear();
            listAgeingSlashingItemsScan = objDbManagerAgeing.loadLastScanItems();
            objMyAgeingSlashingScanItemAdp = new AgeingSlashingFragment.MyAgeingSlashingScanItemAdp(listAgeingSlashingItemsScan);
            lv_ageing_slashing.setAdapter(objMyAgeingSlashingScanItemAdp);
            b_Result = objDbManagerAgeing.loadScannedCountTotal();
            if (!b_Result) {
                okMessage("Stock Taking", objGlobal.getErrorMessage());
                return false;
            }
            tv_ageing_slashing_total_scan.setText(formatter.format(objAgeingSlashingGlobal.getTotalScan()));
            b_Result = objDbManagerAgeing.loadScannedCountExportTotal();
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

    private class ImportAgeingFromMainToLocal extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        public ImportAgeingFromMainToLocal() {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Importing data... AgeingItems & RFIDTransfer from Server");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            Integer retVal = null;
            try {
                String batchNo = et_ageing_slashing_batchno.getText().toString();
                b_Result = objDbManagerAgeing.loadAgeingItems(batchNo);
                if (!b_Result) {
                    okMessage("Ageing Slashing", objGlobal.getErrorMessage());
                }
                b_Result=objDbManagerAgeing.loadRFIDTransfer();
                if(!b_Result){
                    okMessage("Ageing Slashing", objGlobal.getErrorMessage());
                }
            } catch (Exception e) {
                okMessage("Ageing Slashing", e.toString());
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            objAgeingSlashingSharedRef.savePrinterRed(sp_ageing_printer_red.getSelectedItem().toString());
            objAgeingSlashingSharedRef.savePrinterYellow(sp_ageing_printer_yellow.getSelectedItem().toString());
            et_ageing_slashing_batchno.setEnabled(false);
            sp_ageing_printer_red.setEnabled(false);
            sp_ageing_printer_yellow.setEnabled(false);
            okMessage("Ageing Slashing", "Completed, RFIDMaster(" + formatter.format(objAgeingSlashingGlobal.getTotalRfidTransfer()) + "), AgeingItems(" + formatter.format(objAgeingSlashingGlobal.getTotalAgeingItemsImport()) + ")");
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class ExportAgeingFromLocalToMain extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;
        public ExportAgeingFromLocalToMain() {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Exporting data... Local Server to Main server");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                b_Result = objDbManagerAgeing.exportToMainServer(et_ageing_slashing_batchno.getText().toString());
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
            if(result==0){
                okMessage("Ageing Slashing", objGlobal.getErrorMessage());
            } else {
                b_Result = loadLastScanedItems();
                if (!b_Result) {
                    okMessage("Ageing Slashing", objGlobal.getErrorMessage());
                } else {
                    okMessage("Ageing Slashing", "Export Completed, Entry Number:");
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
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

            /*TextView tv_aging_scashing_export = (TextView) myView.findViewById(R.id.tv_aging_scashing_export);
            tv_aging_scashing_export.setText(String.valueOf(s.export));*/

            return myView;
        }
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
