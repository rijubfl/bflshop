package com.bflgroup.bflshop.ui.salesinvoice;

import android.app.ApplicationExitInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bflgroup.bflshop.R;
import com.bflgroup.bflshop.comm.Controls;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.PosGlobal;
import com.bflgroup.bflshop.printclass.SunmiPrintHelper;

import java.util.ArrayList;
import java.util.List;

public class SalesInvoiceFragment extends Fragment {

    private TextView tv_sales_invoice_invoiceno;
    private TextView tv_sales_invoice_invoicedate;
    private ListView lv_sales_invoice_items;
    private TextView tv_sales_invoice_netamount;
    private TextView tv_sales_invoice_total_discount;
    private TextView et_sales_invoice_paidamt;
    private TextView tv_sales_invoice_change;
    private CheckBox ch_sales_invoice_payment_mode;
    private CheckBox ch_sales_invoice_loyalty;
    private CheckBox ch_sales_invoice_reprint;
    private Button bt_sales_invoice_add_items;
    private Button bt_sales_invoice_clear;
    private Button bt_sales_invoice_save;

    private EditText et_sales_invoice_popup_scan_item_barcode;
    private Spinner sp_sales_invoice_popup_scan_item_barcode;
    private Spinner sp_sales_invoice_popup_scan_item_salesprice;
    private EditText et_sales_invoice_popup_scan_item_qty;
    private Button bt_sales_invoice_popup_scan_item_add;
    private Button bt_sales_invoice_popup_scan_item_close;

    private Spinner sp_sales_invoice_popup_payment_type;
    private Button bt_sales_invoice_popup_payment_show_typ;
    private ListView lv_sales_invoice_popup_payment;
    private TextView tv_sales_invoice_popup_payment_totalamt;
    private TextView tv_sales_invoice_popup_payment_totalamt_net;
    private TextView tv_sales_invoice_popup_payment_totalamt_balance;
    private Button bt_sales_invoice_popup_close;

    private EditText et_sales_invoice_popup_payment_staffpurchase_empcode;
    private Button bt_sales_invoice_popup_payment_staffpurchase_validate;
    private TextView tv_sales_invoice_popup_payment_staffpurchase_empname;
    private Button bt_sales_invoice_popup_payment_staffpurchase_add;
    private Button bt_sales_invoice_popup_payment_staffpurchase_close;
    private TextView tv_sales_invoice_popup_payment_staffpurchase_limit;
    private TextView tv_sales_invoice_popup_payment_staffpurchase_allow_amt;
    private TextView et_sales_invoice_popup_payment_stafpurchase_useamt;

    private EditText et_sales_invoice_popup_payment_guestpurchase_empcode;
    private Button bt_sales_invoice_popup_payment_guestpurchase_validate;
    private TextView tv_sales_invoice_popup_payment_guestpurchase_empname;
    private EditText et_sales_invoice_popup_payment_guestpurchase_useamt;
    private Button bt_sales_invoice_popup_payment_guestpurchase_add;
    private Button bt_sales_invoice_popup_payment_guestpurchase_close;

    private EditText et_sales_invoice_popup_reprint_invno;
    private EditText et_sales_invoice_popup_reprint_empname;
    private EditText et_sales_invoice_popup_reprint_password;
    private Button bt_sales_invoice_popup_reprint_close;
    private Button bt_sales_invoice_popup_reprint_print;

    private EditText et_sales_invoice_popup_payment_cash_amt;
    private Button bt_sales_invoice_popup_payment_cash_close;
    private Button bt_sales_invoice_popup_payment_cash_add;

    private TextView tv_sales_invoice_popup_payment_creditcard_posid;
    private EditText et_sales_invoice_popup_payment_creditcard_useamt;
    private Button bt_sales_invoice_popup_payment_creditcard_proceed;
    private TextView tv_sales_invoice_popup_payment_creditcard_status;
    private TextView tv_sales_invoice_popup_payment_creditcard_appcode;
    private Button bt_sales_invoice_popup_payment_creditcard_close;
    private Button bt_sales_invoice_popup_payment_creditcard_add;

    private EditText et_sales_invoice_popup_payment_creditnote_crno;
    private Button bt_sales_invoice_popup_payment_creditnote_validate;
    private TextView tv_sales_invoice_popup_payment_creditnote_totamt;
    private TextView tv_sales_invoice_popup_payment_creditnote_expdate;
    private Button bt_sales_invoice_popup_payment_creditnote_add;
    private EditText et_sales_invoice_popup_payment_creditnote_useamt;
    private Button bt_sales_invoice_popup_payment_creditnote_close;

    private EditText et_sales_invoice_popup_payment_giftvoucher_crno;
    private Button bt_sales_invoice_popup_payment_giftvoucher_validate;
    private TextView tv_sales_invoice_popup_payment_giftvoucher_totamt;
    private TextView tv_sales_invoice_popup_payment_giftvoucher_expdate;
    private Button bt_sales_invoice_popup_payment_giftvoucher_add;
    private EditText et_sales_invoice_popup_payment_giftvoucher_useamt;
    private Button bt_sales_invoice_popup_payment_giftvoucher_close;

    Controls objControls = new Controls();
    private Global objGlobal = Global.getInstance();
    private PosGlobal objPosGlobal = PosGlobal.getInstance();
    private SalesInvoiceGlobal objSalesInvoiceGlobal = SalesInvoiceGlobal.getInstance();
    private SalesInvoiceControl objSalesInvoiceControl = new SalesInvoiceControl();
    private SalesInvoicePayments objSalesInvoicePayments = new SalesInvoicePayments();
    private SalesInvoicePaymentsCreditNote objSalesInvoicePaymentsCreditNote = new SalesInvoicePaymentsCreditNote();
    private SalesInvoicePaymentsStaffPurchase objSalesInvoicePaymentsStaffPurchase = new SalesInvoicePaymentsStaffPurchase();
    SalesInvoicePrint objSalesInvoicePrint = new SalesInvoicePrint();

