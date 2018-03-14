package subdiv.transvision.com.billing.billing;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import subdiv.transvision.com.billing.R;
import subdiv.transvision.com.billing.database.Databasehelper;
import subdiv.transvision.com.billing.values.FunctionCalls;

import static subdiv.transvision.com.billing.values.ConstantValues.BILLINGSTS_DLG;
import static subdiv.transvision.com.billing.values.ConstantValues.BILLING_CUT_OFF_TIME;
import static subdiv.transvision.com.billing.values.ConstantValues.BILLING_ERROR;
import static subdiv.transvision.com.billing.values.ConstantValues.BILLING_TIME_OVER;

public class BillingFragment extends Fragment implements View.OnClickListener {
    Databasehelper dbh;
    ImageView billing;
    Cursor c1, c5;
    String billingstatus = "", counttobill = "", billing_flag = "";
    TextView total_count;
    FunctionCalls fcall;

    public BillingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_billing, container, false);
        billing = (ImageView) view.findViewById(R.id.img_billing);
        total_count = (TextView) view.findViewById(R.id.tv_total);
        billing.setOnClickListener(this);
        fcall = new FunctionCalls();
        dbh = new Databasehelper(getActivity());
        dbh.openDatabase();
        c5 = dbh.counttobill();
        c5.moveToNext();
        counttobill = c5.getString(c5.getColumnIndex("CUST"));
        total_count.setText(counttobill);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_billing:
                checkforbilling();
        }
    }

    public void checkforbilling() {
        if (fcall.compare_end_billing_time(fcall.convertTo24Hour(BILLING_CUT_OFF_TIME))) {
            c1 = dbh.subdivdetails();
            c1.moveToNext();
            billingstatus = c1.getString(c1.getColumnIndex("BILLING_STATUS"));
            billing_flag = c1.getString(c1.getColumnIndex("COLLECTION_FLAG"));
            if (billingstatus.equals("BO")) {
                if (billing_flag.equals("N") || billing_flag.equals("n")) {
                    if (dbh.getData().getCount() > 0) {
                        //getFragmentManager().beginTransaction().replace(R.id.content_frame, new ConsumerBilling()).addToBackStack(null).commit();
                        Intent intent = new Intent(getActivity(), ConsumerBilling.class);
                        startActivity(intent);
                    } else showDialog(BILLING_ERROR);
                } else
                    fcall.showtoastatcenter(getActivity(), "Can't take billing from this device!!");
            } else showDialog(BILLINGSTS_DLG);
        } else showDialog(BILLING_TIME_OVER);
    }

    protected void showDialog(int id) {
        Dialog d = null;
        switch (id) {
            case BILLING_ERROR:
                AlertDialog.Builder billerror = new AlertDialog.Builder(getActivity());
                billerror.setTitle("Billing");
                billerror.setMessage("Sorry no Records found in Billing so cannot take bills..");
                billerror.setNeutralButton("OK", null);
                d = billerror.create();
                d.show();
                break;
            case BILLINGSTS_DLG:
                AlertDialog.Builder ab1 = new AlertDialog.Builder(getActivity());
                ab1.setTitle("Subdiv Billing");
                ab1.setMessage("Billing Completed");
                ab1.setNeutralButton("OK", null);
                d = ab1.create();
                d.show();
                break;
            case BILLING_TIME_OVER:
                AlertDialog.Builder time_over = new AlertDialog.Builder(getActivity());
                time_over.setTitle("Time Over");
                time_over.setCancelable(false);
                time_over.setMessage("Billing Time is over... Can not take billing now...");
                time_over.setNeutralButton("OK", null);
                d = time_over.create();
                d.show();
                break;
        }
    }

}
