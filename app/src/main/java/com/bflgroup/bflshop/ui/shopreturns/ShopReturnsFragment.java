package com.bflgroup.bflshop.ui.shopreturns;

import static com.bflgroup.bflshop.ui.shopreturns.ShopReturnsGlobal.getCount;
import static com.bflgroup.bflshop.ui.shopreturns.ShopReturnsGlobal.setCount;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Controls;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ShopReturnsFragment extends Fragment {


    public Spinner sp_category_spinner;
    private TextView tv_shop_returns_category_name;
    private EditText et_scan_itemcode;
    private TextView error_message;
    private TextView last_scanned_itemcode;
    private TextView tv_count;
    private Button btn_close;
//    private Spinner sp_remarks;
    private boolean b_Result;
    private String lastitemcode = "";
    private String itemcode = "";
    private Button bt_scan;
    private ListView lv_shop_returns_scanitems;
    private Button bt_shop_returns_add;
    private EditText et_remarks;

    AutoCompleteTextView et_remarks_item;
//    private EditText et_remarks_item;
    private TextView sp_select_shop;


    //String selected_cat = "";
    String selected_shopname = "";
    private Button bt_clearall;
    private Button bt_Scan_item;
    private ProgressBar pr_shop_returns;
    Integer count = 0;
    ShopReturnsSharedRef objShopReturnsSharedRef;


    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private ShopReturnsGlobal objShopReturnsGlobal = ShopReturnsGlobal.getInstance();
    ArrayList<AddScanItemDetails> ObjAddScanItemDetails = new ArrayList<AddScanItemDetails>();
    MyShopResturnsStatusAdp objShopReturnsAdp = null;
    Dialog dialog;

    Controls objControls = new Controls();
    private Global objGlobal = Global.getInstance();
    ShopReturnsControl objShopReturnsControl = new ShopReturnsControl();


    public ShopReturnsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_returns, container, false);

        try {

            sp_category_spinner = view.findViewById(R.id.sp_shop_returns_category);
            tv_shop_returns_category_name = view.findViewById(R.id.tv_shop_returns_category_name);
            bt_scan = view.findViewById(R.id.bt_scan_itemcode);
            lv_shop_returns_scanitems = view.findViewById(R.id.lv_shop_returns_scanitems);
            bt_shop_returns_add = view.findViewById(R.id.bt_shop_returns_add);
            et_remarks = view.findViewById(R.id.et_remarks);
            sp_select_shop = view.findViewById(R.id.sp_select_shop);
            bt_clearall = view.findViewById(R.id.btn_clear);
            tv_count = view.findViewById(R.id.tv_count);
            pr_shop_returns = (ProgressBar) view.findViewById(R.id.pr_shop_returns);

            //tv_count.setText(getCount());


            sp_select_shop.setVisibility(View.GONE);
            try {
                if (objShopReturnsControl.checkStocktakeDate()) {
                    okMessage("Alert", "Not allowed to do a Return 2 days prior to the stocktake");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            objShopReturnsSharedRef = new ShopReturnsSharedRef(getContext());
            try {
                if (objShopReturnsControl.checkStocktakeDate()) {
                    okMessage("Alert", "Not allowed to do a Return 2 days prior to the stocktake");

                } else {
                    List<String> arr1 = objShopReturnsControl.loadCategory();
                    ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
                    sp_category_spinner.setAdapter(arrayAdp1);
                    if (!objShopReturnsSharedRef.LoadCategory().isEmpty()) {
                        sp_category_spinner.setSelection(arrayAdp1.getPosition(objShopReturnsSharedRef.LoadCategory()));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
//        String[] catItems = {"---Select One---","Damaged In Shop","Recalls","Non Saleable Items", "Online Returns","No Barcode, No RFID", "Quality issues","Shop to Shop Transfer (Direct)","Repair" };
//        ArrayAdapter<String> SpinerAdapter = new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_dropdown_item_1line, catItems){
//            @Override
//            public boolean isEnabled(int position){
//                // Disable the first item from Spinner
//                // First item will be use for hint
//                return position != 0;
//            }
//            @Override
//            public View getDropDownView(
//                    int position, View convertView,
//                    @NonNull ViewGroup parent) {
//
//                // Get the item view
//                View view = super.getDropDownView(
//                        position, convertView, parent);
//                TextView textView = (TextView) view;
//                if(position == 0){
//                    // Set the hint text color gray
//                    textView.setTextColor(Color.GRAY);
//                }
//                else { textView.setTextColor(Color.BLACK); }
//                return view;
//            }
//        };
//        sp_category_spinner.setAdapter(SpinerAdapter);


            sp_category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    //itempos = sp_category_spinner.getSelectedItemPosition();
                    tv_shop_returns_category_name.setText(objShopReturnsControl.loadCategoryName(String.valueOf(sp_category_spinner.getSelectedItem())));
                    //Log.e("spinner item selected", itempos+"");
                    if (tv_shop_returns_category_name.getText().toString().contains("Shop Transfer")) {
                        sp_select_shop.setVisibility(View.VISIBLE);
                        try {
                            final ArrayList<String> arraylist = objShopReturnsControl.ShopNames();//

                            sp_select_shop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Initialize dialog
                                    dialog = new Dialog(getContext());
                                    // set custom dialog
                                    dialog.setContentView(R.layout.searchable_spinner);
                                    // set custom height and width
                                    dialog.getWindow().setLayout(500, 1000);
                                    // set transparent background
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    // show dialog
                                    dialog.show();
                                    // Initialize and assign variable
                                    EditText editText = dialog.findViewById(R.id.edit_text);
                                    ListView listView = dialog.findViewById(R.id.list_view);

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arraylist);
                                    // set adapter
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
                                            // when item selected from list
                                            // set selected item on textView
                                            sp_select_shop.setText(adapter.getItem(position));

                                            // Dismiss dialog
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });

                        } catch (Exception e) {
                            Log.e("ALERT", e.getMessage().toString());
                        }


//
                    } else {
                        sp_select_shop.setVisibility(View.GONE);
                        sp_select_shop.setText("");

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            if (objShopReturnsSharedRef.LoadCategory() != "") {

                Log.e("Load Cat", objShopReturnsSharedRef.LoadCategory());
                // Log.e("Cate", SpinerAdapter.getPosition(ShopReturnsSharedRef.LoadCategory())+"");
                //sp_category_spinner.setSelection(SpinerAdapter.getPosition(objShopReturnsSharedRef.LoadCategory()));


                //sp_category_spinner.setEnabled(false);
                // selected_cat = objShopReturnsSharedRef.LoadCategory();
                try {
                    tv_shop_returns_category_name.setText(objShopReturnsSharedRef.LoadCategoryName());
                    sp_select_shop.setText(objShopReturnsSharedRef.LoadShopName());
                    sp_category_spinner.setEnabled(false);
                    sp_select_shop.setEnabled(false);
                    ObjAddScanItemDetails = objShopReturnsControl.gettmpitem();
                    objShopReturnsAdp = new MyShopResturnsStatusAdp(ObjAddScanItemDetails);
                    lv_shop_returns_scanitems.setAdapter(objShopReturnsAdp);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


            bt_scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sp_category_spinner.getSelectedItemId() == 0 || tv_shop_returns_category_name.getText().toString().isEmpty()) {
                        okMessage("<font color='red'>ALERT</font>", "Please select Category!");
                    } else {
                        if (tv_shop_returns_category_name.getText().toString().contains("Shop Transfer")) {
                            if (sp_select_shop.getText().toString().isEmpty()) {
                                okMessage("<font color='red'>ALERT</font>", "Please select shopname!");
                            } else {
                                objShopReturnsSharedRef.SaveCategory(String.valueOf(sp_category_spinner.getSelectedItem()));
                                objShopReturnsSharedRef.SaveCategoryName(String.valueOf(tv_shop_returns_category_name.getText()));
                                objShopReturnsSharedRef.SaveShopName(String.valueOf(sp_select_shop.getText()));
                                sp_category_spinner.setEnabled(false);
                                sp_select_shop.setEnabled(false);
                                openPopupInvoiceItems();
                            }
                        } else {
                            objShopReturnsSharedRef.SaveCategory(String.valueOf(sp_category_spinner.getSelectedItem()));
                            objShopReturnsSharedRef.SaveCategoryName(String.valueOf(tv_shop_returns_category_name.getText()));
                            objShopReturnsSharedRef.SaveShopName(String.valueOf(sp_select_shop.getText()));
                            sp_category_spinner.setEnabled(false);
                            sp_select_shop.setEnabled(false);
                            openPopupInvoiceItems();
                        }
                    }
                }
            });
            bt_shop_returns_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_shopname = sp_select_shop.getText().toString();
                    String remarks = et_remarks.getText().toString();
                    String shopreturnCat = objShopReturnsControl.loadCategoryName(String.valueOf(sp_category_spinner.getSelectedItem()));
                    if (objShopReturnsAdp != null && sp_category_spinner.getSelectedItemId() != 0) {
                        Log.e("Here", "clicked inside");
                        if (objShopReturnsAdp != null) {
                            if (shopreturnCat.equals("Online Returns"))
                                selected_shopname = "ONLINE";
                            if (sp_select_shop.getVisibility() == View.VISIBLE && sp_select_shop.getText().toString() != "") {
                                progressVisivle(true);
                                try {
                                    if (objShopReturnsControl.InsertAddDetails(remarks, shopreturnCat, selected_shopname, getActivity())) {
                                        okMessage("<font color='green'>SUCCESS</font>", "Added Successfully Entry no is - " + ShopReturnsGlobal.getEntryNo());
                                        clear();
                                    } else {
                                        okMessage("<font color='red'>ALERT</font>", objGlobal.getErrorMessage());
                                    }
                                } catch (Exception e) {
                                    okMessage("Error ", e.getMessage().toString());
                                }
                            } else if (sp_select_shop.getVisibility() == View.GONE) {
                                progressVisivle(true);
                                try {

                                    if (objShopReturnsControl.InsertAddDetails(remarks, shopreturnCat, selected_shopname, getActivity())) {
                                        okMessage("<font color='green'>SUCCESS</font>", "Added Successfully Entry no is - " + ShopReturnsGlobal.getEntryNo());

                                        clear();
                                    } else {
                                        okMessage("<font color='red'>ALERT</font>", objGlobal.getErrorMessage());
                                    }
                                } catch (Exception e) {
                                    okMessage("Error ", e.getMessage().toString());
                                }
                            } else {
                                okMessage("<font color='red'>ALERT</font>", "please select shopname");
                            }
                        } else {
                            okMessage("<font color='red'>ALERT</font>", "Empty Fields cannot be added!");
                        }

                    } else {
                        okMessage("<font color='red'>ALERT</font>", "Empty Fields cannot be added!");
                    }
                }
            });


            bt_clearall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are you sure to clear all?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clear();


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
            try {
                count = Integer.valueOf(objShopReturnsControl.getCount().toString());
                count = getCount();
                tv_count.setText(count + "");
            } catch (SQLException e) {
                okMessage("Alert", e.getMessage());
            }

        } catch (Exception e) {
            okMessage(e.getMessage(), e.toString());
        }
        return view;
    }



    private void openPopupInvoiceItems() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_shop_returns_itemcode);

        et_scan_itemcode = (EditText) myDialog.findViewById(R.id.et_scan_itemcode);
        bt_Scan_item = (Button) myDialog.findViewById(R.id.bt_Scan_item);
        error_message = (TextView) myDialog.findViewById(R.id.tv_error_message);
        last_scanned_itemcode = (TextView) myDialog.findViewById(R.id.tv_last_scanned_itemcode);
        et_remarks_item = (AutoCompleteTextView) myDialog.findViewById(R.id.et_remarks_item);
        btn_close = (Button) myDialog.findViewById(R.id.btn_close);
