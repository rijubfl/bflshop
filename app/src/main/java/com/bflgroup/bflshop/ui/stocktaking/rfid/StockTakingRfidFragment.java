package com.bflgroup.bflshop.ui.stocktaking.rfid;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bflgroup.bflshop.R;

public class StockTakingRfidFragment extends Fragment {

    public StockTakingRfidFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_taking_rfid, container, false);

        return view;
    }
}