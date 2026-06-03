package com.bflgroup.bflshop.ui.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.printclass.SunmiPrintHelper;
import com.bflgroup.bflshop.ui.salesinvoice.SalesInvoicePrint;

public class SettinFragment extends Fragment {

    Global objGlobal=Global.getInstance();

    private Switch sw_settings_keyboard;
private Button bt_scan_rfid_device;

    GenerateBarcodePrint objSalesInvoicePrint = new GenerateBarcodePrint();
    public SettinFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settin, container, false);

        sw_settings_keyboard=(Switch) view.findViewById(R.id.sw_settings_keyboard);
        bt_scan_rfid_device=(Button) view.findViewById(R.id.bt_scan_rfid_device);

        sw_settings_keyboard.setChecked(false);
        if(objGlobal.getHideKeyPad()) {
            sw_settings_keyboard.setChecked(true);
        }

        SunmiPrintHelper.getInstance().initSunmiPrinterService(getContext());

        sw_settings_keyboard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    objGlobal.setHideKeyPad(true);
                }
                else {
                    objGlobal.setHideKeyPad(false);
                }
            }
        });

        bt_scan_rfid_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openPopupScan();
                objSalesInvoicePrint.printGenerateBarcode("ERTN/23/00006");

            }
        });

        return view;
    }
}