package com.bflgroup.bflshop.ui.ageingstocktakingverification;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Global;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

public class StocktakingZoneFragment extends Fragment {

    Spinner sp_stocktaking_zone;
    TextView et_date_from;
    TextView et_date_to;
    private StocktakingZoneControl objStocktakingZoneControl = new StocktakingZoneControl();
    private StockTakingZoneGlobal objStockTakingZoneGlobal = StockTakingZoneGlobal.getInstance();
    private Global objGlobal = Global.getInstance();
    private boolean b_Result;
    DatePickerDialog datePicker;
    TextView tv_zone;
    TextView tv_Qty;
    TextView tv_Time;
    TextView et_User;
    TextView et_Manual;
    TextView tv_Diff;
    Button button_load;
   // Button button_diff;
    Button button_clear;
    Button button_save;
    DecimalFormat formatter00 = new DecimalFormat("00");


    public StocktakingZoneFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stocktaking_zone, container, false);
        sp_stocktaking_zone = view.findViewById(R.id.sp_stocktaking_zone);
        et_date_from = view.findViewById(R.id.et_date_from);
        et_date_to = view.findViewById(R.id.et_date_to);

        tv_zone = view.findViewById(R.id.tv_zone);
        tv_Qty = view.findViewById(R.id.tv_Qty);
        tv_Time = view.findViewById(R.id.tv_Time);
        et_User = view.findViewById(R.id.et_User);
        et_Manual = view.findViewById(R.id.et_Manual);
        tv_Diff = view.findViewById(R.id.tv_Diff);
        button_load = view.findViewById(R.id.button_load);
//        button_diff = view.findViewById(R.id.button_diff);
        button_clear = view.findViewById(R.id.button_clear);
        button_save = view.findViewById(R.id.button_save);


        b_Result = objStocktakingZoneControl.getStockTakeDate();
        if(!b_Result) {
            okMessage("Stock Taking:objStocktakingZoneControl.getStockTakeDate()",objGlobal.getErrorMessage());
        } else {
            et_date_from.setText(objStockTakingZoneGlobal.getDtFrom());
            et_date_to.setText(objStockTakingZoneGlobal.getDtTo());
        }


        final Calendar calendar = Calendar.getInstance();

        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);

        b_Result = objStocktakingZoneControl.loadZone();
        if (!b_Result) {
            okMessage("Stock Taking", objGlobal.getErrorMessage());
        } else {
            List<String> arr;
            ArrayAdapter<String> arrayAdp;
            arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objStockTakingZoneGlobal.getZoneList());
            sp_stocktaking_zone.setAdapter(arrayAdp);
        }



        et_date_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                datePicker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear++;
                                et_date_from.setText(formatter00.format(dayOfMonth) + "/" + formatter00.format(monthOfYear) + "/" + year);
                              //  b_Result = objStocktakingZoneControl.loadZone(et_date_from.getText().toString(), et_date_to.getText().toString());
                            }
                        }, mYear, mMonth, mDay);
                datePicker.show();
            }
        });

        et_date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                datePicker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                monthOfYear++;
                                et_date_to.setText(formatter00.format(dayOfMonth) + "/" + formatter00.format(monthOfYear) + "/" + year);
                              //  b_Result = objStocktakingZoneControl.loadZone(et_date_from.getText().toString(), et_date_to.getText().toString());
                            }
                        }, mYear, mMonth, mDay);
                datePicker.show();
            }
        });




        et_Manual.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here

                // yourEditText...
                String Diff ="";
                String manual_count = et_Manual.getText().toString();
                if(et_Manual.getText().equals("")){
                    et_Manual.setText("0");
                    manual_count="0";
                }
                try {
                    Diff = String.valueOf(Integer.parseInt(tv_Qty.getText().toString()) - Integer.parseInt(manual_count));
                }catch (Exception e){
                    manual_count="0";
                }

                tv_Diff.setText(Diff);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


//        et_Manual.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                et_Manual.setText("");
//            }
//        });