    SalesInvoiceScanItemsAdp objSalesInvoiceScanItemsAdp;
    SalesInvoicePaymentAdp objSalesInvoicePaymentAdp;

    private boolean b_Result;
    private String s_Result;

    public SalesInvoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_invoice, container, false);

        ch_sales_invoice_payment_mode = (CheckBox) view.findViewById(R.id.ch_sales_invoice_payment_mode);
        ch_sales_invoice_loyalty = (CheckBox) view.findViewById(R.id.ch_sales_invoice_loyalty);
        ch_sales_invoice_reprint = (CheckBox) view.findViewById(R.id.ch_sales_invoice_reprint);
        bt_sales_invoice_add_items = (Button) view.findViewById(R.id.bt_sales_invoice_add_items);
        bt_sales_invoice_clear = (Button) view.findViewById(R.id.bt_sales_invoice_clear);
        bt_sales_invoice_save = (Button) view.findViewById(R.id.bt_sales_invoice_save);
        tv_sales_invoice_invoiceno = (TextView) view.findViewById(R.id.tv_sales_invoice_invoiceno);
        tv_sales_invoice_invoicedate = (TextView) view.findViewById(R.id.tv_sales_invoice_invoicedate);
        tv_sales_invoice_netamount = (TextView) view.findViewById(R.id.tv_sales_invoice_netamount);
        tv_sales_invoice_total_discount = (TextView) view.findViewById(R.id.tv_sales_invoice_total_discount);
        et_sales_invoice_paidamt = (EditText) view.findViewById(R.id.et_sales_invoice_paidamt);
        tv_sales_invoice_change = (TextView) view.findViewById(R.id.tv_sales_invoice_change);
        lv_sales_invoice_items = (ListView) view.findViewById(R.id.lv_sales_invoice_items);
        tv_sales_invoice_invoicedate.setText(objGlobal.getServerDate());

        SunmiPrintHelper.getInstance().initSunmiPrinterService(getContext());

        objSalesInvoiceGlobal.setStaffPurchaseEmpCode("");
        objSalesInvoiceGlobal.setStaffPurchaseEmpName("");
        objSalesInvoiceGlobal.setGuestPurchaseEmpCode("");
        objSalesInvoiceGlobal.setGuestPurchaseEmpName("");

        loadScanItemsInvoice();
        objSalesInvoicePayments.loadPaymentMode();
        objSalesInvoicePayments.loadInvoicePaymentAmount();
        objSalesInvoicePayments.validateMainPaymentType();
        if (objSalesInvoiceGlobal.getTotalPaymentAmt() > 0) {
            ch_sales_invoice_payment_mode.setChecked(true);
        }
        bt_sales_invoice_add_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopupInvoiceItems();
            }
        });

        ch_sales_invoice_payment_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    openPopupInvoicePayment();
                } else {
                    if (objSalesInvoiceGlobal.getTotalPaymentAmt() > 0) {
                        ch_sales_invoice_payment_mode.setChecked(true);
                        openPopupInvoicePayment();
                    }
                }
            }
        });

        ch_sales_invoice_loyalty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    openPopupInvoiceLoyalty();
                } else {
                    // not checked
                }
            }
        });

        ch_sales_invoice_reprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    openPopupInvoiceReprint();
                } else {
                    // not checked
                }
            }
        });

        bt_sales_invoice_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("Are you sure to clear all?")
                        .setTitle("Conformation")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearMainControls();
                                bt_sales_invoice_add_items.requestFocus();
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

        bt_sales_invoice_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float paidAmt, change;
                if (et_sales_invoice_paidamt.getText().toString().isEmpty()) {
                    paidAmt = 0;
                } else {
                    paidAmt = Float.valueOf(et_sales_invoice_paidamt.getText().toString());
                }
                if (tv_sales_invoice_change.getText().toString().isEmpty()) {
                    change = 0;
                } else {
                    change = Float.valueOf(tv_sales_invoice_change.getText().toString());
                }
                b_Result = objSalesInvoiceControl.validateMain(paidAmt);
                if (!b_Result) {
                    okMessage("Sales Invoice", objGlobal.getErrorMessage());
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are you sure to save?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    b_Result = objSalesInvoiceControl.saveInvoice(paidAmt, change);
                                    if (!b_Result) {
                                        okMessage("Sales Invoice", objGlobal.getErrorMessage());
                                    } else {
                                        b_Result = objSalesInvoicePrint.printMainInvoice(objSalesInvoiceGlobal.getInvoiceNumber());
                                        if (!b_Result) {
                                            clearMainControls();
                                            okMessage("Sales Invoice, Print Error", objGlobal.getErrorMessage());
                                        } else {
                                            clearMainControls();
                                            openPopupInvoiceItems();
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

        et_sales_invoice_paidamt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                calcChange();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        return view;
    }
    private void calcChange() {
        float change = 0;
        tv_sales_invoice_change.setText("0.00");
        if (!et_sales_invoice_paidamt.getText().toString().isEmpty() && objSalesInvoiceGlobal.getCashAmt() > 0) {
            change = Float.valueOf(et_sales_invoice_paidamt.getText().toString()) - objSalesInvoiceGlobal.getCashAmt();
            if (change > 0) {
                tv_sales_invoice_change.setText(String.format("%.2f", change));
            }
        }
    }
    private void openPopupInvoiceItems() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_sales_invoice_scan_items_text);

        et_sales_invoice_popup_scan_item_barcode = (EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_scan_item_barcode);
        sp_sales_invoice_popup_scan_item_barcode = (Spinner) myDialog.findViewById(R.id.sp_sales_invoice_popup_scan_item_barcode);
        sp_sales_invoice_popup_scan_item_salesprice = (Spinner) myDialog.findViewById(R.id.sp_sales_invoice_popup_scan_item_salesprice);
        et_sales_invoice_popup_scan_item_qty = (EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_scan_item_qty);
        bt_sales_invoice_popup_scan_item_add = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_scan_item_add);
        bt_sales_invoice_popup_scan_item_close = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_scan_item_close);
        et_sales_invoice_popup_scan_item_qty.setEnabled(false);
        sp_sales_invoice_popup_scan_item_barcode.setEnabled(false);
        et_sales_invoice_popup_scan_item_barcode.setEnabled(true);

        if(objPosGlobal.getCoffeeInvoice().equals("Y")) {
            List<String> arr1 = objSalesInvoiceControl.loadCoffeeItemsSpinner("");
            ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr1);
            sp_sales_invoice_popup_scan_item_barcode.setAdapter(arrayAdp1);
            sp_sales_invoice_popup_scan_item_barcode.setEnabled(true);
            et_sales_invoice_popup_scan_item_barcode.setEnabled(false);
        }

        et_sales_invoice_popup_scan_item_barcode.setOnTouchListener(new View.OnTouchListener() {
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

        et_sales_invoice_popup_scan_item_barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    scanBarcodeRfid();
                }
                return false;
            }
        });

        bt_sales_invoice_popup_scan_item_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcodeRfid();
            }
        });

        bt_sales_invoice_popup_scan_item_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        sp_sales_invoice_popup_scan_item_barcode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selItems = sp_sales_invoice_popup_scan_item_barcode.getSelectedItem().toString();
                if (!selItems.isEmpty()) {
                    String[] arrSelItems = selItems.split("-");
                    String itemcode = arrSelItems[0];
                    et_sales_invoice_popup_scan_item_barcode.setText(itemcode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        myDialog.show();
        et_sales_invoice_popup_scan_item_barcode.requestFocus();
    }
    private void openPopupPaymentCash() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_sales_invoice_payment_cash);

        et_sales_invoice_popup_payment_cash_amt = (EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_payment_cash_amt);
        bt_sales_invoice_popup_payment_cash_close = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_cash_close);
        bt_sales_invoice_popup_payment_cash_add = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_cash_add);

        bt_sales_invoice_popup_payment_cash_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_sales_invoice_popup_payment_cash_amt.getText().toString().trim().isEmpty()) et_sales_invoice_popup_payment_cash_amt.setText("0");
                float cashAmt = Float.valueOf(et_sales_invoice_popup_payment_cash_amt.getText().toString().trim());
                if(cashAmt==0){
                    okMessage("Sales Invoice, Payment Cash", "Please Enter Cash Amount");
                    et_sales_invoice_popup_payment_cash_amt.requestFocus();
                } else {
                    b_Result = objSalesInvoicePayments.addPaymentModes("Cash","",cashAmt);
                    if(!b_Result){
                        et_sales_invoice_popup_payment_cash_amt.requestFocus();
                        okMessage("Sales Invoice, Payment Cash", objGlobal.getErrorMessage());
                    } else {
                        loadPayments();
                        myDialog.dismiss();
                    }
                }
            }
        });

        bt_sales_invoice_popup_payment_cash_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPayments();
                myDialog.dismiss();
            }
        });

        myDialog.show();
        et_sales_invoice_popup_payment_cash_amt.requestFocus();
    }
    private void openPopupPaymentCreditCard() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_sales_invoice_payment_creditcard);

        tv_sales_invoice_popup_payment_creditcard_posid=(TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_creditcard_posid);
        et_sales_invoice_popup_payment_creditcard_useamt=(EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_payment_creditcard_useamt);
        bt_sales_invoice_popup_payment_creditcard_proceed=(Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_creditcard_proceed);
        tv_sales_invoice_popup_payment_creditcard_status=(TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_creditcard_status);
        tv_sales_invoice_popup_payment_creditcard_appcode=(TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_creditcard_appcode);
        bt_sales_invoice_popup_payment_creditcard_close=(Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_creditcard_close);
        bt_sales_invoice_popup_payment_creditcard_add=(Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_creditcard_add);

        bt_sales_invoice_popup_payment_creditcard_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bt_sales_invoice_popup_payment_creditcard_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (et_sales_invoice_popup_payment_creditcard_useamt.getText().toString().trim().isEmpty())
                        et_sales_invoice_popup_payment_creditcard_useamt.setText("0");
                    float cardAmt = Float.valueOf(et_sales_invoice_popup_payment_creditcard_useamt.getText().toString().trim());
                    if (cardAmt == 0) {
                        okMessage("Sales Invoice, Payment Card", "Please Enter Card Amount");
                        et_sales_invoice_popup_payment_creditcard_useamt.requestFocus();
                    } else {
                        b_Result = objSalesInvoicePayments.addPaymentModes("Card", "", cardAmt);
                        if (!b_Result) {
                            et_sales_invoice_popup_payment_creditcard_useamt.requestFocus();
                            okMessage("Sales Invoice, Payment Card", objGlobal.getErrorMessage());
                        } else {
                            loadPayments();
                            myDialog.dismiss();
                        }
                    }
                } catch(Exception ex){
                    okMessage("",ex.toString());
                }
            }
        });

        bt_sales_invoice_popup_payment_creditcard_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPayments();
                myDialog.dismiss();
            }
        });

        bt_sales_invoice_popup_payment_creditcard_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPayments();
                myDialog.dismiss();
            }
        });


        //loadPayments

        myDialog.show();
        et_sales_invoice_popup_payment_creditcard_useamt.requestFocus();
    }
    private void openPopupPaymentCreditNote() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_sales_invoice_payment_creditnote);

        et_sales_invoice_popup_payment_creditnote_crno = (EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_payment_creditnote_crno);
        bt_sales_invoice_popup_payment_creditnote_validate = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_creditnote_validate);
        tv_sales_invoice_popup_payment_creditnote_totamt = (TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_creditnote_totamt);
        tv_sales_invoice_popup_payment_creditnote_expdate = (TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_creditnote_expdate);
        et_sales_invoice_popup_payment_creditnote_useamt = (EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_payment_creditnote_useamt);
        bt_sales_invoice_popup_payment_creditnote_add = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_creditnote_add);
        bt_sales_invoice_popup_payment_creditnote_close = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_creditnote_close);
        et_sales_invoice_popup_payment_creditnote_crno.setOnTouchListener(new View.OnTouchListener() {
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

        bt_sales_invoice_popup_payment_creditnote_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String crNoteNo=et_sales_invoice_popup_payment_creditnote_crno.getText().toString().trim().toUpperCase();
                tv_sales_invoice_popup_payment_creditnote_totamt.setText("");
                tv_sales_invoice_popup_payment_creditnote_expdate.setText("");
                b_Result = objSalesInvoicePaymentsCreditNote.validatePaymentCreditNote(crNoteNo);
                if(!b_Result) {
                    okMessage("Sales Invoice, Payment Credit Note", objGlobal.getErrorMessage());
                    et_sales_invoice_popup_payment_creditnote_crno.setText("");
                    et_sales_invoice_popup_payment_creditnote_crno.requestFocus();
                } else {
                    tv_sales_invoice_popup_payment_creditnote_totamt.setText(String.format("%.2f", objSalesInvoiceGlobal.getCrnoteTotalAmt()));;
                    tv_sales_invoice_popup_payment_creditnote_expdate.setText(objSalesInvoiceGlobal.getCrnoteExpDate());
                }
            }
        });

        bt_sales_invoice_popup_payment_creditnote_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPayments();
            }
        });

        bt_sales_invoice_popup_payment_creditnote_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPayments();
                myDialog.dismiss();
            }
        });

        myDialog.show();
        et_sales_invoice_popup_payment_creditnote_crno.requestFocus();
    }
    private void openPopupPaymentTotalDiscount() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_sales_invoice_payment_totaldiscount);

        //loadPayments();

        myDialog.show();
        //et_sales_invoice_popup_scan_item_barcode.requestFocus();
    }
    private void openPopupPaymentEwallet() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        //myDialog.setContentView(R.layout.popup_sales_invoice_payment_ewaller);

        //loadPayments();

        myDialog.show();
        //et_sales_invoice_popup_scan_item_barcode.requestFocus();
    }
    private void openPopupPaymentGiftVoucher() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_sales_invoice_payment_giftvoucher);

        et_sales_invoice_popup_payment_giftvoucher_crno = (EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_payment_giftvoucher_crno);
        bt_sales_invoice_popup_payment_giftvoucher_validate = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_giftvoucher_validate);
        tv_sales_invoice_popup_payment_giftvoucher_totamt = (TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_giftvoucher_totamt);
        tv_sales_invoice_popup_payment_giftvoucher_expdate = (TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_giftvoucher_expdate);
        et_sales_invoice_popup_payment_giftvoucher_useamt = (EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_payment_giftvoucher_useamt);
        bt_sales_invoice_popup_payment_giftvoucher_add = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_giftvoucher_add);
        bt_sales_invoice_popup_payment_giftvoucher_close = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_giftvoucher_close);
        et_sales_invoice_popup_payment_giftvoucher_crno.setOnTouchListener(new View.OnTouchListener() {
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

        bt_sales_invoice_popup_payment_giftvoucher_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String voucherNo=et_sales_invoice_popup_payment_giftvoucher_crno.getText().toString().trim().toUpperCase();
                tv_sales_invoice_popup_payment_giftvoucher_totamt.setText("");
                tv_sales_invoice_popup_payment_giftvoucher_expdate.setText("");
                /*b_Result = objSalesInvoicePaymentsCreditNote.validatePaymentGiftVoucher(voucherNo);
                if(!b_Result) {
                    okMessage("Sales Invoice, Payment Gift Voucher", objGlobal.getErrorMessage());
                    et_sales_invoice_popup_payment_creditnote_crno.setText("");
                    et_sales_invoice_popup_payment_creditnote_crno.requestFocus();
                } else {
                    tv_sales_invoice_popup_payment_giftvoucher_totamt.setText(String.format("%.2f", objSalesInvoiceGlobal.getCrnoteTotalAmt()));;
                    tv_sales_invoice_popup_payment_giftvoucher_expdate.setText(objSalesInvoiceGlobal.getCrnoteExpDate());
                }*/
            }
        });

        bt_sales_invoice_popup_payment_giftvoucher_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadPayments();
            }
        });

        bt_sales_invoice_popup_payment_giftvoucher_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPayments();
                myDialog.dismiss();
            }
        });

        myDialog.show();
        et_sales_invoice_popup_payment_giftvoucher_crno.requestFocus();
    }
    private void openPopupPaymentMallVoucher() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_sales_invoice_payment_mallvouchers);

        //loadPayments();

        myDialog.show();
        //et_sales_invoice_popup_scan_item_barcode.requestFocus();
    }
    private void openPopupPaymentStaffPurchase() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_sales_invoice_payment_staffpurchase);

        et_sales_invoice_popup_payment_staffpurchase_empcode = (EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_payment_staffpurchase_empcode);
        bt_sales_invoice_popup_payment_staffpurchase_validate = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_staffpurchase_validate);
        tv_sales_invoice_popup_payment_staffpurchase_empname = (TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_staffpurchase_empname);
        bt_sales_invoice_popup_payment_staffpurchase_add = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_staffpurchase_add);
        bt_sales_invoice_popup_payment_staffpurchase_close = (Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_staffpurchase_close);
        tv_sales_invoice_popup_payment_staffpurchase_limit = (TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_staffpurchase_limit);
        tv_sales_invoice_popup_payment_staffpurchase_allow_amt = (TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_staffpurchase_allow_amt);
        et_sales_invoice_popup_payment_stafpurchase_useamt = (EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_payment_stafpurchase_useamt);

        bt_sales_invoice_popup_payment_staffpurchase_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_sales_invoice_popup_payment_staffpurchase_empname.setText("");
                tv_sales_invoice_popup_payment_staffpurchase_limit.setText("");
                tv_sales_invoice_popup_payment_staffpurchase_allow_amt.setText("");
                String empcode=et_sales_invoice_popup_payment_staffpurchase_empcode.getText().toString().trim().toUpperCase();
                b_Result = objSalesInvoicePaymentsStaffPurchase.validateEmployeeStaffPurchase(empcode);
                if(!b_Result) {
                    et_sales_invoice_popup_payment_staffpurchase_empcode.requestFocus();
                    okMessage("Sales Invoice, Payment Staff Purchase", objGlobal.getErrorMessage());
                } else {
                    et_sales_invoice_popup_payment_staffpurchase_empcode.setEnabled(false);
                    bt_sales_invoice_popup_payment_staffpurchase_validate.setEnabled(false);
                    tv_sales_invoice_popup_payment_staffpurchase_empname.setText(objSalesInvoiceGlobal.getStaffPurchaseEmpName());
                    tv_sales_invoice_popup_payment_staffpurchase_limit.setText(String.format("%.2f", objSalesInvoiceGlobal.getStaffPurchaseEmpPurchaseLimit()));
                    float amt = (float) (objSalesInvoiceGlobal.getStaffPurchaseEmpPurchaseLimit()-objSalesInvoiceGlobal.getStaffPurchaseEmpPurchaseAmt());
                    tv_sales_invoice_popup_payment_staffpurchase_allow_amt.setText(String.format("%.2f", amt));
                }
            }
        });

        bt_sales_invoice_popup_payment_staffpurchase_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPayments();
                myDialog.dismiss();
            }
        });

        bt_sales_invoice_popup_payment_staffpurchase_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String empcode = et_sales_invoice_popup_payment_staffpurchase_empcode.getText().toString();
                float amt = Float.valueOf(et_sales_invoice_popup_payment_stafpurchase_useamt.getText().toString());
                b_Result = objSalesInvoicePayments.addPaymentModes("Staff Purchase",empcode,amt);
                if(!b_Result){
                    et_sales_invoice_popup_payment_staffpurchase_empcode.requestFocus();
                    okMessage("Sales Invoice, Payment Staff Purchase", objGlobal.getErrorMessage());
                }
                loadPayments();
            }
        });

        myDialog.show();
        et_sales_invoice_popup_payment_staffpurchase_empcode.requestFocus();
    }

    private void openPopupGuestPurchase() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_sales_invoice_payment_guestpurchase);

        et_sales_invoice_popup_payment_guestpurchase_empcode=(EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_payment_guestpurchase_empcode);
        bt_sales_invoice_popup_payment_guestpurchase_validate=(Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_guestpurchase_validate);
        tv_sales_invoice_popup_payment_guestpurchase_empname=(TextView)myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_guestpurchase_empname);
        bt_sales_invoice_popup_payment_guestpurchase_add=(Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_guestpurchase_add);
        et_sales_invoice_popup_payment_guestpurchase_useamt=(EditText) myDialog.findViewById(R.id.et_sales_invoice_popup_payment_guestpurchase_useamt);
        bt_sales_invoice_popup_payment_guestpurchase_close=(Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_guestpurchase_close);

        bt_sales_invoice_popup_payment_guestpurchase_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_sales_invoice_popup_payment_guestpurchase_empname.setText("");
                String empcode=et_sales_invoice_popup_payment_guestpurchase_empcode.getText().toString().trim().toUpperCase();
                b_Result = objSalesInvoicePaymentsStaffPurchase.validateEmployeeGuestPurchase(empcode);
                if(!b_Result) {
                    et_sales_invoice_popup_payment_guestpurchase_empcode.requestFocus();
                    okMessage("Sales Invoice, Payment Guest Purchase", objGlobal.getErrorMessage());
                } else {
                    et_sales_invoice_popup_payment_guestpurchase_empcode.setEnabled(false);
                    bt_sales_invoice_popup_payment_guestpurchase_validate.setEnabled(false);
                    tv_sales_invoice_popup_payment_guestpurchase_empname.setText(objSalesInvoiceGlobal.getGuestPurchaseEmpName());
                }
            }
        });
        bt_sales_invoice_popup_payment_staffpurchase_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String empcode = et_sales_invoice_popup_payment_staffpurchase_empcode.getText().toString();
                float amt = Float.valueOf(et_sales_invoice_popup_payment_stafpurchase_useamt.getText().toString());
                b_Result = objSalesInvoicePayments.addPaymentModes("Guest Purchase",empcode,amt);
                if(!b_Result){
                    et_sales_invoice_popup_payment_staffpurchase_empcode.requestFocus();
                    okMessage("Sales Invoice, Payment Guest Purchase", objGlobal.getErrorMessage());
                }
                loadPayments();
            }
        });

        bt_sales_invoice_popup_payment_guestpurchase_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPayments();
                myDialog.dismiss();
            }
        });
        myDialog.show();
        et_sales_invoice_popup_payment_guestpurchase_empcode.requestFocus();
    }

    private void clearMainControls() {
        b_Result = objSalesInvoiceControl.deleteScanItems("", 0);
        if (!b_Result) {
            okMessage("Sales Invoice", "deleteScanItems:" + objGlobal.getErrorMessage());
        }
        b_Result = objSalesInvoicePayments.deletePayments(0);
        if (!b_Result) {
            okMessage("Sales Invoice:", "deletePayments:" + objGlobal.getErrorMessage());
        }
        loadScanItemsInvoice();
        loadPayments();
        objSalesInvoiceGlobal.setStaffPurchaseEmpName("");
        objSalesInvoiceGlobal.setGuestPurchaseEmpName("");
        tv_sales_invoice_invoiceno.setText(objSalesInvoiceGlobal.getInvoiceNumber());
        tv_sales_invoice_invoicedate.setText(objGlobal.getServerDate());
        ch_sales_invoice_loyalty.setChecked(false);
        ch_sales_invoice_payment_mode.setChecked(false);
        tv_sales_invoice_netamount.setText("");
        tv_sales_invoice_total_discount.setText("");
        et_sales_invoice_paidamt.setText("");
        tv_sales_invoice_change.setText("0.00");
    }

    private void clearScanItem() {
        et_sales_invoice_popup_scan_item_barcode.setText("");
        et_sales_invoice_popup_scan_item_qty.setText("1");
        sp_sales_invoice_popup_scan_item_barcode.setSelection(0);
        List<String> arr;
        arr = new ArrayList<String>();
        ArrayAdapter<String> arrayAdp;
        arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, arr);
        sp_sales_invoice_popup_scan_item_salesprice.setAdapter(arrayAdp);
        loadScanItemsInvoice();
    }

    private void loadPayments() {
        try {
            ArrayList<SalesInvoicePaymentItems> listSalesInvoicePaymentItems = objSalesInvoicePayments.loadAllScanPayments();
            objSalesInvoicePaymentAdp = new SalesInvoiceFragment.SalesInvoicePaymentAdp(listSalesInvoicePaymentItems);
            lv_sales_invoice_popup_payment.setAdapter(objSalesInvoicePaymentAdp);
            b_Result = objSalesInvoicePayments.loadInvoicePaymentAmount();
            if (!b_Result) {
                okMessage("Sales Invoice", objGlobal.getErrorMessage());
            } else {
                tv_sales_invoice_popup_payment_totalamt.setText(String.format("%.2f", objSalesInvoiceGlobal.getTotalPaymentAmt()));
                tv_sales_invoice_popup_payment_totalamt_net.setText(String.format("%.2f", objSalesInvoiceGlobal.getTotalAmt()));
                tv_sales_invoice_popup_payment_totalamt_balance.setText(String.format("%.2f", objSalesInvoiceGlobal.getTotalAmt() - objSalesInvoiceGlobal.getTotalPaymentAmt()));
                tv_sales_invoice_total_discount.setText(String.format("%.2f", objSalesInvoiceGlobal.getTotalDiscountAmt()));
                tv_sales_invoice_netamount.setText(String.format("%.2f", objSalesInvoiceGlobal.getTotalAmt() - objSalesInvoiceGlobal.getTotalDiscountAmt()));
            }
            sp_sales_invoice_popup_payment_type.setSelection(0);
        } catch (Exception e) {
            okMessage("Sales Invoice", e.toString());
        }
    }

    private void loadScanItemsInvoice() {
        try {
            ArrayList<SalesInvoiceScanItems> listSalesInvoiceScanItems = objSalesInvoiceControl.loadAllScanItems();
            objSalesInvoiceScanItemsAdp = new SalesInvoiceFragment.SalesInvoiceScanItemsAdp(listSalesInvoiceScanItems);
            lv_sales_invoice_items.setAdapter(objSalesInvoiceScanItemsAdp);
            objSalesInvoiceControl.loadInvoiceAmount();
            et_sales_invoice_paidamt.setText("");
            tv_sales_invoice_change.setText("0.00");
            tv_sales_invoice_netamount.setText(String.format("%.2f",objSalesInvoiceGlobal.getTotalAmt()-objSalesInvoiceGlobal.getTotalDiscountAmt()));
            b_Result = objSalesInvoiceControl.getSalesInvoiceNumber();
            if (!b_Result) {
                okMessage("Sales Invoice", objGlobal.getErrorMessage());
            } else {
                tv_sales_invoice_invoiceno.setText(objSalesInvoiceGlobal.getInvoiceNumber());
            }
        } catch (Exception e) {
            okMessage("Sales Invoice", e.toString());
        }
    }

    private boolean scanBarcodeRfid() {
        String scan = objControls.replaceString(et_sales_invoice_popup_scan_item_barcode.getText().toString()).toUpperCase();
        float salesPrice = 0;
        int qty = Integer.valueOf(et_sales_invoice_popup_scan_item_qty.getText().toString());
        if (sp_sales_invoice_popup_scan_item_salesprice.getCount() > 0) {
            salesPrice = Float.valueOf(sp_sales_invoice_popup_scan_item_salesprice.getSelectedItem().toString());
        }
        if (scan.isEmpty()) {
            okMessage("Sales Invoice", "Please scan Barcode / Rfid");
            et_sales_invoice_popup_scan_item_barcode.setText("");
            et_sales_invoice_popup_scan_item_barcode.requestFocus();
            return false;
        }
        if (qty == 0) {
            qty = 1;
        }
        b_Result = objSalesInvoiceControl.getItemDetailsBarcodeRfid(scan, qty, salesPrice);
        if (!b_Result) {
            if (objSalesInvoiceGlobal.isLoadSalesPrice()) {
                ArrayAdapter<String> arrayAdp;
                arrayAdp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objSalesInvoiceGlobal.getListScanSalesPrice());
                sp_sales_invoice_popup_scan_item_salesprice.setAdapter(arrayAdp);
                et_sales_invoice_popup_scan_item_barcode.setText("");
                et_sales_invoice_popup_scan_item_barcode.setText(scan);
                sp_sales_invoice_popup_scan_item_salesprice.requestFocus();
                return false;
            } else {
                okMessage("Sales Invoice", objGlobal.getErrorMessage());
                et_sales_invoice_popup_scan_item_barcode.setText("");
                et_sales_invoice_popup_scan_item_barcode.requestFocus();
                return false;
            }
        }
        clearScanItem();
        et_sales_invoice_popup_scan_item_barcode.requestFocus();
        return true;
    }

    private class SalesInvoiceScanItemsAdp extends BaseAdapter {
        public ArrayList<SalesInvoiceScanItems> listSalesInvoiceScanItems;

        public SalesInvoiceScanItemsAdp(ArrayList<SalesInvoiceScanItems> listSalesInvoiceScanItems) {
            this.listSalesInvoiceScanItems = listSalesInvoiceScanItems;
        }

        @Override
        public int getCount() {
            return listSalesInvoiceScanItems.size();
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
            View myView = mInflater.inflate(R.layout.ticket_sales_invoice_items, null);
            final SalesInvoiceScanItems s = listSalesInvoiceScanItems.get(position);

            TextView tv_sales_invoice_rowno = (TextView) myView.findViewById(R.id.tv_sales_invoice_rowno);
            tv_sales_invoice_rowno.setText(String.valueOf(s.rowno));

            TextView tv_sales_invoice_slno = (TextView) myView.findViewById(R.id.tv_sales_invoice_slno);
            tv_sales_invoice_slno.setText(String.valueOf(s.slno));

            TextView tv_sales_invoice_itemcode = (TextView) myView.findViewById(R.id.tv_sales_invoice_itemcode);
            tv_sales_invoice_itemcode.setText(String.valueOf(s.itemcode));

            TextView tv_sales_invoice_description = (TextView) myView.findViewById(R.id.tv_sales_invoice_description);
            tv_sales_invoice_description.setText(String.valueOf(s.description));

            TextView tv_sales_invoice_qty = (TextView) myView.findViewById(R.id.tv_sales_invoice_qty);
            tv_sales_invoice_qty.setText(String.valueOf(s.quantity));

            TextView tv_sales_invoice_discount = (TextView) myView.findViewById(R.id.tv_sales_invoice_discount);
            tv_sales_invoice_discount.setText(String.format("%.2f", s.discount));

            TextView tv_sales_invoice_salesrate = (TextView) myView.findViewById(R.id.tv_sales_invoice_salesrate);
            tv_sales_invoice_salesrate.setText(String.format("%.2f", s.salesprice));

            TextView tv_sales_invoice_total = (TextView) myView.findViewById(R.id.tv_sales_invoice_total);
            tv_sales_invoice_total.setText(String.format("%.2f", s.total));

            Button bt_sales_invoice_edit = (Button) myView.findViewById(R.id.bt_sales_invoice_edit);
            bt_sales_invoice_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to delete the selected row?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    b_Result = objSalesInvoiceControl.deleteScanItems(s.itemcode,s.rowno);
                                    if(!b_Result){
                                        okMessage("Sales Invoice",objGlobal.getErrorMessage());
                                    } else {
                                        loadScanItemsInvoice();
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
            return myView;
        }
    }

    private class SalesInvoicePaymentAdp extends BaseAdapter {
        public ArrayList<SalesInvoicePaymentItems> listSalesInvoicePaymentItems;

        public SalesInvoicePaymentAdp(ArrayList<SalesInvoicePaymentItems> listSalesInvoicePaymentItems) {
            this.listSalesInvoicePaymentItems = listSalesInvoicePaymentItems;
        }

        @Override
        public int getCount() {
            return listSalesInvoicePaymentItems.size();
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
            View myView = mInflater.inflate(R.layout.ticket_sales_invoice_payment, null);
            final SalesInvoicePaymentItems s = listSalesInvoicePaymentItems.get(position);

            TextView tv_ticket_sales_invoice_payment_type = (TextView) myView.findViewById(R.id.tv_ticket_sales_invoice_payment_type);
            tv_ticket_sales_invoice_payment_type.setText(String.valueOf(s.paymentType));

            TextView tv_ticket_sales_invoice_payment_refno = (TextView) myView.findViewById(R.id.tv_ticket_sales_invoice_payment_refno);
            tv_ticket_sales_invoice_payment_refno.setText(String.valueOf(s.refNo));

            TextView tv_ticket_sales_invoice_payment_amount = (TextView) myView.findViewById(R.id.tv_ticket_sales_invoice_payment_amount);
            tv_ticket_sales_invoice_payment_amount.setText(String.format("%.2f", s.amount));

            Button bt_ticket_sales_invoice_popup_payment_delete = (Button) myView.findViewById(R.id.bt_ticket_sales_invoice_popup_payment_delete);
            bt_ticket_sales_invoice_popup_payment_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setMessage("Are You sure to delete the selected row?")
                            .setTitle("Conformation")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    b_Result = objSalesInvoicePayments.deletePayments(s.rowno);
                                    if(!b_Result){
                                        okMessage("Sales Invoice",objGlobal.getErrorMessage());
                                    } else {
                                        loadPayments();
                                        sp_sales_invoice_popup_payment_type.setSelection(0);
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
            return myView;
        }
    }

    private void openPopupInvoiceLoyalty() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.popup_sales_invoice_loyalty);
        myDialog.show();
    }

    private void openPopupInvoiceReprint() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_sales_invoice_reprint);

        et_sales_invoice_popup_reprint_invno = (EditText) myDialog.findViewById((R.id.et_sales_invoice_popup_reprint_invno));
        et_sales_invoice_popup_reprint_empname = (EditText) myDialog.findViewById((R.id.et_sales_invoice_popup_reprint_empname));
        et_sales_invoice_popup_reprint_password = (EditText) myDialog.findViewById((R.id.et_sales_invoice_popup_reprint_password));
        bt_sales_invoice_popup_reprint_print = (Button) myDialog.findViewById((R.id.bt_sales_invoice_popup_reprint_print));
        bt_sales_invoice_popup_reprint_close = (Button) myDialog.findViewById((R.id.bt_sales_invoice_popup_reprint_close));

        bt_sales_invoice_popup_reprint_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String invoiceno=et_sales_invoice_popup_reprint_invno.getText().toString().toUpperCase();
                String empname = et_sales_invoice_popup_reprint_empname.getText().toString().toUpperCase();
                String password = et_sales_invoice_popup_reprint_password.getText().toString().toUpperCase();
                b_Result = objSalesInvoicePrint.validateInvoiceForPrint(invoiceno,empname,password);
                if(!b_Result) {
                    okMessage("Sales Invoice", objGlobal.getErrorMessage());
                } else {
                    b_Result = objSalesInvoicePrint.printMainInvoice(invoiceno);
                    if (!b_Result) {
                        okMessage("Sales Invoice", objGlobal.getErrorMessage());
                    } else {
                        et_sales_invoice_popup_reprint_invno.setText("");
                        et_sales_invoice_popup_reprint_empname.setText("");
                        et_sales_invoice_popup_reprint_password.setText("");
                    }
                }
            }
        });

        bt_sales_invoice_popup_reprint_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ch_sales_invoice_reprint.setChecked(false);
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }

    private void loadPaymentMode(String paymode){
        switch (paymode) {
            case "Cash":
                openPopupPaymentCash();
                break;
            case "Card":
                openPopupPaymentCreditCard();
                break;
            case "Total Discount":
                openPopupPaymentTotalDiscount();
                break;
            case "E-Wallet":
                openPopupPaymentEwallet();
                break;
            case "Credit Note":
                openPopupPaymentCreditNote();
                break;
            case "Gift Voucher":
                openPopupPaymentGiftVoucher();
                break;
            case "Mall Voucher":
                openPopupPaymentMallVoucher();
                break;
            case "Staff Purchase":
                openPopupPaymentStaffPurchase();
                break;
            case "Guest Purchase":
                openPopupGuestPurchase();
                break;
            default:
                break;
        }
    }

    private void openPopupInvoicePayment() {
        Dialog myDialog;
        myDialog = new Dialog(getContext());
        myDialog.setCancelable(false);
        myDialog.setContentView(R.layout.popup_sales_invoice_payment);

        bt_sales_invoice_popup_payment_show_typ=(Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_payment_show_typ);
        sp_sales_invoice_popup_payment_type=(Spinner) myDialog.findViewById(R.id.sp_sales_invoice_popup_payment_type);
        lv_sales_invoice_popup_payment=(ListView) myDialog.findViewById(R.id.lv_sales_invoice_popup_payment);
        tv_sales_invoice_popup_payment_totalamt=(TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_totalamt);
        tv_sales_invoice_popup_payment_totalamt_net=(TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_totalamt_net);
        tv_sales_invoice_popup_payment_totalamt_balance=(TextView) myDialog.findViewById(R.id.tv_sales_invoice_popup_payment_totalamt_balance);
        bt_sales_invoice_popup_close=(Button) myDialog.findViewById(R.id.bt_sales_invoice_popup_close);

        loadPayments();

        ArrayAdapter<String> arrayAdp1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, objSalesInvoiceGlobal.getListPayments());
        sp_sales_invoice_popup_payment_type.setAdapter(arrayAdp1);

        bt_sales_invoice_popup_payment_show_typ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sel=sp_sales_invoice_popup_payment_type.getSelectedItem().toString();
                loadPaymentMode(sel);
            }
        });

        bt_sales_invoice_popup_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_Result = objSalesInvoicePayments.loadInvoicePaymentAmount();
                if(!b_Result){
                    okMessage("Sales Invoice",objGlobal.getErrorMessage());
                } else {
                    b_Result = objSalesInvoiceControl.loadInvoiceAmount();
                    if(!b_Result) {
                        okMessage("Sales Invoice", objGlobal.getErrorMessage());
                    } else {
                        if(objSalesInvoiceGlobal.getTotalPaymentAmt()>objSalesInvoiceGlobal.getTotalAmt()){
                            okMessage("Sales Invoice", "Paid amount should not be greater than net amount");
                        } else {
                            if (objSalesInvoiceGlobal.getTotalPaymentAmt() > 0) {
                                ch_sales_invoice_payment_mode.setChecked(true);
                            } else {
                                ch_sales_invoice_payment_mode.setChecked(false);
                            }
                            myDialog.dismiss();
                        }
                    }
                }
            }
        });

        myDialog.show();
        sp_sales_invoice_popup_payment_type.requestFocus();
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
    }
}