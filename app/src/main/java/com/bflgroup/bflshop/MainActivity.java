package com.bflgroup.bflshop;

import android.os.Bundle;

import com.bflgroup.bflshop.comm.PosGlobal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private PosGlobal objPosGlobal = new PosGlobal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_grn_transfer,R.id.nav_grn_transfer,R.id.nav_grn_transfer_new, R.id.nav_settings, R.id.nav_price_check, R.id.nav_stock_taking, R.id.nav_stock_taking_reports,
                R.id.nav_add_discount, R.id.nav_sales_invoice, R.id.nav_store_entry, R.id.nav_ageing_print, R.id.nav_grn_direct_delivery, R.id.nav_store_entry,R.id.nav_gin_verification,
                R.id.nav_ageing_print_wify, R.id.nav_ageing_print_wify_report, R.id.nav_user_create, R.id.nav_stock_taking_verification,R.id.nav_rfid_grn_transfer,R.id.nav_ho_pricechange_print_wify,R.id.nav_stock_taking_rfid)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_ageing_print_wify).setEnabled(!objPosGlobal.getSlashActive());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
