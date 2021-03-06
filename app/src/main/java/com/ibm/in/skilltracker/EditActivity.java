package com.ibm.in.skilltracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditActivity extends AppCompatActivity {
    public ProgressDialog progress;

    private int startClientID = 1117 , endClientID , cbllClientKey = 3323 , dClientKey = 4723 , lwClientKey = 6343 ;
    private int startCertID = 7109  , endCertID , cbllCertKey = 8231 , dCertKey = 9319 , lwCertKey = 10079 ;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkIfNetworkIsConnected();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //  refreshUserClients();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new EditSkillFragment(), "SKILL");
        adapter.addFrag(new EditClientFragment(), "CLIENT");
        adapter.addFrag(new EditCertificateFragment(), "CERTIFICATE");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }




    private void checkIfNetworkIsConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            startActivity(new Intent(EditActivity.this,FirstActivity.class));
        }
    }



    private class GetUserCertificates extends HttpPost {

        @Override
        protected void onPreExecute(){
            checkIfNetworkIsConnected();
            //progress= new ProgressDialog(EditActivity.this);
            progress.setMessage("Fetching Certificates");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(String res){

            try {
                progress.setMessage("Updating Certificates");
                JSONArray allCerts= new JSONArray(res);

                LinearLayout certLayout = (LinearLayout) findViewById(R.id.edit_certificates);
                certLayout.removeAllViews();

                CheckBox c;
                LinearLayout ll;
                String text;
                int checkboxID;
                JSONObject cert;
                for(int i=0;i<allCerts.length();i++){
                    checkboxID = startCertID+i;

                    cert = allCerts.getJSONObject(i);

                    c = new CheckBox(EditActivity.this);
                    text = cert.getString("data");
                    c.setText(text);
                    c.setId(checkboxID);
                    final int finalCheckboxID = checkboxID;
                    c.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckBox c = (CheckBox) v;
                            LinearLayout l = (LinearLayout) findViewById(finalCheckboxID + cbllCertKey);
                            if (c.isChecked()) {
                                Spinner spinner = new Spinner(EditActivity.this);

                                ArrayList<String> spinnerArray = new ArrayList<String>();
                                spinnerArray.add("Year");
                                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                for (int i = 1990; i <= currentYear; i++) {
                                    spinnerArray.add("" + i);
                                }
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(EditActivity.this, R.layout.spinner_format, spinnerArray);
                                spinner.setAdapter(spinnerArrayAdapter);
                                spinner.setId(finalCheckboxID + dCertKey);
                                l.addView(spinner);

                                spinner = new Spinner(EditActivity.this);
                                spinnerArray = new ArrayList<String>();
                                spinnerArray.add("Month");
                                for (int i = 1990; i <= currentYear; i++) {
                                    spinnerArray.add("" + i);
                                }
                                spinnerArrayAdapter = new ArrayAdapter<String>(EditActivity.this, R.layout.spinner_format, spinnerArray);
                                spinner.setAdapter(spinnerArrayAdapter);
                                spinner.setId(finalCheckboxID + lwCertKey);
                                l.addView(spinner);

                            } else {
                                if (l != null) {
                                    l.removeAllViews();
                                }
                            }
                        }
                    });

                    ll = new LinearLayout(EditActivity.this);
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.setId(checkboxID+cbllCertKey);


                    if(cert.getBoolean("checked")){
                        c.setChecked(true);
                    } else {
                        c.setChecked(false);
                    }
                    certLayout.addView(c);
                    certLayout.addView(ll);
                }
                endCertID= startCertID + allCerts.length()-1;

                Button b = new Button(EditActivity.this);
                b.setText("Submit");
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox c;
                        Spinner d , lw ;
                        String dText , lwText;
                        boolean flag = true;
                        JSONArray certificates = new JSONArray();
                        JSONObject cert;
                        for (int i = startCertID; i <= endCertID; i++) {
                            if(flag) {
                                c = (CheckBox) findViewById(i);
                                if (c.isChecked()) {
                                    d = ((Spinner) findViewById(i + dCertKey));
                                    lw = ((Spinner) findViewById(i + lwCertKey));
                                    if(d!=null && lw!=null) {
                                        dText = d.getSelectedItem().toString();
                                        lwText = lw.getSelectedItem().toString();
                                        if (dText.equals("Year")) {
                                            flag = false;
                                            toastMessage("Select Year for all Certificates");
                                        } else {
                                            if (lwText.equals("Month")) {
                                                lwText = null;
                                            }
                                            cert = new JSONObject();
                                            try {
                                                cert.put("data", c.getText().toString());
                                                cert.put("year", dText);
                                                cert.put("month", lwText);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            certificates.put(cert);
                                        }
                                    } else {
                                        cert = new JSONObject();
                                        try {
                                            cert.put("data", c.getText().toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        certificates.put(cert);
                                    }
                                }
                            } else {
                                break;
                            }
                        }

                        if(flag) {
                            SharedPreferences user = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
                            (new UpdateUserCertificates()).execute(
                                    "/app/UpdateUserCertificates",
                                    "id=" + user.getString("id", null) + "&certificate=" + certificates.toString()
                            );
                        }
                    }
                });
                certLayout.addView(b);
                progress.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private class UpdateUserCertificates extends HttpPost {

        @Override
        protected void onPreExecute(){
            checkIfNetworkIsConnected();
            progress= new ProgressDialog(EditActivity.this);
            progress.setMessage("Updating Clients");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(String res){
            progress.dismiss();
            if(res.equals("success")){
                LinearLayout ll;
                for(int i = startCertID+cbllCertKey; i<= endCertID+cbllCertKey; i++){
                    ll = (LinearLayout) findViewById(i);
                    if(ll!=null){
                        ll.removeAllViews();
                    }
                }
            } else if (res.equals("fail")){
                toastMessage("There was some error, try again");
            } else {
                toastMessage("There was some error. Please restart the app or contact the admin.");
            }
        }
    }

    private void toastMessage(String message){
        Toast.makeText(EditActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
