package com.bflgroup.bflshop.ui.ageingstocktaking.manual;

import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.ui.ageingstocktaking.model.AgeingStockTakingReports;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AgeingStockTakingReportFragment extends Fragment {

    private TextView tv_ageing_stock_taking_report_date_from;
    private TextView tv_ageing_stock_taking_report_date_to;
    private CheckBox ch_ageing_stock_taking_rpt_main_server;
    private Spinner sp_ageing_stock_taking_rpt_sort;
    private Button bt_ageing_stock_taking_rpt_refresh;
    private ListView lv_sales_invoice_rpt;
    private TextView tv_ageing_stock_taking_rpt_total;
    private DatePickerDialog datePicker;
    ArrayList<AgeingStockTakingReports> listAgeingStockTakingReports = new ArrayList<AgeingStockTakingReports>();
    AgeingStockTakingDbManager objAgeingStockTakingDbManager;
    private AgeingStockTakingControl objAgeingStockTakingControl = new AgeingStockTakingControl();
    private AgeingStockTakingGlobal objAgeingStockTakingGlobal = AgeingStockTakingGlobal.getInstance();
    AgeingStockTakingReportFragment.AgeingStockTakingReportAdp objAgeingStockTakingReportAdp;
    private boolean b_Result;
    private Global objGlobal = Global.getInstance();

    DecimalFormat formatter00 = new DecimalFormat("00");

    public AgeingStockTakingReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ageing_stock_taking_report, container, false);

        tv_ageing_stock_taking_report_date_from = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_report_date_from);
        tv_ageing_stock_taking_report_date_to = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_report_date_to);
        ch_ageing_stock_taking_rpt_main_server = (CheckBox) view.findViewById(R.id.ch_ageing_stock_taking_rpt_main_server);
        sp_ageing_stock_taking_rpt_sort = (Spinner) view.findViewById(R.id.sp_ageing_stock_taking_rpt_sort);
        bt_ageing_stock_taking_rpt_refresh = (Button) view.findViewById(R.id.bt_ageing_stock_taking_rpt_refresh);
        lv_sales_invoice_rpt = (ListView) view.findViewById(R.id.lv_sales_invoice_rpt);
        tv_ageing_stock_taking_rpt_total = (TextView) view.findViewById(R.id.tv_ageing_stock_taking_rpt_total);

        b_Result = objAgeingStockTakingControl.getStockTakeDate();
        if(!b_Result) {
            okMessage("Stock Taking:objAgeingStockTakingControl.getStockTakeDate()",objGlobal.getErrorMessage());
        } else {
            tv_ageing_stock_taking_report_date_from.setText(objAgeingStockTakingGlobal.getDtFrom());
            tv_ageing_stock_taking_report_date_to.setText(objAgeingStockTakingGlobal.getDtTo());
        }
        ch_ageing_stock_taking_rpt_main_server.setChecked(true);
        List<String> arr;
        arr=new ArrayList<String>();
        arr.add("User");
        arr.add("Zone");
        arr.add("Quantity");
        ArrayAdapter<String> arrayAdp=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,arr);
        sp_ageing_stock_taking_rpt_sort.setAdapter(arrayAdp);

        tv_ageing_stock_taking_report_date_from.setOnClickListener(new View.OnClickListener() {
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
                                tv_ageing_stock_taking_report_date_from.setText(formatter00.format(dayOfMonth) + "/" + formatter00.format(monthOfYear) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePicker.show();
            }
        });

        tv_ageing_stock_taking_report_date_to.setOnClickListener(new View.OnClickListener() {
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
                                tv_ageing_stock_taking_report_date_to.setText(formatter00.format(dayOfMonth) + "/" + formatter00.format(monthOfYear) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePicker.show();
            }
        });

        bt_ageing_stock_taking_rpt_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAgeingStockTakingReports.clear();
                String sort = sp_ageing_stock_taking_rpt_sort.getSelectedItem().toString();
                String dtFrom = tv_ageing_stock_taking_report_date_from.getText().toString();
                String dtTo = tv_ageing_stock_taking_report_date_to.getText().toString();
                if (sort.isEmpty() || dtFrom.isEmpty() || dtTo.isEmpty()) {
                    okMessage("Stock Taking","Empty Sort/DateFrom/DateTo");
                } else {
                    if (ch_ageing_stock_taking_rpt_main_server.isChecked()) {
                        listAgeingStockTakingReports = objAgeingStockTakingControl.loadAgingStockTakingRpt(sort, dtFrom, dtTo);
                        objAgeingStockTakingReportAdp = new AgeingStockTakingReportFragment.AgeingStockTakingReportAdp(listAgeingStockTakingReports);
                        lv_sales_invoice_rpt.setAdapter(objAgeingStockTakingReportAdp);
                    } else {
                        listAgeingStockTakingReports = objAgeingStockTakingDbManager.loadAgingStockTakingRpt(getActivity(), sort, dtFrom, dtTo);
                        objAgeingStockTakingReportAdp = new AgeingStockTakingReportFragment.AgeingStockTakingReportAdp(listAgeingStockTakingReports);
                        lv_sales_invoice_rpt.setAdapter(objAgeingStockTakingReportAdp);
                    }
                    tv_ageing_stock_taking_rpt_total.setText(String.valueOf(objAgeingStockTakingGlobal.getTotalScan()));
                }
            }
        });
        return view;
    }
    private class AgeingStockTakingReportAdp extends BaseAdapter {
        public ArrayList<AgeingStockTakingReports> listAgeingStockTakingReports;

        public AgeingStockTakingReportAdp(ArrayList<AgeingStockTakingReports> listAgeingStockTakingReports) {
            this.listAgeingStockTakingReports = listAgeingStockTakingReports;
        }

        @Override
        public int getCount() {
            return listAgeingStockTakingReports.size();
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
            View myView = mInflater.inflate(R.layout.ticket_ageing_stock_taking_reports, null);
            final AgeingStockTakingReports s = listAgeingStockTakingReports.get(position);

            TextView tv_ticket_ageing_stock_taking_report_zone = (TextView) myView.findViewById(R.id.tv_ticket_ageing_stock_taking_report_zone);
            tv_ticket_ageing_stock_taking_report_zone.setText(String.valueOf(s.zone));

            TextView tv_ticket_ageing_stock_taking_report_user = (TextView) myView.findViewById(R.id.tv_ticket_ageing_stock_taking_report_user);
            tv_ticket_ageing_stock_taking_report_user.setText(String.valueOf(s.user));

            TextView tv_ticket_ageing_stock_taking_report_scanqty = (TextView) myView.findViewById(R.id.tv_ticket_ageing_stock_taking_report_scanqty);
            tv_ticket_ageing_stock_taking_report_scanqty.setText(String.valueOf(s.scanqty));

            TextView tv_ticket_ageing_stock_taking_report_manqty = (TextView) myView.findViewById(R.id.tv_ticket_ageing_stock_taking_report_manqty);
            tv_ticket_ageing_stock_taking_report_manqty.setText(String.valueOf(s.manqty));

            TextView tv_ticket_ageing_stock_taking_report_diff = (TextView) myView.findViewById(R.id.tv_ticket_ageing_stock_taking_report_diff);
            tv_ticket_ageing_stock_taking_report_diff.setText(String.valueOf(s.diff));

            return myView;
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