package subdiv.transvision.com.billing.billing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import subdiv.transvision.com.billing.ClassCalculation;
import subdiv.transvision.com.billing.R;
import subdiv.transvision.com.billing.adapters.RoleAdapter;
import subdiv.transvision.com.billing.database.Databasehelper;
import subdiv.transvision.com.billing.service.ClassGPS;
import subdiv.transvision.com.billing.values.FunctionCalls;
import subdiv.transvision.com.billing.values.GetSetValues;
import subdiv.transvision.com.billing.values.Pay;

import static android.Manifest.permission.CAMERA;
import static subdiv.transvision.com.billing.values.ConstantValues.BILLING_CUT_OFF_TIME;
import static subdiv.transvision.com.billing.values.ConstantValues.CURRENT_VERSION;
import static subdiv.transvision.com.billing.values.ConstantValues.PREFS_NAME;

public class ConsumerBilling extends Activity {
    public static final int CHLD_REQ1 = 1;
    public static final int RequestPermissionCode = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "MyCamera";
    private static final int SUB_NORMAL_DLG = 3;
    private static final int AB_NORMAL_DLG = 4;
    private static final int DLG_TIME_OVER = 5;
    private static final int DLG_MTR_ASSET = 6;

    private static Uri fileUri; // file url to store image/video
    static File mediaFile;
    // private Uri fileUri;
    LocationManager locationManager;
    public int i = 0;

    TextView tvName, tvAdd, tvTarrif, tvPrvStatus, tvDate, tvPrvRead, tvRRNO, tvheader, tvStatus, tvtextRRNo, tvsemiRRNo, tvtextDate,
            tvtextStatus, tvbilledpayable, tvbilledpayablesemi, tvbilledpayabletext, tvbilledunits, tvbilledunitssemi,
            tvbilledunitstext, tvbilledreading, tvbilledreadingsemi, tvbilledreadingtext, tvbilledstatus, tvbilledstatussemi,
            tvbilledstatustext, tvMF, tvcount, tvaccount_id, tvtext_accountid, tvsemi_accountid, tv_no_of_weeks, tv_pd_recorded, tv_tccode,
            tv_curr_reading, tv_mtr_digit, tv_pf, tv_tod_on_peak, tv_tod_off_peak, tv_bmd, tv_kvah;

    AutoCompleteTextView actvsearch;

    ImageView imMeterReding, imgbilled, fullbilledimage;

    RelativeLayout billinglayout;
    LinearLayout feder_dtc_layout;

    Bitmap image_bmp;

    EditText etCurReading, etPF, etTOD, etBMD, etTODOFPEAK, etTODONPEAK, etTCCODE, etNoofWeeks, etPDReading,
            etPhNumber, et_kvah, et_mtr_digit;

    Button takepic_btn;

    // //////////////////-----Strings to get values from MAST CUST
    // Table----////////////////////////////////

    String cons_Month = "", cons_CurrRead_date = "", cons_RRNO = "", cons_Name = "", cons_Addr = "", cons_imageextension = "",
            cons_Tarrif = "", cons_MF = "", cons_PrevMonthStatus = "", cons_Tariff_name = "", strBilledNumber = "",
            cons_InterestAmt = "", cons_Avg_cons = "", cons_Linemin = "", cons_SantionHp = "", cons_IMEI = "",
            cons_SanctionKw = "", cons_PrvRead = "", cons_FR = "", cons_IR = "", present_date = "",
            prvstatus = "", name1 = "", name2 = "", name3 = "", tariff_name = "", cons_billeddate = "",
            cons_DLCount1 = "", cons_Arrears = "", cons_PfFlag = "", cons_BillFor = "", cons_PhNumber = "0",
            cons_LegFol = "", cons_OddEven = "", cons_SSNO = "", cons_Num = "", cons_SSNO1 = "",
            cons_RebateFlag = "", cons_RRebate = "", cons_Extra1 = "", cons_Data1 = "", cons_aadhaar_no = "0", cons_mail_id = "0",
            cons_Extra2 = "", cons_Data2 = "", cons_Deposit = "", cons_MtrDigit = "", cons_PFValue = "",
            cons_BMDvalue = "", cons_ASDamnt = "", cons_IODAmnt = "", cons_BillNo = "",
            cons_CapFlag = "", cons_TodFlag = "", cons_TodPrev1 = "", cons_TodPrev3 = "",
            cons_InterOnDep = "", cons_So_FeedTLPole = "", cons_PrevReadDate = "",
            cons_BillDays = "", cons_MtrSrlNO = "", cons_CHQDisHnrFlag = "", cons_IMGdisplay = "",
            cons_CHQDisHnrDate = "0", cons_testStatus2 = "", cons_FDRName = "", cons_TCCode = "", cons_TCCode1 = "",
            cons_MTRFlag = "", cons_checkid = "", cons_readbillingid = "", cons_status = "", cons_otherbillings = "",
            cons_testRRno = "", cons_Hp = "", cons_Kw = "", cons_StatusLabel = "", cons_Units = "",
            cons_Duedate = "", cons_ImgAdd = "", cons_CurReading = "", cons_todOfPeak = "",
            cons_todOnPeak = "", cons_testStatus = "", cons_billedpayable = "", cons_stsvalue = "",
            cons_billedunits = "", cons_billedreading = "", cons_billedstatus = "",
            cons_billedstatus2 = "", cons_billedstatus3 = "", cons_statusbilled = "", cons_creadj = "",
            cons_billedstatus4 = "", cons_display = "", cons_billtime = "", cons_gpslat = "", cons_readkvah = "",
            cons_gpslong = "", cons_payprofit = "", cons_payloss = "", cons_pfval = "", cons_bmdkw = "", cons_dl_count = "",
            cons_bmdval = "", cons_billingdate = "", cons_checkstatus = "", cons_weekSTS = "", cons_PDread = "", cons_PDpenalty = "",
            cons_inven_load = "", cons_user_latitude = "", cons_user_longitude = "", cons_mtr_digit = "", cons_election_no = "0",
            cons_ration_no = "0", cons_water_no = "0", cons_house_no = "0", cons_battery_level = "", cons_signal_strength = "";
    static String pathname = "";
    static String pathextension = "";
    static String cons_subdivcode = "";
    static String Consid = "";
    static String cons_MRcode = "";

    int a = 0, yy = 0, mm = 0, dd = 0, intpfFlag = 0, intTodFlag = 0, int_monthPrevReadDate = 0,
            intPrvMonthStatus = 0, intTariff = 0, intPrvRead = 0, intMF = 0, intCapFlag = 0,
            intTodOffPeak = 0, intTodOnPeak = 0, intTodPrev1 = 0, intTodPrev3 = 0,
            intCurReading = 0, Present_STS = 0, cons_primarykey = 0,
            cons_todonconsume = 0, cons_todoffconsume = 0, FR = 0, IR = 0, idread = 0, idreadnext = 0;

    double intPFEnteredValue = 0, int_Arrears = 0, intBMDValue = 0, cons_DLCount = 0, data1 = 0, data2 = 0;

    double cons_billAmount = 0, cons_TOD = 0, cons_GOK = 0, cons_rebate = 0, int_consDeposit = 0, cons_charity = 0,
            BMDPeanlities = 0, cons_paydiff = 0, cons_payamount = 0, cons_payableAmount = 0,
            cons_tax = 0, cons_intr = 0, cons_PF = 0, Int_Amt = 0, cons_flGOK = 0, cons_billAmount1 = 0;

    boolean btEnterclicked = false, btPFclicked = false, btBMDclicked = false, phnumberclicked = false,
            btTODclicked = false, reportbilled = false, todvalueread = false, tccodeclicked = false,
            weeksclicked = false, pdreadclicked = false, onpreviousclick = false, valuefound = false,
            UnBilled = false, DLBilled = false, billedimage = false, imagetaken = false, insert_success = false;

    double cons_totalEC = 0, cons_totalFC = 0, cons_totalEC1 = 0, cons_fec = 0;
    double[] cons_ArrFrate = new double[10];
    double[] cons_ArrFslab = new double[10];
    double[] cons_ArrErate = new double[10];
    double[] cons_ArrEslab = new double[10];
    double[] cons_ArrEc = new double[10];
    double[] cons_ArrFc = new double[10];

    File destination;

