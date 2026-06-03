package com.bflgroup.bflshop.ui.ageingslashing.reports;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bflgroup.bflshop.ui.ageingslashing.AgeingSlashingGlobal;
import com.bflgroup.bflshop.ui.ageingslashing.model.AgeingSlashingItemsReports;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AgeingSlashingReportFragment extends Fragment {

    private Spinner sp_ageing_slashing_rpt_batchno;
    private Spinner sp_ageing_slashing_rpt_type;
    private EditText et_ageing_slashing_rpt_search;
    private Spinner sp_ageing_slashing_rpt_sort_field;
    private Spinner sp_ageing_slashing_rpt_sort_type;
    private Button bt_ageing_slashing_report_refresh;
    private TextView tv_ageing_slashing_report_fieldname;
    private ListView lv_ageing_slashing_rpt;
    private TextView tv_ageing_slashing_rpt_total_balance;
    private TextView tv_ageing_slashing_rpt_total_scan;
    private TextView tv_ageing_slashing_rpt_total_print;
    private TextView tv_ageing_slashing_rpt_total_eligible;

    private AgeingSlashingGlobal objAgeingSlashingGlobal = AgeingSlashingGlobal.getInstance();
    private AgeingSlashingReportControl objAgeingSlashingReportControl = new AgeingSlashingReportControl();
    private ArrayList<AgeingSlashingItemsReports> listAgeingSlashingItemsReports = new ArrayList<AgeingSlashingItemsReports>();
    AgeingSlashingReportFragment.MyAgeingSlashingReportsAdp objMyAgeingSlashingReportsAdp;

    DecimalFormat formatter = new DecimalFormat("###,###");
    private Global objGlobal = Global.getInstance();

    public AgeingSlashingReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ageing_slashing_report, container, false);

        sp_ageing_slashing_rpt_batchno = (Spinner) view.findViewById(R.id.sp_ageing_slashing_rpt_batchno);
        sp_ageing_slashing_rpt_type = (Spinner) view.findViewById(R.id.sp_ageing_slashing_rpt_type);
        et_ageing_slashing_rpt_search = (EditText) view.findViewById(R.id.et_ageing_slashing_rpt_search);
        sp_ageing_slashing_rpt_sort_field = (Spinner) view.findViewById(R.id.sp_ageing_slashing_rpt_sort_field);
        sp_ageing_slashing_rpt_sort_type = (Spinner) view.findViewById(R.id.sp_ageing_slashing_rpt_sort_type);
        bt_ageing_slashing_report_refresh = (Button) view.findViewById(R.id.bt_ageing_slashing_report_refresh);
        tv_ageing_slashing_report_fieldname = (TextView) view.findViewById(R.id.tv_ageing_slashing_report_fieldname);
        lv_ageing_slashing_rpt = (ListView) view.findViewById(R.id.lv_ageing_slashing_rpt);
        tv_ageing_slashing_rpt_total_balance = (TextView) view.findViewById(R.id.tv_ageing_slashing_rpt_total_balance);
        tv_ageing_slashing_rpt_total_scan = (TextView) view.findViewById(R.id.tv_ageing_slashing_rpt_total_scan);
        tv_ageing_slashing_rpt_total_print = (TextView) view.findViewById(R.id.tv_ageing_slashing_rpt_total_print);
        tv_ageing_slashing_rpt_total_eligible = (TextView) view.findViewById(R.id.tv_ageing_slashing_rpt_total_eligible);

        List<String> arr = objAgeingSlashingReportControl.loadBatchNo();
        ArrayAdapter<String> arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_ageing_slashing_rpt_batchno.setAdapter(arrayAdp);

        arr=new ArrayList<String>();
        arr.add("Itemcode");
        arr.add("GroupName");
        arr.add("Department");
        arr.add("Division");
        arrayAdp=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,arr);
        sp_ageing_slashing_rpt_type.setAdapter(arrayAdp);

        arr=new ArrayList<String>();
        arr.add("A to Z");
        arr.add("Z to A");
        arrayAdp=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,arr);
        sp_ageing_slashing_rpt_sort_type.setAdapter(arrayAdp);

        arr=new ArrayList<String>();
        arr.add(sp_ageing_slashing_rpt_type.getSelectedItem().toString());
        arr.add("Eligible Qty");
        arr.add("Scan Qty");
        arr.add("Print Qty");
        arr.add("Balance Qty");
        arrayAdp=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,arr);
        sp_ageing_slashing_rpt_sort_field.setAdapter(arrayAdp);

        sp_ageing_slashing_rpt_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tv_ageing_slashing_report_fieldname.setText(sp_ageing_slashing_rpt_type.getSelectedItem().toString());
                List<String> arr;
                ArrayAdapter<String> arrayAdp;
                arr=new ArrayList<String>();
                arr.add(sp_ageing_slashing_rpt_type.getSelectedItem().toString());
                arr.add("Eligible Qty");
                arr.add("Scan Qty");
                arr.add("Print Qty");
                arr.add("Balance Qty");
                arrayAdp=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,arr);
                sp_ageing_slashing_rpt_sort_field.setAdapter(arrayAdp);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        bt_ageing_slashing_report_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String batchNo = "", fieldType = "", search = "", sortField = "", sortType = "";
                if (!sp_ageing_slashing_rpt_batchno.getSelectedItem().toString().isEmpty())
                    batchNo = sp_ageing_slashing_rpt_batchno.getSelectedItem().toString();
                if (!sp_ageing_slashing_rpt_type.getSelectedItem().toString().isEmpty())
                    fieldType = sp_ageing_slashing_rpt_type.getSelectedItem().toString();
                if (!sp_ageing_slashing_rpt_sort_field.getSelectedItem().toString().isEmpty())
                    sortField = sp_ageing_slashing_rpt_sort_field.getSelectedItem().toString();
                if (!sp_ageing_slashing_rpt_sort_type.getSelectedItem().toString().isEmpty())
                    sortType = sp_ageing_slashing_rpt_sort_type.getSelectedItem().toString();
                search = et_ageing_slashing_rpt_search.getText().toString();
                loadRpt(batchNo, fieldType, search, sortField, sortType);
            }
        });

        return view;
    }

    void loadRpt(String batchNo,String fieldType,String search,String sortField, String sortType){
        ArrayList<AgeingSlashingItemsReports> listSlashReport = objAgeingSlashingReportControl.loadReports(batchNo,fieldType,search,sortField,sortType);
        if (listSlashReport != null) {
            objMyAgeingSlashingReportsAdp = new MyAgeingSlashingReportsAdp(listSlashReport);
            lv_ageing_slashing_rpt.setAdapter(objMyAgeingSlashingReportsAdp);
            tv_ageing_slashing_rpt_total_balance.setText(formatter.format(objAgeingSlashingGlobal.getRptTotalDiff()));
            tv_ageing_slashing_rpt_total_eligible.setText(formatter.format(objAgeingSlashingGlobal.getRptTotalElgQty()));
            tv_ageing_slashing_rpt_total_print.setText(formatter.format(objAgeingSlashingGlobal.getRptTotalPrintQty()));
            tv_ageing_slashing_rpt_total_scan.setText(formatter.format(objAgeingSlashingGlobal.getRptTotalScanQty()));
        }
        else{
            okMessage("AgeingSlashingReport",objGlobal.getErrorMessage());
        }
    }

    private class MyAgeingSlashingReportsAdp extends BaseAdapter {
        public ArrayList<AgeingSlashingItemsReports> listAgeingSlashingReports;

        public MyAgeingSlashingReportsAdp(ArrayList<AgeingSlashingItemsReports> listAgeingSlashingReports) {
            this.listAgeingSlashingReports = listAgeingSlashingReports;
        }

        @Override
        public int getCount() {
            return listAgeingSlashingReports.size();
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
            View myView = mInflater.inflate(R.layout.ticket_ageing_slashing_report, null);
            final AgeingSlashingItemsReports s = listAgeingSlashingReports.get(position);

            TextView tv_ageing_slashing_report_itemcode = (TextView) myView.findViewById(R.id.tv_ageing_slashing_report_itemcode);
            tv_ageing_slashing_report_itemcode.setText(s.itemcode);

            TextView tv_ageing_slashing_report_eligible = (TextView) myView.findViewById(R.id.tv_ageing_slashing_report_eligible);
            tv_ageing_slashing_report_eligible.setText(formatter.format(s.eligible));

            TextView tv_ageing_slashing_report_scan = (TextView) myView.findViewById(R.id.tv_ageing_slashing_report_scan);
            tv_ageing_slashing_report_scan.setText(formatter.format(s.scan));

            TextView tv_ageing_slashing_report_print = (TextView) myView.findViewById(R.id.tv_ageing_slashing_report_print);
            tv_ageing_slashing_report_print.setText(formatter.format(s.print));

            TextView tv_ageing_slashing_report_balance = (TextView) myView.findViewById(R.id.tv_ageing_slashing_report_balance);
            tv_ageing_slashing_report_balance.setText(formatter.format(s.bal));

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