//        sp_remarks = (Spinner) myDialog.findViewById(R.id.sp_remarks);


        if(sp_category_spinner.getSelectedItem().toString().equals("Non Saleable Items") || sp_category_spinner.getSelectedItem().toString().equals("Damaged In Shop")|| sp_category_spinner.getSelectedItem().toString().equals("Quality issues")|| sp_category_spinner.getSelectedItem().toString().equals("Repair")  ) {

            et_remarks_item.setVisibility(View.VISIBLE);

        }else{
            et_remarks_item.setVisibility(View.GONE);
        }


        try {
            ArrayList<String> arrayList = objShopReturnsControl.loadRemarks();
            ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arrayList);
            et_remarks_item.setThreshold(1);
            et_remarks_item.setAdapter(arrayAdp1);
        }catch(Exception e){
            Log.e("Alert",e.getMessage().toString());
        }


        et_scan_itemcode.setFocusable(true);
        et_scan_itemcode.requestFocus();

//        et_scan_itemcode.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                view.onTouchEvent(motionEvent);
//                InputMethodManager imm = (InputMethodManager) myDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//                return objGlobal.getHideKeyPad();
//            }
//        });

        //

        bt_Scan_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    scanItemcode();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        et_scan_itemcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        scanItemcode();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }

    private boolean scanItemcode() throws SQLException {
        String Itemcode = et_scan_itemcode.getText().toString();
        int i;
        String itemcode_scan = "", trfNo = "",sprice="";
        if (et_scan_itemcode.getText().toString().contains("/")) {
            String[] scanAr = et_scan_itemcode.getText().toString().split("/");
            itemcode_scan = scanAr[0];
            sprice= scanAr[1];
            trfNo = scanAr[2];
        } else {
            itemcode_scan = et_scan_itemcode.getText().toString();
            trfNo = "";
        }
        String  itemcode1 = "";
        if(itemcode_scan.charAt(0) == '0'){
            itemcode1 = itemcode_scan.substring(1);
        }else{
            itemcode1 = itemcode_scan;
        }

        String shopreturnCat=tv_shop_returns_category_name.getText().toString();
        if(shopreturnCat.isEmpty()){
            error_message.setText(Html.fromHtml("<font color='red'>Please select shop return category</Font>"));
            return false;
        }

        if (itemcode1.isEmpty()) {
            // okMessage("Alert", "Please scan Itemcode");
            error_message.setText("Please Scan ItemCode!");
            et_scan_itemcode.setText("");
            et_scan_itemcode.requestFocus();
            return false;
        }
        ObjAddScanItemDetails = new ArrayList<>();
        b_Result = objShopReturnsControl.scanItemcode(itemcode1.replace("\n", ""),sprice,String.valueOf(sp_category_spinner.getSelectedItem()),sp_select_shop.getText().toString());
        if (b_Result) {
            itemcode = itemcode1.replace("\n", "");
            Log.e("Msg","valid id");

            lastitemcode = itemcode;
            last_scanned_itemcode.setText(lastitemcode);
            error_message.setText("");
            try {
                if(shopreturnCat.equals("Non Saleable Items") || shopreturnCat.equals("Damaged In Shop")|| shopreturnCat.equals("Quality issues")|| shopreturnCat.equals("Repair")  ) {
                    if (!et_remarks_item.getText().equals("")) {
                        ObjAddScanItemDetails = objShopReturnsControl.additemcode(itemcode,sprice, getContext(), shopreturnCat, et_remarks_item.getText().toString());
                        objShopReturnsAdp = new MyShopResturnsStatusAdp(ObjAddScanItemDetails);
                        lv_shop_returns_scanitems.setAdapter(objShopReturnsAdp);
                    }else{
                        error_message.setText(Html.fromHtml("<font color='red'>Empty Remarks not Allowed</font>"));
                    }
                }else{
                    ObjAddScanItemDetails = objShopReturnsControl.additemcode(itemcode, sprice, getContext(), shopreturnCat, et_remarks_item.getText().toString());
                    objShopReturnsAdp = new MyShopResturnsStatusAdp(ObjAddScanItemDetails);
                    lv_shop_returns_scanitems.setAdapter(objShopReturnsAdp);
                }

                try {
                    count = Integer.valueOf(objShopReturnsControl.getCount().toString());
                    count = getCount();
                    tv_count.setText(count+"");
                    if(objShopReturnsGlobal.getMessage().equals("Itemcode Added")){
                        error_message.setText(Html.fromHtml("<font color='green'>" + objShopReturnsGlobal.getMessage() + "</Font>"));
                    }else {
                        error_message.setText(Html.fromHtml(objShopReturnsGlobal.getMessage()));
                    }
                } catch (SQLException e) {
                    error_message.setText( e.getMessage());
                    //okMessage("Alert",e.getMessage());
                }

                if (objShopReturnsAdp != null) {
                   // selected_cat = ((selected_cat == "Shop to Shop Transfer (Direct)") ? "Shop Transfer" : selected_cat);
              //      objShopReturnsSharedRef.SaveCategory(String.valueOf(sp_category_spinner.getSelectedItem()));
//                    sp_category_spinner.setEnabled(false);
//                    sp_category_spinner.setClickable(false);
                    et_scan_itemcode.requestFocus();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            ObjAddScanItemDetails = objShopReturnsControl.gettmpitem();
            objShopReturnsAdp = new MyShopResturnsStatusAdp(ObjAddScanItemDetails);
            lv_shop_returns_scanitems.setAdapter(objShopReturnsAdp);
            et_scan_itemcode.setText("");
            et_remarks_item.setText("");
            // error_message.setText("");
            et_scan_itemcode.requestFocus();
            return true;

        }
        else{
            Log.e("Alert","Pls enter valid id");
            error_message.setText(objGlobal.getErrorMessage() + " - " + et_scan_itemcode.getText().toString());
            et_scan_itemcode.setText("");
            // error_message.setText("");
            et_scan_itemcode.requestFocus();
            return false;
        }
    }

    private void progressVisivle(boolean vl) {
        if (vl) {
            pr_shop_returns.setVisibility(ProgressBar.VISIBLE);
            Handler handler = new Handler();

// Create and start a new Thread
            new Thread(new Runnable() {
                public void run() {
                    try{
                        Thread.sleep(5000);
                    }
                    catch (Exception e) { } // Just catch the InterruptedException

                    // Now we use the Handler to post back to the main thread
                    handler.post(new Runnable() {
                        public void run() {
                            // Set the View's visibility back on the main UI Thread
                            pr_shop_returns.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }).start();
        } else {
            pr_shop_returns.setVisibility(ProgressBar.INVISIBLE);
        }

    }




    private void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(Html.fromHtml(title));
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    private void clear(){
        if(objShopReturnsControl.clearTable()){
            lv_shop_returns_scanitems.setAdapter(null);
        }
        progressVisivle(false);
        objShopReturnsAdp = null;
//        sp_select_shop.setSelection(0);
//        sp_select_shop.setAdapter(null);
        sp_select_shop.setVisibility(View.GONE);
        sp_category_spinner.setSelection(0);
        count = 0;
        setCount(count);
        tv_count.setText("");
        et_remarks.setText("");
        sp_select_shop.setVisibility(View.GONE);
        sp_select_shop.setText("");
//        sp_category_spinner.setEnabled(true);
//        sp_category_spinner.setClickable(true);
        objShopReturnsSharedRef.SaveCategory("");
        objShopReturnsSharedRef.SaveCategoryName("");
        objShopReturnsSharedRef.SaveShopName("");
        sp_category_spinner.setEnabled(true);
        sp_select_shop.setEnabled(true);
    }


    private class MyShopResturnsStatusAdp extends BaseAdapter {
        public  ArrayList<AddScanItemDetails> AddScanItemDetailsList;

        public MyShopResturnsStatusAdp(ArrayList<AddScanItemDetails> AddScanItemDetails) {
            this.AddScanItemDetailsList = AddScanItemDetails;
        }

        @Override
        public int getCount() {
            return AddScanItemDetailsList.size();
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
            View myView = mInflater.inflate(R.layout.shop_returns_details, null);
            final AddScanItemDetails s = AddScanItemDetailsList.get(position);

            TextView tv_srno = (TextView) myView.findViewById(R.id.tv_srno) ;
            tv_srno.setText(String.valueOf(s.SrNo));
            TextView tv_Itemcode_details = (TextView) myView.findViewById(R.id.tv_Itemcode_details);
            tv_Itemcode_details.setText(String.valueOf(s.ItemCode));
            TextView tv_toteid_details = (TextView) myView.findViewById(R.id.tv_description);
            tv_toteid_details.setText(String.valueOf(s.Description));
            TextView tv_qty = (TextView) myView.findViewById(R.id.tv_qty);
            tv_qty.setText(String.valueOf(s.Qty));
            TextView tv_sprice = (TextView) myView.findViewById(R.id.tv_sprice);
            tv_sprice.setText(String.valueOf(s.Salesprice));
            return myView;



        }


    }

}