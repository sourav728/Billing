package subdiv.transvision.com.billing.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

import subdiv.transvision.com.billing.MainActivity;
import subdiv.transvision.com.billing.R;
import subdiv.transvision.com.billing.billing.BillingFragment;
import subdiv.transvision.com.billing.database.Databasehelper;
import subdiv.transvision.com.billing.values.FunctionCalls;
import subdiv.transvision.com.billing.values.GetSetValues;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_CONTACTS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static subdiv.transvision.com.billing.values.ConstantValues.GETSET;

public class SplashActivity extends AppCompatActivity {
    private final String NAMESPACE = "http://tempuri.org/";
    private final String URL = "http://106.51.57.253:8086/Service.asmx";
    private final String DOWNLOADUPLOADURL = "http://202.38.181.250:999/Android_Upload_Download.asmx";
    private final String SOAP_ACTION = "http://tempuri.org/";

    public static final int DLG_DATE = 2;
    public static final int RequestPermissionCode = 1;

    Databasehelper dbh;

    //	Settings st;
    FunctionCalls fcall;
    BluetoothAdapter deviceadapter;

    GetSetValues getSetValues;
    private static final int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_splash);
        fcall = new FunctionCalls();
        getSetValues = new GetSetValues();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkpermissionAbove();
            }
        },1000);
    }
    private void startup() {

        if (!Build.MANUFACTURER.matches("alps")) {
            deviceadapter = BluetoothAdapter.getDefaultAdapter();
            deviceadapter.enable();
        }
        checkforlogin();
    }
    public void checkpermissionAbove()
    {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            if (checkPermission()) {
                startup();
            } else {
                requestPermission();
            }
        } else {
            startup();
        }
    }
    private void checkforlogin()
    {
        try
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("Main_Checking","Checking_Mydb_file");
                    File MYDB =fcall.filestorepath("databases","mydb.db");
                    if (MYDB.exists())
                    {
                        Log.d("Main_Database","Exists");
                        Log.d("Main","Moving to Home Fragment");
                        movetohome();
                    }
                    else
                    {
                        Log.d("Main_Database","Does not Exists");
                        Log.d("Main","Moving to Login Activity");
                        movetologin();
                    }
                }
            }, SPLASH_TIME_OUT);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void movetohome()
    {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("GETSET",GETSET);
        startActivity(intent);
        finish();
    }
    private void movetologin()
    {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(SplashActivity.this, new String[]
                {
                        READ_PHONE_STATE,
                        WRITE_EXTERNAL_STORAGE,
                        ACCESS_FINE_LOCATION,
                        WRITE_CONTACTS
                }, RequestPermissionCode);
    }
    private boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_CONTACTS);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourthPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean ReadPhoneStatePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadLocationPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadLogsPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    if (ReadPhoneStatePermission && ReadStoragePermission && ReadLocationPermission && ReadLogsPermission) {
                        startup();
                    } else {
                        Toast.makeText(SplashActivity.this, "Required All Permissions to granted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
