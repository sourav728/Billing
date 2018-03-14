package subdiv.transvision.com.billing.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

import subdiv.transvision.com.billing.values.FunctionCalls;

public class Databasehelper extends SQLiteOpenHelper {
    private String downfilepath = "";
    private String downfilename = "";
    private String downfileformat = "";
    private SQLiteDatabase myDatabase;
    public final Context mycontext;
    static FunctionCalls fcalls = new FunctionCalls();
    private static final String DATABASE_NAME = "mydb.db";
    static String path = fcalls.filepath("databases");
    public final static String DATABASE_PATH = path + File.separator;
    public static final int DATABASE_VERSION = 1;

    public Databasehelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mycontext = context;
    }

    public void openDatabase() throws SQLException {
        String mypath = DATABASE_PATH + DATABASE_NAME;
        myDatabase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDatabase() throws SQLException {
        if (myDatabase == null)
            myDatabase.close();
        super.close();
    }
    public Cursor updateDLrecord(String monthdiff) {
        Cursor data = null;
        data = myDatabase.rawQuery("update MAST_CUST set DLCOUNT = '"+monthdiff+"' where rowid = (select billed_record from subdiv_details)", null);
        return data;
    }
    // Cursor to get Billed Record number
    public Cursor getBilledRecordNumber() {
        Cursor c = null;
        c = myDatabase.rawQuery("Select * from SUBDIV_DETAILS", null);
        return c;
    }
    public Cursor getUnBilledRecordData() {
        Cursor c = null;
        c = myDatabase.rawQuery("select * from mast_cust where CONSNO not in (select CONSNO from mast_out) and rowid = (select billed_record from subdiv_details)", null);
        return c;
    }
    public Cursor getDLBilledRecordData() {
        Cursor c = null;
        c = myDatabase.rawQuery("select * from MAST_OUT where PRES_STS = '1' and rowid = (select billed_record from subdiv_details)", null);
        return c;
    }
    public Cursor getImageAddressString(String RRNO1) {
        Cursor c = null;
        c = myDatabase.rawQuery("Select IMGADD from MAST_OUT where CONSNO = " + "'" + RRNO1 + "'", null);
        return c;
    }
    //Update the Billed Record
    public void updatebill(int billUpdate) {
        ContentValues cv = new ContentValues();
        cv.put("Billed_Record", billUpdate);
        myDatabase.update("SUBDIV_DETAILS", cv, "Billed_Record", null);
    }
    public Cursor meter_asset_details(String account_id) {
        return myDatabase.rawQuery("SELECT * FROM METER_ASSET WHERE ACCOUNT_ID = '"+account_id+"'", null);
    }
    public Cursor getData() {
        Cursor c = null;
        c = myDatabase.query("MAST_CUST", null, null, null, null, null, null);
        return c;
    }
    public Cursor getBilledRecordData() {
        Cursor c = null;
        c = myDatabase.rawQuery("select * from mast_cust where rowid = (select billed_record from subdiv_details)", null);
        return c;
    }
    //Tariff = 10
    public Cursor getTarrifDataBJ(String rRebate, String Tariff) {
        Cursor c = null;
        c = myDatabase.rawQuery("select * from TARIFF_CONFIG_CURRENT where TARIFF_CODE = " + "'" + Tariff + "'and RUFLAG = " + "'" + rRebate + "'", null);
        Log.d("debug", "select * from TARIFF_CONFIG_CURRENT where TARIFF_CODE = " + "'" + Tariff + "'and RUFLAG = " + "'" + rRebate + "'");
        return c;
    }
    public boolean checkinserteddata(String account_id) {
        Cursor data = null;
        data = myDatabase.rawQuery("SELECT * FROM MAST_OUT WHERE CONSNO ='"+account_id+"'", null);
        if (data.getCount() > 0) {
            data.close();
            return true;
        } else {
            data.close();
            return false;
        }
    }
    //Cursor to set data for spinner
    public Cursor spinnerData() {
        Cursor c = null;
        c = myDatabase.rawQuery("Select STATUS_NAME from BILLING_STATUS", null);
        return c;
    }
    public void insertInTable(ContentValues cv1) {
        myDatabase.insert("MAST_OUT", null, cv1);
    }
    public void insert_meter_asset_details(ContentValues cv) {
        myDatabase.insert("METER_ASSET", null, cv);
    }

    // Cursor to Select particular Item to select for spinner item
    public Cursor spinnerSelectedData(int index) {
        Cursor c = null;
        c = myDatabase.rawQuery("Select STATUS_LABEL from BILLING_STATUS where STATUS_CODE =" + "'" + index + "'", null);
        return c;
    }
    public Cursor getTarrifData(String rRNO, String rRebate) {
        Cursor c = null;
        String query = "select * from TARIFF_CONFIG_CURRENT where TARIFF_CODE = (select TARIFF from MAST_CUST where CONSNO = " + "'" + rRNO + "') and RUFLAG = (select RREBATE from MAST_CUST where RREBATE = " + "'" + rRebate + "')";
        Log.d("Query", query);
        c = myDatabase.rawQuery("select * from TARIFF_CONFIG_CURRENT where TARIFF_CODE = (select TARIFF from MAST_CUST where CONSNO = " + "'" + rRNO + "') and RUFLAG = (select RREBATE from MAST_CUST where RREBATE = " + "'" + rRebate + "')", null);
        return c;
    }
    public Cursor getBilledRRNo(String value) {
        Cursor c1 = null;
        c1 = myDatabase.rawQuery("Select * from MAST_OUT where CONSNO = " + "'" + value + "'", null);
        return c1;
    }
    public Cursor getRRNo(String value) {
        Cursor c1 = null;
        c1 = myDatabase.rawQuery("Select * from MAST_CUST where RRNO = " + "'" + value + "'", null);
        return c1;
    }
    public Cursor getAccountID(String value) {
        Cursor c1 = null;
        c1 = myDatabase.rawQuery("Select * from MAST_CUST where CONSNO = " + "'" + value + "'", null);
        return c1;
    }
    public Cursor searchbyid() {
        Cursor c29 = null;
        c29 = myDatabase.rawQuery("select CONSNO from MAST_CUST", null);
        return c29;
    }
    public Cursor statuscode(String value) {
        Cursor c4 = null;
        c4 = myDatabase.rawQuery("select * from BILLING_STATUS where STATUS_CODE = " + "'" + value + "'", null);
        return c4;
    }
    public Cursor notbilled() {
        Cursor c25 = null;
        c25 = myDatabase.rawQuery("SELECT * from MAST_CUST where CONSNO not in (SELECT CONSNO from MAST_OUT)", null);
        return c25;
    }
    public Cursor dlbilled() {
        Cursor c25 = null;
        c25 = myDatabase.rawQuery("select * from MAST_OUT where PRES_STS = '1'", null);
        return c25;
    }
    public Cursor totalbillingrecords() {
        Cursor c25 = null;
        c25 = myDatabase.rawQuery("select * from MAST_CUST", null);
        return c25;
    }
    public Cursor searchbyname() {
        Cursor c27 = null;
        c27 = myDatabase.rawQuery("select name from MAST_CUST", null);
        return c27;
    }
    public Cursor searchbyrrno() {
        Cursor c28 = null;
        c28 = myDatabase.rawQuery("select rrno from MAST_CUST", null);
        return c28;
    }
    public Cursor getID(String value) {
        Cursor c30 = null;
        c30 = myDatabase.rawQuery("select * from MAST_CUST where CONSNO = " + "'" + value + "'", null);
        return c30;
    }
    public Cursor getBilledID(String value) {
        Cursor c30 = null;
        c30 = myDatabase.rawQuery("select * from MAST_OUT where CONSNO = " + "'" + value + "'", null);
        return c30;
    }
    public Cursor getName(String value) {
        Cursor c31 = null;
        c31 = myDatabase.rawQuery("select * from MAST_CUST where NAME = " + "'" + value + "'", null);
        return c31;
    }
    public Cursor prvstatus(String value) {
        Cursor c36 = null;
        c36 = myDatabase.rawQuery("select * from MAST_CUST,BILLING_STATUS where BILLING_STATUS.STATUS_CODE = MAST_CUST.PREVSTAT and MAST_CUST.CONSNO = " + "'" + value + "'", null);
        return c36;
    }
    public Cursor deletebillingrow(String value) {
        Cursor c44 = null;
        c44 = myDatabase.rawQuery("delete from MAST_OUT where CONSNO = " + "'" + value + "'", null);
        return c44;
    }
    public Cursor addingSSNO() {
        Cursor c48 = null;
        c48 = myDatabase.rawQuery("select count(CONSNO)+1 SSNO from MAST_OUT", null);
        return c48;
    }
    public Cursor subdivdetails() {
        Cursor c49 = null;
        c49 = myDatabase.rawQuery("select * from SUBDIV_DETAILS", null);
        return c49;
    }
    public Cursor counttobill() {
        Cursor c57 = null;
        c57 = myDatabase.rawQuery("SELECT COUNT(*)CUST,MRCODE FROM MAST_CUST", null);
        return c57;
    }
    public void insertInRowEmptyTable(ContentValues cv1) {
        myDatabase.insert("MAST_ROW_EMPTY", null, cv1);
    }
    public Cursor flECcount() {
        Cursor c58 = null;
        c58 = myDatabase.rawQuery("select * from TARIFF_CONFIG_CURRENT where TARIFF_CODE = '23'", null);
        return c58;
    }
    public Cursor getrowemptytable() {
        Cursor c59 = null;
        c59 = myDatabase.rawQuery("select * from MAST_ROW_EMPTY", null);
        return c59;
    }
    public Cursor feder_details() {
        return myDatabase.rawQuery("select DISTINCT FEDER_NAME, FEDER_CODE FROM TC_DETAILS", null);
    }
    public Cursor dtc_code_details(String value) {
        return myDatabase.rawQuery("select DTC_CODE FROM TC_DETAILS WHERE Feder_Code = '"+value+"'", null);
    }
    public String feder_code(String value) {
        Cursor data = myDatabase.rawQuery("select DISTINCT FEDER_CODE FROM TC_DETAILS WHERE FEDER_NAME = '"+value+"'", null);
        data.moveToNext();
        String code = data.getString(data.getColumnIndexOrThrow("FEDER_CODE"));
        data.close();
        return code;
    }
    public boolean doesTableExist() {
        Cursor cursor = myDatabase.rawQuery("select DISTINCT FEDER_NAME, FEDER_CODE FROM TC_DETAILS", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
