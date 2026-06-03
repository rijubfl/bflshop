package com.bflgroup.bflshop.ui.discount;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Global;

import java.util.ArrayList;
import java.util.List;

public class AddDiscountFragment extends Fragment {

    Dialog myDialog;
    Global objGlobal=Global.getInstance();
    boolean result;
    String query;

    AddDiscountControl objAddDiscountControl=new AddDiscountControl();
    MyAddDiscountItemAdapter objMyAddDiscountItemAdapter;

    private ListView lv_add_discount_det;
    private EditText et_add_discount_itemcode;
    private Button bt_add_discount_scan;
    private Button bt_add_discount_save;
    private Button btn_grn_transfer_clear;
    private Spinner sp_add_disc_perc;

    ArrayList<AddDiscountScanItems> listGrnTransferScan = new ArrayList<AddDiscountScanItems>();

    public AddDiscountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_discount, container, false);

        lv_add_discount_det=(ListView) view.findViewById(R.id.lv_add_discount_det);
        et_add_discount_itemcode=(EditText) view.findViewById(R.id.et_add_discount_itemcode);
        bt_add_discount_scan=(Button) view.findViewById(R.id.bt_add_discount_scan);
        bt_add_discount_save=(Button) view.findViewById(R.id.bt_add_discount_save);
        btn_grn_transfer_clear=(Button) view.findViewById(R.id.btn_grn_transfer_clear);
        sp_add_disc_perc=(Spinner) view.findViewById(R.id.sp_add_disc_perc);

        getDiscountPercentage();

        sp_add_disc_perc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                okMessage("","1");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                okMessage("","2");
            }
        });

        et_add_discount_itemcode.setOnTouchListener(new View.OnTouchListener() {
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

        et_add_discount_itemcode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String iCode = seperateBarcode(et_add_discount_itemcode.getText().toString());
                    et_add_discount_itemcode.setText(iCode);
                    return true;
                }
                return false;
            }
        });

        bt_add_discount_scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                result=objAddDiscountControl.getDetail(et_add_discount_itemcode.getText().toString(),Float.parseFloat(sp_add_disc_perc.getSelectedItem().toString()));
                if(result==false) {
                    okMessage("", objGlobal.getErrorMessage());
                    vibrate(500);
                } else {
                    listGrnTransferScan=objAddDiscountControl.getAddDiscountScanItems();
                    objMyAddDiscountItemAdapter=new MyAddDiscountItemAdapter(listGrnTransferScan);
                    lv_add_discount_det.setAdapter(objMyAddDiscountItemAdapter);
                    et_add_discount_itemcode.setText("");
                    et_add_discount_itemcode.requestFocus();
                }
            }
        });

        bt_add_discount_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to save?")
                        .setTitle("Conformation")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result = objAddDiscountControl.addDiscountSave();
                                if (result == false) {
                                    okMessage("Add Discount:bt_add_discount_save", objGlobal.getErrorMessage());
                                    vibrate(500);
                                    bt_add_discount_save.requestFocus();
                                } else {
                                    result = objAddDiscountControl.clearAllScan();
                                    if (result == false) {
                                        okMessage("Add Discount:bt_add_discount_save,else", objGlobal.getErrorMessage());
                                        vibrate(500);
                                    } else {
                                        listGrnTransferScan=objAddDiscountControl.getAddDiscountScanItems();
                                        objMyAddDiscountItemAdapter=new MyAddDiscountItemAdapter(listGrnTransferScan);
                                        lv_add_discount_det.setAdapter(objMyAddDiscountItemAdapter);
                                        et_add_discount_itemcode.setText("");
                                        okMessage("Add Discount", "Done");
                                        et_add_discount_itemcode.requestFocus();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { }
                        })
                        .show();
            }
        });

        btn_grn_transfer_clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                alert.setMessage("Are You sure to clear all?")
                        .setTitle("Conformation")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result=objAddDiscountControl.clearAllScan();
                                if(result==false) {
                                    okMessage("Add Discount:btn_grn_transfer_clear", objGlobal.getErrorMessage());
                                    vibrate(500);
                                }
                                listGrnTransferScan=objAddDiscountControl.getAddDiscountScanItems();
                                objMyAddDiscountItemAdapter=new MyAddDiscountItemAdapter(listGrnTransferScan);
                                lv_add_discount_det.setAdapter(objMyAddDiscountItemAdapter);
                                et_add_discount_itemcode.setText("");
                                et_add_discount_itemcode.requestFocus();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { }
                        })
                        .show();
            }
        });
        return view;
    }

    private void getDiscountPercentage(){
        List<String> arr;
        arr=new ArrayList<String>();
        arr.add("0");
        arr.add("30");
        arr.add("50");
        ArrayAdapter<String> arrayAdp=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,arr);
        sp_add_disc_perc.setAdapter(arrayAdp);
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

    private class MyAddDiscountItemAdapter extends BaseAdapter {
        public ArrayList<AddDiscountScanItems> listAddDiscountScanItems;

        public MyAddDiscountItemAdapter(ArrayList<AddDiscountScanItems>  listAddDiscountScanItems) {
            this.listAddDiscountScanItems=listAddDiscountScanItems;
        }

        @Override
        public int getCount() {
            return listAddDiscountScanItems.size();
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
            View myView = mInflater.inflate(R.layout.add_discount_ticket, null);
            final AddDiscountScanItems s = listAddDiscountScanItems.get(position);

            TextView tv_add_discount_ticket_itemcode=(TextView) myView.findViewById(R.id.tv_add_discount_ticket_itemcode);
            tv_add_discount_ticket_itemcode.setText(String.valueOf(s.itemCode));

            TextView tv_add_discount_ticket_disc_price=(TextView)myView.findViewById(R.id.tv_add_discount_ticket_disc_price);
            tv_add_discount_ticket_disc_price.setText(String.valueOf(s.price));

            TextView tv_add_discount_ticket_disc_per=(TextView)myView.findViewById(R.id.tv_add_discount_ticket_disc_per);
            tv_add_discount_ticket_disc_per.setText(String.valueOf(s.discPer));

            Button bt_add_discount_delete=(Button)myView.findViewById(R.id.bt_add_discount_delete);
            bt_add_discount_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                    alert.setMessage("Do you want delete the selected item?")
                            .setTitle("Conformation")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    result=objAddDiscountControl.deleteSingle(String.valueOf(s.itemCode));
                                    if(result==false) {
                                        okMessage("Add Discount:bt_add_discount_delete", objGlobal.getErrorMessage());
                                        vibrate(500);
                                    } else {
                                        listGrnTransferScan = objAddDiscountControl.getAddDiscountScanItems();
                                        objMyAddDiscountItemAdapter = new MyAddDiscountItemAdapter(listGrnTransferScan);
                                        lv_add_discount_det.setAdapter(objMyAddDiscountItemAdapter);
                                        et_add_discount_itemcode.setText("");
                                        et_add_discount_itemcode.requestFocus();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) { }
                            })
                            .show();
                }
            });
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