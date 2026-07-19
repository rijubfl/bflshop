package com.bflgroup.bflshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bflgroup.bflshop.comm.Controls;
import com.bflgroup.bflshop.comm.Global;
import com.bflgroup.bflshop.comm.SaredRef;
import com.bflgroup.bflshop.db.DBConnection;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    Global objGlobal = Global.getInstance();
    DBConnection dbConnection = new DBConnection();
    Controls objControls = new Controls();
    SaredRef objSaredRef;
    private ResultSet rs;
    private EditText signInUserId;
    private EditText signInPasssword;
    private EditText signInServer;
    private Button signInButton;
    private ProgressBar signInProgress;
    boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        signInUserId = (EditText) findViewById(R.id.sign_in_userid);
        signInPasssword = (EditText) findViewById(R.id.sign_in_password);
        signInServer = (EditText) findViewById(R.id.sign_in_server);
        signInButton = (Button) findViewById(R.id.sign_in_btn);
        signInProgress = (ProgressBar) findViewById(R.id.sign_in_progress);
        objSaredRef = new SaredRef(this);
        signInServer.setText(objSaredRef.loadServer());

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInProgress.setVisibility(View.VISIBLE);
                objGlobal.setDbName("FabsMain");
                objGlobal.setServerIP(signInServer.getText().toString());
                objGlobal.setOfficeType("SHOP");
                if (objGlobal.getServerIP().equals("192.168.5.51")) objGlobal.setOfficeType("HO");
                result = checkPassMain();
                if (result) {
                    result = dbConnection.connectDb();
                    if (result) {
                        objGlobal.setCloudDbName("BFLDATA");
                        result = dbConnection.connectCloudDb();
                        if (result) {
                            result = validateUser();//check the user details
                            if (result) {
                                result = objControls.getControlMain();//assign global values
                                if (result) {
                                    result = dbConnection.connectDb();//onnect main database
                                    if (result) {
                                        result = objControls.getControl();//assign global values
                                        if (result) {
                                            Intent intent;
                                            objSaredRef.saveServer(signInServer.getText().toString());
                                            signInProgress.setVisibility(view.INVISIBLE);
                                            intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!result) {
                    Toast.makeText(LoginActivity.this, objGlobal.getErrorMessage(), Toast.LENGTH_LONG).show();
                    signInProgress.setVisibility(view.INVISIBLE);
                }
            }
        });
    }
    private boolean checkPassMain() {
        try {
            objGlobal.setSqUserName("");
            objGlobal.setSqPassword("");
            result = dbConnection.connectDbMain();
            if (result) {
                rs = dbConnection.getResultSet("select field2=CONVERT(VARCHAR(100),DecryptByPassPhrase('bfl$wms@123', field2)),field3=CONVERT(VARCHAR(100),DecryptByPassPhrase('bfl$wms@123', field3)) from " +
                        "fabsmain.dbo.sys_credentials where field1='PDA'", objGlobal.getConnection());
                if (rs.next()) {
                    objGlobal.setSqUserName(rs.getString("field2"));
                    objGlobal.setSqPassword(rs.getString("field3"));
                } else {
                    objGlobal.setErrorMessage("LoginActivity:checkPassMain");
                    return false;
                }
            }
            if (objGlobal.getSqUserName().isEmpty() || objGlobal.getSqPassword().isEmpty()) {
                objGlobal.setErrorMessage("LoginActivity:checkPassMain:Invalid Login");
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("LoginActivity:checkPassMain:" + ex);
            return false;
        }
        return true;
    }

    public boolean validateUser() {
        String pdaVerActive = "", pdaVer = "";
        if (TextUtils.isEmpty(signInUserId.getText())) {
            objGlobal.setErrorMessage("Please enter username");
            signInUserId.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(signInPasssword.getText())) {
            objGlobal.setErrorMessage("Please enter password");
            signInPasssword.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(signInServer.getText())) {
            objGlobal.setErrorMessage("Please enter server");
            signInServer.requestFocus();
            return false;
        }
        objGlobal.setDeviceName(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        if (TextUtils.isEmpty(objGlobal.getDeviceName())) {
            objGlobal.setErrorMessage("Device name is blank");
            return false;
        }
        objGlobal.setPdaversion(getApplicationContext().getString(R.string.menu_version));
        try {
            rs = dbConnection.getResultSet("select * from BFLDATA.Dbo.appversion where app='BFLShop'", objGlobal.getCloudCon());
            if (rs.next()) {
                pdaVerActive = rs.getString("active");
                pdaVer = rs.getString("version");
            }
            if (pdaVerActive.equals("Y")) {
                if (!pdaVer.equals(objGlobal.getPdaversion())) {
                    objGlobal.setErrorMessage("Pls check the version. The latest version is - " + pdaVer);
                    return false;
                }
            }
            String query = "select a.userid,a.username,a.DBName,empcode=isnull(b.RecStartingNo,''),b.country from pdausers a,[user] b where a.userid=b.userid " +
                    "and a.username='" + signInUserId.getText() + "' and a.pass='" + signInPasssword.getText() + "'";
            Statement stmt = objGlobal.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                objGlobal.setUserId(rs.getInt("userid"));
                objGlobal.setUserName(rs.getString("username"));
                objGlobal.setDbName(rs.getString("DBName"));
                objGlobal.setEmpCode(rs.getString("empcode"));
                objGlobal.setCountryCode(rs.getString("Country"));
                return true;
            } else {
                objGlobal.setErrorMessage("Invalid username or password");
                return false;
            }
        } catch (Exception ex) {
            objGlobal.setErrorMessage("LoginActivity:signInButton:Click" + ex.toString());
            return false;
        }
    }
}
