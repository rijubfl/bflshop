package com.bflgroup.bflshop.ui.pdausercreation;

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
import android.widget.Button;
import android.widget.EditText;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Global;

public class PdaUserCreationFragment extends Fragment {

    private Global objGlobal = Global.getInstance();
    private PdaUserCreationControl objPdaUserCreationControl = new PdaUserCreationControl();

    private EditText et_user_create_username;
    private EditText et_user_create_password;
    private Button bt_user_create_save;
    private Button bt_user_create_clear;

    private boolean b_Result;

    public PdaUserCreationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pda_user_creation, container, false);

        et_user_create_username=(EditText) view.findViewById(R.id.et_user_create_username);
        et_user_create_password=(EditText) view.findViewById(R.id.et_user_create_password);
        bt_user_create_save=(Button) view.findViewById(R.id.bt_user_create_save);
        bt_user_create_clear=(Button) view.findViewById(R.id.bt_user_create_clear);

        bt_user_create_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                b_Result = objPdaUserCreationControl.saveUsers(et_user_create_username.getText().toString(),et_user_create_password.getText().toString());
                if (b_Result == false) {
                    okMessage("User Creation",objGlobal.getErrorMessage());
                } else {
                    okMessage("User Creation","Done");
                    et_user_create_password.setText("");
                    et_user_create_username.setText("");
                    et_user_create_username.requestFocus();
                }
            }
        });

        bt_user_create_clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                et_user_create_password.setText("");
                et_user_create_username.setText("");
                et_user_create_username.requestFocus();
            }
        });


        return view;
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