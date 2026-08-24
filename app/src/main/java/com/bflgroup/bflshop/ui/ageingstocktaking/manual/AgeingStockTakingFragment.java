package com.bflgroup.bflshop.ui.ageingstocktaking.manual;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Controls;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.ui.ageingstocktaking.model.AgeingStockTakingReportsForDelete;
import com.bflgroup.bflshop.ui.ageingstocktaking.model.AgeingStockTakingScanItems;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AgeingStockTakingFragment extends Fragment {

    private TextView tv_ageing_stock_taking_date;
    private TextView tv_ageing_stock_taking_user;
    private TextView tv_ageing_stock_taking_shop;
    private TextView tv_ageing_stock_taking_zone;
    private Button bt_ageing_stock_taking_add;
    private Button bt_ageing_stock_taking_closezone;
    private ListView lv_ageing_stock_taking;
    private TextView tv_ageing_stock_taking_total_scan;
    private TextView tv_ageing_stock_taking_total_exported;
    private TextView tv_ageing_stock_taking_total_pendingforexport;
    private Button bt_ageing_stock_taking_import;
    private Button bt_ageing_stock_taking_export;
    private Button bt_ageing_stock_taking_delete;
    private TextView tv_ageing_stock_taking_battery_percentage;
    private EditText et_ageing_stock_taking_popup_barcode;
    private TextView tv_ageing_stock_taking_popup_last;
    private TextView tv_ageing_stock_taking_popup_last_barcode;
    private TextView tv_ageing_stock_taking_popup_last_rfid;
    private TextView tv_ageing_stock_taking_popup_result;
    private Button bt_ageing_stock_taking_popup_close;
    private Button bt_ageing_stock_taking_popup_add;
    private CheckBox ch_ageing_stock_taking_popup_main_server;
    private EditText et_ageing_stock_taking_popup_password;
    private Button bt_ageing_stock_taking_popup_password_ok;
    private Button bt_ageing_stock_taking_popup_password_close;
    private Spinner sp_ageing_stock_taking_delete_zone;
    private Button bt_ageing_stock_taking_delete_load;
    private EditText et_ageing_stock_taking_delete_itemcode;
    private ListView lv_ageing_stock_delete_taking;
    private TextView tv_ageing_stock_taking_total_del_qty;
    private Button bt_ageing_stock_taking_delete_delete_device;
    private Button bt_ageing_stock_taking_delete_delete_zone;
    private Button bt_ageing_stock_taking_delete_delete_close;
    private Button bt_ageing_stock_taking_delete_delete_clear;
    private Controls objControls = new Controls();
    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private AgeingStockTakingControl objAgeingStockTakingControl = new AgeingStockTakingControl();
    private AgeingStockTakingGlobal objAgeingStockTakingGlobal = AgeingStockTakingGlobal.getInstance();
    AgeingStockTakingDbManager objAgeingStockTakingDbManager;
    MyAgeingStockTakingItemAdp objMyAgeingStockTakingItemAdp;
    ArrayList<AgeingStockTakingScanItems> listAgeingStockTakingScanItems = new ArrayList<AgeingStockTakingScanItems>();
    AgeingStockTakingReportSearchItemForDeleteAdp objAgeingStockTakingReportSearchItemForDeleteAdp;
    ArrayList<AgeingStockTakingReportsForDelete> listAgeingStockTakingReportsForDelete = new ArrayList<AgeingStockTakingReportsForDelete>();
    DecimalFormat formatter = new DecimalFormat("###,###");
    private boolean b_Result;

    public AgeingStockTakingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ageing_stock_taking, container, false);

        tv_ageing_stock_taking_date = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_date);
        tv_ageing_stock_taking_user = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_user);
        tv_ageing_stock_taking_shop = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_shop);
        tv_ageing_stock_taking_zone = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_zone);
        bt_ageing_stock_taking_add = (Button) view.findViewById(R.id.bt_ageing_stock_taking_add);
        bt_ageing_stock_taking_closezone = (Button) view.findViewById(R.id.bt_ageing_stock_taking_closezone);
        lv_ageing_stock_taking = (ListView) view.findViewById(R.id.lv_ageing_stock_taking);
        tv_ageing_stock_taking_total_scan = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_total_scan);
        tv_ageing_stock_taking_total_exported = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_total_exported);
        tv_ageing_stock_taking_total_pendingforexport = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_total_pendingforexport);
        bt_ageing_stock_taking_import = (Button) view.findViewById(R.id.bt_ageing_stock_taking_import);
        bt_ageing_stock_taking_export = (Button) view.findViewById(R.id.bt_ageing_stock_taking_export);
        bt_ageing_stock_taking_delete = (Button) view.findViewById(R.id.bt_ageing_stock_taking_delete);
        tv_ageing_stock_taking_battery_percentage = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_battery_percentage);

        tv_ageing_stock_taking_shop.setText(objPosGlobal.getShopName());
        tv_ageing_stock_taking_user.setText(objGlobal.getUserName());
        tv_ageing_stock_taking_date.setText(objGlobal.getServerDate());

        bt_ageing_stock_taking_closezone.setEnabled(false);
        objAgeingStockTakingDbManager = new AgeingStockTakingDbManager(getContext());

        b_Result = loadItemsStockTaking();
        if (!b_Result) {
            okMessage("Stock Taking", objGlobal.getErrorMessage());
        }

        bt_ageing_stock_taking_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double pend = Double.valueOf(tv_ageing_stock_taking_total_pendingforexport.getText().toString());
                if (pend != 0) {
                    okMessage("Stock Taking", "Please Export the data before trying to delete");
                } else {
                    openPopupDelete();
                }
            }
        });

        bt_ageing_stock_taking_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zone = "";
                zone = tv_ageing_stock_taking_zone.getText().toString();
                if (zone.isEmpty()) {
                    okMessage("Stock Taking", "Please select zone");
                } else {
                    b_Result = objAgeingStockTakingControl.validateZoneUsed(zone);
                    if (!b_Result) {
                        okMessage("Stock Taking", objGlobal.getErrorMessage());
                    } else {
                        openPopupScan();
                        tv_ageing_stock_taking_zone.setEnabled(false);
                        bt_ageing_stock_taking_closezone.setEnabled(true);
                    }
                }
            }
        });

        bt_ageing_stock_taking_closezone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zone = "";
                zone = tv_ageing_stock_taking_zone.getText().toString();
                if (zone.isEmpty()) {
                    okMessage("Stock Taking", "Please select zone");
                } else {
                    tv_ageing_stock_taking_zone.setText("");
                    tv_ageing_stock_taking_zone.setEnabled(true);
                    bt_ageing_stock_taking_closezone.setEnabled(false);
                }
            }
        });

        bt_ageing_stock_taking_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to export main server?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                exportToServer();
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

        tv_ageing_stock_taking_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;
                b_Result = objAgeingStockTakingControl.loadZone(true);
                if (!b_Result) {
                    okMessage("Stock Taking", objGlobal.getErrorMessage());
                } else {
                    dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.searchable_spinner);
                    dialog.getWindow().setLayout(500, 1000);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    EditText editText = dialog.findViewById(R.id.edit_text);
                    ListView listView = dialog.findViewById(R.id.list_view);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, objAgeingStockTakingGlobal.getZoneList());
                    listView.setAdapter(adapter);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            tv_ageing_stock_taking_zone.setText(adapter.getItem(position));
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        return view;
    }

    private void openPopupPassword(String typs, String dval) {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_ageing_stock_taking_clear_password);

        et_ageing_stock_taking_popup_password = (EditText) myDialog.findViewById(R.id.et_ageing_stock_taking_popup_password);
        bt_ageing_stock_taking_popup_password_ok = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_password_ok);
        bt_ageing_stock_taking_popup_password_close = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_password_close);

        bt_ageing_stock_taking_popup_password_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passOrgMsg = "";
                String pass = et_ageing_stock_taking_popup_password.getText().toString();
                boolean isValid = objAgeingStockTakingControl.validateManagerPassword(typs, pass);
                if (pass.isEmpty()) {
                    okMessage("Stock Taking", "Please enter password");
                } else if (!isValid) {
                    okMessage("Stock Taking", "Invalid password");
                }
                else{
                    if(typs.equals("ITEM")) {passOrgMsg="ITEM";}
                    if(typs.equals("ZONE")) { passOrgMsg="ZONE";}
                    if(typs.equals("DEVI")) {passOrgMsg="DEVICE";}
//                 else if(!pass.equals(passOrg)){
//                    okMessage("Stock Taking","Invalid password");

                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("Are you sure to delete the selected " + passOrgMsg + "?")
                                .setTitle("Conformation")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        b_Result = objAgeingStockTakingDbManager.deleteStockTakeMain(getContext(), typs, dval);
                                        if (!b_Result) {
                                            okMessage("Stock Taking", objGlobal.getErrorMessage());
                                        } else {
                                            if (typs.equals("ITEM")) {
                                                myDialog.dismiss();
                                                b_Result = loadStockTakeItemsForDelete();
                                                if (!b_Result) {
                                                    okMessage("Stock Taking", objGlobal.getErrorMessage());
                                                }
                                            } else {
                                                b_Result = loadStockTakeItemsForDelete();
                                                if (!b_Result) {
                                                    okMessage("Stock Taking", objGlobal.getErrorMessage());
                                                } else {
                                                    b_Result = objAgeingStockTakingControl.loadZone(false);
                                                    if (!b_Result) {
                                                        okMessage("Stock Taking", objGlobal.getErrorMessage());
                                                    } else {
                                                        List<String> arr;
                                                        ArrayAdapter<String> arrayAdp;
                                                        arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objAgeingStockTakingGlobal.getZoneList());
                                                        sp_ageing_stock_taking_delete_zone.setAdapter(arrayAdp);
                                                        myDialog.dismiss();
                                                        sp_ageing_stock_taking_delete_zone.setEnabled(true);
                                                        bt_ageing_stock_taking_delete_load.setEnabled(true);
                                                        bt_ageing_stock_taking_delete_delete_zone.setEnabled(false);
                                                        //bt_ageing_stock_taking_delete_delete_device.setEnabled(false);
                                                    }
                                                }
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
            }
        });

        bt_ageing_stock_taking_popup_password_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        et_ageing_stock_taking_popup_password.requestFocus();
        myDialog.show();
    }

    private void openPopupDelete() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_ageing_stocktaking_delete);

        sp_ageing_stock_taking_delete_zone = (Spinner) myDialog.findViewById(R.id.sp_ageing_stock_taking_delete_zone);
        bt_ageing_stock_taking_delete_load = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_delete_load);
        et_ageing_stock_taking_delete_itemcode = (EditText) myDialog.findViewById(R.id.et_ageing_stock_taking_delete_itemcode);
        lv_ageing_stock_delete_taking = (ListView) myDialog.findViewById(R.id.lv_ageing_stock_delete_taking);
        tv_ageing_stock_taking_total_del_qty = (TextView) myDialog.findViewById(R.id.tv_ageing_stock_taking_total_del_qty);
        bt_ageing_stock_taking_delete_delete_device = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_delete_delete_device);
        bt_ageing_stock_taking_delete_delete_zone = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_delete_delete_zone);
        bt_ageing_stock_taking_delete_delete_close = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_delete_delete_close);
        bt_ageing_stock_taking_delete_delete_clear = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_delete_delete_clear);

        //bt_ageing_stock_taking_delete_delete_device.setEnabled(false);
        bt_ageing_stock_taking_delete_delete_zone.setEnabled(false);

        b_Result = objAgeingStockTakingControl.loadZone(false);
        if (!b_Result) {
            okMessage("Stock Taking", objGlobal.getErrorMessage());
        } else {
            List<String> arr;
            ArrayAdapter<String> arrayAdp;
            arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objAgeingStockTakingGlobal.getZoneList());
            sp_ageing_stock_taking_delete_zone.setAdapter(arrayAdp);
        }

        bt_ageing_stock_taking_delete_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_Result = loadStockTakeItemsForDelete();
                if (!b_Result) {
                    okMessage("Stock Taking", objGlobal.getErrorMessage());
                }
                sp_ageing_stock_taking_delete_zone.setEnabled(false);
                bt_ageing_stock_taking_delete_load.setEnabled(false);
                //bt_ageing_stock_taking_delete_delete_device.setEnabled(true);
                bt_ageing_stock_taking_delete_delete_zone.setEnabled(true);
            }
        });
        bt_ageing_stock_taking_delete_delete_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupPassword("DEVI", objGlobal.getDeviceName());
            }
        });
        bt_ageing_stock_taking_delete_delete_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zone = sp_ageing_stock_taking_delete_zone.getSelectedItem().toString();
                openPopupPassword("ZONE", zone);
            }
        });

        bt_ageing_stock_taking_delete_delete_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        bt_ageing_stock_taking_delete_delete_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp_ageing_stock_taking_delete_zone.setEnabled(true);
                bt_ageing_stock_taking_delete_load.setEnabled(true);
                //bt_ageing_stock_taking_delete_delete_device.setEnabled(false);
                bt_ageing_stock_taking_delete_delete_zone.setEnabled(false);
                b_Result = loadStockTakeItemsForDelete();
                if (!b_Result) {
                    okMessage("Stock Taking", objGlobal.getErrorMessage());
                }
                listAgeingStockTakingReportsForDelete.clear();
                listAgeingStockTakingReportsForDelete = objAgeingStockTakingControl.loadAgingStockTakingForDelete("", "");
                objAgeingStockTakingReportSearchItemForDeleteAdp = new AgeingStockTakingFragment.AgeingStockTakingReportSearchItemForDeleteAdp(listAgeingStockTakingReportsForDelete);
                lv_ageing_stock_delete_taking.setAdapter(objAgeingStockTakingReportSearchItemForDeleteAdp);
                tv_ageing_stock_taking_total_del_qty.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScanDelQty()));
            }
        });
        myDialog.show();
    }

    private class AgeingStockTakingReportSearchItemForDeleteAdp extends BaseAdapter {
        public ArrayList<AgeingStockTakingReportsForDelete> listAgeingStockTakingReportsForDelete;

        public AgeingStockTakingReportSearchItemForDeleteAdp(ArrayList<AgeingStockTakingReportsForDelete> listAgeingStockTakingReportsForDelete) {
            this.listAgeingStockTakingReportsForDelete = listAgeingStockTakingReportsForDelete;
        }

        @Override
        public int getCount() {
            return listAgeingStockTakingReportsForDelete.size();
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
            View myView = mInflater.inflate(R.layout.ageing_stock_taking_items_delete_ticket, null);
            final AgeingStockTakingReportsForDelete s = listAgeingStockTakingReportsForDelete.get(position);

            TextView tv_aging_stock_taking_delete_itemdetails = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_delete_itemdetails);
            tv_aging_stock_taking_delete_itemdetails.setText(String.valueOf(s.itemcode));

            TextView tv_aging_stock_taking_delete_barcode = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_delete_barcode);
            tv_aging_stock_taking_delete_barcode.setText(String.valueOf(s.barcode));

            Button bt_aging_stock_taking_delete = (Button) myView.findViewById(R.id.bt_aging_stock_taking_delete);
            bt_aging_stock_taking_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPopupPassword("ITEM", s.srid);
                }
            });

            return myView;
        }
    }

    private void openPopupScan() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_ageing_stock_taking_scan_items);

        et_ageing_stock_taking_popup_barcode = (EditText) myDialog.findViewById(R.id.et_ageing_stock_taking_popup_barcode);
        tv_ageing_stock_taking_popup_last = (TextView) myDialog.findViewById(R.id.tv_ageing_stock_taking_popup_last);
        tv_ageing_stock_taking_popup_last_barcode = (TextView) myDialog.findViewById(R.id.tv_ageing_stock_taking_popup_last_barcode);
        tv_ageing_stock_taking_popup_last_rfid = (TextView) myDialog.findViewById(R.id.tv_ageing_stock_taking_popup_last_rfid);
        tv_ageing_stock_taking_popup_result = (TextView) myDialog.findViewById(R.id.tv_ageing_stock_taking_popup_result);
        bt_ageing_stock_taking_popup_close = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_close);
        bt_ageing_stock_taking_popup_add = (Button) myDialog.findViewById(R.id.bt_ageing_stock_taking_popup_add);
        ch_ageing_stock_taking_popup_main_server = (CheckBox) myDialog.findViewById(R.id.ch_ageing_stock_taking_popup_main_server);

        ch_ageing_stock_taking_popup_main_server.setChecked(false);
        if (objPosGlobal.getStockTakeValServer().equals("Y"))
            ch_ageing_stock_taking_popup_main_server.setChecked(true);

        et_ageing_stock_taking_popup_barcode.setOnTouchListener(new View.OnTouchListener() {
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

        et_ageing_stock_taking_popup_barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    scanBarcode();
                }
                return false;
            }
        });

        bt_ageing_stock_taking_popup_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });

        bt_ageing_stock_taking_popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        et_ageing_stock_taking_popup_barcode.requestFocus();
        myDialog.show();
    }

    boolean scanBarcode() {
        String scan = objControls.replaceString(et_ageing_stock_taking_popup_barcode.getText().toString()).toUpperCase();
        String zoneId = tv_ageing_stock_taking_zone.getText().toString();
        String result = "";
        String rfid = "";
        tv_ageing_stock_taking_popup_result.setText("");
        if (zoneId.isEmpty()) {
            //okMessage("Stock Taking", "Please Select Zone");
            tv_ageing_stock_taking_popup_result.setText("Please Select Zone");
            vibrateSound(1000);
            et_ageing_stock_taking_popup_barcode.setText("");
            et_ageing_stock_taking_popup_barcode.requestFocus();
            return false;
        }
        if (scan.isEmpty()) {
            scan = "";
        }
        int qty = 1;
        if (scan.isEmpty()) {
            //okMessage("Stock Taking", "Please scan Barcode");
            tv_ageing_stock_taking_popup_result.setText("Please scan Barcode");
            vibrateSound(1000);
            et_ageing_stock_taking_popup_barcode.setText("");
            et_ageing_stock_taking_popup_barcode.requestFocus();
            return false;
        }
        if (scan.length() > 30) {
            //okMessage("Stock Taking", "Please Double check the scanned barcode");
            tv_ageing_stock_taking_popup_result.setText("Please Double check the scanned barcode");
            vibrateSound(1000);
            et_ageing_stock_taking_popup_barcode.setText("");
            et_ageing_stock_taking_popup_barcode.requestFocus();
            return false;
        }

        tv_ageing_stock_taking_popup_last_barcode.setText("");
        tv_ageing_stock_taking_popup_last_rfid.setText("");
        tv_ageing_stock_taking_popup_last.setText(scan);
        et_ageing_stock_taking_popup_barcode.setText("");
        et_ageing_stock_taking_popup_barcode.requestFocus();

        if (ch_ageing_stock_taking_popup_main_server.isChecked()) {
            b_Result = objAgeingStockTakingControl.validateServerScan(scan);
            if (!b_Result) {
                //okMessage("Stock Taking", objGlobal.getErrorMessage());
                tv_ageing_stock_taking_popup_result.setText(objGlobal.getErrorMessage());
                vibrateSound(1000);
                return false;
            }
            scan = objAgeingStockTakingGlobal.getBarcode();
            rfid = objAgeingStockTakingGlobal.getRfid();
        }

        tv_ageing_stock_taking_popup_last_barcode.setText(objAgeingStockTakingDbManager.seperateBarcode(scan));
        tv_ageing_stock_taking_popup_last_rfid.setText(rfid);
        b_Result = objAgeingStockTakingDbManager.saveScanToLocaldb(scan, qty, zoneId, result, rfid);
        if (!b_Result) {
            //okMessage("Stock Taking", objGlobal.getErrorMessage());
            tv_ageing_stock_taking_popup_result.setText(objGlobal.getErrorMessage());
            vibrateSound(1000);
            return false;
        }

        if (ch_ageing_stock_taking_popup_main_server.isChecked()) {
            b_Result = objAgeingStockTakingDbManager.exportToMainServer(getActivity());
            if (!b_Result) {
                //okMessage("Stock Taking", objGlobal.getErrorMessage());
                tv_ageing_stock_taking_popup_result.setText(objGlobal.getErrorMessage());
                vibrateSound(1000);
                return false;
            }
        }

        b_Result = objAgeingStockTakingDbManager.loadScannedCountTotal();
        if (!b_Result) {
            //okMessage("Stock Taking", objGlobal.getErrorMessage());
            tv_ageing_stock_taking_popup_result.setText(objGlobal.getErrorMessage());
            vibrateSound(1000);
            return false;
        }
        b_Result = objAgeingStockTakingDbManager.loadScannedCountExportTotal();
        if (!b_Result) {
            //okMessage("Stock Taking", objGlobal.getErrorMessage());
            tv_ageing_stock_taking_popup_result.setText(objGlobal.getErrorMessage());
            vibrateSound(1000);
            return false;
        }

        b_Result = loadItemsStockTaking();
        if (!b_Result) {
            //okMessage("Stock Taking", objGlobal.getErrorMessage());
            tv_ageing_stock_taking_popup_result.setText(objGlobal.getErrorMessage());
            vibrateSound(1000);
            return false;
        }
        tv_ageing_stock_taking_total_scan.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScan()));
        tv_ageing_stock_taking_total_exported.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScanExport()));
        tv_ageing_stock_taking_total_pendingforexport.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScan() - objAgeingStockTakingGlobal.getTotalScanExport()));
        return true;
    }

    private class MyAgeingStockTakingItemAdp extends BaseAdapter {
        public ArrayList<AgeingStockTakingScanItems> listAgeingStockTakingScanItems;

        public MyAgeingStockTakingItemAdp(ArrayList<AgeingStockTakingScanItems> listAgeingStockTakingScanItems) {
            this.listAgeingStockTakingScanItems = listAgeingStockTakingScanItems;
        }

        @Override
        public int getCount() {
            return listAgeingStockTakingScanItems.size();
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
            View myView = mInflater.inflate(R.layout.ageing_stock_taking_items_ticket, null);
            final AgeingStockTakingScanItems s = listAgeingStockTakingScanItems.get(position);

            TextView tv_aging_stock_taking_itemdetails = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_itemdetails);
            tv_aging_stock_taking_itemdetails.setText(String.valueOf(s.itemcode));

            TextView tv_aging_stock_taking_date = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_date);
            tv_aging_stock_taking_date.setText(String.valueOf(s.date));

            TextView tv_aging_stock_taking_time = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_time);
            tv_aging_stock_taking_time.setText(String.valueOf(s.time));

            TextView tv_aging_stock_taking_result = (TextView) myView.findViewById(R.id.tv_aging_stock_taking_result);
            tv_aging_stock_taking_result.setText(String.valueOf(s.result));

            return myView;
        }
    }

    private boolean loadStockTakeItemsForDelete() {
        String zone = sp_ageing_stock_taking_delete_zone.getSelectedItem().toString();
        String itemcode = et_ageing_stock_taking_delete_itemcode.getText().toString().toUpperCase();
        try {
            listAgeingStockTakingReportsForDelete.clear();
            listAgeingStockTakingReportsForDelete = objAgeingStockTakingControl.loadAgingStockTakingForDelete(zone, itemcode);
            objAgeingStockTakingReportSearchItemForDeleteAdp = new AgeingStockTakingFragment.AgeingStockTakingReportSearchItemForDeleteAdp(listAgeingStockTakingReportsForDelete);
            lv_ageing_stock_delete_taking.setAdapter(objAgeingStockTakingReportSearchItemForDeleteAdp);
            tv_ageing_stock_taking_total_del_qty.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScanDelQty()));
            b_Result = loadItemsStockTaking();
            if (!b_Result) {
                okMessage("Stock Taking", objGlobal.getErrorMessage());
                return false;
            }
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("loadItemsStockTaking:catch: " + e);
            return false;
        }
    }

    private boolean loadItemsStockTaking() {
        try {
            listAgeingStockTakingScanItems.clear();
            listAgeingStockTakingScanItems = objAgeingStockTakingDbManager.loadAgingStockTakingItems("50", getActivity());
            objMyAgeingStockTakingItemAdp = new AgeingStockTakingFragment.MyAgeingStockTakingItemAdp(listAgeingStockTakingScanItems);
            lv_ageing_stock_taking.setAdapter(objMyAgeingStockTakingItemAdp);
            b_Result = objAgeingStockTakingDbManager.loadScannedCountTotal();
            if (!b_Result) {
                okMessage("Stock Taking", objGlobal.getErrorMessage());
                return false;
            }
            b_Result = objAgeingStockTakingDbManager.loadScannedCountExportTotal();
            if (!b_Result) {
                okMessage("Stock Taking", objGlobal.getErrorMessage());
                return false;
            }
            tv_ageing_stock_taking_total_scan.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScan()));
            tv_ageing_stock_taking_total_exported.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScanExport()));
            tv_ageing_stock_taking_total_pendingforexport.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScan() - objAgeingStockTakingGlobal.getTotalScanExport()));
            int bPer = objControls.getBatteryPercentage(getContext());
            tv_ageing_stock_taking_battery_percentage.setText(String.valueOf(bPer) + " %");
            if (bPer >= 20)
                tv_ageing_stock_taking_battery_percentage.setTextColor(Color.rgb(0, 145, 0));
            else
                tv_ageing_stock_taking_battery_percentage.setTextColor(Color.RED);
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage("loadItemsStockTaking:catch: " + e.toString());
            return false;
        }
    }

    private void exportToServer() {
        new AgeingStockTakingFragment.ExportToServer().execute();
    }

    private class ExportToServer extends AsyncTask<Void, Void, Integer> {
        private ProgressDialog dialog;

        public ExportToServer() {
            dialog = new ProgressDialog(getContext());
        }

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Export, Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... args) {
            try {
                b_Result = objAgeingStockTakingDbManager.exportToMainServer(getActivity());
                if (b_Result == false) {
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
                okMessage("Stock Taking:exportToMainServer", objGlobal.getErrorMessage());
            } else {
                b_Result = loadItemsStockTaking();
                if (!b_Result)
                    okMessage("Stock Taking:loadItemsStockTaking", objGlobal.getErrorMessage());
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
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
                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                assert v != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(duration);
                }
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
            okMessage("Error vibrateSound", e.toString());
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