    Databasehelper dbh;
    ClassGPS gps;
    FunctionCalls fcall;
    static FunctionCalls functionCalls;
    Cursor c, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c14, c15, c16, c17, c18, c19, c20, c21;
    Spinner Status, sp_feder, sp_dtc;
    RoleAdapter feder_adapter, dtc_adapter;
    ArrayList<GetSetValues> feder_list, dtc_list;
    Myspinneradapter aa;
    ArrayAdapter<String> aa1, aa2, aa3;
    ArrayList<Pay> billStatus;
    ArrayList<String> a1, a2, a3;
    SQLiteDatabase sdb;
    ClassCalculation cb;
    GetSetValues getset;
    static Context con;
    SharedPreferences settings;
    Pay pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_datail);

        try {
            intializeViews(); // Calling Function to initialize Views
            cons_display = "billing";
            // actionbaricon();
            /*Internet = isNetworkAvailable(getApplicationContext());
            if (!Internet) {
		    	try {
					turnMobileConnection(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}*/
            try {
                dateSet();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            c = dbh.getData();
            countNumberofRows();

            c21 = dbh.subdivdetails();
            c21.moveToNext();
            cons_subdivcode = c21.getString(c21.getColumnIndex("SUBDIV_CODE"));
            try {
                cons_fec = Double.parseDouble(c21.getString(c21.getColumnIndex("FEC")));
            } catch (NullPointerException e) {
                e.printStackTrace();
                cons_fec = 0;
            }

            Intent in = getIntent();
            if (in.getExtras() != null) {
                Bundle bnd = in.getExtras();
                if (bnd.getString("RRNO") != null) {
                    String billing_rrno = bnd.getString("RRNO");
                    cons_otherbillings = "Yes";
                    c14 = dbh.getAccountID(billing_rrno);
                    while (c14.moveToNext()) {
                        settextviewsvalue(c14);
                    }
                    if (bnd.getString("STATUS") != null) {
                        cons_status = bnd.getString("STATUS");
                    }
                    if (bnd.getString("billing") != null) {
                        cons_stsvalue = bnd.getString("billing");
                    }
                } else {
                    cons_otherbillings = "No";
                    if (bnd.getString("status") != null) {
                        String billing_status = bnd.getString("status");
                        if (billing_status.equals("Billing Deleted")) {
                            onprevious();
                        }
                    }
                }
            } else {
                cons_otherbillings = "No";
                setData();
            }
            spinnerSetData();
            cons_billtime = fcall.currentDateandTime();
            cons_Month = fcall.monthnumber(present_date);
            checkbilled(cons_Num);
            try {
                Cursor c1 = dbh.getImageAddressString(cons_Num);
                c1.moveToNext();
                String imagepath = c1.getString(c1.getColumnIndex("IMGADD"));
                if (imagepath.equals("noimage") || imagepath.equals("") || imagepath.equals(" ")) {
                    cons_IMGdisplay = "";
                } else
                    cons_IMGdisplay = fcall.filepath("Compressed") + File.separator + c1.getString(c1.getColumnIndex("IMGADD"));
            } catch (CursorIndexOutOfBoundsException e) {
                if (imMeterReding.getVisibility() == View.VISIBLE) {
                    imMeterReding.setImageResource(R.drawable.ffff);
                } else {
                    imgbilled.setImageResource(R.drawable.ffff);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            try {
                if (cons_IMGdisplay.equals("")) {
//                    Toast.makeText(ConsumerBilling.this, "No Pic ", Toast.LENGTH_SHORT).show();
                } else {
                    File imagefile = new File(cons_IMGdisplay);
                    if (imagefile.exists()) {
                        Uri uri = Uri.parse(cons_IMGdisplay);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;
                        final Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
                        if (imMeterReding.getVisibility() == View.VISIBLE) {
                            imMeterReding.setImageBitmap(bitmap);
                        } else {
                            imgbilled.setVisibility(View.VISIBLE);
                            billedimage = true;
                            imgbilled.setImageBitmap(bitmap);
                        }
                    } else {
                        if (imMeterReding.getVisibility() == View.VISIBLE) {
                            imMeterReding.setImageResource(R.drawable.ffff);
                        } else {
                            imgbilled.setImageResource(R.drawable.ffff);
                        }
                        if (imgbilled.getVisibility() == View.VISIBLE)
                            imgbilled.setVisibility(View.GONE);
                    }
                }
            } catch (NullPointerException e) {
                if (imMeterReding.getVisibility() == View.VISIBLE) {
                    imMeterReding.setImageResource(R.drawable.ffff);
                } else {
                    imgbilled.setImageResource(R.drawable.ffff);
                }
                if (imgbilled.getVisibility() == View.VISIBLE)
                    imgbilled.setVisibility(View.GONE);
            }

            imMeterReding.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!fcall.isDeviceSupportCamera(ConsumerBilling.this)) {
                        Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
                        // will close the app if the device does't have camera
                        finish();
                    } else {
                        // capture picture
                        /*captureImage();*/
                        checkforCameraPermissionMandAbove();
                    }
                    /*Intent in = new Intent(ConsumerBilling.this, CameraStarting.class);
                    in.putExtra("consid", cons_Num);
					startActivityForResult(in, CHLD_REQ1);*/
                }
            });

            // Function to set Item Selected by
            // Spinner==========================================================
            Status.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Present_STS = position + 1;
                    selectedspinneritem(Present_STS);
                    c = dbh.spinnerSelectedData(Present_STS);
                    if (c != null) {
                        c.moveToNext();
                        cons_StatusLabel = c.getString(c.getColumnIndex("STATUS_LABEL"));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ConsumerBilling.this, "Unable to Process", Toast.LENGTH_SHORT).show();
            finish();
        }

        imgbilled.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (billedimage) {
                    Uri uri = Uri.parse(cons_IMGdisplay);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
                    fullbilledimage.setImageBitmap(bitmap);
                    billinglayout.setVisibility(View.GONE);
                    fullbilledimage.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ConsumerBilling.this, "No image to show", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        fullbilledimage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                billinglayout.setVisibility(View.GONE);
                fullbilledimage.setVisibility(View.GONE);
            }
        });

        etPF.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    if (s.length() == 1) {
                        if (s.toString().equals("0")) {
                            etPF.setText(s.toString() + ".");
                            etPF.setSelection(etPF.getText().length());
                        } else etPF.setText("");
                    }
                }
                if (before == 1) {
                    if (s.length() == 1) {
                        etPF.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.billingmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                if (fcall.compare_end_billing_time(fcall.convertTo24Hour(BILLING_CUT_OFF_TIME))) {
                    onpreviousclick = true;
                    c.close();
                    onprevious();
                    checkbilled(cons_Num);
                    onpreviousclick = false;
                } else showdialog(DLG_TIME_OVER);
                /*onpreviousclick = true;
                c.close();
                onprevious();
                checkbilled(cons_Num);
                onpreviousclick = false;*/
                break;

            case R.id.item2:
                if (fcall.compare_end_billing_time(fcall.convertTo24Hour(BILLING_CUT_OFF_TIME))) {
                    c.close();
                    onnext();
                    checkbilled(cons_Num);
                } else showdialog(DLG_TIME_OVER);
                /*c.close();
                onnext();
                checkbilled(cons_Num);*/
                break;

            case R.id.item3:
                if (fcall.compare_end_billing_time(fcall.convertTo24Hour(BILLING_CUT_OFF_TIME))) {
                    sub_ab_normal_readings();
                } else showdialog(DLG_TIME_OVER);
                /*try {
                    checkdlbilled();
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                break;

            case R.id.item4:
                oncancel();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sub_ab_normal_readings() {
        if (!TextUtils.isEmpty(etCurReading.getText().toString())) {
            double avg_cons = Double.parseDouble(cons_Avg_cons);
            double units = Double.parseDouble(etCurReading.getText().toString()) - Double.parseDouble(cons_PrvRead);
            if (units > (avg_cons * 2.5)) {
                showdialog(AB_NORMAL_DLG);
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 150);
            } else if (units < (avg_cons * 0.5)) {
                showdialog(SUB_NORMAL_DLG);
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 150);
            } else {
                try {
                    checkdlbilled();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            fcall.showtoast(ConsumerBilling.this, "Enter Current Reading...");
            etCurReading.setError("Enter Current Reading...");
        }
    }

    public void onnext() {
        billedimage = false;
        if (tvheader.getVisibility() == View.VISIBLE) {
            tvheader.setVisibility(View.GONE);
            actvsearch.setVisibility(View.GONE);
        }
        c = dbh.getBilledRecordNumber();
        c.moveToNext();
        String strBilledNumber = c.getString(c.getColumnIndex("Billed_Record"));
        a = Integer.parseInt(strBilledNumber);
        if (UnBilled) {
            c16 = dbh.notbilled();
            c16.moveToLast();
            idreadnext = c16.getInt(c16.getColumnIndex("_id"));
        } else {
            if (DLBilled) {
                c16 = dbh.dlbilled();
                c16.moveToLast();
                idreadnext = c16.getInt(c16.getColumnIndex("_id"));
            } else {
                c16 = dbh.totalbillingrecords();
                c16.moveToLast();
                idreadnext = c16.getInt(c16.getColumnIndex("_id"));
            }
        }
        if (idreadnext == a) {
            dbh.updatebill(a);
        } else {
            a++;
            dbh.updatebill(a);
            setData();
            etPhNumber.setText("");
            etCurReading.setText("");
            etTODONPEAK.setText("");
            etTODOFPEAK.setText("");
            if (intpfFlag == 1) {
                etPF.setText(cons_PFValue);
                etBMD.setText(cons_bmdval);
            } else {
                etPF.setText("");
                etBMD.setText("");
            }
            Status.setSelection(2);
        }
    }

    public void onprevious() {
        billedimage = false;
        if (tvheader.getVisibility() == View.VISIBLE) {
            tvheader.setVisibility(View.GONE);
            actvsearch.setVisibility(View.GONE);
        }
        c = dbh.getBilledRecordNumber();
        c.moveToNext();
        String strBilledNumber = c.getString(c.getColumnIndex("Billed_Record"));
        a = Integer.parseInt(strBilledNumber);
        if (UnBilled) {
            c17 = dbh.notbilled();
            c17.moveToFirst();
            idread = c17.getInt(c17.getColumnIndex("_id"));
        } else {
            if (DLBilled) {
                c17 = dbh.dlbilled();
                c17.moveToFirst();
                idread = c17.getInt(c17.getColumnIndex("_id"));
            } else {
                c17 = dbh.totalbillingrecords();
                c17.moveToFirst();
                idread = c17.getInt(c17.getColumnIndex("_id"));
            }
        }
        if (idread == a) {
            dbh.updatebill(1);
        } else {
            a--;
            dbh.updatebill(a);
            setData();
            etPhNumber.setText("");
            etCurReading.setText("");
            etTODONPEAK.setText("");
            etTODOFPEAK.setText("");
            if (intpfFlag == 1) {
                etPF.setText(cons_PFValue);
                etBMD.setText(cons_bmdval);
            } else {
                etPF.setText("");
                etBMD.setText("");
            }
            Status.setSelection(2);
        }
    }

    public void onprint() throws ParseException {
        GPSLocation();
        setValueClassBean();
        if (cons_Tarrif.equals("10")) {
            String Consume = getset.getUnits();
            int consUnits = Integer.parseInt(Consume);
            if (consUnits > 40) {
                c = dbh.getTarrifDataBJ("1", "20");
            } else {
                c = dbh.getTarrifDataBJ("1", "10");
            }
        } else {
            if (cons_Tarrif.equals("23")) {
                if (cons_RRebate.equals("0")) {
                    c = dbh.getTarrifDataBJ("0", "20");
                } else {
                    if (cons_RRebate.equals("1")) {
                        c = dbh.getTarrifDataBJ("1", "20");
                    }
                }
            } else {
                if (cons_Tarrif.equals("60") || cons_Tarrif.equals("61") || cons_Tarrif.equals("70")) {
                    c = dbh.getTarrifDataBJ("0", cons_Tarrif);
                } else {
                    if (cons_Tarrif.equals("70")) {
                        c = dbh.getTarrifDataBJ("0", cons_Tarrif);
                    } else {
                        if (cons_Tarrif.equals("40")) {
                            c = dbh.getTarrifDataBJ("0", cons_Tarrif);
                        } else c = dbh.getTarrifData(cons_Num, cons_RRebate);
                    }
                }
            }
        }
        if (cons_RebateFlag.equals("4")) {
            if (cons_Tarrif.equals("30")) {
                if (cons_RRebate.equals("0")) {
                    c = dbh.getTarrifDataBJ("3", cons_Tarrif);
                }
                if (cons_RRebate.equals("1")) {
                    c = dbh.getTarrifDataBJ("4", cons_Tarrif);
                }
            }
            if (cons_Tarrif.equals("50")) {
                if (cons_RRebate.equals("0")) {
                    c = dbh.getTarrifDataBJ("3", cons_Tarrif);
                }
                if (cons_RRebate.equals("1")) {
                    c = dbh.getTarrifDataBJ("4", cons_Tarrif);
                }
            }
            if (cons_Tarrif.equals("51")) {
                if (cons_RRebate.equals("0")) {
                    c = dbh.getTarrifDataBJ("3", cons_Tarrif);
                }
                if (cons_RRebate.equals("1")) {
                    c = dbh.getTarrifDataBJ("4", cons_Tarrif);
                }
            }
            if (cons_Tarrif.equals("52")) {
                if (cons_RRebate.equals("0")) {
                    c = dbh.getTarrifDataBJ("3", cons_Tarrif);
                }
                if (cons_RRebate.equals("1")) {
                    c = dbh.getTarrifDataBJ("4", cons_Tarrif);
                }
            }
            if (cons_Tarrif.equals("53")) {
                if (cons_RRebate.equals("0")) {
                    c = dbh.getTarrifDataBJ("3", cons_Tarrif);
                }
                if (cons_RRebate.equals("1")) {
                    c = dbh.getTarrifDataBJ("4", cons_Tarrif);
                }
            }
        }
        if (!cons_RebateFlag.equals("4")) {
            if (cons_Tarrif.equals("50") || cons_Tarrif.equals("51")
                    || cons_Tarrif.equals("52") || cons_Tarrif.equals("53")) {
                c = dbh.getTarrifDataBJ("0", cons_Tarrif);
            }
        }
        cb.FcCalculation(c, cons_RebateFlag);
        cb.EcCalculation(c);
        cons_GOK = cb.gokCalculation(c);
        cons_rebate = cb.billDeduCtion(c);
        cons_charity = cb.charityCalculation(c);
        if (intpfFlag == 2 || intpfFlag == 1) {
            cb.billPenalties(c);
        }
        cons_PF = cb.pfPenality();
        cb.billExtraCharges(c);
        BMDPeanlities = cb.bmdPenalities(intBMDValue, cons_inven_load, cons_PfFlag, cons_bmdkw);
        if (cons_Tarrif.equals("70")) {
            double pdpen = Double.parseDouble(cons_PDpenalty);
            double amount = cb.billAmount();
            cons_billAmount = (amount + pdpen);
        } else {
            cons_billAmount = cb.billAmount();
        }
        cons_TOD = cb.todCalculation(c);
        if (cons_Tarrif.equals("70")) {
            double payable = cb.billFinalPayment();
            double pdpen = Double.parseDouble(cons_PDpenalty);
            cons_payamount = (payable + pdpen);
        } else {
            cons_payamount = cb.billFinalPayment();
        }
        cons_payableAmount = Math.round(cons_payamount);
        cons_paydiff = cons_payableAmount - cons_payamount;
        if (cons_paydiff >= 0) {
            cons_payprofit = fcall.decimalroundoff(cons_paydiff);
            cons_payloss = "0";
        }
        if (cons_paydiff < 0) {
            cons_payprofit = "0";
            cons_payloss = fcall.decimalroundoff(cons_paydiff);
        }
        cons_ArrErate = cb.erateForTextViews();
        cons_ArrFrate = cb.frateForTextViews();
        cons_ArrEslab = cb.eslabForTextViews();
        cons_ArrFslab = cb.fslabForTextViews();
        cons_ArrEc = cb.ecForTextViews();
        cons_ArrFc = cb.fcForTextViews();
        if (cons_RebateFlag.equals("5")) {
            double totalEC = cb.totalEC();
            cons_totalEC = totalEC - cons_GOK;
        } else {
            cons_totalEC = cb.totalEC();
        }
        cons_totalFC = cb.totalFC();
        cons_tax = Double.parseDouble(fcall.decimalround(cb.tax()));
        cons_intr = cb.intr();
        String Units1 = cb.consumeUnits();
        cons_Units = Units1.substring(0, Units1.indexOf("."));
        if (!dbh.checkinserteddata(cons_Num)) {
            if (Integer.parseInt(getset.getUnits()) >= 0) {
                if (cons_billAmount > 0) {
                    insertIntoTable();
                } else {
                    fcall.showtoast(ConsumerBilling.this, "Some junk value found.. Please bill it once again...");
                    finish();
                }
            } else {
                etCurReading.setError("Please check with the current reading..");
                etCurReading.requestFocus();
                etCurReading.setSelection(etCurReading.getText().length());
            }
        }
        /**************************BELOW CODE WILL BE ADDED********************/
        /*********************************************************************/
        /*********************************************************************/
       /* if (insert_success) {
            insert_success = false;
            Intent in = new Intent(ConsumerBilling.this, Reportdisp.class);
            in.putExtra("RRNO", cons_Num);
            in.putExtra("billing", cons_display);
            if (cons_otherbillings.equals("Yes")) {
                in.putExtra("STATUS", cons_status);
            }
            startActivity(in);
            cb.resetAllValues();

            etCurReading.setText("");
            if (etPF.getVisibility() == View.VISIBLE) {
                if (intpfFlag == 1) {
                    etPF.setText(cons_PFValue);
                    etBMD.setText(cons_bmdval);
                    et_kvah.setText(cons_readkvah);
                } else {
                    etPF.setText("");
                    etBMD.setText("");
                    et_kvah.setText("");
                }
            }
            if (etTODONPEAK.getVisibility() == View.VISIBLE) {
                etTODONPEAK.setText("");
                etTODOFPEAK.setText("");
            }
        }*/
        /************************Bnd OF CODE***********************/
    }

    private void valuesclear() {
        strBilledNumber = "";
        cons_RRNO = "";
        cons_Name = "";
        cons_Addr = "";
        cons_Tarrif = "";
        tariff_name = "";
        cons_MF = "";
        cons_PrevMonthStatus = "";
        cons_Avg_cons = "";
        cons_Linemin = "";
        cons_SantionHp = "";
        cons_SanctionKw = "";
        cons_PrvRead = "";
        cons_FR = "";
        cons_IR = "";
        cons_DLCount1 = "";
        cons_Arrears = "";
        cons_PfFlag = "";
        cons_BillFor = "";
        cons_MRcode = "";
        cons_LegFol = "";
        cons_OddEven = "";
        cons_SSNO1 = "";
        cons_Num = "";
        cons_RebateFlag = "";
        cons_RRebate = "";
        cons_Data1 = "";
        cons_Extra2 = "";
        cons_Data2 = "";
        cons_Deposit = "";
        cons_MtrDigit = "";
        cons_PFValue = "";
        cons_bmdval = "";
        cons_ASDamnt = "";
        cons_IODAmnt = "";
        cons_InterestAmt = "";
        cons_CapFlag = "";
        cons_TodFlag = "";
        cons_TodPrev1 = "";
        cons_TodPrev3 = "";
        cons_InterOnDep = "";
        cons_So_FeedTLPole = "";
        cons_Tariff_name = "";
        cons_PrevReadDate = "";
        cons_CurrRead_date = "";
        cons_BillDays = "";
        cons_MtrSrlNO = "";
        cons_bmdkw = "";
        cons_creadj = "";
        cons_FDRName = "";
        cons_TCCode1 = "";
        cons_MTRFlag = "";
        cons_readkvah = "";
        cons_inven_load = "";
        intTariff = 0;
        intMF = 0;
        intPrvMonthStatus = 0;
        intPrvRead = 0;
        FR = 0;
        IR = 0;
        cons_DLCount = 0;
        int_Arrears = 0;
        intpfFlag = 0;
        data1 = 0;
        data2 = 0;
        int_consDeposit = 0;
        Int_Amt = 0;
        intCapFlag = 0;
        intTodFlag = 0;
        intTodPrev1 = 0;
        intTodPrev3 = 0;
        int_monthPrevReadDate = 0;
        cons_CurReading = "";
        imagetaken = false;
    }

    public void oncancel() {
        imMeterReding.setImageBitmap(null);
        finish();
    }

    @TargetApi(23)
    public void checkforCameraPermissionMandAbove() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                captureImage();
            } else {
                requestPermission();
            }
        } else {
            captureImage();
        }
    }

    @TargetApi(23)
    private void requestPermission() {
        ActivityCompat.requestPermissions(ConsumerBilling.this, new String[]
                {
                        CAMERA
                }, RequestPermissionCode);
    }

    @TargetApi(23)
    private boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission) {
                        captureImage();
                    } else {
                        fcall.logStatus("Permission Denied");
                    }
                }
                break;
        }
    }

    /**
     * Capturing Camera Image will launch camera app request image capture
     */
    @TargetApi(24)
    private void captureImage() {
        Consid = cons_Num;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, this);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // savetotext file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
        previewCapturedImage();
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                /*previewCapturedImage();*/
                cons_ImgAdd = pathname;
                cons_imageextension = pathextension;
                Bitmap bitmap = null;
                try {
                    bitmap = fcall.getImage(cons_ImgAdd, getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fcall.checkimage_and_delete("Compressed", cons_Num);
                destination = fcall.filestorepath("Compressed", cons_imageextension);
                saveExternalPrivateStorage(destination, bitmap);
                String destinationfile = fcall.filepath("Compressed") + File.separator + cons_imageextension;
                Bitmap bitmap1 = null;
                try {
                    bitmap1 = fcall.getImage(destinationfile, getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imMeterReding.setImageBitmap(bitmap1);
                imagetaken = true;
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            /*options.inSampleSize = 8;*/

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            imMeterReding.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
            fcall.logStatus("Image Size: " + sizeOf(bitmap));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            imMeterReding.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
            fcall.logStatus("OME Image Size: " + sizeOf(bitmap));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    protected int sizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
    }

    public static Bitmap rotateImage(Bitmap src, String Imagepath) {
        Bitmap bmp = null;
        // create new matrix
        Matrix matrix = new Matrix();
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(Imagepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Log.d("debug", "" + orientation);
        if (orientation == 1) {
            bmp = src;
            /*matrix.postRotate(270);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);*/
        } else if (orientation == 3) {
            matrix.postRotate(180);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else if (orientation == 8) {
            matrix.postRotate(270);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else {
            matrix.postRotate(90);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        }
        return bmp;
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    /*@RequiresApi(api = Build.VERSION_CODES.N)*/
    public Uri getOutputMediaFileUri(int type, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", getOutputMediaFile(type));
        } else return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(android.os.Environment.getExternalStorageDirectory(),
                functionCalls.Appfoldername() + File.separator + IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("MMddyyyy", Locale.getDefault()).format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            functionCalls.checkimage_and_delete(IMAGE_DIRECTORY_NAME, Consid);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + Consid + "_" + cons_MRcode + "_" + timeStamp + ".jpg");
            pathname = mediaStorageDir.getPath() + File.separator + Consid + "_" + cons_MRcode + "_" + timeStamp + ".jpg";
            pathextension = Consid + "_" + cons_MRcode + "_" + timeStamp + ".jpg";
        } else {
            return null;
        }

        return mediaFile;
    }

    @SuppressLint("InflateParams")
    public void actionbaricon() {

        // Initialize your custom layout
        View v = getLayoutInflater().inflate(R.layout.actionbar, null);
        ImageView iv = (ImageView) v.findViewById(R.id.imageView1);
        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ConsumerBilling.this, v);

                popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1:
                                tvheader.setVisibility(View.VISIBLE);
                                actvsearch.setVisibility(View.VISIBLE);
                                tvheader.setText("Search By Account ID");
                                actvsearch.setInputType(InputType.TYPE_CLASS_NUMBER);
                                actvsearch.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                                actvsearch.requestFocus();
                                textviewstonull();
                                c1 = dbh.searchbyid();
                                aa1 = new ArrayAdapter<String>(ConsumerBilling.this, android.R.layout.simple_dropdown_item_1line, a1);
                                actvsearch.setAdapter(aa1);
                                a1.clear();
                                aa1.notifyDataSetChanged();
                                while (c1.moveToNext()) {
                                    name1 = c1.getString(c1.getColumnIndex("CONSNO"));
                                    a1.add(name1);
                                }
                                Collections.sort(a1);
                                aa1.notifyDataSetChanged();
                                actvsearch.setThreshold(1);
                                actvsearch.setOnItemClickListener(new OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                                            int arg2, long arg3) {
                                        cons_checkid = (String) arg0.getItemAtPosition(arg2);
                                        c4 = dbh.getID(cons_checkid);
                                        hidekeyboard();
                                        while (c4.moveToNext()) {
                                            settextviewsvalue(c4);
                                        }
                                    }
                                });
                                return true;

                            case R.id.item2:
                                tvheader.setVisibility(View.VISIBLE);
                                actvsearch.setVisibility(View.VISIBLE);
                                tvheader.setText("Search By RRNo");
                                actvsearch.setInputType(InputType.TYPE_CLASS_TEXT);
                                actvsearch.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                                actvsearch.requestFocus();
                                textviewstonull();
                                c2 = dbh.searchbyrrno();
                                aa2 = new ArrayAdapter<String>(ConsumerBilling.this,
                                        android.R.layout.simple_dropdown_item_1line, a2);
                                actvsearch.setAdapter(aa2);
                                a2.clear();
                                aa2.notifyDataSetChanged();
                                while (c2.moveToNext()) {
                                    name2 = c2.getString(c2.getColumnIndex("RRNO"));
                                    a2.add(name2);
                                }
                                c2.close();
                                Collections.sort(a2, new Comparator<String>() {

                                    @Override
                                    public int compare(String s1, String s2) {
                                        return s1.compareToIgnoreCase(s2);
                                    }
                                });
                                aa2.notifyDataSetChanged();
                                actvsearch.setThreshold(1);
                                actvsearch.setOnItemClickListener(new OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                                            int arg2, long arg3) {
                                        String name4 = (String) arg0.getItemAtPosition(arg2);
                                        c4 = dbh.getRRNo(name4);
                                        hidekeyboard();
                                        while (c4.moveToNext()) {
                                            settextviewsvalue(c4);
                                        }
                                    }
                                });
                                return true;

                            case R.id.item3:
                                tvheader.setVisibility(View.VISIBLE);
                                actvsearch.setVisibility(View.VISIBLE);
                                tvheader.setText("Search By Customer Name");
                                actvsearch.setInputType(InputType.TYPE_CLASS_TEXT);
                                actvsearch.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                                actvsearch.requestFocus();
                                textviewstonull();
                                c3 = dbh.searchbyname();
                                aa3 = new ArrayAdapter<String>(ConsumerBilling.this,
                                        android.R.layout.simple_dropdown_item_1line, a3);
                                actvsearch.setAdapter(aa3);
                                a3.clear();
                                while (c3.moveToNext()) {
                                    name3 = c3.getString(c3.getColumnIndex("NAME"));
                                    a3.add(name3);
                                }
                                c3.close();
                                Collections.sort(a3, new Comparator<String>() {

                                    @Override
                                    public int compare(String s1, String s2) {
                                        aa3.notifyDataSetChanged();
                                        return s1.compareToIgnoreCase(s2);
                                    }
                                });
                                aa3.notifyDataSetChanged();
                                actvsearch.setThreshold(1);
                                actvsearch.setOnItemClickListener(new OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                                            int arg2, long arg3) {
                                        String name4 = (String) arg0.getItemAtPosition(arg2);
                                        c4 = dbh.getName(name4);
                                        hidekeyboard();
                                        while (c4.moveToNext()) {
                                            settextviewsvalue(c4);
                                        }
                                    }
                                });
                                return true;

                            case R.id.item4:
                                tvheader.setVisibility(View.GONE);
                                actvsearch.setVisibility(View.GONE);
                                finish();
                                return true;

                            case R.id.item5:
                                UnBilled = true;
                                DLBilled = false;
                                c18 = dbh.notbilled();
                                if (c18.getCount() > 0) {
                                    c18.moveToNext();
                                    settextviewsvalue(c18);
                                } else {
                                    UnBilled = false;
                                }
                                return true;

                            case R.id.item6:
                                UnBilled = false;
                                DLBilled = true;
                                c19 = dbh.dlbilled();
                                if (c19.getCount() > 0) {
                                    c19.moveToNext();
                                    settextviewsvalue(c19);
                                } else {
                                    DLBilled = false;
                                }
                                return true;

                            case R.id.item7:
                                UnBilled = false;
                                DLBilled = false;
                                c20 = dbh.totalbillingrecords();
                                c20.moveToNext();
                                settextviewsvalue(c20);
                                return true;
                            /********SUBDIV MAPS COMMENTED**************/
                            /****************************************/
                            /****************************************/
                          /*  case R.id.poly_menu_dir:
                                if (!cons_user_longitude.equals("0")) {
                                    Intent directions = new Intent(ConsumerBilling.this, SubdivMaps.class);
                                    directions.putExtra("mapstoshow", "directions");
                                    directions.putExtra("account_id", cons_Num);
                                    directions.putExtra("rrno", cons_RRNO);
                                    directions.putExtra(DESTINATION_LATITUDE, cons_user_latitude);
                                    directions.putExtra(DESTINATION_LONGITUDE, cons_user_longitude);
                                    startActivity(directions);
                                } else
                                    fcall.showtoast(ConsumerBilling.this, "User location not available to show...");
                                break;*/

                            case R.id.menu_mtr_asset:
                                Cursor data = dbh.meter_asset_details(cons_Num);
                                if (!(data.getCount() > 0)) {
                                    showdialog(DLG_MTR_ASSET);
                                } else
                                    fcall.showtoast(ConsumerBilling.this, "Meter asset for current account ID is already registered...");
                                break;
                        }
                        return false;
                    }
                });
                getMenuInflater().inflate(R.menu.billingactionbar, popup.getMenu());
                popup.show();
            }
        });

        // Add the custom View to your ActionBar
        final ActionBar actionBar = getActionBar();
        actionBar.setCustomView(v, new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.RIGHT));
        actionBar.setDisplayShowCustomEnabled(true);
        // Remove the "up" affordance
        actionBar.setDisplayShowHomeEnabled(true);
    }

    public void textviewstonull() {
        tvRRNO.setText("");
        tvaccount_id.setText("");
        tvName.setText("");
        tvAdd.setText("");
        tvTarrif.setText("");
        tvPrvRead.setText("");
        tvStatus.setText("");
        tvMF.setText("");
        tvPrvStatus.setText("");
    }

    public void settextviewsvalue(Cursor c) {
        cons_primarykey = Integer.parseInt(c.getString(c.getColumnIndex("_id")));
        dbh.updatebill(cons_primarykey);
        setData();
        checkbilled(cons_Num);
    }

    // Function to set data to
    // spinner===================================================================

    public void spinnerSetData() {
        c = dbh.spinnerData();
        if (c != null) {
            while (c.moveToNext()) {
                pay = new Pay();
                String billStatus1 = c.getString(c.getColumnIndex("STATUS_NAME"));
                pay.setstatus(billStatus1);
                billStatus.add(pay);
            }
        }
        Status.setAdapter(aa);
        Status.setSelection(2);
        c.close();
    }

    // Function to set data to text
    // Views================================================================
    public void setData() {
        valuesclear();
        try {
            Cursor data = dbh.getBilledRecordData();
            if (data.getCount() > 0) {
                data.moveToNext();
                String pres_date = data.getString(data.getColumnIndex("READDATE"));
                String prev_date = data.getString(data.getColumnIndex("PREV_READ_DATE"));
                double days_diff = Double.parseDouble(fcall.CalculateDays(prev_date, pres_date));
                if (days_diff > 31 || days_diff < 28) {
                    double dl_diff = (days_diff / 30);
                    cons_dl_count = "" + dl_diff;
                    dl_diff = dl_diff - 1;
                    Cursor updatedl = dbh.updateDLrecord("" + dl_diff);
                    updatedl.moveToNext();
                }
                /*if (fcall.monthdiff(readMonth) > 0) {
                    String date = settings.getString(DOWNLOAD_FILE_NAME, "");
                    String pres_date="";
                    if (!TextUtils.isEmpty(date)) {
                        date = date.substring(2, 4);
                        date = date+fcall.dateSet().substring(2);
                        pres_date = fcall.changedateformat(date, "-");
                    } else {
                        pres_date = fcall.changedateformat(fcall.dateSet(), "-");
                    }
                    double days_diff = Double.parseDouble(fcall.CalculateDays(prev_date, pres_date));
                    double dl_diff = days_diff / 30;
                    cons_dl_count = ""+dl_diff;
                    dl_diff = dl_diff - 1;
                    Cursor updatedl = dbh.updateDLrecord(""+dl_diff);
                    updatedl.moveToNext();
                    Cursor updatedate = dbh.updateReadDate(pres_date);
                    updatedate.moveToNext();
                } else {
                    String prev_date = data.getString(data.getColumnIndex("PREV_READ_DATE"));
                    double daysdiff = Double.parseDouble(fcall.CalculateDays(prev_date, readMonth));
                    if (daysdiff < 28) {
                        double dl_diff = (daysdiff / 30);
                        cons_dl_count = ""+dl_diff;
                        dl_diff = dl_diff - 1;
                        Cursor updatedl = dbh.updateDLrecord(""+dl_diff);
                        updatedl.moveToNext();
                    }
                }*/
            }
            c = dbh.getBilledRecordNumber();
            c.moveToNext();
            strBilledNumber = c.getString(c.getColumnIndex("Billed_Record"));
            tvcount.setText(strBilledNumber);
            a = Integer.parseInt(strBilledNumber);
            if (a == 0) {
                dbh.updatebill(1);
            }
            if (a <= i) {
                if (UnBilled) {
                    c = dbh.getUnBilledRecordData();
                } else {
                    if (DLBilled) {
                        c = dbh.getDLBilledRecordData();
                    } else {
                        c = dbh.getBilledRecordData();
                    }
                }
                c = dbh.getBilledRecordData();
                if (c.getCount() > 0) {
                    c.moveToNext();
                    cons_RRNO = c.getString(c.getColumnIndexOrThrow("RRNO"));
                    cons_Name = c.getString(c.getColumnIndex("NAME"));
                    cons_Addr = c.getString(c.getColumnIndex("ADD1"));
                    cons_Tarrif = c.getString(c.getColumnIndex("TARIFF"));
                    intTariff = Integer.parseInt(cons_Tarrif);
                    tariff_name = c.getString(c.getColumnIndex("TARIFF_NAME"));
                    cons_MF = c.getString(c.getColumnIndex("MF"));
                    // Integer MF
                    intMF = Integer.parseInt(cons_MF);
                    cons_PrevMonthStatus = c.getString(c.getColumnIndex("PREVSTAT"));
                    // Integer Previous Month Status
                    intPrvMonthStatus = Integer.parseInt(cons_PrevMonthStatus.replaceAll("\\s", ""));
                    cons_Avg_cons = c.getString(c.getColumnIndex("AVGCON"));
                    cons_Linemin = c.getString(c.getColumnIndex("LINEMIN"));
                    cons_SantionHp = c.getString(c.getColumnIndex("SANCHP"));
                    cons_SanctionKw = c.getString(c.getColumnIndex("SANCKW"));
                    cons_PrvRead = c.getString(c.getColumnIndex("PRVRED"));
                    // Integer previous reading
                    intPrvRead = Integer.parseInt(cons_PrvRead);
                    cons_FR = c.getString(c.getColumnIndex("FR"));
                    FR = Integer.parseInt(cons_FR);
                    cons_IR = c.getString(c.getColumnIndex("IR"));
                    IR = Integer.parseInt(cons_IR);
                    cons_DLCount1 = c.getString(c.getColumnIndex("DLCOUNT"));
                    cons_DLCount = Double.parseDouble(cons_DLCount1);
                    cons_Arrears = c.getString(c.getColumnIndex("ARREARS"));
                    // Integer Arrears
                    int_Arrears = Double.parseDouble(cons_Arrears);
                    cons_PfFlag = c.getString(c.getColumnIndex("PF_FLAG"));
                    // pfFlag converted into integer
                    intpfFlag = (int) Math.round(Double.parseDouble(cons_PfFlag));
                    cons_BillFor = c.getString(c.getColumnIndex("BILLFOR"));
                    cons_MRcode = c.getString(c.getColumnIndex("MRCODE"));
                    cons_LegFol = c.getString(c.getColumnIndex("LEGFOL"));
                    cons_OddEven = c.getString(c.getColumnIndex("ODDEVEN"));
                    cons_SSNO1 = c.getString(c.getColumnIndex("SSNO"));
                    cons_Num = c.getString(c.getColumnIndex("CONSNO"));
                    cons_RebateFlag = c.getString(c.getColumnIndex("REBATE_FLAG"));
                    cons_RRebate = c.getString(c.getColumnIndex("RREBATE"));
                    try {
                        cons_Extra1 = c.getString(c.getColumnIndex("EXTRA1"));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        cons_Extra1 = "";
                    }
                    cons_Data1 = c.getString(c.getColumnIndex("DATA1"));
                    data1 = Double.parseDouble(cons_Data1);
                    cons_Extra2 = c.getString(c.getColumnIndex("EXTRA2"));
                    cons_Data2 = c.getString(c.getColumnIndex("DATA2"));
                    data2 = Double.parseDouble(cons_Data2);
                    if (cons_fec != 0.0) {
                        cons_Extra2 = "FAC";
                    }
                    cons_Deposit = c.getString(c.getColumnIndex("DEPOSIT"));
                    // Converting to integer deposit value
                    int_consDeposit = Double.parseDouble(cons_Deposit);
                    cons_MtrDigit = c.getString(c.getColumnIndex("MTRDIGIT"));
                    cons_PFValue = c.getString(c.getColumnIndex("PFVAL"));
                    cons_bmdval = c.getString(c.getColumnIndex("BMDVAL"));
                    cons_ASDamnt = c.getString(c.getColumnIndex("ASDAMT"));
                    cons_IODAmnt = c.getString(c.getColumnIndex("IODAMT"));
                    // cons_BillNo = data.getString(data.getColumnIndex("BILL_NO"));
                    cons_InterestAmt = c.getString(c.getColumnIndex("INTEREST_AMT"));
                    Int_Amt = Double.parseDouble(cons_InterestAmt);
                    cons_CapFlag = c.getString(c.getColumnIndex("CAP_FLAG"));
                    // capFlag Value converted into integer
                    intCapFlag = Integer.parseInt(cons_CapFlag);
                    cons_TodFlag = c.getString(c.getColumnIndex("TOD_FLAG"));
                    // TodFlag to Integer
                    intTodFlag = Integer.parseInt(cons_TodFlag);
                    cons_TodPrev1 = c.getString(c.getColumnIndex("TOD_PREVIOUS1"));
                    // TOD prev1 to integer
                    intTodPrev1 = Integer.parseInt(cons_TodPrev1);
                    cons_TodPrev3 = c.getString(c.getColumnIndex("TOD_PREVIOUS3"));
                    // TOD prev3 to integer
                    intTodPrev3 = Integer.parseInt(cons_TodPrev3);
                    cons_InterOnDep = c.getString(c.getColumnIndex("INT_ON_DEP"));
                    cons_So_FeedTLPole = c.getString(c.getColumnIndex("SO_FEEDER_TC_POLE"));
                    cons_Tariff_name = c.getString(c.getColumnIndex("TARIFF_NAME"));
                    cons_PrevReadDate = c.getString(c.getColumnIndex("PREV_READ_DATE"));
                    cons_CurrRead_date = c.getString(c.getColumnIndex("READDATE"));
                    cons_BillDays = fcall.CalculateDays(cons_PrevReadDate, cons_CurrRead_date);
                    cons_MtrSrlNO = c.getString(c.getColumnIndex("MTR_SERIAL_NO"));
                    cons_bmdkw = c.getString(c.getColumnIndex("BMDKW"));
                    cons_creadj = c.getString(c.getColumnIndex("CREADJ"));
                    // cons_CHQDisHnrFlag =
                    // data.getString(data.getColumnIndex("CHQ_DISHONOUR_FLAG"));
                    // cons_CHQDisHnrDate =
                    // data.getString(data.getColumnIndex("CHQ_DISHONOUR_DATE"));
                    cons_FDRName = c.getString(c.getColumnIndex("FDRNAME"));
                    cons_TCCode1 = c.getString(c.getColumnIndex("TCCODE"));
                    cons_MTRFlag = c.getString(c.getColumnIndex("MTR_FLAG"));
                    cons_readkvah = c.getString(c.getColumnIndex("READKVAH"));
                    if (!TextUtils.isEmpty(c.getString(c.getColumnIndexOrThrow("PH_NO")))) {
                        cons_PhNumber = c.getString(c.getColumnIndexOrThrow("PH_NO"));
                    } else cons_PhNumber = "N";
                    if (!TextUtils.isEmpty(c.getString(c.getColumnIndexOrThrow("AADHAAR")))) {
                        cons_aadhaar_no = c.getString(c.getColumnIndexOrThrow("AADHAAR"));
                    } else cons_aadhaar_no = "0";
                    if (!TextUtils.isEmpty(c.getString(c.getColumnIndexOrThrow("MAIL")))) {
                        cons_mail_id = c.getString(c.getColumnIndexOrThrow("MAIL"));
                    } else cons_mail_id = "0";
                    if (!TextUtils.isEmpty(c.getString(c.getColumnIndexOrThrow("GPS_LAT")))) {
                        cons_user_latitude = c.getString(c.getColumnIndexOrThrow("GPS_LAT"));
                    } else cons_user_latitude = "0";
                    if (!TextUtils.isEmpty(c.getString(c.getColumnIndexOrThrow("GPS_LONG")))) {
                        cons_user_longitude = c.getString(c.getColumnIndexOrThrow("GPS_LONG"));
                    } else cons_user_longitude = "0";
                    if (!TextUtils.isEmpty(c.getString(c.getColumnIndexOrThrow("ELECTION")))) {
                        cons_election_no = c.getString(c.getColumnIndexOrThrow("ELECTION"));
                    } else cons_election_no = "0";
                    if (!TextUtils.isEmpty(c.getString(c.getColumnIndexOrThrow("RATION")))) {
                        cons_ration_no = c.getString(c.getColumnIndexOrThrow("RATION"));
                    } else cons_ration_no = "0";
                    if (!TextUtils.isEmpty(c.getString(c.getColumnIndexOrThrow("WATER")))) {
                        cons_water_no = c.getString(c.getColumnIndexOrThrow("WATER"));
                    } else cons_water_no = "0";
                    if (!TextUtils.isEmpty(c.getString(c.getColumnIndexOrThrow("HOUSE_NO")))) {
                        cons_house_no = c.getString(c.getColumnIndexOrThrow("HOUSE_NO"));
                    } else cons_house_no = "0";
                    try {
                        cons_inven_load = c.getString(c.getColumnIndex("INVENTORY_LOAD"));
                        if (cons_inven_load == null)
                            cons_inven_load = "0";
                    } catch (CursorIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        cons_inven_load = "0";
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        cons_inven_load = "0";
                    }
                    try {
                        Cursor c1 = dbh.getImageAddressString(cons_Num);
                        c1.moveToNext();
                        String path = c1.getString(c1.getColumnIndex("IMGADD"));
                        Log.d("debug", "Image: " + path);
                        if (path.equals("noimage")) {
                            cons_IMGdisplay = "";
                            imMeterReding.setVisibility(View.GONE);
                            if (imgbilled.getVisibility() == View.VISIBLE)
                                imgbilled.setVisibility(View.GONE);
                        } else if (path.equals("")) {
                            cons_IMGdisplay = "";
                            imMeterReding.setImageResource(R.drawable.ffff);
                            if (imgbilled.getVisibility() == View.VISIBLE)
                                imgbilled.setVisibility(View.GONE);
                        } else {
                            imMeterReding.setVisibility(View.GONE);
                            cons_IMGdisplay = fcall.filepath("Compressed") + File.separator + path;
                            File imagefile = new File(cons_IMGdisplay);
                            if (imagefile.exists()) {
                                Uri uri = Uri.parse(cons_IMGdisplay);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 8;
                                final Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
                                imgbilled.setVisibility(View.VISIBLE);
                                imgbilled.setImageBitmap(bitmap);
                                billedimage = true;
                            } else {
                                imgbilled.setVisibility(View.VISIBLE);
                                imgbilled.setImageResource(R.drawable.ffff);
                                billedimage = true;
                            }
                        }

                    } catch (CursorIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        cons_IMGdisplay = "";
                        if (imMeterReding.getVisibility() == View.VISIBLE) {
                            imMeterReding.setImageResource(R.drawable.ffff);
                        } else {
                            imMeterReding.setImageResource(R.drawable.ffff);
                        }
                        if (imgbilled.getVisibility() == View.VISIBLE)
                            imgbilled.setVisibility(View.GONE);
                    }
                    String prevReadDate = fcall.changedateformat(cons_PrevReadDate, "/").substring(3, 5);
                    int_monthPrevReadDate = Integer.parseInt(prevReadDate);
                    tvName.setText(cons_Name);
                    tvAdd.setText(cons_Addr);
                    tvTarrif.setText(tariff_name);
                    tvPrvRead.setText(cons_PrvRead);
                    tvRRNO.setText(cons_RRNO);
                    tvaccount_id.setText(cons_Num);
                    tvMF.setText(cons_MF);
                    if (cons_TCCode1.equals("0")) {
                        etTCCODE.setVisibility(View.GONE);
                        tv_tccode.setVisibility(View.GONE);
                        /*etTCCODE.setVisibility(View.VISIBLE);
                        etTCCODE.requestFocus();*/
                        cons_TCCode = cons_TCCode1;
                    } else {
                        cons_TCCode = cons_TCCode1;
                        etTCCODE.setVisibility(View.GONE);
                        tv_tccode.setVisibility(View.GONE);
                    }
                    if (cons_Tarrif.equals("70")) {
                        etNoofWeeks.setVisibility(View.VISIBLE);
                        tv_no_of_weeks.setVisibility(View.VISIBLE);
                        etPDReading.setVisibility(View.VISIBLE);
                        tv_pd_recorded.setVisibility(View.VISIBLE);
                        etNoofWeeks.requestFocus();
                    } else {
                        etNoofWeeks.setVisibility(View.GONE);
                        tv_no_of_weeks.setVisibility(View.GONE);
                        etPDReading.setVisibility(View.GONE);
                        tv_pd_recorded.setVisibility(View.GONE);
                    }
                    if (intpfFlag == 2) {
                        etPF.setVisibility(View.VISIBLE);
                        tv_pf.setVisibility(View.VISIBLE);
                        // btPFEnter.setVisibility(View.VISIBLE);
                        etPF.setHint("Enter PF");
                        // btBMD.setVisibility(View.VISIBLE);
                        etBMD.setVisibility(View.VISIBLE);
                        tv_bmd.setVisibility(View.VISIBLE);
                        etBMD.setHint("Enter BMD Value");
                        et_kvah.setVisibility(View.VISIBLE);
                        tv_kvah.setVisibility(View.VISIBLE);
                        etPF.setEnabled(true);
                        etBMD.setEnabled(true);
                        et_kvah.setEnabled(true);
                    } else {
                        if (intpfFlag == 1) {
                            etPF.setVisibility(View.VISIBLE);
                            tv_pf.setVisibility(View.VISIBLE);
                            etBMD.setVisibility(View.VISIBLE);
                            tv_bmd.setVisibility(View.VISIBLE);
                            et_kvah.setVisibility(View.VISIBLE);
                            tv_kvah.setVisibility(View.VISIBLE);
                            etPF.setText(cons_PFValue);
                            etBMD.setText(cons_bmdval);
                            et_kvah.setText(cons_readkvah);
                            etPF.setEnabled(false);
                            etBMD.setEnabled(false);
                            et_kvah.setEnabled(false);
                        } else {
                            etPF.setVisibility(View.GONE);
                            tv_pf.setVisibility(View.GONE);
                            intPFEnteredValue = 0;
                            etBMD.setVisibility(View.GONE);
                            tv_bmd.setVisibility(View.GONE);
                            et_kvah.setVisibility(View.GONE);
                            tv_kvah.setVisibility(View.GONE);
                            cons_pfval = "0.00";
                            cons_BMDvalue = "0";
                            cons_readkvah = "0";
                        }
                    }

                    // Button TOD
                    // Enter==============================================
                    if (intTodFlag == 1) {
                        etTODONPEAK.setVisibility(View.VISIBLE);
                        etTODONPEAK.setHint("Enter TOD ON PEAK");
                        tv_tod_on_peak.setVisibility(View.VISIBLE);
                        etTODOFPEAK.setVisibility(View.VISIBLE);
                        etTODOFPEAK.setHint("Enter TOD OFF PEAK");
                        tv_tod_off_peak.setVisibility(View.VISIBLE);
                    } else {
                        cons_todOnPeak = "0";
                        cons_todOfPeak = "0";
                        etTODONPEAK.setVisibility(View.GONE);
                        tv_tod_on_peak.setVisibility(View.GONE);
                        etTODOFPEAK.setVisibility(View.GONE);
                        tv_tod_off_peak.setVisibility(View.GONE);
                    }
			/*		if (cons_PhNumber1.equals("0")) {
						etPhNumber.setVisibility(View.VISIBLE);
						etPhNumber.requestFocus();
					}
					else {
						etPhNumber.setVisibility(View.GONE);
					}*/
                }
            } else {
                --a;
                dbh.updatebill(a);
            }

            c5 = dbh.prvstatus(cons_Num);
            c5.moveToNext();
            prvstatus = c5.getString(c5.getColumnIndex("STATUS_LABEL"));
            tvPrvStatus.setText(prvstatus);

            c8 = dbh.addingSSNO();
            c8.moveToNext();
            cons_SSNO = c8.getString(c8.getColumnIndex("SSNO"));
        } catch (Exception e) {
            e.printStackTrace();
            if (onpreviousclick) {
                checkrowerror();
                onprevious();
                /*c15.close();*/
            } else {
                checkrowerror();
                onnext();
                /*c15.close();*/
            }
        }
    }

    private void checkrowerror() {
        c15 = dbh.getrowemptytable();
        if (c15.getCount() > 0) {
            while (c15.moveToNext()) {
                String Custid = c15.getString(c15.getColumnIndex("CONSNO"));
                if (Custid.equals(cons_Num)) {
                    valuefound = true;
                }
            }
            if (!valuefound) {
                ContentValues cv1 = new ContentValues();
                cv1 = insertintorowempty();
                dbh.insertInRowEmptyTable(cv1);
                valuefound = false;
            }
        } else {
            ContentValues cv1 = new ContentValues();
            cv1 = insertintorowempty();
            dbh.insertInRowEmptyTable(cv1);
        }
    }

    // Function to Count Number of
    // Rows===================================================================
    public void countNumberofRows() {
        if (c != null) {
            while (c.moveToNext()) {
                i++;
            }
        }
    }

    // Function To Initialize the
    // Views===================================================================
    public void intializeViews() {
        BottomNavigationView buttomnavigationview = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        Menu menu = buttomnavigationview.getMenu();

        buttomnavigationview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.item1:
                        if (fcall.compare_end_billing_time(fcall.convertTo24Hour(BILLING_CUT_OFF_TIME))) {
                            onpreviousclick = true;
                            c.close();
                            onprevious();
                            checkbilled(cons_Num);
                            onpreviousclick = false;
                        } else showdialog(DLG_TIME_OVER);
                        break;
                    case R.id.item2:
                        if (fcall.compare_end_billing_time(fcall.convertTo24Hour(BILLING_CUT_OFF_TIME))) {
                            c.close();
                            onnext();
                            checkbilled(cons_Num);
                        } else showdialog(DLG_TIME_OVER);
                        break;
                    case R.id.item3:
                        if (fcall.compare_end_billing_time(fcall.convertTo24Hour(BILLING_CUT_OFF_TIME))) {
                            sub_ab_normal_readings();
                        } else showdialog(DLG_TIME_OVER);
                        break;
                }
                return false;
            }
        });

        tvRRNO = (TextView) findViewById(R.id.textView17);
        tvAdd = (TextView) findViewById(R.id.textView6);
        tvName = (TextView) findViewById(R.id.textView5);
        tvTarrif = (TextView) findViewById(R.id.textView7);
        tvPrvStatus = (TextView) findViewById(R.id.textView9);
        tvDate = (TextView) findViewById(R.id.textView15);
        tvPrvRead = (TextView) findViewById(R.id.textView12);
        tvStatus = (TextView) findViewById(R.id.textView24);
        tvtextRRNo = (TextView) findViewById(R.id.textView16);
        tvsemiRRNo = (TextView) findViewById(R.id.textView13);
        tvtextDate = (TextView) findViewById(R.id.textView14);
        tvtextStatus = (TextView) findViewById(R.id.textView10);
        tvMF = (TextView) findViewById(R.id.textView28);
        tvbilledstatustext = (TextView) findViewById(R.id.textView29);
        tvbilledstatussemi = (TextView) findViewById(R.id.textView30);
        tvbilledstatus = (TextView) findViewById(R.id.textView31);
        tvbilledreadingtext = (TextView) findViewById(R.id.textView32);
        tvbilledreadingsemi = (TextView) findViewById(R.id.textView33);
        tvbilledreading = (TextView) findViewById(R.id.textView34);
        tvbilledunitstext = (TextView) findViewById(R.id.textView35);
        tvbilledunitssemi = (TextView) findViewById(R.id.textView36);
        tvbilledunits = (TextView) findViewById(R.id.textView37);
        tvbilledpayabletext = (TextView) findViewById(R.id.textView38);
        tvbilledpayablesemi = (TextView) findViewById(R.id.textView39);
        tvbilledpayable = (TextView) findViewById(R.id.textView40);
        tvcount = (TextView) findViewById(R.id.textView41);
        tvtext_accountid = (TextView) findViewById(R.id.txt_billing_account_id);
        tvsemi_accountid = (TextView) findViewById(R.id.semi_billing_account_id);
        tvaccount_id = (TextView) findViewById(R.id.billing_account_id);
        tv_no_of_weeks = (TextView) findViewById(R.id.txt_et_no_of_weeks);
        tv_pd_recorded = (TextView) findViewById(R.id.txt_et_pd_recorded);
        tv_tccode = (TextView) findViewById(R.id.txt_et_tc_code);
        tv_curr_reading = (TextView) findViewById(R.id.txt_et_cur_reading);
        tv_mtr_digit = (TextView) findViewById(R.id.txt_et_mtr_digit);
        tv_pf = (TextView) findViewById(R.id.txt_et_pf_value);
        tv_tod_on_peak = (TextView) findViewById(R.id.txt_et_tod_on_peak);
        tv_tod_off_peak = (TextView) findViewById(R.id.txt_et_tod_off_peak);

        tv_bmd = (TextView) findViewById(R.id.txt_et_bmd);

        tv_kvah = (TextView) findViewById(R.id.txt_et_kvah);

        etCurReading = (EditText) findViewById(R.id.editText1);
        etTODONPEAK = (EditText) findViewById(R.id.editText3);
        etPF = (EditText) findViewById(R.id.editText2);

        etBMD = (EditText) findViewById(R.id.editText4);
        etTODOFPEAK = (EditText) findViewById(R.id.editText5);

        etTCCODE = (EditText) findViewById(R.id.editText6);

        etNoofWeeks = (EditText) findViewById(R.id.editText7);
        etPDReading = (EditText) findViewById(R.id.editText8);
        etPhNumber = (EditText) findViewById(R.id.editText9);

        et_kvah = (EditText) findViewById(R.id.et_kvah);
        et_mtr_digit = (EditText) findViewById(R.id.et_mtr_digit);

        takepic_btn = (Button) findViewById(R.id.button9);

        imMeterReding = (ImageView) findViewById(R.id.billingview);
        imgbilled = (ImageView) findViewById(R.id.billedview);
        fullbilledimage = (ImageView) findViewById(R.id.full_billed_image);

        billinglayout = (RelativeLayout) findViewById(R.id.full_billing_view);
        feder_dtc_layout = (LinearLayout) findViewById(R.id.feder_dtc_layout);

        Status = (Spinner) findViewById(R.id.spinner1);
        tvheader = (TextView) findViewById(R.id.textView2);
        actvsearch = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        billStatus = new ArrayList<Pay>();
        a1 = new ArrayList<String>();
        a2 = new ArrayList<String>();
        a3 = new ArrayList<String>();
        aa = new Myspinneradapter(this, billStatus);

        sp_feder = (Spinner) findViewById(R.id.sp_feder_name);
        feder_list = new ArrayList<>();
        feder_adapter = new RoleAdapter(feder_list, ConsumerBilling.this);
        sp_feder.setAdapter(feder_adapter);
        sp_dtc = (Spinner) findViewById(R.id.sp_dtc_name);
        dtc_list = new ArrayList<>();
        dtc_adapter = new RoleAdapter(dtc_list, ConsumerBilling.this);
        sp_dtc.setAdapter(dtc_adapter);

        cb = new ClassCalculation(con);
        getset = new GetSetValues();
        gps = new ClassGPS(ConsumerBilling.this);
        fcall = new FunctionCalls();
        functionCalls = new FunctionCalls();
        dbh = new Databasehelper(this);
        dbh.openDatabase();

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        cons_IMEI = tm.getDeviceId();

        try {
            if (dbh.doesTableExist()) {
                Cursor feder = dbh.feder_details();
                if (feder.getCount() > 0) {
                    while (feder.moveToNext()) {
                        GetSetValues getset = new GetSetValues();
                        getset.setLogin_role(feder.getString(feder.getColumnIndexOrThrow("FEDER_NAME")));
                        feder_list.add(getset);
                        feder_adapter.notifyDataSetChanged();
                    }
                    feder_dtc_spinner();
                } else feder_dtc_layout.setVisibility(View.GONE);
            } else feder_dtc_layout.setVisibility(View.GONE);
        } catch (SQLiteException e) {
            e.printStackTrace();
            feder_dtc_layout.setVisibility(View.GONE);
        }
    }

    private void checkbilled(String rrno) {
        c7 = dbh.getBilledRRNo(rrno);
        dbh.updatebill(a);
        if (c7.getCount() > 0) {
            c7.moveToNext();
            cons_testRRno = c7.getString(c7.getColumnIndex("CONSNO"));
            cons_billeddate = c7.getString(c7.getColumnIndex("READDATE"));
            cons_testStatus2 = c7.getString(c7.getColumnIndex("PRES_STS"));
            cons_billedpayable = c7.getString(c7.getColumnIndex("PAYABLE"));
            cons_billedunits = c7.getString(c7.getColumnIndex("UNITS"));
            cons_billedreading = c7.getString(c7.getColumnIndex("PRES_RDG"));
            c10 = dbh.statuscode(cons_testStatus2);
            c10.moveToNext();
            cons_billedstatus = c10.getString(c10.getColumnIndex("STATUS_NAME"));
            cons_billedstatus2 = c10.getString(c10.getColumnIndex("STATUS_LABEL"));
            if (cons_testStatus2.equals("1")) {
                if (!cons_testRRno.equals(cons_Num)) {
                    Unbilled();
                } else {
                    DLBilled();
                }
            } else {
                if (!cons_testRRno.equals(cons_Num)) {
                    Unbilled();
                } else {
                    Billed();
                }
            }
        } else {
            Unbilled();
        }
    }

    // Function to Set Current
    // Date=======================================================================
    public void dateSet() throws ParseException {
        final Calendar c = Calendar.getInstance();
        yy = c.get(Calendar.YEAR);
        mm = c.get(Calendar.MONTH);
        dd = c.get(Calendar.DAY_OF_MONTH);
        int mnth2 = mm + 1;
        String present_date1 = dd + "/" + mnth2 + "/" + "" + yy;
        Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(present_date1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        c.setTime(date);
        present_date = sdf.format(c.getTime());
        cons_billingdate = fcall.changedateformat(present_date, "-");
    }

    // Function to Set Value for Bean
    // Class================================================================
    public void setValueClassBean() {
        cb.settariff(cons_Tarrif);
        cb.setPrvRead(cons_PrvRead);
        cb.setCurReading(cons_CurReading);
        if (intTodFlag != 1) {
            if (Present_STS == 3 || Present_STS == 4 || Present_STS == 6 || Present_STS == 8 || Present_STS == 9 || Present_STS == 10
                    || Present_STS == 11 || Present_STS == 12 || Present_STS == 13 || Present_STS == 14 || Present_STS == 16
                    || Present_STS == 17 || Present_STS == 18 || Present_STS == 19 || Present_STS == 20 || Present_STS == 21
                    || Present_STS == 22 || Present_STS == 23 || Present_STS == 24 || Present_STS == 25 || Present_STS == 26
                    || Present_STS == 27 || Present_STS == 28 || Present_STS == 29 || Present_STS == 30) {
                int units = intMF * (intCurReading - intPrvRead);
                int diff = FR - IR;
                int consume = units + diff;
                cons_Units = "" + consume;
                getset.setUnits(cons_Units);
                cb.setconsumtion(cons_Units);
            } else {
                if (Present_STS == 5) {
                    cons_Units = dialover(cons_PrvRead, cons_CurReading);
                    getset.setUnits(cons_Units);
                    cb.setconsumtion(cons_Units);
                } else {
                    Cursor data = dbh.subdivdetails();
                    data.moveToNext();
                    String dl_flag = data.getString(data.getColumnIndexOrThrow("DL_FLAG"));
                    if (!TextUtils.isEmpty(dl_flag)) {
                        if (StringUtils.startsWithIgnoreCase(dl_flag, "Y") || StringUtils.startsWithIgnoreCase(dl_flag, "y")) {
                            cons_Units = "" + (intMF * Integer.parseInt(cons_Avg_cons));
                            getset.setUnits(cons_Units);
                            cb.setconsumtion(cons_Units);
                        } else {
                            getset.setUnits("0");
                            cb.setconsumtion("0");
                        }
                    } else {
                        cons_Units = "" + (intMF * Integer.parseInt(cons_Avg_cons));
                        getset.setUnits(cons_Units);
                        cb.setconsumtion(cons_Units);
                    }
                }
            }
        } else {
            int units = intCurReading - intPrvRead;
            int totalunits = intMF * (units + cons_todonconsume + cons_todoffconsume);
            int diff = FR - IR;
            int totalconsume = totalunits + diff;
            cons_Units = "" + totalconsume;
            getset.setUnits(cons_Units);
            cb.setconsumtion(cons_Units);
        }
        cb.setSanctionKw(cons_SanctionKw);
        cb.setSantionHp(cons_SantionHp);
        cb.setStatusLabel(cons_StatusLabel);
        cb.setFlagRebate(cons_RebateFlag);
        cb.setPfFlagValue(intpfFlag);
        cb.setPfValue(intPFEnteredValue);
        cb.setCapFlagValue(intCapFlag);
        cb.setTod_ofPeakValue(cons_todoffconsume);
        cb.setTod_onPeakValue(cons_todonconsume);
        cb.setInt_calArrears(int_Arrears);
        cb.setCal_deposit(int_consDeposit);
        cb.setdl_count(cons_DLCount);
        cb.setCalMF(intMF);
        cb.setRrebate(cons_RRebate);
        cb.setIntr_Amt(Int_Amt);
        cb.setdata1(data1);
        if (cons_Extra2.equals("FAC")) {
            data2 = cons_fec * Double.parseDouble(cons_Units);
            cons_Data2 = "" + data2;
        }
        cb.setdata2(data2);
        if (cons_Tarrif.equals("70")) {
            cb.setweeks(cons_weekSTS);
        }
    }

    // Dialog Function for TOD
    // Calculation================================================================
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog d = null;
        switch (id) {

            case 2:
                AlertDialog.Builder ab2 = new AlertDialog.Builder(this);
                ab2.setTitle("ENable GPS");
                ab2.setMessage("GPS is not enabled. Do you want to go to Settings Menu?");
                ab2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                ab2.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                d = ab2.create();
                break;
        }
        return d;
    }

    // ============================================================================================================//

    // //////////////////////////////////////////////CAMERA
    // SECTION///////////////////////////////////////////////

    // =============================================================================================================//

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHLD_REQ1) {
			if (resultCode == RESULT_OK) {
				Bundle bnd = data.getExtras();
				cons_ImgAdd = bnd.getString("IMGADD");
				cons_imageextension = bnd.getString("PATHEXT");
				Bitmap bitmap = null;
		        try {
					bitmap = fcall.getImage(cons_ImgAdd, getApplicationContext());
				} catch (IOException e) {
					e.printStackTrace();
				}
		        fcall.checkimage_and_delete("Compressed", cons_Num);
		        destination = fcall.filestorepath("Compressed", cons_imageextension);
				saveExternalPrivateStorage(destination, bitmap);
				String destinationfile = fcall.filepath("Compressed") + File.separator + cons_imageextension;
				Bitmap bitmap1 = null;
				try {
					bitmap1 = fcall.getImage(destinationfile, getApplicationContext());
				} catch (IOException e) {
					e.printStackTrace();
				}
				imMeterReding.setImageBitmap(bitmap1);
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
	}*/

    public void GPSLocation() {
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            cons_gpslong = "" + longitude;
            cons_gpslat = "" + latitude;
        }
    }

    private void Unbilled() {
        tvDate.setText(present_date);
        tvStatus.setText("Unbilled");
        tvStatus.setTextColor(Color.RED);
        tvtextRRNo.setTextColor(Color.BLACK);
        tvsemiRRNo.setTextColor(Color.BLACK);
        tvRRNO.setTextColor(Color.BLACK);
        tvtext_accountid.setTextColor(Color.BLACK);
        tvsemi_accountid.setTextColor(Color.BLACK);
        tvaccount_id.setTextColor(Color.BLACK);
        tvtextStatus.setVisibility(View.VISIBLE);
        Status.setVisibility(View.VISIBLE);
        etCurReading.setVisibility(View.VISIBLE);
        tv_curr_reading.setVisibility(View.VISIBLE);
        et_mtr_digit.setVisibility(View.VISIBLE);
        tv_mtr_digit.setVisibility(View.VISIBLE);
        imMeterReding.setVisibility(View.VISIBLE);
        textgone();
        if (!reportbilled) {
            hidekeyboard();
        }
    }

    private void Billed() {
        reportbilled = true;
        tvDate.setText(present_date);
        tvStatus.setText("Billed");
        tvStatus.setTextColor(Color.parseColor("#7DD62A"));
        tvtextRRNo.setTextColor(Color.parseColor("#7DD62A"));
        tvsemiRRNo.setTextColor(Color.parseColor("#7DD62A"));
        tvRRNO.setTextColor(Color.parseColor("#7DD62A"));
        tvtext_accountid.setTextColor(Color.parseColor("#7DD62A"));
        tvsemi_accountid.setTextColor(Color.parseColor("#7DD62A"));
        tvaccount_id.setTextColor(Color.parseColor("#7DD62A"));
        tvtextStatus.setVisibility(View.GONE);
        Status.setVisibility(View.GONE);
        etCurReading.setVisibility(View.GONE);
        tv_curr_reading.setVisibility(View.GONE);
        et_mtr_digit.setVisibility(View.GONE);
        /*imMeterReding.setVisibility(View.VISIBLE);*/
        if (etTODONPEAK.getVisibility() == View.VISIBLE) {
            etTODONPEAK.setVisibility(View.GONE);
            tv_tod_on_peak.setVisibility(View.GONE);
            etTODOFPEAK.setVisibility(View.GONE);
            tv_tod_off_peak.setVisibility(View.GONE);
        }
        if (etPF.getVisibility() == View.VISIBLE) {
            etPF.setVisibility(View.GONE);
            tv_pf.setVisibility(View.GONE);
        }
        if (etBMD.getVisibility() == View.VISIBLE) {
            etBMD.setVisibility(View.GONE);
            tv_bmd.setVisibility(View.GONE);
        }
        if (et_kvah.getVisibility() == View.VISIBLE) {
            et_kvah.setVisibility(View.GONE);
            tv_kvah.setVisibility(View.GONE);
        }
        textvisible(Color.BLACK);
        if (cons_Tarrif.equals("70")) {
            etNoofWeeks.setVisibility(View.GONE);
            tv_no_of_weeks.setVisibility(View.GONE);
            etPDReading.setVisibility(View.GONE);
            tv_pd_recorded.setVisibility(View.GONE);
        }
        etPhNumber.setVisibility(View.GONE);
    }

    private void DLBilled() {
        tvDate.setText(present_date);
        tvStatus.setText("DLBilled");
        tvStatus.setTextColor(Color.BLUE);
        tvtextRRNo.setTextColor(Color.BLUE);
        tvsemiRRNo.setTextColor(Color.BLUE);
        tvRRNO.setTextColor(Color.BLUE);
        tvtext_accountid.setTextColor(Color.BLUE);
        tvsemi_accountid.setTextColor(Color.BLUE);
        tvaccount_id.setTextColor(Color.BLUE);
        tvtextStatus.setVisibility(View.VISIBLE);
        Status.setVisibility(View.VISIBLE);
        etCurReading.setVisibility(View.VISIBLE);
        tv_curr_reading.setVisibility(View.VISIBLE);
        et_mtr_digit.setVisibility(View.VISIBLE);
        tv_mtr_digit.setVisibility(View.VISIBLE);
        imMeterReding.setVisibility(View.VISIBLE);
        textvisible(Color.BLUE);
        if (!reportbilled) {
            hidekeyboard();
        }
    }

    private void textvisible(int color) {
        if (etCurReading.getVisibility() == View.GONE) {
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.BELOW, R.id.textView14);
            p.setMargins(0, 100, 0, 0);
            tvbilledstatustext.setLayoutParams(p);
        } else {
            if (etCurReading.getVisibility() == View.VISIBLE) {
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.BELOW, R.id.billingview);
                p.setMargins(0, 50, 0, 0);
                tvbilledstatustext.setLayoutParams(p);
            }
        }
        tvbilledstatustext.setVisibility(View.VISIBLE);
        tvbilledstatussemi.setVisibility(View.VISIBLE);
        tvbilledstatus.setVisibility(View.VISIBLE);
        tvbilledstatus.setText(cons_billedstatus + "(" + cons_billedstatus2 + ")");
        tvbilledstatustext.setTextColor(color);
        tvbilledstatussemi.setTextColor(color);
        tvbilledstatus.setTextColor(color);
        tvbilledreadingtext.setVisibility(View.VISIBLE);
        tvbilledreadingsemi.setVisibility(View.VISIBLE);
        tvbilledreading.setVisibility(View.VISIBLE);
        tvbilledreading.setText(cons_billedreading);
        tvbilledreadingtext.setTextColor(color);
        tvbilledreadingsemi.setTextColor(color);
        tvbilledreading.setTextColor(color);
        tvbilledunitstext.setVisibility(View.VISIBLE);
        tvbilledunitssemi.setVisibility(View.VISIBLE);
        tvbilledunits.setVisibility(View.VISIBLE);
        tvbilledunits.setText(cons_billedunits);
        tvbilledunitstext.setTextColor(color);
        tvbilledunitssemi.setTextColor(color);
        tvbilledunits.setTextColor(color);
        tvbilledpayabletext.setVisibility(View.VISIBLE);
        tvbilledpayablesemi.setVisibility(View.VISIBLE);
        tvbilledpayable.setVisibility(View.VISIBLE);
        tvbilledpayable.setText(cons_billedpayable);