//        et_date_from.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                view.onTouchEvent(motionEvent);
//                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//                return objGlobal.getHideKeyPad();
//            }
//        });


        et_date_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // adding the selected date in the edittext
                        et_date_to.setText(dayOfMonth + "/" + (month + 01) + "/" + year);
                    }
                }, year, month, day);

                // set maximum date to be selected as today
                // datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());

                // show the dialog
                datePicker.show();
            }
        });


        sp_stocktaking_zone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                tv_zone.setText(sp_stocktaking_zone.getSelectedItem().toString());
                b_Result = objStocktakingZoneControl.loadQuantity(sp_stocktaking_zone.getSelectedItem().toString(), et_date_from.getText().toString(), et_date_to.getText().toString());
                if (!b_Result) {
                    okMessage("Stock Taking Quantity", objGlobal.getErrorMessage());
                } else {
                    List<String> arr;
                    ArrayAdapter<String> arrayAdp;
                    et_User.setText(String.valueOf(objStockTakingZoneGlobal.getUsername()));
                    tv_Qty.setText(String.valueOf(objStockTakingZoneGlobal.getQuantity()));
                    et_Manual.setText("");
                }
            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        button_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_zone.setText(sp_stocktaking_zone.getSelectedItem().toString());
                tv_zone.setText(sp_stocktaking_zone.getSelectedItem().toString());
                //et_User.setText(objGlobal.getUserName());

                b_Result = objStocktakingZoneControl.loadQuantity(sp_stocktaking_zone.getSelectedItem().toString(), et_date_from.getText().toString(), et_date_to.getText().toString());
                if (!b_Result) {
                    okMessage("Stock Taking Quantity", objGlobal.getErrorMessage());
                } else {
                    List<String> arr;
                    ArrayAdapter<String> arrayAdp;
                    et_User.setText(String.valueOf(objStockTakingZoneGlobal.getUsername()));
                    tv_Qty.setText(String.valueOf(objStockTakingZoneGlobal.getQuantity()));
                    et_Manual.setText("");
                }
                try {
//                    if (!et_Manual.getText().equals("")) {
//                        tv_Diff.setText(String.valueOf(Integer.parseInt(tv_Qty.getText().toString()) - Integer.parseInt(et_Manual.getText().toString())));
//                    } else {
//                        tv_Diff.setText("0");
//                    }
                }catch (Exception e){
                    okMessage("Error",e.toString());
                }
            }
        });

//        button_diff.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//
//                    String manual_count = et_Manual.getText().toString();
//                    if (et_Manual.getText().equals("")) {
//                        manual_count = "0";
//                    }
//                    String Diff = String.valueOf(Integer.parseInt(tv_Qty.getText().toString()) - Integer.parseInt(manual_count));
//
//                    tv_Diff.setText(Diff);
//                }catch(Exception e){
//                    okMessage("Error", e.toString());
//                }
//            }
//        });

        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                Clear();

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


        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setMessage("Are you sure to save?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (tv_Qty.getText().equals("0") || tv_Qty.getText().equals("") || et_Manual.getText().toString().trim().equals("")) {
                                    okMessage("Save Error", "Quantity or Manual count is not available");
                                } else {
                                    b_Result = objStocktakingZoneControl.saveRecord(sp_stocktaking_zone.getSelectedItem().toString(), et_User.getText().toString(), Integer.parseInt(tv_Qty.getText().toString()), Integer.parseInt(et_Manual.getText().toString()), getContext());
                                    if (!b_Result) {
                                        okMessage("Stock Taking Quantity", objGlobal.getErrorMessage());
                                    } else {
                                        okMessage("Successful", "Completed Successfully");
                                        Clear();
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
        });
        return view;
    }

    void Clear(){
        sp_stocktaking_zone.setSelection(0);
        tv_zone.setText("");
        tv_Qty.setText("");
        tv_Time.setText("");
        et_User.setText("");
        et_Manual.setText("");
        tv_Diff.setText("");
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

    void vibrate(int duration){
        Vibrator v = (Vibrator) getContext().getSystemService(Context. VIBRATOR_SERVICE );
        assert v != null;
        if (Build.VERSION. SDK_INT >= Build.VERSION_CODES. O ) {
            v.vibrate(VibrationEffect. createOneShot (duration , VibrationEffect. DEFAULT_AMPLITUDE )) ;
        } else {
            v.vibrate( duration ) ;
        }
    }






}
