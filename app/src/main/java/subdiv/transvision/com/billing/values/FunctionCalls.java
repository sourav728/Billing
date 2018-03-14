package subdiv.transvision.com.billing.values;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FunctionCalls {
    public String Appfoldername() {
        return "TRM_Smart_Billing" + File.separator + "data" + File.separator + "files";
    }
    public void showtoast(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
    }
    public void showtoastnormal(Context context, String Message) {
        Toast toast = Toast.makeText(context, Message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }
    public String currentDateandTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
        return sdf.format(new Date());
    }
    public String monthnumber(String date) {
        return date.substring(3, 5);
    }
    public boolean isDeviceSupportCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            return false;
        }
    }
    public Bitmap getImage(String path, Context con) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        int[] newWH = new int[2];
        newWH[0] = srcWidth / 2;
        newWH[1] = (newWH[0] * srcHeight) / srcWidth;

        int inSampleSize = 1;
        while (srcWidth / 2 >= newWH[0]) {
            srcWidth /= 2;
            srcHeight /= 2;
            inSampleSize *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inSampleSize = inSampleSize;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(path, options);
        ExifInterface exif = new ExifInterface(path);
        String s = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        System.out.println("Orientation>>>>>>>>>>>>>>>>>>>>" + s);
        Matrix matrix = new Matrix();
        float rotation = rotationForImage(con, Uri.fromFile(new File(path)));
        if (rotation != 0f) {
            matrix.preRotate(rotation);
        }

        Bitmap pqr = Bitmap.createBitmap(
                sampledSrcBitmap, 0, 0, sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(), matrix, true);


        return pqr;
    }
    public float rotationForImage(Context context, Uri uri) {
        if (uri.getScheme().equals("content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(
                    uri, projection, null, null, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } else if (uri.getScheme().equals("file")) {
            try {
                ExifInterface exif = new ExifInterface(uri.getPath());
                int rotation = (int) exifOrientationToDegrees(
                        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL));
                return rotation;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return 0f;
    }
    public static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    public void checkimage_and_delete(String foldername, String ConsumerID) {
        String folderpath = filepath(foldername);
        int considlength = ConsumerID.length();
        File imagefiledir = new File(folderpath);
        File[] files = imagefiledir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String filepath = files[i].getName();
            String findimage = filepath.substring(0, considlength);
            if (findimage.equals(ConsumerID)) {
                File file = new File(folderpath + File.separator + filepath);
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }
    public String currentDateTimeforBilling() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss", Locale.US);
        return sdf.format(new Date());
    }
    public Date selectiondate(String date) {
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }
    //Getting the duedate
    public String duedate(String s, int duedate) throws ParseException {
        Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(s);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, duedate);
        return sdf.format(c.getTime());
    }
    public File filestorepath(String value, String file) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfoldername()
                + File.separator + value);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, File.separator + file);
    }
    public String decimalround(double value) {
        String val = String.valueOf(value);
        String val1 = val.substring(val.indexOf('.')+1, val.length());
        if (val1.length() > 2) {
            String val3 = val1.substring(2, 3);
            BigDecimal a = new BigDecimal(value);
            BigDecimal roundOff;
            BigDecimal roundOff1;
            BigDecimal a1;
            if (val1.length() > 3) {
                String val4 = val1.substring(3, 4);
                if (Integer.parseInt(val4) < 5) {
                    roundOff1 = a.setScale(3, BigDecimal.ROUND_HALF_EVEN);
                    a1 = new BigDecimal(String.valueOf(roundOff1));
                    roundOff = a1.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                } else {
                    roundOff1 = a.setScale(3, BigDecimal.ROUND_UP);
                    a1 = new BigDecimal(String.valueOf(roundOff1));
                    roundOff = a1.setScale(2, BigDecimal.ROUND_UP);
                }
            } else {
                if (Integer.parseInt(val3) < 5) {
                    roundOff = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                } else roundOff = a.setScale(2, BigDecimal.ROUND_UP);
            }
            return String.valueOf(roundOff);
        } else return String.valueOf(value);
    }
    public String CalculateDays(String Prev_date, String Pres_date) {
        Prev_date = changedateformat(Prev_date, "/");
        Pres_date = changedateformat(Pres_date, "/");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date Date1 = null;
        Date Date2 = null;
        try {
            Date1 = format.parse(Prev_date);
            Date2 = format.parse(Pres_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long result = (Date2.getTime() - Date1.getTime()) / (24 * 60 * 60 * 1000);
        return "" + result;
    }
    public String changedateformat(String datevalue, String changedivider) {
        Date date = null;
        if ((datevalue.substring(datevalue.length() - 5, datevalue.length() - 4)).equals("/")) {
            try {
                date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(datevalue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if ((datevalue.substring(datevalue.length() - 5, datevalue.length() - 4)).equals("-")) {
                try {
                    date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(datevalue);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                if (datevalue.length() == 8) {
                    try {
                        date = new SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(datevalue.substring(0, 2) + "-"
                                + datevalue.substring(2, 4) + "-" + datevalue.substring(4));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String format = sdf.format(c.getTime());
        return format.substring(0, 2) + changedivider + format.substring(3, 5) + changedivider + format.substring(6, 10);
    }
    public String filepath(String value) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfoldername()
                + File.separator + value);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.toString();
    }

    public File filestore(String value) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfoldername()
                + File.separator + value);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    public boolean compare_end_billing_time(String end_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        Date todate = null;
        try {
            todate = sdf.parse(end_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date currentime = collectiontime(currentTime());
        if (currentime.before(todate)) {
            logStatus("less");
            return true;
        } else return false;
    }
    public Date collectiontime(String date) {
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("HH:mm", Locale.US).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }
    public String pdcalculation(String pdrecorded, Cursor c, String sanctionload, String weeksts) {
        double pdcal = 0;
        double pd = Double.parseDouble(pdrecorded);
        double load = Double.parseDouble(sanctionload);
        double pddiff = pd - load;
        if (pddiff > 0) {
            c.moveToNext();
            if (weeksts.equals("1")) {
                String frate = c.getString(c.getColumnIndex("FRATE1"));
                double frate1 = Double.parseDouble(frate);
                pdcal = frate1 * pddiff * 2;
            } else {
                if (weeksts.equals("2")) {
                    String frate = c.getString(c.getColumnIndex("FRATE2"));
                    double frate1 = Double.parseDouble(frate);
                    pdcal = frate1 * pddiff * 2;
                } else {
                    if (weeksts.equals("3")) {
                        String frate = c.getString(c.getColumnIndex("FRATE3"));
                        double frate1 = Double.parseDouble(frate);
                        pdcal = frate1 * pddiff * 2;
                    } else {
                        if (weeksts.equals("4")) {
                            String frate = c.getString(c.getColumnIndex("FRATE4"));
                            double frate1 = Double.parseDouble(frate);
                            pdcal = frate1 * pddiff * 2;
                        } else {
                            if (weeksts.equals("5")) {
                                String frate = c.getString(c.getColumnIndex("FRATE5"));
                                double frate1 = Double.parseDouble(frate);
                                pdcal = frate1 * pddiff * 2;
                            }
                        }
                    }
                }
            }
        }
        return decimalroundoff(pdcal);
    }
    public String currentTime() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String present_date1 = hour + ":" + minute;
        Date date = null;
        try {
            date = new SimpleDateFormat("HH:mm", Locale.US).parse(present_date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        c.setTime(date);
        return sdf.format(c.getTime());
    }
    public String convertTo24Hour(String Time) {
        String convert = Time.substring(Time.length()-2);
//        convert = convert.substring(0, 1)+"."+convert.substring(1, 2)+".";
        convert = convert.toUpperCase();
        Time = Time.substring(0, Time.length()-2)+ " " + convert;
        String formattedDate="";
        try {
            SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
            SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm", Locale.US);
            formattedDate = outFormat.format(inFormat.parse(Time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        logStatus("Converted: "+formattedDate);
        return formattedDate;
    }
    public void logStatus(String message) {
        Log.d("debug", message);
    }
    public void showtoastatcenter(Context context, String Message) {
        Toast toast = Toast.makeText(context, Message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public String decimalroundoff(double value) {
        BigDecimal a = new BigDecimal(value);
        BigDecimal roundOff = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return "" + roundOff;
    }

}
