package com.bflgroup.bflshop.ui.ageingstocktaking.rfid;

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
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Controls;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.rfidreadercw.UhfInfo;
import com.bflgroup.bflshop.rfidreadercw.tools.NumberTool;
import com.bflgroup.bflshop.rfidreadercw.tools.StringUtils;
import com.bflgroup.bflshop.rfidreadercw.tools.UIHelper;
import com.bflgroup.bflshop.ui.ageingstocktaking.model.AgeingStockTakingReportsForDelete;
import com.bflgroup.bflshop.ui.ageingstocktaking.model.AgeingStockTakingScanItems;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.interfaces.IUHFInventoryCallback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StocktakeRfidFragment extends Fragment {
    private TextView tv_ageing_stock_taking_date;
    private TextView tv_ageing_stock_taking_user;
    private TextView tv_ageing_stock_taking_shop;
    boolean clicked = false;
    boolean clicked_dialog = false;
    boolean CloseDialog = false;
    private ProgressBar spinner;


    private RecyclerView lv_popup_rfid_missing;
    private ImageView iv_item_image;
    private TextView tv_text_itemcode;
    private TextView tv_text_description;
    private RecyclerView rv_popup_rfid_missing_items;
    RecyclerView lv_popup_rfid_missing_department;
    RecyclerView lv_popup_rfid_missing_division;
    private ListView lv_popup_rfid_missing1;
    private Button bt_popup_rfid_close;
    private Button bt_popup_rfid_close_dept;
    private TextView Department_Txt;
    private Button bt_popup_rfid_close1;
    private Button bt_popup_rfid_back;
    public boolean loopFlag = false;

    private AudioManager am;
   // private TextView tv_ageing_stock_taking_zone;
   public static ArrayList<String> epcTidUser = new ArrayList<>();
    public static HashMap<String, String> map;
   private SoundPool soundPool;
    public RFIDWithUHFUART mReader;
    public static final String TAG_EPC = "tagEPC";
    public static final String TAG_EPC_TID = "tagEpcTID";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_RSSI = "tagRssi";
    public UhfInfo uhfInfo=new UhfInfo();
    public static List<String> tempDatas = new ArrayList<>();
    private int total;

    private int  selectItem=-1;

    private float volumnRatio;

    ArrayList<RfidMissingItems> listRfidMissingItems = new ArrayList<>();

    ArrayList<RfidMissingDepartment> listRfidMissingDepartment = new ArrayList<>();
    ArrayList<RfidMissingDivision> listRfidMissingDivision = new ArrayList<>();
    ArrayList<RfidMissingitemcodes> listRfidMissingItemcodes = new ArrayList<>();
    private Button bt_ageing_stock_taking_add;
    HashMap<Integer, Integer> soundMap = new HashMap<>();

    private Button bt_ageing_stock_taking_insert;
    private long time;
    private TextView tv_ageing_stock_taking_battery_percentage;
    public ArrayList<HashMap<String, String>> tagList = new ArrayList<HashMap<String, String>>();
    private EditText et_ageing_stock_taking_popup_barcode;
    private TextView tv_ageing_stock_taking_popup_last;
    private TextView tv_ageing_stock_taking_popup_last_barcode;
    private TextView tv_ageing_stock_taking_popup_last_rfid;
    private TextView tv_ageing_stock_taking_popup_result;
    private Button bt_ageing_stock_taking_popup_close;
    private Button bt_ageing_stock_taking_popup_add;
    private CheckBox ch_ageing_stock_taking_popup_main_server;
    private EditText et_ageing_stock_taking_popup_password;

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
  //  MyRfidTransferScanRfidsAdp objMyGrnRfidTransferScanRfidsAdp;
    MyRfidMissingItemcodeAdp objMyRfidItemcodeAdp;
    MyRfidTransferScanRfidsAdp objMyGrnRfidTransferScanRfidsAdp;
    MyRfidTransferScanRfidsAdp1 objMyGrnRfidTransferScanRfidsAdp1;
    DecimalFormat formatter = new DecimalFormat("###,###");
    Dialog myDialogRfidScan;
    private boolean b_Result;

    private TextView tv_popup_grn_rfid_transfer_scantime;
    private TextView tv_popup_grn_rfid_transfer_count;
    private TextView tv_popup_grn_rfid_transfer_epc_count;
    private TextView tv_popup_grn_rfid_transfer_total;
    private RadioButton rb_popup_grn_rfid_transfer_single;
    private RadioButton rb_popup_grn_rfid_transfer_auto;
    private Button bt_popup_grn_rfid_transfer_connect;
    private Button bt_popup_grn_rfid_transfer_start;
    private Button bt_popup_grn_rfid_transfer_options;
    private Button bt_refresh;
    private ListView lv_popup_grn_rfid_transfer_rfids;

    private Button bt_popup_grn_rfid_transfer_clear;
    private Button bt_popup_grn_rfid_transfer_close;

    private TextView et_RfidscanQty;
    private TextView et_dg1_sysqty;
    private TextView et_totlqty;
    private Button bt_excess_rfid;
    private Button bt_missingRfid_qty;
    private TextView et_totalsys_qty;
    private TextView et_sys_qty;
    private Spinner sp_level1_category;
    private TextView sp_level2_category;

    MyRfidMissingAdp objMyRfidMissingAdp;
    MyRfidMissingDivisionAdp objMyRfidMissingDivisionAdp;
    MyRfidMissingDepartmentAdp objMyRfidMissingDepartmentAdp;

    final String[][] langArray = {null};
    final ArrayList<String>[] arr1 = new ArrayList[]{null};
    ArrayList<Integer>[] langList = new ArrayList[]{new ArrayList<>()};
    final boolean[][] selectedCat = {null};


    public StocktakeRfidFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ageing_stock_taking_rfid, container, false);

        tv_ageing_stock_taking_date = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_date);
        tv_ageing_stock_taking_user = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_user);
        tv_ageing_stock_taking_shop = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_shop);
        et_RfidscanQty = (TextView) view.findViewById(R.id.et_RfidscanQty);
        et_dg1_sysqty = (TextView) view.findViewById(R.id.et_dg1_sysqty);
        et_totlqty = (TextView) view.findViewById(R.id.et_totlqty);
        bt_excess_rfid = (Button) view.findViewById(R.id.bt_excess_rfid);
        et_sys_qty = (TextView) view.findViewById(R.id.et_sys_qty);
        et_totalsys_qty = (TextView) view.findViewById(R.id.et_totalsys_qty);
        bt_missingRfid_qty = (Button) view.findViewById(R.id.bt_missingRfid_qty);
        bt_ageing_stock_taking_add = (Button) view.findViewById(R.id.bt_ageing_stock_taking_add);
        bt_ageing_stock_taking_insert = (Button) view.findViewById(R.id.bt_ageing_stock_taking_insert);
        tv_ageing_stock_taking_battery_percentage = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_battery_percentage);
        sp_level1_category = (Spinner) view.findViewById(R.id.sp_level1_category);
        sp_level2_category = (TextView) view.findViewById(R.id.sp_level2_category);
        bt_refresh = (Button) view.findViewById(R.id.bt_refresh);

        spinner = (ProgressBar)view.findViewById(R.id.progressBar1);
        spinner.setVisibility(view.GONE);
        tv_ageing_stock_taking_shop.setText(objPosGlobal.getShopName());
        tv_ageing_stock_taking_user.setText(objGlobal.getUserName());
        tv_ageing_stock_taking_date.setText(objGlobal.getServerDate());
        objAgeingStockTakingDbManager = new AgeingStockTakingDbManager(getContext());
        objMyGrnRfidTransferScanRfidsAdp=new MyRfidTransferScanRfidsAdp(getContext());

        final ArrayList<ColorVO> colorList = new ArrayList<ColorVO>();



        b_Result = loadItemsStockTaking();
        if (!b_Result) {
            okMessage("Stock Taking", objGlobal.getErrorMessage());
        }
        List<String> arr2 = objAgeingStockTakingControl.loadCategory();
        ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, arr2);

        // ArrayList<String> arraylist = objShopReturnsControl.loadCategory();

        sp_level1_category.setAdapter(arrayAdp1);
        sp_level1_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here


                    arr1[0] = objAgeingStockTakingControl.loadSubCategory(sp_level1_category.getSelectedItem().toString());
                    langList[0] = new ArrayList<>();

                    sp_level2_category.setText("");
                langArray[0] = new String[arr1[0].size()];

                for (int i = 0; i < arr1[0].size(); i++) {
                    langArray[0][i] = arr1[0].get(i);

                }
                selectedCat[0] = new boolean[arr1[0].size()];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        bt_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp_level1_category.getSelectedItemId() == 0){
                    okMessage("Alert","Select the Display Group L1");
                }
                else{
                    objAgeingStockTakingControl.loadRfIdDetails(sp_level1_category.getSelectedItem().toString(),sp_level2_category.getText().toString());
                }
                objAgeingStockTakingControl.UserCategory(sp_level1_category.getSelectedItem().toString(),sp_level2_category.getText().toString());
                et_RfidscanQty.setText(objAgeingStockTakingGlobal.getrfidScanQty() + "");
                et_dg1_sysqty.setText(objAgeingStockTakingGlobal.getDg1TotalQty() + "");
                et_totlqty.setText(objAgeingStockTakingGlobal.getTotalScanQty() + "");
                et_sys_qty.setText(objAgeingStockTakingGlobal.getRFIDsysQty() + "");
                et_totalsys_qty.setText(objAgeingStockTakingGlobal.gettotalsys() + "");
                bt_missingRfid_qty.setText(objAgeingStockTakingGlobal.gettotaldiffqty() + "");
                bt_excess_rfid.setText(objAgeingStockTakingGlobal.gettotalexcessqty() + "");
                Toast.makeText(getContext(), "Data Refreshed", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        final ListView listView = builder.create().getListView();
        //boolean selectAll = true;
        // ListView Item Click Listener that enables "Select all" choice
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            boolean selectAll = true;
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                boolean isChecked = listView.isItemChecked(position);
//                if (position == 0) {
//                    if(selectAll) {
//                        for (int i = 1; i < langList.length; i++) { // we start with first element after "Select all" choice
//                            if (isChecked && !listView.isItemChecked(i)
//                                    || !isChecked && listView.isItemChecked(i)) {
//                                listView.performItemClick(listView, i, 0);
//                            }
//                        }
//                    }
//                } else {
//                    if (!isChecked && listView.isItemChecked(0)) {
//                        // if other item is unselected while "Select all" is selected, unselect "Select all"
//                        // false, performItemClick, true is a must in order for this code to work
//                        selectAll = false;
//                        listView.performItemClick(listView, 0, 0);
//                        selectAll = true;
//                    }
//                }
//            }
//        });



        sp_level2_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Initialize alert dialog

                // set title
                builder.setTitle("Select Category");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(langArray[0], selectedCat[0], new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                        if(i==0 && b==true){
                            for (int j = 0; j < langArray[0].length; j++) {
                                ColorVO colorVO = new ColorVO();
                                colorVO.setName(langArray[0][j]);
                                langList[0].add(j);
                                colorVO.setSelected(selectedCat[0][j]);
                                colorList.add(colorVO);
                               // langArray
                                colorList.get(j).setSelected(b);
                            }
                        }else if(i==0 && b==false){
                            Collections.sort(langList[0]);
                            for (int j = 0; j < langArray[0].length; j++) {
                                langList[0].remove(Integer.valueOf(j));
                                selectedCat[0][j] = b;
                            }
                        }


                        if (selectedCat[0] != null && i < selectedCat[0].length) {
                            selectedCat[0][i] = b;

                        } else {
                            throw new IllegalArgumentException(
                                    "Argument '"+i+"' is out of bounds.");
                        }
                     //   okMessage("",langList[0].toString());
                        //removeDuplicateElements(langList[0],langList[0].size());

                            // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in lang list
                            if(langList[0].contains(i)) {

                            }else langList[0].add(i);
                           // selectedCat.add(true);
                            // Sort array list
                            Collections.sort(langList[0]);


                        } else {
                            // when checkbox unselected
                            // Remove position from langList
                            langList[0].remove(Integer.valueOf(i));

                          //  selectedCat.add(false);
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        removeDuplicateElements(langList[0],langList[0].size());

                        // use for loop
                        for (int j = 0; j < langList[0].size(); j++) {
                            // concat array value
                            stringBuilder.append(langArray[0][langList[0].get(j)]);
                            // check condition
                            if (j != langList[0].size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        sp_level2_category.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
//                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        // use for loop
//                        for (int j = 0; j < langList[0].size(); j++) {
//                            // remove all selection
//                          //  selectedLanguage[j] = false;
//                            // clear language list
//                            langList[0].clear();
//                            // clear text view value
//                            sp_level2_category.setText("");
//                        }
//                    }
//                });
                // show dialog
                builder.show();
            }
        });

        Double rfidCount = objAgeingStockTakingControl.loadRfIdDetails(sp_level1_category.getSelectedItem().toString(),sp_level2_category.getText().toString());
        et_RfidscanQty.setText(objAgeingStockTakingGlobal.getrfidScanQty() + "");
        et_dg1_sysqty.setText(objAgeingStockTakingGlobal.getDg1TotalQty() + "");
        et_totlqty.setText(objAgeingStockTakingGlobal.getTotalScanQty() + "");
        et_sys_qty.setText(objAgeingStockTakingGlobal.getRFIDsysQty() + "");
        et_totalsys_qty.setText(objAgeingStockTakingGlobal.gettotalsys() + "");
        bt_missingRfid_qty.setText(objAgeingStockTakingGlobal.gettotaldiffqty() + "");
        bt_excess_rfid.setText(objAgeingStockTakingGlobal.gettotalexcessqty() + "");

        bt_ageing_stock_taking_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to save?")
                        .setTitle("Confirmation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                b_Result = objAgeingStockTakingControl.InsertScanned(false);
                                if (!b_Result) {
                                    okMessage("Stock Taking", objGlobal.getErrorMessage());
                                }else {
                                    okMessage("Alert", "Stock Updated");
                                    et_RfidscanQty.setText("");
                                    et_sys_qty.setText("");
                                    et_totalsys_qty.setText("");
                                    bt_missingRfid_qty.setText("");
                                    bt_excess_rfid.setText("");
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




//        sp_level1_category.setOnItemClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//               // return 1;
//            }
//        });



        bt_missingRfid_qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!clicked) {
                    if (objAgeingStockTakingGlobal.gettotaldiffqty() != 0.0) {
                        openPopupMissing();
                        clicked = true;
                    }
                }
            }
        });
        bt_ageing_stock_taking_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if(v.hasOnClickListeners()) {
                    String zone = "";
                if(sp_level1_category.getSelectedItemId() == 0){
                    okMessage("Alert","Select the Display Group L1");
                }
                else {
                    if (!clicked) {
                        spinner.setVisibility(view.VISIBLE);
                        Runnable r = new Runnable() {
                            @Override
                            public void run(){
                                Toast.makeText(getContext(), "Loading.. Please wait..", Toast.LENGTH_SHORT).show();
                                //<-- put your code in here.

                            }
                        };

                        Handler h = new Handler();
                        h.postDelayed(r, 100);
                        openPopupScan("B");

                       // ProgressDialog.show(getContext(), "Loading", "Wait while loading...");


                        clicked = true;
                        spinner.setVisibility(view.GONE);

                    }
                    // v.setOnClickListener(null);
                }
            }


        });

        return view;
    }