//        imgbilled.setVisibility(View.VISIBLE);
        /*if (!cons_IMGdisplay.equals("")) {
            Bitmap bitmap = null;
            try {
                bitmap = fcall.getImage(cons_IMGdisplay, getApplicationContext());
                imgbilled.setImageBitmap(bitmap);
                fcall.logStatus("Image Size: " + sizeOf(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        tvbilledpayabletext.setTextColor(color);
        tvbilledpayablesemi.setTextColor(color);
        tvbilledpayable.setTextColor(color);
    }

    private void textgone() {
        tvbilledstatus.setVisibility(View.GONE);
        tvbilledstatussemi.setVisibility(View.GONE);
        tvbilledstatustext.setVisibility(View.GONE);
        tvbilledreading.setVisibility(View.GONE);
        tvbilledreadingsemi.setVisibility(View.GONE);
        tvbilledreadingtext.setVisibility(View.GONE);
        tvbilledunits.setVisibility(View.GONE);
        tvbilledunitssemi.setVisibility(View.GONE);
        tvbilledunitstext.setVisibility(View.GONE);
        tvbilledpayable.setVisibility(View.GONE);
        tvbilledpayablesemi.setVisibility(View.GONE);
        tvbilledpayabletext.setVisibility(View.GONE);
        imgbilled.setVisibility(View.GONE);
    }

    private void selectedspinneritem(int status) {
        if (status == 3 || status == 4 || status == 6 || status == 8 || status == 9 || status == 10 || status == 11 || status == 12
                || status == 13 || status == 14 || status == 16 || status == 17 || status == 18 || status == 19
                || status == 20 || status == 21 || status == 22 || status == 23 || status == 24 || status == 25
                || status == 26 || status == 27 || status == 28 || status == 29 || status == 30) {
            etCurReading.setVisibility(View.VISIBLE);
            tv_curr_reading.setVisibility(View.VISIBLE);
            imMeterReding.setVisibility(View.VISIBLE);
            etCurReading.setText("");
            etCurReading.setHint("Enter Current Reading");
            etCurReading.setEnabled(true);
            pftodbmdvisible(true, "", status);
            et_mtr_digit.setVisibility(View.VISIBLE);
            tv_mtr_digit.setVisibility(View.VISIBLE);
            et_mtr_digit.setText("");
            et_mtr_digit.setEnabled(true);
        }
        if (status == 1) {
            c11 = dbh.getBilledRRNo(cons_Num);
            if (c11.getCount() > 0) {
                c11.moveToNext();
                cons_billedstatus3 = c11.getString(c11.getColumnIndex("PRES_STS"));
                c12 = dbh.statuscode(cons_billedstatus3);
                c12.moveToNext();
                cons_billedstatus4 = c12.getString(c12.getColumnIndex("STATUS_LABEL"));
                if (cons_billedstatus3.equals("1")) {
                    etCurReading.setVisibility(View.GONE);
                    tv_curr_reading.setVisibility(View.GONE);
                    if (etTODONPEAK.getVisibility() == View.VISIBLE) {
                        etTODONPEAK.setVisibility(View.GONE);
                        tv_tod_on_peak.setVisibility(View.GONE);
                        etTODOFPEAK.setVisibility(View.GONE);
                        tv_tod_off_peak.setVisibility(View.GONE);
                    }
                    if (etPF.getVisibility() == View.VISIBLE) {
                        etPF.setVisibility(View.GONE);
                        tv_pf.setVisibility(View.GONE);
                    }
                    if (etBMD.getVisibility() == View.VISIBLE) {
                        etBMD.setVisibility(View.GONE);
                        tv_bmd.setVisibility(View.GONE);
                    }
                    fcall.showtoastatcenter(ConsumerBilling.this, "Can't take reading for this Status");
                } else {
                    etCurReading.setText(cons_PrvRead);
                    etCurReading.setEnabled(false);
                    et_mtr_digit.setText(cons_MtrDigit);
                    et_mtr_digit.setEnabled(false);
                    pftodbmdvisible(true, "", status);
                }
            } else {
                etCurReading.setText(cons_PrvRead);
                etCurReading.setEnabled(false);
                et_mtr_digit.setText(cons_MtrDigit);
                et_mtr_digit.setEnabled(false);
                pftodbmdvisible(false, "0", status);
            }
        } else if (status == 5) {
            etCurReading.setVisibility(View.VISIBLE);
            tv_curr_reading.setVisibility(View.VISIBLE);
            imMeterReding.setVisibility(View.VISIBLE);
            etCurReading.setText("");
            etCurReading.setHint("Enter Current Reading");
            etCurReading.setEnabled(true);
            pftodbmdvisible(true, "", status);
            et_mtr_digit.setVisibility(View.VISIBLE);
            tv_mtr_digit.setVisibility(View.VISIBLE);
            et_mtr_digit.setText("");
            et_mtr_digit.setEnabled(true);
        } else if (status == 2 || status == 7 || status == 15) {
            etCurReading.setVisibility(View.VISIBLE);
            tv_curr_reading.setVisibility(View.VISIBLE);
            etCurReading.setText(cons_PrvRead);
            etCurReading.setEnabled(false);
            et_mtr_digit.setVisibility(View.VISIBLE);
            tv_mtr_digit.setVisibility(View.VISIBLE);
            et_mtr_digit.setText(cons_MtrDigit);
            et_mtr_digit.setEnabled(false);
            pftodbmdvisible(false, "0", status);
        }
    }

    private void pftodbmdvisible(boolean value, String settext, int status) {
        if (intpfFlag == 2) {
            etPF.setVisibility(View.VISIBLE);
            tv_pf.setVisibility(View.VISIBLE);
            etPF.setText(settext);
            etPF.setHint("Enter PF");
            etPF.setEnabled(value);
            etBMD.setVisibility(View.VISIBLE);
            tv_bmd.setVisibility(View.VISIBLE);
            etBMD.setText(settext);
            etBMD.setHint("Enter BMD Value");
            etBMD.setEnabled(value);
            if (status == 1 || status == 2 || status == 7 || status == 15) {
                et_kvah.setText(cons_readkvah);
                et_kvah.setEnabled(value);
            } else {
                et_kvah.setText("");
                et_kvah.setEnabled(value);
            }
        }
        if (intpfFlag == 1) {
            etPF.setVisibility(View.VISIBLE);
            tv_pf.setVisibility(View.VISIBLE);
            etPF.setText(cons_PFValue);
            etPF.setEnabled(false);
            etBMD.setVisibility(View.VISIBLE);
            tv_bmd.setVisibility(View.VISIBLE);
            etBMD.setText(cons_bmdval);
            etBMD.setEnabled(false);
            et_kvah.setText(cons_readkvah);
            et_kvah.setEnabled(false);
        }
        if (intTodFlag == 1) {
            etTODONPEAK.setVisibility(View.VISIBLE);
            tv_tod_on_peak.setVisibility(View.VISIBLE);
            etTODOFPEAK.setVisibility(View.VISIBLE);
            tv_tod_off_peak.setVisibility(View.VISIBLE);
            etTODONPEAK.setText(settext);
            etTODONPEAK.setHint("Enter TOD ON PEAK");
            etTODONPEAK.setEnabled(value);
            etTODOFPEAK.setText(settext);
            etTODOFPEAK.setHint("Enter TOD OFF PEAK");
            etTODOFPEAK.setEnabled(value);
        }
    }

    private void checkdlbilled() throws ParseException {
        c6 = dbh.getBilledID(cons_Num);
        if (c6.getCount() > 0) {
            c6.moveToNext();
            cons_readbillingid = c6.getString(c6.getColumnIndex("CONSNO"));
            cons_checkstatus = c6.getString(c6.getColumnIndex("PRES_STS"));
            if (Present_STS != 1) {
                if (!cons_Num.equals(cons_readbillingid)) {
                    checkbuttonclicked();
                } else {
                    if (cons_checkstatus.equals("1")) {
                        c9 = dbh.deletebillingrow(cons_Num);
                        c9.moveToNext();
                        checkbuttonclicked();
                    } else {
                        fcall.showtoastatcenter(ConsumerBilling.this, "Billing for this Customer already done for this month");
                    }
                }
            } else {
//				c9 = dbh.deletebillingrow(cons_RRNO);
//				c9.moveToNext();
                checkbuttonclicked();
            }
        } else {
            checkbuttonclicked();
        }
    }

    private void checkbuttonclicked() throws ParseException {
        if (Present_STS == 3)
            imagetaken = true;
        if (imagetaken) {
            /*if (etPhNumber.getVisibility() == View.VISIBLE) {
                cons_PhNumber = etPhNumber.getText().toString();
                if (!cons_PhNumber.equals("")) {
                    PhNumberreading();
                    if (phnumberclicked) {
                        tccodeinsertion();
                    }
                } else {
                    cons_PhNumber = "0";
                    tccodeinsertion();
                }
            } else {
                tccodeinsertion();
            }*/
            tccodeinsertion();
        } else Toast.makeText(this, "Take image and then proceed...", Toast.LENGTH_SHORT).show();
    }

    private void tccodeinsertion() throws ParseException {
        if (etNoofWeeks.getVisibility() == View.VISIBLE) {
            weeksreading();
            if (weeksclicked) {
                if (etPDReading.getVisibility() == View.VISIBLE) {
                    pdreading();
                    if (pdreadclicked) {
                        if (etTCCODE.getVisibility() == View.VISIBLE) {
                            tccodereading();
                            if (tccodeclicked) {
                                billinginsert();
                            }
                        } else {
                            billinginsert();
                        }
                    }
                } else {
                    if (etTCCODE.getVisibility() == View.VISIBLE) {
                        tccodereading();
                        if (tccodeclicked) {
                            billinginsert();
                        }
                    } else {
                        billinginsert();
                    }
                }
            }
        } else {
            if (etTCCODE.getVisibility() == View.VISIBLE) {
                tccodereading();
                if (tccodeclicked) {
                    billinginsert();
                }
            } else {
                billinginsert();
            }
        }
    }

    private void billinginsert() throws ParseException {
        if (etCurReading.getVisibility() == View.VISIBLE) {
            CurReading();
            if (btEnterclicked) {
                if (et_mtr_digit.getVisibility() == View.VISIBLE) {
                    cons_mtr_digit = et_mtr_digit.getText().toString();
                    if (!TextUtils.isEmpty(cons_mtr_digit)) {
                        if (cons_CurReading.length() <= Integer.parseInt(cons_mtr_digit)) {
                            if (etPF.getVisibility() == View.VISIBLE) {
                                pfReading();
                                if (etTODONPEAK.getVisibility() == View.VISIBLE) {
                                    todReading();
                                    if (btTODclicked) {
                                        onprint();
                                        onnext();
                                    }
                                } else {
                                    if (btPFclicked) {
                                        if (btBMDclicked) {
                                            onprint();
                                            onnext();
                                        }
                                    }
                                }
                            } else {
                                if (etTODONPEAK.getVisibility() == View.VISIBLE) {
                                    todReading();
                                    if (btTODclicked) {
                                        onprint();
                                        onnext();
                                    }
                                } else {
                                    onprint();
                                    onnext();
                                }
                            }
                        } else {
                            fcall.showtoast(ConsumerBilling.this, "Please enter valid Current reading or check with Mtr Digit reading...");
                            etCurReading.requestFocus();
                            etCurReading.setSelection(etCurReading.getText().length());
                            etCurReading.setError("Check with current reading...");
                            et_mtr_digit.setError("Check with mtr digit...");
                        }
                    } else {
                        et_mtr_digit.requestFocus();
                        et_mtr_digit.setError("Please enter Mtr Digit reading...");
                    }
                }
            }
        }
    }

    public ContentValues insertintorowempty() {
        ContentValues cv = new ContentValues();
        cv.put("RRNO", cons_RRNO);
        cv.put("CONSNO", cons_Num);
        return cv;
    }

    public void insertIntoTable() throws ParseException {
        DecimalFormat num1 = new DecimalFormat("##.0");
        DecimalFormat num = new DecimalFormat("##.00");
        ContentValues cv = new ContentValues();
        cv.put("MONTH", cons_Month);
        cv.put("READDATE", fcall.changedateformat(cons_CurrRead_date, ""));
        cv.put("RRNO", cons_RRNO);
        cv.put("NAME", cons_Name);
        cv.put("ADD1", cons_Addr);
        cv.put("TARIFF", cons_Tarrif);
        cv.put("MF", cons_MF);
        cv.put("PREVSTAT", cons_PrevMonthStatus);
        cv.put("AVGCON", cons_Avg_cons);
        cv.put("LINEMIN", cons_Linemin);
        cv.put("SANCHP", cons_SantionHp);
        cv.put("SANCKW", cons_SanctionKw);
        cv.put("PRVRED", cons_PrvRead);
        cv.put("FR", cons_FR);
        cv.put("IR", cons_IR);
        cv.put("DLCOUNT", cons_DLCount);
        cv.put("ARREARS", cons_Arrears);
        cv.put("PF_FLAG", cons_PfFlag);
        cv.put("BILLFOR", "30-" + fcall.currentDateTimeforBilling() + "-" + cons_IMEI);
        cv.put("MRCODE", cons_MRcode);
        cv.put("LEGFOL", cons_LegFol);
        cv.put("ODDEVEN", cons_OddEven);
        cv.put("SSNO", cons_SSNO);
        cv.put("CONSNO", cons_Num);
        cv.put("REBATE_FLAG", cons_RebateFlag);
        cv.put("RREBATE", cons_RRebate);
        if (cons_Extra1.equals(""))
            cv.put("EXTRA1", "0");
        else cv.put("EXTRA1", cons_Extra1);
        if (cons_Data1.equals("0"))
            if (!num.format(Double.parseDouble(cons_Data1)).equals(".00"))
                cv.put("DATA1", num.format(Double.parseDouble(cons_Data1)));
            else cv.put("DATA1", "0.00");
        else cv.put("DATA1", cons_Data1);
        if (cons_fec == 0.0) {
            cv.put("EXTRA2", "0");
            cv.put("DATA2", "0.00");
        } else {
            if (!num.format(Double.parseDouble(cons_Data2)).equals(".00")) {
                cv.put("EXTRA2", cons_Extra2);
                cv.put("DATA2", num.format(Double.parseDouble(cons_Data2)));
            } else {
                cv.put("EXTRA2", "0");
                cv.put("DATA2", "0.00");
            }
        }
        cv.put("PH_NO", cons_PhNumber);
        cv.put("DEPOSIT", cons_Deposit);
        cv.put("MTRDIGIT", cons_MtrDigit);
        cv.put("ASDAMT", cons_ASDamnt);
        cv.put("IODAMT", cons_TOD);// double
        cv.put("PFVAL", cons_pfval);
        if (cons_Tarrif.equals("70")) {
            cv.put("BMDVAL", cons_PDread);
        } else {
            if (cons_PfFlag.equals("0")) {
                if (Double.parseDouble(cons_inven_load) > 0) {
                    cv.put("BMDVAL", cons_inven_load);
                } else cv.put("BMDVAL", cons_BMDvalue);
            } else cv.put("BMDVAL", cons_BMDvalue);
        }
        cv.put("BILL_NO", cons_Num + "-" + fcall.changedateformat(cons_CurrRead_date, "/"));
        if (cons_InterestAmt.equals("0"))
            cv.put("INTEREST_AMT", "0.0");
        else cv.put("INTEREST_AMT", cons_InterestAmt);// double
        cv.put("CAP_FLAG", cons_CapFlag);
        cv.put("TOD_FLAG", cons_TodFlag);
        cv.put("TOD_PREVIOUS1", cons_TodPrev1);
        cv.put("TOD_PREVIOUS3", cons_TodPrev3);
        cv.put("INT_ON_DEP", cons_InterOnDep);
        cv.put("SO_FEEDER_TC_POLE", cons_So_FeedTLPole);
        cv.put("TARIFF_NAME", cons_Tariff_name);
        cv.put("PREV_READ_DATE", cons_PrevReadDate);
        if (cons_Tarrif.equals("70")) {
            cv.put("BILL_DAYS", cons_weekSTS);
        } else {
            cv.put("BILL_DAYS", cons_BillDays);
        }
        cv.put("MTR_SERIAL_NO", cons_MtrSrlNO);
        cv.put("CHQ_DISSHONOUR_FLAG", "0");
        cv.put("CHQ_DISHONOUR_DATE", cons_CHQDisHnrDate);
        cv.put("FDRNAME", cons_FDRName);
        if (cons_TCCode.equals(""))
            cv.put("TCCODE", cons_TCCode);
        else cv.put("TCCODE", "0");
        cv.put("MTR_FLAG", cons_MTRFlag);
        cv.put("PRES_RDG", cons_CurReading);
        cv.put("PRES_STS", Present_STS);// int
        cv.put("UNITS", getset.getUnits());
        cv.put("FIX", fcall.decimalroundoff(cons_totalFC));// double
        cv.put("ENGCHG", fcall.decimalroundoff(cons_totalEC));// double
        if (cons_RRebate.equals("7"))
            cv.put("REBATE_AMOUNT", fcall.decimalroundoff(cons_charity));
        else cv.put("REBATE_AMOUNT", fcall.decimalroundoff(cons_rebate));// double
        cv.put("TAX_AMOUNT", "" + cons_tax);// double
        if (cons_Tarrif.equals("70")) {
            cv.put("BMD_PENALTY", cons_PDpenalty);
        } else {
            cv.put("BMD_PENALTY", fcall.decimalroundoff(BMDPeanlities));// double
        }
        cv.put("PF_PENALTY", fcall.decimalroundoff(cons_PF));// double
        cv.put("PAYABLE", fcall.decimalroundoff(cons_payableAmount));// double
        cv.put("BILLDATE", fcall.changedateformat(cons_billingdate, ""));
        cv.put("BILLTIME", fcall.currentDateandTime());
        cv.put("TOD_CURRENT1", cons_todOfPeak);
        cv.put("TOD_CURRENT3", cons_todOnPeak);
        if (num.format(cons_GOK).equals(".00"))
            cv.put("GOK_SUBSIDY", "0.00");// double
        else cv.put("GOK_SUBSIDY", num.format(cons_GOK));// double
        cv.put("DEM_REVENUE", fcall.decimalroundoff(cons_billAmount));// double
        cv.put("GPS_LAT", cons_gpslat);
        cv.put("GPS_LONG", cons_gpslong);
        cv.put("ONLINE_FLAG", "N");
        if (!cons_imageextension.equals(""))
            cv.put("IMGADD", cons_imageextension);
        else cv.put("IMGADD", "noimage");
        cv.put("PAYABLE_REAL", fcall.decimalroundoff(cons_payamount));// double
        cv.put("PAYABLE_PROFIT", cons_payprofit);
        cv.put("PAYABLE_LOSS", cons_payloss);
        cv.put("BILL_PRINTED", "N");
        cv.put("BATTERY_CHARGE", cons_battery_level);
        cv.put("SIGNAL_STRENGTH", cons_signal_strength);

        //Inserting totalfs values
        String ArrFC = "";
        ArrFC = "" + num.format(cons_ArrFc[1]);
        if (ArrFC.equals(".00"))
            cv.put("FSLAB1", "0.00 x 0.00 , 0.0");
        else
            cv.put("FSLAB1", "" + num.format(cons_ArrFslab[1]) + " x " + "" + num.format(cons_ArrFrate[1]) + " , " + "" + num1.format(cons_ArrFc[1]));
        ArrFC = "" + num.format(cons_ArrFc[2]);
        if (ArrFC.equals(".00"))
            cv.put("FSLAB2", "0.00 x 0.00 , 0.0");
        else
            cv.put("FSLAB2", "" + num.format(cons_ArrFslab[2]) + " x " + "" + num.format(cons_ArrFrate[2]) + " , " + "" + num1.format(cons_ArrFc[2]));
        ArrFC = "" + num.format(cons_ArrFc[3]);
        if (ArrFC.equals(".00"))
            cv.put("FSLAB3", "0.00 x 0.00 , 0.0");
        else
            cv.put("FSLAB3", "" + num.format(cons_ArrFslab[3]) + " x " + "" + num.format(cons_ArrFrate[3]) + " , " + "" + num1.format(cons_ArrFc[3]));
        ArrFC = "" + num.format(cons_ArrFc[4]);
        if (ArrFC.equals(".00"))
            cv.put("FSLAB4", "0.00 x 0.00 , 0.0");
        else
            cv.put("FSLAB4", "" + num.format(cons_ArrFslab[4]) + " x " + "" + num.format(cons_ArrFrate[4]) + " , " + "" + num1.format(cons_ArrFc[4]));
        ArrFC = "" + num.format(cons_ArrFc[5]);
        if (ArrFC.equals(".00"))
            cv.put("FSLAB5", "0.00 x 0.00 , 0.0");
        else
            cv.put("FSLAB5", "" + num.format(cons_ArrFslab[5]) + " x " + "" + num.format(cons_ArrFrate[5]) + " , " + "" + num1.format(cons_ArrFc[5]));

        //Inserting totalec values
        DecimalFormat num3 = new DecimalFormat("##.000");
        if (("" + num.format(cons_ArrEc[1])).equals(".00"))
            cv.put("ESLAB1", "0.000 x 0.00 , 0.00");
        else
            cv.put("ESLAB1", "" + num3.format(cons_ArrEslab[1]) + " x " + "" + num.format(cons_ArrErate[1]) + " , " + "" + num.format(cons_ArrEc[1]));
        if (("" + num.format(cons_ArrEc[2])).equals(".00"))
            cv.put("ESLAB2", "0.000 x 0.00 , 0.00");
        else
            cv.put("ESLAB2", "" + num3.format(cons_ArrEslab[2]) + " x " + "" + num.format(cons_ArrErate[2]) + " , " + "" + num.format(cons_ArrEc[2]));
        if (("" + num.format(cons_ArrEc[3])).equals(".00"))
            cv.put("ESLAB3", "0.000 x 0.00 , 0.00");
        else
            cv.put("ESLAB3", "" + num3.format(cons_ArrEslab[3]) + " x " + "" + num.format(cons_ArrErate[3]) + " , " + "" + num.format(cons_ArrEc[3]));
        if (("" + num.format(cons_ArrEc[4])).equals(".00"))
            cv.put("ESLAB4", "0.000 x 0.00 , 0.00");
        else
            cv.put("ESLAB4", "" + num3.format(cons_ArrEslab[4]) + " x " + "" + num.format(cons_ArrErate[4]) + " , " + "" + num.format(cons_ArrEc[4]));
        if (("" + num.format(cons_ArrEc[5])).equals(".00"))
            cv.put("ESLAB5", "0.000 x 0.00 , 0.00");
        else
            cv.put("ESLAB5", "" + num3.format(cons_ArrEslab[5]) + " x " + "" + num.format(cons_ArrErate[5]) + " , " + "" + num.format(cons_ArrEc[5]));
        if (("" + num.format(cons_ArrEc[6])).equals(".00"))
            cv.put("ESLAB6", "0.000 x 0.00 , 0.00");
        else
            cv.put("ESLAB6", "" + num3.format(cons_ArrEslab[6]) + " x " + "" + num.format(cons_ArrErate[6]) + " , " + "" + num.format(cons_ArrEc[6]));
        if (cons_Tarrif.equals("20") || cons_Tarrif.equals("21"))
            cv.put("CHARITABLE_RBT_AMT", fcall.decimalroundoff(cons_charity));
        else cv.put("CHARITABLE_RBT_AMT", "0.0");
        if (cons_Tarrif.equals("20") || cons_Tarrif.equals("21") || cons_Tarrif.equals("23")) {
            if (cons_RebateFlag.equals("1")) {
                cv.put("SOLAR_RBT_AMT", fcall.decimalroundoff(cons_rebate));
            } else cv.put("SOLAR_RBT_AMT", "0.0");
        } else cv.put("SOLAR_RBT_AMT", "0.0");
        if (cons_Tarrif.equals("30")) {
            if (cons_RebateFlag.equals("2")) {
                cv.put("HANDICAP_RBT_AMT", fcall.decimalroundoff(cons_rebate));
            } else cv.put("HANDICAP_RBT_AMT", "0.0");
        } else cv.put("HANDICAP_RBT_AMT", "0.0");
        if (cons_Tarrif.equals("50") || cons_Tarrif.equals("51") || cons_Tarrif.equals("52") ||
                cons_Tarrif.equals("53")) {
            if (cons_RebateFlag.equals("5")) {
                cv.put("PL_RBT_AMT", fcall.decimalroundoff(cons_GOK));
            } else cv.put("PL_RBT_AMT", "0.0");
        } else cv.put("PL_RBT_AMT", "0.0");
        if (cons_Tarrif.equals("23")) {
            cv.put("FL_RBT_AMT", fcall.decimalroundoff(cons_rebate));
        } else cv.put("FL_RBT_AMT", "0.0");
        cv.put("IPSET_RBT_AMT", "0.0");
        cv.put("REBATEFROMCCB_AMT", "0.0");
        cv.put("TOD_CHARGES", "0.0");
        cv.put("PF_PENALITY_AMT", "0.0");
        cv.put("EXLOAD_MDPENALITY", "0.0");
        cv.put("CURR_BILL_AMOUNT", "0.0");
        cv.put("ROUNDING_AMOUNT", "0.0");
        cv.put("DUE_DATE", fcall.changedateformat(fcall.duedate(fcall.changedateformat(cons_CurrRead_date, "/"), 14), ""));
        cv.put("DISCONN_DATE", fcall.changedateformat(fcall.duedate(fcall.changedateformat(cons_CurrRead_date, "/"), 30), ""));
        cv.put("CREADJ", cons_creadj);
        cv.put("PREADKVAH", cons_readkvah);
        cv.put("AADHAAR", cons_aadhaar_no);
        cv.put("MAIL", cons_mail_id);
        cv.put("MTR_DIGIT", cons_mtr_digit);
        cv.put("ELECTION", cons_election_no);
        cv.put("RATION", cons_ration_no);
        cv.put("WATER", cons_water_no);
        cv.put("HOUSE_NO", cons_house_no);
        cv.put("VERSION", settings.getString(CURRENT_VERSION, ""));
        dbh.insertInTable(cv);
        insert_success = true;
    }

    public String dialover(String last_reading, String pre_reading) {
        int s5 = Integer.parseInt(pre_reading);
        int s6 = Integer.parseInt(last_reading);
        int s7 = last_reading.length();
        String repeat = new String(new char[s7]).replace("\0", "9");
        int s8 = Integer.parseInt(repeat);
        int s9 = s8 - s6;
        int s10 = s9 + s5 + 1;
        String s1 = "" + s10;
        return s1;

    }

    private void hidekeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    private void PhNumberreading() {
        int PhNumberlength = cons_PhNumber.length();
        if (PhNumberlength >= 10) {
            phnumberclicked = true;
        } else {
            etPhNumber.requestFocus();
            fcall.showtoastatcenter(ConsumerBilling.this, "Enter Correct Phone Number");
        }
    }

    private void CurReading() {
        cons_CurReading = etCurReading.getText().toString();
        if (!TextUtils.isEmpty(cons_CurReading)) {
            intCurReading = Integer.parseInt(cons_CurReading);
            /*if (Present_STS == 1) {
                btEnterclicked = true;
                fcall.showtoastatcenter(ConsumerBilling.this, "Current Reading is " + cons_CurReading);
            }*/
            if (Present_STS == 3 || Present_STS == 4 || Present_STS == 6 || Present_STS == 8 || Present_STS == 9 || Present_STS == 10
                    || Present_STS == 11 || Present_STS == 12 || Present_STS == 13 || Present_STS == 14 || Present_STS == 16
                    || Present_STS == 17 || Present_STS == 18 || Present_STS == 19 || Present_STS == 20 || Present_STS == 21
                    || Present_STS == 22 || Present_STS == 23 || Present_STS == 24 || Present_STS == 25 || Present_STS == 26
                    || Present_STS == 27 || Present_STS == 28 || Present_STS == 29 || Present_STS == 30) {
                if (intCurReading >= intPrvRead) {
                    btEnterclicked = true;
                    fcall.showtoastatcenter(ConsumerBilling.this, "Current Reading is " + cons_CurReading);
                } else {
                    etCurReading.setError("Enter Valid Current Reading or Take Status as Dial Over");
                    etCurReading.requestFocus();
                    Toast.makeText(ConsumerBilling.this, "Enter Valid Current Reading or Take Status as Dial Over",
                            Toast.LENGTH_SHORT).show();
                }
            }
            if (Present_STS == 5) {
                if (intCurReading < intPrvRead) {
                    btEnterclicked = true;
                    fcall.showtoastatcenter(ConsumerBilling.this, "Current Reading is " + cons_CurReading);
                } else {
                    etCurReading.setError("Enter Valid Current Reading or Take Status as Normal");
                    etCurReading.requestFocus();
                    Toast.makeText(ConsumerBilling.this, "Enter Valid Current Reading or Take Status as Normal",
                            Toast.LENGTH_SHORT).show();
                }
            }
            if (Present_STS == 1 || Present_STS == 2 || Present_STS == 7 || Present_STS == 15) {
                btEnterclicked = true;
            }
        } else {
            etCurReading.setError("Take Current Reading");
            etCurReading.requestFocus();
            fcall.showtoastatcenter(ConsumerBilling.this, "Take Current Reading");
        }
    }

    private void pfReading() {
        cons_pfval = etPF.getText().toString();
        if (!TextUtils.isEmpty(cons_pfval)) {
            if (Present_STS == 1 || Present_STS == 2 || Present_STS == 7 || Present_STS == 15) {
                intPFEnteredValue = Double.parseDouble(fcall.decimalroundoff(Double.parseDouble(cons_pfval)));
                if (intPFEnteredValue < 0.70) {
                    if (intPFEnteredValue == 0) {
                        btPFclicked = true;
                        bmdReading();
                        fcall.showtoastatcenter(ConsumerBilling.this, "PF Value is " + cons_pfval);
                    } else {
                        intPFEnteredValue = 0.70;
                        btPFclicked = true;
                        bmdReading();
                        fcall.showtoastatcenter(ConsumerBilling.this, "PF Value is " + cons_pfval);
                    }
                } else {
                    btPFclicked = true;
                    bmdReading();
                    fcall.showtoastatcenter(ConsumerBilling.this, "PF Value is " + cons_pfval);
                }
            } else {
                if (Double.parseDouble(cons_pfval) > 0 && Double.parseDouble(cons_pfval) < 1) {
                    intPFEnteredValue = Double.parseDouble(fcall.decimalroundoff(Double.parseDouble(cons_pfval)));
                    if (intPFEnteredValue < 0.70) {
                        if (intPFEnteredValue == 0) {
                            btPFclicked = true;
                            bmdReading();
                            fcall.showtoastatcenter(ConsumerBilling.this, "PF Value is " + cons_pfval);
                        } else {
                            intPFEnteredValue = 0.70;
                            btPFclicked = true;
                            bmdReading();
                            fcall.showtoastatcenter(ConsumerBilling.this, "PF Value is " + cons_pfval);
                        }
                    } else {
                        btPFclicked = true;
                        bmdReading();
                        fcall.showtoastatcenter(ConsumerBilling.this, "PF Value is " + cons_pfval);
                    }
                } else {
                    etPF.setError("Please enter valid PF value between 0 to 1...");
                    etPF.setSelection(etPF.getText().length());
                    etPF.requestFocus();
                    fcall.showtoast(ConsumerBilling.this, "Please enter valid PF value between 0 to 1...");
                }
            }
        } else {
            etPF.setError("Enter PF Value");
            etPF.requestFocus();
            fcall.showtoastatcenter(ConsumerBilling.this, "Enter PF Value");
        }
    }

    private void bmdReading() {
        cons_BMDvalue = etBMD.getText().toString();
        if (!TextUtils.isEmpty(cons_BMDvalue)) {
            intBMDValue = Double.parseDouble(cons_BMDvalue);
            if (intBMDValue <= 100) {
                fcall.showtoastatcenter(ConsumerBilling.this, "BMD Value is " + cons_BMDvalue);
                cons_readkvah = et_kvah.getText().toString();
                if (!TextUtils.isEmpty(cons_readkvah)) {
                    btBMDclicked = true;
                } else {
                    et_kvah.setError("Enter KVAH Value");
                    et_kvah.requestFocus();
                    fcall.showtoast(ConsumerBilling.this, "Enter KVAH Value");
                }
            } else {
                etBMD.setError("Please enter valid BMD value within 100...");
                etBMD.setSelection(etBMD.getText().length());
                etBMD.requestFocus();
                fcall.showtoast(ConsumerBilling.this, "Please enter valid BMD value within 100...");
            }
        } else {
            etBMD.setError("Enter BMD Value");
            etBMD.requestFocus();
            fcall.showtoastatcenter(ConsumerBilling.this, "Enter BMD Value");
        }
    }

    private void todReading() {
        cons_todOnPeak = etTODONPEAK.getText().toString();
        cons_todOfPeak = etTODOFPEAK.getText().toString();
        if (!cons_todOnPeak.equals("")) {
            if (!cons_todOfPeak.equals("")) {
                btTODclicked = true;
                intTodOnPeak = Integer.parseInt(cons_todOnPeak);
                cons_todonconsume = intTodOnPeak - intTodPrev1;
                intTodOffPeak = Integer.parseInt(cons_todOfPeak);
                cons_todoffconsume = intTodOffPeak - intTodPrev3;
            } else {
                etTODOFPEAK.setError("Enter TOD OFF PEAK Value");
                etTODOFPEAK.setSelection(etTODOFPEAK.getText().length());
                etTODOFPEAK.requestFocus();
                fcall.showtoastnormal(ConsumerBilling.this, "Enter TOD OFF PEAK Value");
            }
        } else {
            etTODONPEAK.setError("Enter TOD ON PEAK Value");
            etTODONPEAK.setSelection(etTODONPEAK.getText().length());
            etTODONPEAK.requestFocus();
            fcall.showtoastnormal(ConsumerBilling.this, "Enter TOD ON PEAK Value");
        }
    }

    private void tccodereading() {
        String tccode = etTCCODE.getText().toString();
        if (!tccode.equals("")) {
            tccodeclicked = true;
            cons_TCCode = tccode;
        } else {
            etTCCODE.setError("");
            etTCCODE.setSelection(etTCCODE.getText().length());
            etTCCODE.requestFocus();
            fcall.showtoastatcenter(ConsumerBilling.this, "Enter TCCODE");
        }
    }

    private void weeksreading() {
        String weeks = etNoofWeeks.getText().toString();
        if (!TextUtils.isEmpty(weeks)) {
            weeksclicked = true;
            cons_weekSTS = weeks;
        } else {
            etNoofWeeks.setError("Enter No of days");
            etNoofWeeks.setSelection(etNoofWeeks.getText().length());
            etNoofWeeks.requestFocus();
            fcall.showtoastatcenter(ConsumerBilling.this, "Enter No of days");
        }
        /*if (!weeks.equals("")) {
            int noofweek = Integer.parseInt(weeks);
            if (noofweek > 0 && noofweek <= 5) {
                weeksclicked = true;
                cons_weekSTS = weeks;
            } else {
                etNoofWeeks.setError("Enter Correct no days");
//                etNoofWeeks.setError("Enter Correct no weeks");
                etNoofWeeks.setSelection(etNoofWeeks.getText().length());
                etNoofWeeks.requestFocus();
                fcall.showtoastatcenter(ConsumerBilling.this, "Enter Correct no days");
//                fcall.showtoastatcenter(ConsumerBilling.this, "Enter Correct no weeks");
            }
        } else {
            etNoofWeeks.setError("Enter No of days");
            etNoofWeeks.setSelection(etNoofWeeks.getText().length());
            etNoofWeeks.requestFocus();
            fcall.showtoastatcenter(ConsumerBilling.this, "Enter No of days");
        }*/
    }

    public void pdreading() {
        String pdread = etPDReading.getText().toString();
        if (!pdread.equals("")) {
            pdreadclicked = true;
            cons_PDread = pdread;
            c14 = dbh.getTarrifDataBJ("0", cons_Tarrif);
            cons_PDpenalty = fcall.pdcalculation(pdread, c14, cons_SanctionKw, cons_weekSTS);
        } else {
            etPDReading.setError("Enter PD Recorded");
            etPDReading.setSelection(etPDReading.getText().length());
            etPDReading.requestFocus();
            fcall.showtoastatcenter(ConsumerBilling.this, "Enter PD Recorded");
        }
    }

    public void saveExternalPrivateStorage(File folderDir, Bitmap bitmap) {

        if (folderDir.exists()) {
            folderDir.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(folderDir);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        gps.stopUsingGPS();
        super.onDestroy();
    }
    /*******************I have to remove comment for the below code***************/
    /*****************************************************************************/
    /****************************************************************************/
   /* private void onexit() {
        if (cons_otherbillings.equals("Yes")) {
            if (cons_status.equals("NOT BILLED")) {
                Intent back = new Intent(ConsumerBilling.this, Billstatusext1.class);
                back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                back.putExtra("status", cons_status);
                startActivity(back);
            } else {
                Intent back = new Intent(ConsumerBilling.this, Billstatusext.class);
                back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                back.putExtra("Reportstatus", "Billreport");
                back.putExtra("status", cons_status);
                startActivity(back);
            }
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        onexit();
        super.onBackPressed();
    }*/

    /*****************************************************************************/
    @SuppressLint({"ViewHolder", "InflateParams"})
    public class Myspinneradapter extends BaseAdapter {

        ArrayList<Pay> mylist = new ArrayList<Pay>();
        Context context;
        LayoutInflater inflater;

        public Myspinneradapter(Context context, ArrayList<Pay> arraylist) {
            this.mylist = arraylist;
            this.context = context;
            inflater = LayoutInflater.from(this.context);
        }

        @Override
        public int getCount() {
            return mylist.size();
        }

        @Override
        public Object getItem(int position) {
            return mylist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.spinner_items, null);
            TextView tvstatus = (TextView) convertView.findViewById(R.id.textView1);
            tvstatus.setText(mylist.get(position).getstatus());
            return convertView;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    Log.i("Class", info[i].getState().toString());
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void turnMobileConnection(boolean ON) throws Exception {
        final ConnectivityManager conman = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class<?> conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(iConnectivityManager, ON);
    }

    private void showdialog(int id) {
        AlertDialog alertDialog = null;
        switch (id) {
            case DLG_TIME_OVER:
                AlertDialog.Builder time_over = new AlertDialog.Builder(this);
                time_over.setTitle("Time Over");
                time_over.setCancelable(false);
                time_over.setMessage("Billing Time is over... Can not take billing now...");
                time_over.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                alertDialog = time_over.create();
                alertDialog.show();
                break;

            case SUB_NORMAL_DLG:
                AlertDialog.Builder sub_normal = new AlertDialog.Builder(this);
                sub_normal.setCancelable(false);
                sub_normal.setTitle("Sub Normal Reading");
                sub_normal.setMessage("Please check the reading..." + "\n" + "Average Consumption is: " + cons_Avg_cons);
                sub_normal.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etCurReading.requestFocus();
                        etCurReading.setSelection(etCurReading.getText().length());
                    }
                });
                sub_normal.setNegativeButton("SKIP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            checkdlbilled();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                AlertDialog normal_dialog = sub_normal.create();
                normal_dialog.show();
                break;

            case AB_NORMAL_DLG:
                AlertDialog.Builder ab_normal = new AlertDialog.Builder(this);
                ab_normal.setCancelable(false);
                ab_normal.setTitle("Ab Normal Reading");
                ab_normal.setMessage("Please check the reading..." + "\n" + "Average Consumption is: " + cons_Avg_cons);
                ab_normal.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etCurReading.requestFocus();
                        etCurReading.setSelection(etCurReading.getText().length());
                    }
                });
                ab_normal.setNegativeButton("SKIP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            checkdlbilled();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                AlertDialog ab_dialog = ab_normal.create();
                ab_dialog.show();
                break;

            case DLG_MTR_ASSET:
                AlertDialog.Builder mtr_asset = new AlertDialog.Builder(this);
                mtr_asset.setCancelable(false);
                mtr_asset.setTitle("Meter Asset Details");
                LinearLayout mtr_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.mtr_assets, null);
                mtr_asset.setView(mtr_layout);
                final String[] meter_display = {""};
                final EditText et_mtrtype = (EditText) mtr_layout.findViewById(R.id.et_asset_mtr_type);
                final EditText et_mtrmake = (EditText) mtr_layout.findViewById(R.id.et_asset_mtr_make);
                final EditText et_mtrdigit = (EditText) mtr_layout.findViewById(R.id.et_asset_mtr_digit);
                final EditText et_mtrserial = (EditText) mtr_layout.findViewById(R.id.et_asset_mtr_serial);
                final EditText et_mtracct_id = (EditText) mtr_layout.findViewById(R.id.et_asset_mtr_account_id);
                et_mtracct_id.setText(cons_Num);
                final EditText et_mtr_mrcode = (EditText) mtr_layout.findViewById(R.id.et_asset_mrcode);
                et_mtr_mrcode.setText(cons_MRcode);
                Spinner sp_display = (Spinner) mtr_layout.findViewById(R.id.sp_asset_mtr_display);
                final ArrayList<String> display_list = new ArrayList<>();
                ArrayAdapter<String> display_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, display_list);
                sp_display.setAdapter(display_adapter);
                sp_display.setSelection(0);
                for (int j = 0; j < getResources().getStringArray(R.array.meter_display).length; j++) {
                    display_list.add(getResources().getStringArray(R.array.meter_display)[j]);
                    display_adapter.notifyDataSetChanged();
                }
                sp_display.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!(position == 0)) {
                            meter_display[0] = display_list.get(position);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                GPSLocation();
                mtr_asset.setPositiveButton("UPDATE", null);
                mtr_asset.setNegativeButton("SKIP", null);
                final AlertDialog asset_dialog = mtr_asset.create();
                asset_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button positive = asset_dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button negative = asset_dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        positive.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (validate_EditText(et_mtrtype)) {
                                    if (validate_EditText(et_mtrmake)) {
                                        if (validate_EditText(et_mtrdigit)) {
                                            if (validate_EditText(et_mtrserial)) {
                                                if (validate_EditText(et_mtracct_id)) {
                                                    if (et_mtracct_id.getText().length() == 10) {
                                                        if (validate_EditText(et_mtr_mrcode)) {
                                                            if (et_mtr_mrcode.getText().length() == 8) {
                                                                if (!TextUtils.isEmpty(meter_display[0])) {
                                                                    ContentValues cv = new ContentValues();
                                                                    cv.put("MTR_TYPE", et_mtrtype.getText().toString());
                                                                    cv.put("MTR_MAKE", et_mtrmake.getText().toString());
                                                                    cv.put("MTR_DIGIT", et_mtrdigit.getText().toString());
                                                                    cv.put("MTR_SERIAL_NO", et_mtrserial.getText().toString());
                                                                    cv.put("ACCOUNT_ID", et_mtracct_id.getText().toString());
                                                                    cv.put("MRCODE", et_mtr_mrcode.getText().toString());
                                                                    cv.put("MTR_DISPLAY", meter_display[0]);
                                                                    cv.put("GPS_LONG", cons_gpslong);
                                                                    cv.put("GPS_LAT", cons_gpslat);
                                                                    cv.put("DATE_TIME", fcall.currentDateandTime());
                                                                    dbh.insert_meter_asset_details(cv);
                                                                    asset_dialog.dismiss();
                                                                    fcall.showtoast(ConsumerBilling.this, "Meter Asset Details inserted successfully...");
                                                                } else
                                                                    fcall.showtoast(ConsumerBilling.this, "Please select Meter Display & proceed...");
                                                            } else
                                                                edittext_error(et_mtr_mrcode, "Enter Valid MRCode");
                                                        } else
                                                            edittext_error(et_mtr_mrcode, "Enter MRCode");
                                                    } else
                                                        edittext_error(et_mtracct_id, "Enter Valid Account ID");
                                                } else
                                                    edittext_error(et_mtracct_id, "Enter Account ID");
                                            } else
                                                edittext_error(et_mtrserial, "Enter Meter Serial");
                                        } else edittext_error(et_mtrdigit, "Enter Meter Digit");
                                    } else edittext_error(et_mtrmake, "Enter Meter Make");
                                } else edittext_error(et_mtrtype, "Enter Meter Type");
                            }
                        });
                        negative.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                asset_dialog.dismiss();
                            }
                        });
                    }
                });
                asset_dialog.show();
                break;
        }
    }

    private boolean validate_EditText(EditText et_text) {
        return (!TextUtils.isEmpty(et_text.getText().toString()));
    }

    private void edittext_error(EditText et_text, String msg) {
        et_text.setError(msg);
        et_text.setSelection(et_text.getText().length());
        et_text.requestFocus();
    }

    private void feder_dtc_spinner() {
        sp_feder.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetSetValues getvalue = feder_list.get(position);
                cons_FDRName = getvalue.getLogin_role();
                dtc_list.clear();
                dtc_adapter.notifyDataSetChanged();
                Cursor data = dbh.dtc_code_details(dbh.feder_code(cons_FDRName));
                while (data.moveToNext()) {
                    GetSetValues getset = new GetSetValues();
                    getset.setLogin_role(data.getString(data.getColumnIndexOrThrow("DTC_CODE")));
                    dtc_list.add(getset);
                    dtc_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_dtc.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetSetValues getvalue = dtc_list.get(position);
                cons_TCCode = getvalue.getLogin_role();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            cons_battery_level = String.valueOf(level);
        }
    };

    private void showStrength() {
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength strength) {
                super.onSignalStrengthsChanged(strength);
                if (strength.isGsm()) {
                    String[] parts = strength.toString().split(" ");
                    String signalStrength = "";
                    int currentStrength = strength.getGsmSignalStrength();
                    if (currentStrength <= 0) {
                        if (currentStrength == 0) {
                            signalStrength = String.valueOf(Integer.parseInt(parts[3]));
                        } else {
                            signalStrength = String.valueOf(Integer.parseInt(parts[1]));
                        }
                        signalStrength += " dBm";
                    } else {
                        if (currentStrength != 99) {
                            signalStrength = String.valueOf(((2 * currentStrength) - 113));
                            signalStrength += " dBm";
                        }
                    }
                    cons_signal_strength = signalStrength;
                }
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

}