//    public static int removeDuplicateElements(ArrayList<Integer> arr, int n){
//        if (n==0 || n==1){
//            return n;
//        }
//        int[] temp = new int[n];
//        int j = 0;
//        for (int i=0; i<n-1; i++){
//            if (arr.get(i) != arr.get(i+1)){
//                temp[j++] = arr.get(i);
//            }
//        }
//        temp[j++] = arr.get(n-1);
//        // Changing original array
//        for (int i=0; i<j; i++){
//            arr.set(j++,temp[i]);
//        }
//        return j;
//    }

    public static int removeDuplicateElements(ArrayList<Integer> arr, int n){
        if (n==0 || n==1){
            return n;
        }
        int j = 0;//for next element
        for (int i=0; i < n-1; i++){
            if (arr.get(i) != arr.get(i+1)){
                //j++;
                arr.set(j++,arr.get(i));
            }
        }
        //j++;
        arr.set(j++,arr.get(n-1));
       // arr[j++] = arr[n-1];
        return j;
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
                   // openPopupPassword("ITEM",s.srid);
                }
            });

            return myView;
        }
    }



    void openPopupRFIDitems(String itemcode) {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_rfid_itemcode);
        bt_popup_rfid_back = (Button) myDialog.findViewById(R.id.bt_popup_rfid_back);
        bt_popup_rfid_close1 = (Button) myDialog.findViewById(R.id.bt_popup_rfid_close1);
        tv_text_itemcode = (TextView) myDialog.findViewById(R.id.tv_text_itemcode);
        tv_text_description = (TextView) myDialog.findViewById(R.id.tv_text_description);
        iv_item_image = (ImageView) myDialog.findViewById(R.id.iv_item_image);

        listRfidMissingItemcodes.clear();
        listRfidMissingItemcodes = objAgeingStockTakingControl.loaditemImage(itemcode);

        if(listRfidMissingItemcodes.size() !=0) {
            tv_text_itemcode.setText(listRfidMissingItemcodes.get(0).rfid.toString());
            tv_text_description.setText(listRfidMissingItemcodes.get(0).Description.toString());
            if(listRfidMissingItemcodes.get(0).Image ==null){}
            else{
            Picasso.get().load(listRfidMissingItemcodes.get(0).Image.toString()).into(iv_item_image);}
        }else{
            tv_text_itemcode.setText("");
            //Picasso.get().load(new File("D:\\Android-New\\bflshop\\app\\src\\main\\res\\mipmap-hdpi\\bfllogo.png")).into(iv_item_image);
        }

        bt_popup_rfid_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                clicked = false;
                // lv_popup_rfid_missing1.setAdapter(null);
              //  clicked_dialog = false;
                clicked_dialog = false;
            }
        });

        bt_popup_rfid_close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                //CloseDialog = true;
                myDeptDialog.dismiss();
                ItemsDialog.dismiss();
                myDivisionDialog.dismiss();
                lv_popup_rfid_missing_department.setAdapter(null);
                rv_popup_rfid_missing_items.setAdapter(null);
                lv_popup_rfid_missing_division.setAdapter(null);
                clicked_dialog = false;
                clicked = false;
            }
        });

        myDialog.show();
    }
    Dialog myDeptDialog;
    void openMissingDepartment(String Division) {

        myDeptDialog = new Dialog(getContext());
        myDeptDialog.setCancelable(false);
        myDeptDialog.setContentView(R.layout.popup_rfid_missing_department);

        Department_Txt = myDeptDialog.findViewById(R.id.textView8);
        Department_Txt.setText(Division);
        bt_popup_rfid_close_dept = (Button) myDeptDialog.findViewById(R.id.bt_popup_rfid_close_dept);
        lv_popup_rfid_missing_department = (RecyclerView) myDeptDialog.findViewById(R.id.lv_popup_rfid_missing_department);

        listRfidMissingDepartment.clear();


            listRfidMissingDepartment = objAgeingStockTakingControl.loadMissingDepartment(Division, sp_level1_category.getSelectedItem().toString(), sp_level2_category.getText().toString());
            objMyRfidMissingDepartmentAdp = new MyRfidMissingDepartmentAdp(listRfidMissingDepartment);

            lv_popup_rfid_missing_department.setLayoutManager(new LinearLayoutManager(getContext()));
            lv_popup_rfid_missing_department.setAdapter(objMyRfidMissingDepartmentAdp);

            bt_popup_rfid_close_dept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDeptDialog.dismiss();
                    lv_popup_rfid_missing_department.setAdapter(null);
                    clicked_dialog = false;
                    clicked = false;
                }
            });

            myDeptDialog.show();

    }
    Dialog ItemsDialog;

    void openPopupMissingItems(String Department) {

        ItemsDialog = new Dialog(getContext());
        ItemsDialog.setCancelable(false);
        ItemsDialog.setContentView(R.layout.popup_rfid_missing_itemcodes);

        bt_popup_rfid_close = (Button) ItemsDialog.findViewById(R.id.bt_popup_rfid_close);
        bt_popup_rfid_back = (Button) ItemsDialog.findViewById(R.id.bt_popup_rfid_back);
        rv_popup_rfid_missing_items = (RecyclerView) ItemsDialog.findViewById(R.id.rv_popup_rfid_missing_items);

        listRfidMissingItems.clear();
        listRfidMissingItems = objAgeingStockTakingControl.loadRfidMissing(Department, sp_level1_category.getSelectedItem().toString(), sp_level2_category.getText().toString());
        objMyRfidMissingAdp = new MyRfidMissingAdp(listRfidMissingItems);

        rv_popup_rfid_missing_items.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_popup_rfid_missing_items.setAdapter(objMyRfidMissingAdp);

        bt_popup_rfid_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ItemsDialog.dismiss();
                myDeptDialog.dismiss();
                myDivisionDialog.dismiss();
                lv_popup_rfid_missing_department.setAdapter(null);
                rv_popup_rfid_missing_items.setAdapter(null);
                lv_popup_rfid_missing_division.setAdapter(null);
                clicked = false;
                clicked_dialog = false;
            }
        });
        bt_popup_rfid_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemsDialog.dismiss();
                rv_popup_rfid_missing_items.setAdapter(null);
                clicked = false;
                clicked_dialog = false;
            }
        });

        ItemsDialog.show();
    }

    Dialog myDivisionDialog;
    void openPopupMissing() {

        // Dialog myDialog;
        myDivisionDialog = new Dialog(getContext());
        myDivisionDialog.setCancelable(false);

        myDivisionDialog.setContentView(R.layout.popup_rfid_missing_division);

        bt_popup_rfid_close = (Button) myDivisionDialog.findViewById(R.id.bt_popup_rfid_close);
        lv_popup_rfid_missing_division = (RecyclerView) myDivisionDialog.findViewById(R.id.lv_popup_rfid_missing_division);

            listRfidMissingDivision.clear();
            listRfidMissingDivision = objAgeingStockTakingControl.loadRfidMissingDivision(sp_level1_category.getSelectedItem().toString(), sp_level2_category.getText().toString());
            objMyRfidMissingDivisionAdp = new MyRfidMissingDivisionAdp(listRfidMissingDivision);
            lv_popup_rfid_missing_division.setLayoutManager(new LinearLayoutManager(getContext()));
            lv_popup_rfid_missing_division.setAdapter(objMyRfidMissingDivisionAdp);

            bt_popup_rfid_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDivisionDialog.dismiss();
                    lv_popup_rfid_missing_division.setAdapter(null);
                    clicked = false;
                }
            });
            myDivisionDialog.show();

    }




     //------------------- All List and RecyclerView Adapters ----------------



    public class MyRfidMissingAdp extends RecyclerView.Adapter<MyRfidMissingAdp.ViewHolder> {

        public ArrayList<RfidMissingItems> listRfidMissingItems;
        public  class ViewHolder extends RecyclerView.ViewHolder {
            //private final TextView textView;
            private final TextView tv_itemcode;
            private final TextView tv_description;
            private final TextView tv_sys_qty;
            private final TextView tv_scanQty;
            private final TextView tv_groupcode;
            private final TextView tv_diff;
            //private final TextView tv_itemcode;
            private final ConstraintLayout cl_division;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                //  textView = (TextView) view.findViewById(R.id.textView);
                tv_itemcode = (TextView) view.findViewById(R.id.tv_itemcode);
                tv_description = (TextView) view.findViewById(R.id.tv_description);
                tv_groupcode = (TextView) view.findViewById(R.id.tv_groupcode);
                tv_sys_qty = (TextView) view.findViewById(R.id.tv_sysQty);
                cl_division = (ConstraintLayout) view.findViewById(R.id.cl_division);
                tv_scanQty = (TextView) view.findViewById(R.id.tv_scanqty);
                tv_diff = (TextView) view.findViewById(R.id.tv_diff);
            }
        }

        public MyRfidMissingAdp(ArrayList<RfidMissingItems> listRfidMissingItems) {
            this.listRfidMissingItems = listRfidMissingItems;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.ticket_rfid_missing_itemcodes, viewGroup, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            final RfidMissingItems s = listRfidMissingItems.get(position);
            // Get element from your dataset at this position and replace the
            // contents of the view with that element


            viewHolder.tv_itemcode.setText(String.valueOf(s.itemcode));
            viewHolder.tv_description.setText(String.valueOf(s.Description));
            viewHolder.tv_groupcode.setText(String.valueOf(s.Brand));
            viewHolder.tv_sys_qty.setText(String.valueOf(s.sysQty));
            viewHolder.tv_scanQty.setText(String.valueOf(s.scanQty));
            viewHolder.tv_diff.setText(String.valueOf(s.DiffQty));

            viewHolder.cl_division.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // if(!clicked_dialog){
                    openPopupRFIDitems(s.itemcode);
                    // clicked_dialog = true;
                    //  }
                }
            });

        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return listRfidMissingItems.size();
        }
    }


    private class MyRfidMissingItemcodeAdp extends BaseAdapter {
        public ArrayList<RfidMissingitemcodes> listRfidMissingItemcodes;

        public MyRfidMissingItemcodeAdp(ArrayList<RfidMissingitemcodes> listRfidMissingItemcodes) {
            this.listRfidMissingItemcodes = listRfidMissingItemcodes;
        }

        @Override
        public int getCount() {
            return listRfidMissingItemcodes.size();
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
            View myView = mInflater.inflate(R.layout.ticket_rfid_itemcodes, null);
            final RfidMissingitemcodes s = listRfidMissingItemcodes.get(position);

            TextView tv_rfid_items = (TextView) myView.findViewById(R.id.tv_rfid_items);
            tv_rfid_items.setText(String.valueOf(s.rfid));
            TextView tv_status = (TextView) myView.findViewById(R.id.tv_status);

            return myView;
        }
    }

    public class MyRfidMissingDepartmentAdp extends RecyclerView.Adapter<MyRfidMissingDepartmentAdp.ViewHolder> {

        public ArrayList<RfidMissingDepartment> listRfidMissingDepartment;
        public  class ViewHolder extends RecyclerView.ViewHolder {
            //private final TextView textView;
            private final TextView tv_rfid_missing_items;
            private final TextView tv_sys_qty;
            private final TextView tv_scanQty;
            private final TextView tv_diff;
            private final ConstraintLayout cl_division;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                //  textView = (TextView) view.findViewById(R.id.textView);
                tv_rfid_missing_items = (TextView) view.findViewById(R.id.tv_rfid_missing_items);
                tv_sys_qty = (TextView) view.findViewById(R.id.tv_sys_qty);
                cl_division = (ConstraintLayout) view.findViewById(R.id.cl_division);
                tv_scanQty = (TextView) view.findViewById(R.id.tv_scanQty);
                tv_diff = (TextView) view.findViewById(R.id.tv_diff);

            }

        }

        public MyRfidMissingDepartmentAdp(ArrayList<RfidMissingDepartment> listRfidMissingDepartment) {
            this.listRfidMissingDepartment = listRfidMissingDepartment;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.ticket_rfid_missing_items, viewGroup, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            final RfidMissingDepartment s = listRfidMissingDepartment.get(position);
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.tv_rfid_missing_items.setText(String.valueOf(s.Department));
            viewHolder.tv_sys_qty.setText(String.valueOf(s.sysQty));
            viewHolder.tv_scanQty.setText(String.valueOf(s.scanQty));
            viewHolder.tv_diff.setText(String.valueOf(s.DiffQty));

            viewHolder.cl_division.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!clicked){
                        openPopupMissingItems(s.Department);
                        clicked = true;
                    }
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return listRfidMissingDepartment.size();
        }
    }

    public class MyRfidMissingDivisionAdp extends RecyclerView.Adapter<MyRfidMissingDivisionAdp.ViewHolder> {

        public ArrayList<RfidMissingDivision> listRfidMissingDivision;
        public  class ViewHolder extends RecyclerView.ViewHolder {
            //private final TextView textView;
            private final TextView tv_rfid_missing_items;
            private final TextView tv_sys_qty;
            private final TextView tv_scanQty;
            private final TextView tv_diff;
            private final ConstraintLayout cl_division;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                tv_rfid_missing_items = (TextView) view.findViewById(R.id.tv_rfid_missing_items);
                tv_sys_qty = (TextView) view.findViewById(R.id.tv_sys_qty);
                cl_division = (ConstraintLayout) view.findViewById(R.id.cl_division);
                tv_scanQty = (TextView) view.findViewById(R.id.tv_scanQty);
                tv_diff = (TextView) view.findViewById(R.id.tv_diff);

            }
//            public TextView getTextView() {
//                return textView;
//            }
        }

        public MyRfidMissingDivisionAdp(ArrayList<RfidMissingDivision> listRfidMissingDivision) {
            this.listRfidMissingDivision = listRfidMissingDivision;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.ticket_rfid_missing_items, viewGroup, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            final RfidMissingDivision s = listRfidMissingDivision.get(position);
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.tv_rfid_missing_items.setText(String.valueOf(s.Division));
            viewHolder.tv_sys_qty.setText(String.valueOf(s.sysQty));
            viewHolder.tv_scanQty.setText(String.valueOf(s.scanQty));
            viewHolder.tv_diff.setText(String.valueOf(s.DiffQty));

            viewHolder.cl_division.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!clicked_dialog){
                        openMissingDepartment(s.Division);
                        clicked_dialog = true;
                        clicked = false;
                   }
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return listRfidMissingDivision.size();
        }
    }






    private void openPopupScan(String scanType)  {

            myDialogRfidScan = new Dialog(getContext());
            myDialogRfidScan.setCancelable(false);
            myDialogRfidScan.setContentView(R.layout.popup_rfid_scan_stocktake);

            tv_popup_grn_rfid_transfer_scantime = (TextView) myDialogRfidScan.findViewById(R.id.tv_popup_grn_rfid_transfer_scantime);
        tv_popup_grn_rfid_transfer_count = (TextView) myDialogRfidScan.findViewById(R.id.tv_popup_grn_rfid_transfer_count);
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


                ArrayList<String> Rfids = objAgeingStockTakingControl.ScannedDetails();
                ArrayList<String> Rfidcategory = objAgeingStockTakingControl.UserCategory(sp_level1_category.getSelectedItem().toString(),sp_level2_category.getText().toString());

                if(!Rfids.isEmpty()) {
                      for (int i = 0; i < Rfids.size(); i++) {
                          map = new HashMap<String, String>();
                          tempDatas.add(Rfids.get(i));
                        map.put(TAG_EPC, Rfids.get(i));
                        map.put(TAG_EPC_TID, Rfids.get(i));
                        map.put(TAG_COUNT, "1");
                        map.put(TAG_RSSI, "");
                        epcTidUser.add(Rfids.get(i));
                        tagList.add(map);
                    }


                     if(objAgeingStockTakingGlobal.getrfidScanQty() == 0){
                         objMyGrnRfidTransferScanRfidsAdp = new MyRfidTransferScanRfidsAdp(getContext());

                         tv_popup_grn_rfid_transfer_epc_count.setText(0+"");
                         uhfInfo.setTagNumber(0);
                         objMyGrnRfidTransferScanRfidsAdp.notifyDataSetChanged();
                         //lv_popup_grn_rfid_transfer_rfids.setAdapter(objMyGrnRfidTransferScanRfidsAdp);
                     }
                     else {
                         objMyGrnRfidTransferScanRfidsAdp1 = new MyRfidTransferScanRfidsAdp1(Rfidcategory);
                         objMyGrnRfidTransferScanRfidsAdp1.notifyDataSetChanged();
                         tv_popup_grn_rfid_transfer_epc_count.setText(String.valueOf(objMyGrnRfidTransferScanRfidsAdp1.getCount()));
                         uhfInfo.setTagNumber(objMyGrnRfidTransferScanRfidsAdp1.getCount());
                         lv_popup_grn_rfid_transfer_rfids.setAdapter(objMyGrnRfidTransferScanRfidsAdp1);
                     }
                }
//                else{
//
//                        objMyGrnRfidTransferScanRfidsAdp = new MyRfidTransferScanRfidsAdp(getContext());
//                        objMyGrnRfidTransferScanRfidsAdp.notifyDataSetChanged();
//                        tv_popup_grn_rfid_transfer_epc_count.setText(String.valueOf(objMyGrnRfidTransferScanRfidsAdp.getCount()));
//                        uhfInfo.setTagNumber(objMyGrnRfidTransferScanRfidsAdp.getCount());
//                        lv_popup_grn_rfid_transfer_rfids.setAdapter(objMyGrnRfidTransferScanRfidsAdp);
//
//
//                }
                //lv_popup_grn_rfid_transfer_rfids.setLayoutManager(new LinearLayoutManager(getContext()));


               // lv_popup_grn_rfid_transfer_rfids.setAdapter(objMyGrnRfidTransferScanRfidsAdp);


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

            bt_popup_grn_rfid_transfer_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int inventoryType = 1;
                    if (rb_popup_grn_rfid_transfer_single.isChecked()) inventoryType = 0;
                    readTag(bt_popup_grn_rfid_transfer_start.getText().toString(), inventoryType);
                }
            });

        bt_popup_grn_rfid_transfer_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagList.clear();
                tempDatas.clear();
                myDialogRfidScan.dismiss();
                clicked = false;
            }
        });

            bt_popup_grn_rfid_transfer_close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new UpdateScanedRfids().execute();
                    clicked = false;
                }

            });

            myDialogRfidScan.show();

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

    private void setTotalTime() {
        float useTime = (System.currentTimeMillis() - time) / 1000.0F;
        tv_popup_grn_rfid_transfer_scantime.setText(NumberTool.getPointDouble(1, useTime) + "s");
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
                      //  playSound(1);
                        addDataToList(epc,mergeTidEpc(tid, epc, user), uhftagInfo.getRssi());
                        setTotalTime();

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
                         //   playSound(1);
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
     //   bt_popup_grn_rfid_transfer_clear.setEnabled(val);
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
                        playSound(1);
                        tagList.add(map);
                        tempDatas.add(epc);

                        if(objMyGrnRfidTransferScanRfidsAdp1!=null) {
                            tv_popup_grn_rfid_transfer_epc_count.setText(String.valueOf(objMyGrnRfidTransferScanRfidsAdp1.getCount()));
                            // objMyGrnRfidTransferScanRfidsAdp1.notifyDataSetChanged();
                        }else {
                            if (objMyGrnRfidTransferScanRfidsAdp != null) {
                                lv_popup_grn_rfid_transfer_rfids.setAdapter(objMyGrnRfidTransferScanRfidsAdp);
                                tv_popup_grn_rfid_transfer_epc_count.setText(String.valueOf(objMyGrnRfidTransferScanRfidsAdp.getCount()));

                            }
                        }

                    }
                }
            tv_popup_grn_rfid_transfer_count.setText(tagList.size() + "");
                tv_popup_grn_rfid_transfer_total.setText(String.valueOf(++total));
            if(objMyGrnRfidTransferScanRfidsAdp1!=null){
                objMyGrnRfidTransferScanRfidsAdp1.notifyDataSetChanged();
                }else {
                if (objMyGrnRfidTransferScanRfidsAdp != null) {
                    objMyGrnRfidTransferScanRfidsAdp.notifyDataSetChanged();
                }
            }
              //  objMyGrnRfidTransferScanRfidsAdp.notifyDataSetChanged();
                //objMyGrnRfidTransferScanRfidsAdp1.notifyDataSetChanged();

                uhfInfo.setTempDatas(tempDatas);
                uhfInfo.setTagList(tagList);
                uhfInfo.setCount(total);

            if(objMyGrnRfidTransferScanRfidsAdp1!=null){
                uhfInfo.setTagNumber(objMyGrnRfidTransferScanRfidsAdp1.getCount());
            }else {
                if(objMyGrnRfidTransferScanRfidsAdp!=null) {
                    uhfInfo.setTagNumber(objMyGrnRfidTransferScanRfidsAdp.getCount());
                }
                //   uhfInfo.setTagNumber(objMyGrnRfidTransferScanRfidsAdp.getCount());
            }


        } catch (Exception e){
            okMessage("Stocktaking RFID",e.getMessage());
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
                b_Result = objAgeingStockTakingControl.saveScannedRfid(tempDatas);
                if (!b_Result) return 0;
            } catch (Exception e) {
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                okMessage("Stocktaking Rfid Error" ,objGlobal.getErrorMessage());
                vibrate(500);
//                et_grn_transfer_rfid_ginno.setText("");
//                et_grn_transfer_rfid_ginno.requestFocus();
            } else {
                if (!loadScanTrfRfidItems())
                    okMessage("Stocktaking Rfid Error",objGlobal.getErrorMessage());
                else {
                    tagList.clear();
                    tempDatas.clear();
                    dialog.dismiss();
                    myDialogRfidScan.dismiss();
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
                myDialogRfidScan.dismiss();
            }
            Double rfidCount = objAgeingStockTakingControl.loadRfIdDetails(sp_level1_category.getSelectedItem().toString(),sp_level2_category.getText().toString());
            objAgeingStockTakingControl.UserCategory(sp_level1_category.getSelectedItem().toString(),sp_level2_category.getText().toString());
            et_RfidscanQty.setText(objAgeingStockTakingGlobal.getrfidScanQty() + "");
            et_dg1_sysqty.setText(objAgeingStockTakingGlobal.getDg1TotalQty() + "");
            et_totlqty.setText(objAgeingStockTakingGlobal.getTotalScanQty() + "");
            et_sys_qty.setText(objAgeingStockTakingGlobal.getRFIDsysQty() + "");
            et_totalsys_qty.setText(objAgeingStockTakingGlobal.gettotalsys() + "");
            bt_missingRfid_qty.setText(objAgeingStockTakingGlobal.gettotaldiffqty() + "");
            bt_excess_rfid.setText(objAgeingStockTakingGlobal.gettotalexcessqty() + "");
        }
    }

    boolean loadScanTrfRfidItems() {
        try {
            if (!b_Result) return false;
            b_Result = setEnable(false);
            if (!b_Result) return false;

            boolean diff=false;
            return true;
        } catch (Exception e) {
            objGlobal.setErrorMessage(e.getMessage());
            return false;
        }
    }

    boolean setEnable(boolean val) {
        return true;
    }

    public boolean initUHF() {
        try {
            mReader = RFIDWithUHFUART.getInstance();
        } catch (Exception ex) {
            okMessage("Stocktaking RFID",ex.getMessage());
            return false;
        }
        if (mReader != null) {
            new InitTask().execute();
        }
        return true;
    }

    private boolean initSound() {
        try {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
            soundMap.put(1, soundPool.load(getContext(), R.raw.barcodebeep, 1));
            soundMap.put(2, soundPool.load(getContext(), R.raw.serror, 1));
            am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        } catch (Exception e){
            okMessage("Stocktaking RFID",e.getMessage());
            return false;
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


    boolean scanBarcode() {
        String scan = objControls.replaceString(et_ageing_stock_taking_popup_barcode.getText().toString()).toUpperCase();
      //  String zoneId = tv_ageing_stock_taking_zone.getText().toString();
        String result = "";
        String rfid = "";
        tv_ageing_stock_taking_popup_result.setText("");

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
      //  b_Result = objAgeingStockTakingDbManager.saveScanToLocaldb(scan, qty, zoneId, result, rfid);
//        if (!b_Result) {
//            //okMessage("Stock Taking", objGlobal.getErrorMessage());
//            tv_ageing_stock_taking_popup_result.setText(objGlobal.getErrorMessage());
//            vibrateSound(1000);
//            return false;
//        }

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
//        tv_ageing_stock_taking_total_scan.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScan()));
//        tv_ageing_stock_taking_total_exported.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScanExport()));
//        tv_ageing_stock_taking_total_pendingforexport.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScan() - objAgeingStockTakingGlobal.getTotalScanExport()));
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



    private boolean loadItemsStockTaking() {
        try {
            listAgeingStockTakingScanItems.clear();
            listAgeingStockTakingScanItems = objAgeingStockTakingDbManager.loadAgingStockTakingItems("50", getActivity());
            objMyAgeingStockTakingItemAdp = new MyAgeingStockTakingItemAdp(listAgeingStockTakingScanItems);
         //   lv_ageing_stock_taking.setAdapter(objMyAgeingStockTakingItemAdp);
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
//            tv_ageing_stock_taking_total_scan.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScan()));
//            tv_ageing_stock_taking_total_exported.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScanExport()));
//            tv_ageing_stock_taking_total_pendingforexport.setText(formatter.format(objAgeingStockTakingGlobal.getTotalScan() - objAgeingStockTakingGlobal.getTotalScanExport()));
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
    private void exportToServer(){
        new ExportToServer().execute();
    }
    public void stopLocation(){
        mReader.stopLocation();
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

//    public class MyRfidTransferScanRfidsAdp extends RecyclerView.Adapter<MyRfidTransferScanRfidsAdp.ViewHolder> {
//
//       // private String[] localDataSet;
//
//        /**
//         * Provide a reference to the type of views that you are using
//         * (custom ViewHolder)
//         */
//        public class ViewHolder extends RecyclerView.ViewHolder {
//           // private final TextView textView;
//            public TextView tvEPCTID;
//            public TextView tvTagCount;
//            public TextView tvTagRssi;
//
//            public ViewHolder(View view) {
//                super(view);
//                // Define click listener for the ViewHolder's View
//
//            //    textView = (TextView) view.findViewById(R.id.textView);
//                tvEPCTID = (TextView) view.findViewById(R.id.tv_ticket_grn_transfer_rfids_epc);
//                tvTagCount = (TextView) view.findViewById(R.id.tv_ticket_grn_transfer_rfids_count);
//                tvTagRssi = (TextView) view.findViewById(R.id.tv_ticket_grn_transfer_rfids_rss);
//            }
//
//           // public TextView getTextView() {
//             //   return textView;
//            //}
//        }
//
//        /**
//         * Initialize the dataset of the Adapter
//         *
//        // * @param dataSet String[] containing the data to populate views to be used
//         * by RecyclerView
//         */
//        public MyRfidTransferScanRfidsAdp() {
//            //localDataSet = dataSet;
//        }
//
//        // Create new views (invoked by the layout manager)
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//            // Create a new view, which defines the UI of the list item
//            View view = LayoutInflater.from(viewGroup.getContext())
//                    .inflate(R.layout.ticket_rfids_scan, viewGroup, false);
//
//            return new ViewHolder(view);
//        }
//
//        // Replace the contents of a view (invoked by the layout manager)
//        @Override
//        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
//
//            // Get element from your dataset at this position and replace the
//            // contents of the view with that element
//           // viewHolder.getTextView().setText(localDataSet[position]);
//            viewHolder.tvEPCTID.setText((String) tagList.get(position).get(TAG_EPC_TID));
//            viewHolder.tvTagCount.setText((String) tagList.get(position).get(TAG_COUNT));
//            viewHolder.tvTagRssi.setText((String) tagList.get(position).get(TAG_RSSI));
//        }
//
//        // Return the size of your dataset (invoked by the layout manager)
////        @Override
//        public int getItemCount() {
//            return tagList.size();
////            return localDataSet.length;
//        }
//
//                public int getCount() {
//            // TODO Auto-generated method stub
//            return tagList.size();
//        }
//    }
//
//
        public final class ViewHolder {

//        public void ViewHolder(View intnflate) {
            private LayoutInflater mInflater;
            public TextView tvEPCTID;
            public TextView tvTagCount;
            public TextView tvTagRssi;

//            mInflater = LayoutInflater.from(getContext());
//        }
    }


    public class MyRfidTransferScanRfidsAdp extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyRfidTransferScanRfidsAdp(Context context) {
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
                convertView = mInflater.inflate(R.layout.ticket_rfids_scan, null);
                holder.tvEPCTID = (TextView) convertView.findViewById(R.id.tv_ticket_grn_transfer_rfids_epc);
                holder.tvTagCount = (TextView) convertView.findViewById(R.id.tv_ticket_grn_transfer_rfids_count);
                holder.tvTagRssi = (TextView) convertView.findViewById(R.id.tv_ticket_grn_transfer_rfids_rss);
                convertView.setTag(holder);
            } else {
                holder = (StocktakeRfidFragment.ViewHolder) convertView.getTag();
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

//    public class MyRfidTransferScanRfidsAdp extends RecyclerView.Adapter<MyRfidTransferScanRfidsAdp.ViewHolder> {
//        public ArrayList<String> listRfidTransferScan;
//        private LayoutInflater mInflater;
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            private LayoutInflater mInflater;
//            public TextView vEPCTID;
//            public TextView tvTagCount;
//            public TextView tvTagRssi;
//
//            public ViewHolder(View view) {
//                super(view);
//                // Define click listener for the ViewHolder's View
//                vEPCTID = (TextView) view.findViewById(R.id.tv_ticket_grn_transfer_rfids_epc);
//                tvTagCount = (TextView) view.findViewById(R.id.tv_ticket_grn_transfer_rfids_count);
//                tvTagRssi = (TextView) view.findViewById(R.id.tv_ticket_grn_transfer_rfids_rss);
//
//            }
//        }

//        public MyRfidTransferScanRfidsAdp(ArrayList<String> listRfidTransferScan) {
//            this.listRfidTransferScan = listRfidTransferScan;
//        }

        // Replace the contents of a view (invoked by the layout manager)

//        @NonNull
//        @Override
//        public MyRfidTransferScanRfidsAdp.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//            View view = LayoutInflater.from(viewGroup.getContext())
//                    .inflate(R.layout.ticket_rfids_scan, viewGroup, false);
//            return new MyRfidTransferScanRfidsAdp.ViewHolder(view);
//        }

//        public void onBindViewHolder(@NonNull MyRfidTransferScanRfidsAdp.ViewHolder viewHolder, int position) {
//
//            final String s = listRfidTransferScan.get(position);
//
//            viewHolder.vEPCTID.setText(s.toString());
//            viewHolder.tvTagCount.setText("1");
//            //viewHolder.tvTagRssi.setText((String) s.TAG_RSSI);
//        }


//        @Override
//        public int getItemCount() {
//            return listRfidTransferScan.size();
//        }
//
//
//
//
//    }

    public class MyRfidTransferScanRfidsAdp1 extends BaseAdapter {
        public ArrayList<String> listRfidTransferScan;
        private LayoutInflater mInflater;
        public MyRfidTransferScanRfidsAdp1(ArrayList<String> listRfidTransferScan) {
            this.mInflater = LayoutInflater.from(getContext());
            this.listRfidTransferScan = listRfidTransferScan;
        }
        public int getCount() {
            // TODO Auto-generated method stub
//            return tagList.size();
            return listRfidTransferScan.size();
        }
        public Object getItem(int position) {
            // TODO Auto-generated method stub
//            return tagList.get(arg0);
            return listRfidTransferScan.get(position);
        }
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.ticket_rfids_scan, null);
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

    private void okMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
        vibrate(500);
    }

    private class ColorVO {
        private String name;
        private boolean selected;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}

